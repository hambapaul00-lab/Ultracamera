package com.ultracamera.app

import android.app.Activity
import android.os.Bundle
import android.widget.TextView

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val textView = TextView(this).apply {
            text = "📷 UltraCamera\n\nStatus : L'application fonctionne parfaitement !"
            textSize = 22f
            setPadding(60, 120, 60, 60)
        }

        setContentView(textView)
    }
}
