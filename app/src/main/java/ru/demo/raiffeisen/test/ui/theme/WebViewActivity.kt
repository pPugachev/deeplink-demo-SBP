package ru.demo.raiffeisen.test

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class WebViewActivity : AppCompatActivity() {

    companion object {
        private const val BASE_URL = "https://pay.raif.ru/pay/demo.html"
        private const val TAG = "WebViewDebug"
    }

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        webView = findViewById(R.id.webview)
        webView.settings.apply {
            javaScriptEnabled = true
            setSupportMultipleWindows(false) // отключаем попапы
            domStorageEnabled = true
        }

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest): Boolean {
                val url = request.url.toString()
                Log.d(TAG, "shouldOverrideUrlLoading: $url")

                // Если это не http/https, пытаемся открыть как Intent
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    return try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        startActivity(intent) // запускаем внешнее приложение
                        Log.d(TAG, "Запущено внешнее приложение для URI: $url")
                        true // предотвращаем загрузку в WebView
                    } catch (e: Exception) {
                        Log.e(TAG, "Ошибка запуска внешнего приложения", e)
                        Toast.makeText(this@WebViewActivity, "Не удалось открыть ссылку: ${e.message}", Toast.LENGTH_SHORT).show()
                        true // всё равно не загружаем в WebView, чтобы избежать ошибки
                    }
                }
                // Для http/https разрешаем WebView загрузить
                return false
            }
        }

        webView.loadUrl(BASE_URL)
    }
}