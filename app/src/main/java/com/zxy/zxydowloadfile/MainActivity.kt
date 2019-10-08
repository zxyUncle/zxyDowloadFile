package com.zxy.zxydowloadfile

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import androidx.core.content.FileProvider
import com.zxy.zxydowload.DowloadFile
import com.zxy.zxydowload.PackageUtils
import java.io.File
import java.util.logging.Logger

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //首先获取动态权限
        if (Build.VERSION.SDK_INT >= 23) {
            var REQUEST_CODE_CONTACT = 101

            var permissions = mutableListOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            //验证是否许可权限

            if (checkSelfPermission(permissions.toString()) != PackageManager.PERMISSION_GRANTED) {
                //申请权限
                requestPermissions(permissions.toTypedArray(), REQUEST_CODE_CONTACT);
            }
        }

        var path = Environment.getExternalStorageDirectory().getAbsolutePath()
        var fileName = "${PackageUtils.getAppName(this)}${PackageUtils.getVersionCode(this)}.apk"
        var fileAPK = File(path, fileName)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            val contentUri = FileProvider.getUriForFile(
                this,
                "com.zxy.zxydowloadfile.fileprovider",
                fileAPK
            )
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive")
        } else {
            var uri = Uri.fromFile(fileAPK)
            intent.setDataAndType(uri, "application/vnd.android.package-archive")
        }
        startActivity(intent)

//        var dowloadFile = DowloadFile()
//        dowloadFile.init(
//            "https://cdn-shanghai.oss-cn-shanghai.aliyuncs.com/app/kmiles_v3.6.8.apk", "com.zxy.zxydowloadfile",
//            this
//        )
    }
}
