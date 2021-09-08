# android 版本更新 一句话完成下载，适配7.0,8.0,9.0,10.0,11.0系统安装apk


[![](https://jitpack.io/v/zxyUncle/zxyDowloadFile.svg)](https://jitpack.io/#zxyUncle/zxyDowloadFile)

Gradle
-----
Step 1


     allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

Step 2. Add the dependency

          implementation 'com.github.zxyUncle:zxyDowloadFile:latest.release'

# 使用：Kotlin版，当然也可以用java调用

#  1. 自带动态获取权限读写权限
#  2. 一句话开启下载

     //开始下载
            DowloadFile.init(apkPath, this) { step, isSuccess ->
               //step 返回的进度  isSuccess 是否下载失败
            }

#  3. 自定义对话框布局，更换dialog_button.xml布局就可以

    alertDialogUtils = AlertDialogUtils.build(this)
            .setView(R.layout.dialog_button)    //必选 设置布局View
            .create { view, alertDialogUtils ->
                //对点击事件的处理-这里没有
            }

# 更新日志 1.1：
1、修改9.0不能安装的BUg
2、增加强制更新
3、设置进度条颜色设置，防止不同机型颜色不一致

# 更新日志 1.3：
1、修改去掉内容提供者，在module中执行
2、修改一个手机只能有一个app使用这个module的bug

# 更新日志 1.4：
1、适配10.0