package com.zxy.zxydowload.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager


/**
 * Created by zxy on 2019/9/29-15:25
 * Class functions
 * ******************************************
 * * https://cloudpick-fe-test.oss-cn-shanghai.aliyuncs.com/app-package/yunna/$[DamvpXoXEzGOKbfNJfueb]-app-n1_normal-debug
 * ******************************************
 */
class PackageUtils {
    companion object {
        /**
         * 获取版本名称
         *
         * @param context 上下文
         *
         * @return 版本名称
         */
        fun getVersionName(context: Context): String? {

            //获取包管理器
            val pm = context.getPackageManager()
            //获取包信息
            try {
                val packageInfo = pm.getPackageInfo(context.getPackageName(), 0)
                //返回版本号
                return packageInfo.versionName
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }

            return null

        }

        fun getAppID(context: Activity):String{
           return context.application.packageName
        }

        /**
         * 获取版本号
         *
         * @param context 上下文
         *
         * @return 版本号
         */
        fun getVersionCode(context: Context): Int {

            //获取包管理器
            val pm = context.getPackageManager()
            //获取包信息
            try {
                val packageInfo = pm.getPackageInfo(context.getPackageName(), 0)
                //返回版本号
                return packageInfo.versionCode
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }

            return 0

        }

        /**
         * 获取App的名称
         *
         * @param context 上下文
         *
         * @return 名称
         */
        fun getAppName(context: Context): String? {
            val pm = context.getPackageManager()
            //获取包信息
            try {
                val packageInfo = pm.getPackageInfo(context.getPackageName(), 0)
                //获取应用 信息
                val applicationInfo = packageInfo.applicationInfo
                //获取albelRes
                val labelRes = applicationInfo.labelRes
                //返回App的名称
                return context.getResources().getString(labelRes)
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }

            return null


        }
    }
}