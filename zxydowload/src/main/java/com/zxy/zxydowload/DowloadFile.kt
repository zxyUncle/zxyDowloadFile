package com.zxy.zxydowload

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Message
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import okhttp3.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import androidx.core.content.ContextCompat.startActivity



/**
 * Created by zxy on 2019/9/29-13:27
 * Class functions
 * ******************************************
 * *
 * ******************************************
 */
class DowloadFile {
    var fileAPK: File? = null
    var inputStream: InputStream? = null
    var fileOutputStream: FileOutputStream? = null
    var appPackage: String? = null
    var mContext: Context? = null
    lateinit var fileName: String
    fun init(url: String, appPackage: String, mContext: Context) {
        this.mContext = mContext
        this.fileName = "${PackageUtils.getAppName(mContext)}${PackageUtils.getVersionCode(mContext)}.apk"
        this.appPackage = appPackage
        val request = Request.Builder()
            .url(url)
            .build()
        val call = OkHttpClient().newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("dowload", "下载失败：url")
            }

            override fun onResponse(call: Call, response: Response) {
                Log.e("dowload", response.toString())
                if (response.isSuccessful()) {
                    Log.e("dowload", "开始下载")
                    writeFile(response)
                }
            }

        })
    }

    /**
     * 文件是否存在
     * @param file File
     */
    fun fileExist(file: File) {
        if (!file.exists()) {
            file.delete()
        }
        file.createNewFile()
    }

    fun writeFile(response: Response) {
        inputStream = response.body()!!.byteStream()
        var path = Environment.getExternalStorageDirectory().getAbsolutePath()
        fileAPK = File(path, fileName)
        fileExist(fileAPK!!)
        try {
            fileOutputStream = FileOutputStream(fileAPK)
            var bytes = ByteArray(1024)
            var len = 0
            //获取下载的文件的大小
            var fileSize = response.body()!!.contentLength()
            var sum = 0
            var porSize = 0
            while (inputStream!!.read(bytes).apply { len = this } > 0) {
                fileOutputStream!!.write(bytes);
                sum += len;
                porSize = ((sum * 1.0f / fileSize) * 100).toInt()
//                var message = handler . obtainMessage (1);
//                message.arg1 = porSize;
//                handler.sendMessage(message);
                Log.e("dowload", "下载进度:${porSize}")
            }
            Log.e("dowload", "下载成功")
            installApk(fileAPK!!)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                if (inputStream != null) {
                    inputStream!!.close()
                }
                if (fileOutputStream != null) {
                    fileOutputStream!!.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

    }


    //安装程序
    fun installApk(file: File) {

        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(Uri.fromFile(fileAPK), "application/vnd.android.package-archive")
        mContext!!.startActivity(intent)

//        //判读版本是否在7.0以上 todo 这里是7.0安装是会出现解析包的错误
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            // todo 在AndroidManifest中的android:authorities值  当前应用的包名：cn.xu.test+FileProvider（数据共享）
//            var apkUri = FileProvider.getUriForFile(
//                mContext!!,
//                "${appPackage}.FileProvider", fileAPK!!
//            );
//            var install = Intent(Intent.ACTION_VIEW);
//            install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            //添加这一句表示对目标应用临时授权该Uri所代表的文件
//            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            install.setDataAndType(apkUri, "application/vnd.android.package-archive");
//            mContext!!.startActivity(install);
//
//        } else {
//            var install = Intent(Intent.ACTION_VIEW);
//            install.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
//            install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            mContext!!.startActivity(install);
//        }

    }
}