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
        const val EXTRA_URL = "extra_url"
        private const val BASE_URL = "https://pay.raif.ru/pay?publicId=000002100000101-00000101&amount=10&paymentMethod=ONLY_SBP"
        private const val TAG = "WebViewDebug"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        val webView = findViewById<WebView>(R.id.webview)
        webView.settings.apply {
            javaScriptEnabled = true
            setSupportMultipleWindows(false)
            domStorageEnabled = true
        }

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest): Boolean {
                val url = request.url.toString()
                Log.d(TAG, "shouldOverrideUrlLoading: $url")

                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    return try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        startActivity(intent)
                        Log.d(TAG, "Запущено внешнее приложение для URI: $url")
                        true
                    } catch (e: Exception) {
                        Log.e(TAG, "Ошибка запуска внешнего приложения", e)
                        Toast.makeText(this@WebViewActivity, "Не удалось открыть ссылку: ${e.message}", Toast.LENGTH_SHORT).show()
                        true
                    }
                }
                return false
            }
        }

        val url = intent.getStringExtra(EXTRA_URL) ?: BASE_URL
        Log.d(TAG, "Загружаем URL: $url")

        // Проверяем, не является ли переданный URL deeplink
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            // Это deeplink – пытаемся открыть сразу, без загрузки в WebView
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
                Log.d(TAG, "Запущено внешнее приложение для начального URL: $url")
                finish() // закрываем активность, она не нужна
            } catch (e: Exception) {
                Log.e(TAG, "Не удалось открыть deeplink: $url", e)
                Toast.makeText(this, "Приложение для обработки ссылки не найдено", Toast.LENGTH_LONG).show()
                finish()
            }
        } else {
            // Обычный http/https – загружаем в WebView
            webView.loadUrl(url)
        }
    }
}