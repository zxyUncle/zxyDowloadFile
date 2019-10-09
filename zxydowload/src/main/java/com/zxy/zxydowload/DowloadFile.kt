package com.zxy.zxydowload

import android.Manifest
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import okhttp3.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import androidx.core.content.ContextCompat.startActivity
import com.zxy.zxytools.dialog.AlertDialogUtils
import java.lang.ref.WeakReference


/**
 * Created by zxy on 2019/9/29-13:27
 * Class functions
 * ******************************************
 * * 下载apk module
 * ******************************************
 */
class DowloadFile {
    var fileAPK: File? = null
    var inputStream: InputStream? = null
    var fileOutputStream: FileOutputStream? = null
    var mContext: Context? = null
    lateinit var fileName: String
    var TAG = "dowload"
    var path = Environment.getExternalStorageDirectory()
    lateinit var alertDialogUtils: AlertDialogUtils
    var dialog_progressBar: ProgressBar? = null //滚动条
    var dilaog_temp: TextView? = null//百分比
    lateinit var myHandler: MyHandler
    var isForceUpdate: Boolean = false//是否强制更新

    inner class MyHandler(activity: Activity) : Handler() {
        private val mActivity: WeakReference<Activity> = WeakReference(activity)
        override fun handleMessage(msg: Message) {
            if (mActivity.get() == null) {
                return
            }
            val activity = mActivity.get()

            if (alertDialogUtils == null) {
                showAlertDialog(mContext!!)
            }
            if (dialog_progressBar == null) {
                dialog_progressBar =
                    alertDialogUtils.layoutView.findViewById(R.id.dialog_progressBar)
                dilaog_temp = alertDialogUtils.layoutView.findViewById(R.id.dilaog_temp)
            }
            dialog_progressBar!!.progress = msg.arg1
            dilaog_temp!!.text = "${msg.arg1.toString()}%"
            //100%取消弹出框
            if (msg.arg1 == 100) {
                alertDialogUtils.cancel()
            }
        }
    }

    /**
     * 初始化
     * @param url String    更新APK的路径
     * @param mContext Context
     * @param isForceUpdate Boolean 是否强制更新 可选
     */
    fun init(url: String, mContext: Context, isForceUpdate: Boolean = false) {
        this.mContext = mContext
        this.isForceUpdate = isForceUpdate
        myHandler = MyHandler(mContext!! as Activity)// 初始化Handler
        showAlertDialog(mContext!!)
        initNet(url)
    }

    fun showForceUpdate(){

    }

    /**
     * 网路初始化
     * @param url String
     */
    fun initNet(url: String) {
        this.fileName =
            "${PackageUtils.getAppName(mContext!!)}${PackageUtils.getVersionCode(mContext!!)}${System.currentTimeMillis()}.apk"
        val request = Request.Builder()
            .url(url)
            .build()
        val call = OkHttpClient().newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "下载失败：url")
                if (alertDialogUtils != null) {
                    alertDialogUtils.cancel()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                Log.e(TAG, response.toString())
                if (response.isSuccessful()) {
                    Log.e(TAG, "开始下载")
                    writeFile(response)
                }
            }

        })
    }

    /**
     * 文件是否存在
     * @param file File
     */
    private fun fileExist(file: File) {
        if (!file.exists()) {
            file.delete()
        }
        file.createNewFile()
    }

    /**
     * 数据写入
     * @param response Response
     */
    private fun writeFile(response: Response) {
        inputStream = response.body()!!.byteStream()
        fileAPK = File(path, fileName)
        fileExist(fileAPK!!)
        Log.e(TAG, fileAPK!!.path.toString())
        try {

            fileOutputStream = FileOutputStream(fileAPK)
            var bytes = ByteArray(1024)
            var len = 0
            //获取下载的文件的大小
            var fileSize = response.body()!!.contentLength()
            var sum = 0
            var porSize = 0
            while (inputStream!!.read(bytes).apply { len = this } != -1) {
                fileOutputStream!!.write(bytes, 0, len)
                sum += len
                porSize = ((sum * 1.0f / fileSize) * 100).toInt()
                var message = Message()
                message.arg1 = porSize;
                Log.e(TAG, "下载进度:${porSize}")
                myHandler.sendMessage(message)
            }
            fileOutputStream!!.flush()
            Log.e(TAG, "下载成功")
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

    private fun getApkPath(): String {
        return "${path}/${fileName}"
    }

    /**
     * 显示AlertDialog
     */
    fun showAlertDialog(mContext: Context) {
        alertDialogUtils = AlertDialogUtils.build(mContext)
            .setView(R.layout.dialog_button)    //必选 设置布局View
            .setCancelable(!isForceUpdate)                //可选 设置是否可以取消，默认true
            .setTransparency(0.5f)                //可选 设置窗口透明度，默认0.5
            .setOnClick(R.id.dilaog_temp)    //可选 设置布局的点击事件
            .create(object : AlertDialogUtils.Builder.AlertDialogUtilsListener {
                override fun onClickDialog(view: View) {
                }
            })
    }

    /**
     * 安装APK
     */
    private fun installApk(fileAPK: File) {
        val intent = Intent(Intent.ACTION_VIEW)
        //判读版本是否在7.0以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val contentUri = FileProvider.getUriForFile(mContext!!, "com.zxy.zxydowload.fileprovider", fileAPK!!)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive")
            //兼容8.0
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                var hasInstallPermission = mContext!!.getPackageManager().canRequestPackageInstalls()
                if (!hasInstallPermission) {
                    //请求安装未知应用来源的权限
                    ActivityCompat.requestPermissions(
                        mContext as Activity,
                        arrayOf(Manifest.permission.REQUEST_INSTALL_PACKAGES),
                        6666
                    )
                }
            }
        } else {
            intent.setDataAndType(Uri.fromFile(fileAPK), "application/vnd.android.package-archive")
        }
        mContext!!.startActivity(intent)
    }
}