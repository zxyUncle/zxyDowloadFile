package com.zxy.zxydowload

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import com.zxy.zxydialog.TToast
import com.zxy.zxydialog.tools.Applications
import com.zxy.zxydowload.utils.VersionChecker
import java.lang.Exception

/**
 * Created by zsf on 2021/9/8 15:10
 * ******************************************
 * * 获取Google play的版本号
 * ******************************************
 */
@SuppressLint("StaticFieldLeak")
object VersionManager {
    var pageName = ""

    /**
     * 是否跳转到google play
     */
    @SuppressLint("StaticFieldLeak")
    @JvmStatic
    fun isGooglePlayIntent(mContext: Context, pageName: String) {
        this.pageName = pageName
        var versionName = "0"
        var googleVersionName = "0"
        try {
            googleVersionName = VersionChecker().execute().get()
        } catch (e: Exception) {
            e.printStackTrace()
            return
        }

        try {
            versionName = mContext.packageManager.getPackageInfo(
                mContext.packageName,
                0
            ).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        if (googleVersionName > versionName) {
            goToGooglePlay(mContext)
        } else {

        }
    }

    @SuppressLint("StaticFieldLeak")
    fun goToGooglePlay(mContext: Context) {
        // 做跳转到谷歌play做好评的业务逻辑
        //这里开始执行一个应用市场跳转逻辑，默认this为Context上下文对象
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data =
            Uri.parse("market://details?id=$pageName") //跳转到应用市场，非Google Play市场一般情况也实现了这个接口
        //存在手机里没安装应用市场的情况，跳转会包异常，做一个接收判断
        if (intent.resolveActivity(mContext.packageManager) != null) { //可以接收
            mContext.startActivity(intent)
        } else { //没有应用市场，我们通过浏览器跳转到Google Play
            intent.data =
                Uri.parse("https://play.google.com/store/apps/details?id=" + mContext.packageName)
            //这里存在一个极端情况就是有些用户浏览器也没有，再判断一次
            if (intent.resolveActivity(mContext.packageManager) != null) { //有浏览器
                mContext.startActivity(intent)
            } else { //天哪，这还是智能手机吗？
                Toast.makeText(
                    mContext,
                    "You don't have an app market installed, not even a browser!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}