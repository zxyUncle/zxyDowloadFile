package com.zxy.zxydowloadfile.tools

import android.app.IntentService
import android.content.Intent
import android.util.Log

/**
 * Created by zxy on 2019/10/8-10:17
 * Class functions
 * ******************************************
 * *
 * ******************************************
 */
class MyIntentService:IntentService("MyIntentService") {
    override fun onHandleIntent(intent: Intent?) {
        try {
            Thread.sleep(5000)
            Log.i("MyIntentService", "下载完成...")
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}