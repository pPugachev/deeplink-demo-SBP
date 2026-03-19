package ru.demo.raiffeisen.test

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import java.util.*

class BankListActivity : AppCompatActivity() {

    companion object {
        private const val MERCHANT_ID = "MB0001198422"
        private const val PLATFORM = "android"
        private const val TAG = "BankListDebug"
        private const val AMOUNT = 10
        private const val PUBLIC_ID = "000002100000101-00000101"
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var adapter: BankAdapter
    private val scope = CoroutineScope(Dispatchers.Main + Job())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bank_list)

        recyclerView = findViewById(R.id.recyclerView)
        searchView = findViewById(R.id.searchView)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = BankAdapter(emptyList()) { member ->
            startWebView(member.url)
        }
        recyclerView.adapter = adapter

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return true
            }
        })

        fetchPayloadAndBanks()
    }

    private fun fetchPayloadAndBanks() {
        scope.launch {
            try {
                // 1. Получаем payload от Райффайзенбанка
                val order = UUID.randomUUID().toString()
                val registerRequest = RegisterRequest(
                    publicId = PUBLIC_ID,
                    amount = AMOUNT.toString(),
                    order = order,
                    origin = "PAYMENT"
                )
                Log.d(TAG, "Register request: $registerRequest")

                val registerResponse = withContext(Dispatchers.IO) {
                    ApiClient.raifApi.registerQr(registerRequest)
                }

                if (!registerResponse.isSuccessful) {
                    val errorBody = registerResponse.errorBody()?.string()
                    Log.e(TAG, "Ошибка регистрации: ${registerResponse.code()} - $errorBody")
                    Toast.makeText(this@BankListActivity, "Ошибка регистрации", Toast.LENGTH_LONG).show()
                    return@launch
                }

                val payload = registerResponse.body()?.payload
                if (payload.isNullOrEmpty()) {
                    Toast.makeText(this@BankListActivity, "Пустой payload", Toast.LENGTH_LONG).show()
                    return@launch
                }
                Log.d(TAG, "Payload получен: $payload")

                // 2. Запрос к НСПК
                val membersResponse = withContext(Dispatchers.IO) {
                    ApiClient.instance.getMembers(
                        clientId = MERCHANT_ID,
                        platform = PLATFORM,
                        payload = payload
                    )
                }

                Log.d(TAG, "Response code: ${membersResponse.code()}")
                Log.d(TAG, "Response headers: ${membersResponse.headers()}")

                if (!membersResponse.isSuccessful) {
                    val errorBody = membersResponse.errorBody()?.string()
                    Log.e(TAG, "Error response body: $errorBody")
                    Toast.makeText(this@BankListActivity, "Ошибка НСПК: ${membersResponse.code()}", Toast.LENGTH_LONG).show()
                    return@launch
                }

                val members = membersResponse.body()?.members ?: emptyList()
                adapter.updateData(members)

            } catch (e: Exception) {
                Log.e(TAG, "Исключение: ${e.message}", e)
                Toast.makeText(this@BankListActivity, "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startWebView(url: String) {
        val intent = Intent(this, WebViewActivity::class.java).apply {
            putExtra(WebViewActivity.EXTRA_URL, url)
        }
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}