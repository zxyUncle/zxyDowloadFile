package com.zxy.zxydowloadfile

import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.postDelayed
import com.zxy.zxydowloadfile.tools.MyIntentService
import kotlinx.android.synthetic.main.activity_executor.*
import java.lang.ref.WeakReference
import java.util.*

/**
 * Created by zxy on 2019/10/8-9:59
 * Class functions
 * ******************************************
 * * 多线程
 * ******************************************
 */
class ExecutorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_executor)
        startActivity(Intent(this,MainActivity::class.java))
    }


}