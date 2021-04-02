package com.zxy.zxydowloadfile

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.zxy.zxydialog.AlertDialogUtils
import com.zxy.zxydowload.DowloadFile
import com.zxy.zxydowload.utils.requestPermission
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by zsf on 2021/2/8 10:38
 * ******************************************
 * *
 * ******************************************
 */
class MainActivity : AppCompatActivity() {
    var alertDialogUtils: AlertDialogUtils? = null
    lateinit var mProgressBar: ProgressBar
    lateinit var tvStep: TextView

    val apkPath =
        "你自己的apk网路路径.apk"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //点击按钮开始下载
        btnDowload.setOnClickListener {
            showAlertDialog()
            //开始下载
            DowloadFile.init(apkPath, this) { step, isSuccess ->
                setProgressBar(step, isSuccess)
            }
        }
    }

    /**
     *  显示对话框
     */
    private fun showAlertDialog() {
        alertDialogUtils = AlertDialogUtils.build(this)
            .setView(R.layout.dialog_button)    //必选 设置布局View
            .setTransparency(0.5f)                //可选 设置窗口透明度，默认0.5
            .setOnClick(R.id.tvStep)    //可选 设置布局的点击事件-这里没有点击事件，就默认了
            .create { view, alertDialogUtils ->
                //对点击事件的处理-这里没有
            }
        mProgressBar = alertDialogUtils!!.layoutView!!.findViewById(R.id.mProgressBar)
        tvStep = alertDialogUtils!!.layoutView!!.findViewById(R.id.tvStep)
    }

    /**
     * 设置对话框的进度
     * @param step 下载的百分比
     */
    private fun setProgressBar(step: Int, isSuccess: Boolean) {
        //下载失败
        if (!isSuccess)
            alertDialogUtils?.dismiss()

        //下载完成，取消对话框
        if (step == 100)
            alertDialogUtils?.dismiss()

        mProgressBar.progress = step
        tvStep.text = "$step%"
    }
}
