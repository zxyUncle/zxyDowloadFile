package com.zxy.zxydowload

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.util.Log
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import com.zxy.zxydialog.TToast
import com.zxy.zxydowload.utils.launchMain
import com.zxy.zxydowload.utils.requestPermission
import okhttp3.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream


/**
 * Created by zxy on 2019/9/29-13:27
 * Class functions
 * ******************************************
 * * 下载apk module
 * ******************************************
 */
@SuppressLint("StaticFieldLeak")
object DowloadFile {
    var TAG = "dowload"
    lateinit var fileAPK: File
    var inputStream: InputStream? = null
    var fileOutputStream: FileOutputStream? = null
    lateinit var mContext: Activity
    private lateinit var fileName: String
    private lateinit var fileDirPath: String

    /**
     * 初始化
     * @param url String    更新APK的路径
     * @param mContext Context
     * @param callBack Int 返回的进度  Boolean 是否下载失败
     */
    @JvmStatic
    fun init(url: String, mContext: FragmentActivity, callBack: (Int, Boolean) -> Unit) {
        this.mContext = mContext
        fileDirPath = getMidPath()
        //获取权限
        mContext.requestPermission(
            mutableListOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
            ), {
                initNet(url, callBack)
            }, {
                TToast.show("permission denied")
            })
    }

    /**
     * 获取apk的路径
     */
    @JvmStatic
    fun getApkPath(): String {
        return "$fileDirPath$fileName"
    }


    /**
     * 初始化安装目录，文件名
     */
    private fun getMidPath(): String {
        var sdDir: String
        var sdCardExist = Environment.getExternalStorageState().equals(
            Environment.MEDIA_MOUNTED
        )
        // 判断sd卡是否存在
        sdDir = if (sdCardExist) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                //Android10之后
                Environment.getExternalStorageDirectory().path+"/Download/" //公共区域
            } else {
                Environment.getExternalStorageDirectory().path// 获取SD卡根目录
            }
        } else {
            Environment.getRootDirectory().path// 获取跟目录
        }

        this.fileName =
            "${System.currentTimeMillis()}.apk"
        return "$sdDir/apk/"
    }

    /**
     * 网路初始化
     * @param url String
     */
    private fun initNet(url: String, callBack: (Int, Boolean) -> Unit) {
        val request = Request.Builder()
            .url(url)
            .build()
        val call = OkHttpClient().newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "下载失败：url")
                launchMain {
                    callBack(0, false)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                Log.e(TAG, response.toString())
                if (response.isSuccessful) {
                    Log.e(TAG, "开始下载")
                    writeFile(response, callBack)
                }
            }

        })
    }

    /**
     * 创建文件
     */
    private fun fileMidExist(fileDirPath: String) {
        var fileDir = File(fileDirPath)
        if (!fileDir.isDirectory) {
            fileDir.mkdirs()
        }
    }

    /**
     * 文件是否存在
     * @param file File
     */
    private fun fileExist(file: File) {
        if (!file.exists()) {
            file.createNewFile()
        }
    }

    /**
     * 数据写入
     * @param response Response
     */
    private fun writeFile(response: Response, callBack: (Int, Boolean) -> Unit) {
        inputStream = response.body()?.byteStream()
        fileAPK = File(fileDirPath + fileName)
        // 如果文件夹不存在则创建
        fileMidExist(fileDirPath)
        fileExist(fileAPK)
        Log.e(TAG, fileAPK.path.toString())
        try {
            fileOutputStream = FileOutputStream(fileAPK)
            var bytes = ByteArray(1024)
            var len: Int
            //获取下载的文件的大小
            var fileSize = response.body()?.contentLength() ?: 0
            var sum = 0
            var porSize: Int
            var oldPorSize = 0
            while (inputStream!!.read(bytes).apply { len = this } != -1) {
                fileOutputStream!!.write(bytes, 0, len)
                sum += len
                porSize = ((sum * 1.0f / fileSize) * 100).toInt()
                var message = Message()
                message.arg1 = porSize
                launchMain {
                    if (oldPorSize != porSize) {
                        Log.e(TAG, "下载百分比:$porSize")
                        oldPorSize = porSize
                        callBack(porSize, true)
                    }
                }
            }
            fileOutputStream!!.flush()
            Log.e(TAG, "下载成功")
            installApk(fileAPK)
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


    /**
     * 安装APK
     */
    private fun installApk(fileAPK: File) {
        val intent = Intent(Intent.ACTION_VIEW)
        //判读版本是否在7.0以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val contentUri = FileProvider.getUriForFile(
                mContext,
                "${mContext.applicationContext.packageName}.fileprovider",
                fileAPK
            )
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive")
            //兼容8.0
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                var hasInstallPermission = mContext.packageManager.canRequestPackageInstalls()
                if (!hasInstallPermission) {
                    //请求安装未知应用来源的权限
                    requestPermissions(
                        mContext as Activity,
                        arrayOf(Manifest.permission.REQUEST_INSTALL_PACKAGES),
                        6666
                    )
                }
            }
        } else {
            intent.setDataAndType(Uri.fromFile(fileAPK), "application/vnd.android.package-archive")
        }
        mContext.startActivity(intent)
    }
}