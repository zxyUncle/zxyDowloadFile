package com.zxy.zxydowload

import com.zxy.zxydowload.utils.VersionChecker
import java.lang.Exception

/**
 * Created by zsf on 2021/9/8 15:10
 * ******************************************
 * * 获取Google play的版本号
 * ******************************************
 */
object VersionManager {
    var pageName = ""
    fun getGooglePlayVersion(pageName: String): String {
        this.pageName = pageName
        var verson = "0"
        try {
            verson = VersionChecker().execute().get()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return verson

    }
}