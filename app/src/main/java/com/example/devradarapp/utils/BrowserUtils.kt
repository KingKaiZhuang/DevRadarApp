package com.example.devradarapp.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.browser.customtabs.CustomTabsIntent

object BrowserUtils {
    fun openArticleUrl(context: Context, url: String) {
        if (url.isBlank()) return
        try {
            val builder = CustomTabsIntent.Builder()
            val params = androidx.browser.customtabs.CustomTabColorSchemeParams.Builder()
                .setToolbarColor(0xFF0F172A.toInt())
                .build()
            builder.setDefaultColorSchemeParams(params)
            val customTabsIntent = builder.build()
            customTabsIntent.launchUrl(context, Uri.parse(url))
        } catch (e: Exception) {
            Log.e("Browser", "無法開啟網頁: $url", e)
        }
    }
}
