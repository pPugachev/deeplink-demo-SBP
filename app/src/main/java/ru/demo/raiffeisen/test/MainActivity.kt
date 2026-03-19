package ru.demo.raiffeisen.test

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnRaif = findViewById<Button>(R.id.btn_raif)
        val btnNspk = findViewById<Button>(R.id.btn_nspk)

        btnRaif.setOnClickListener {
            startActivity(Intent(this, WebViewActivity::class.java))
        }

        btnNspk.setOnClickListener {
            startActivity(Intent(this, BankListActivity::class.java))
        }
    }
}