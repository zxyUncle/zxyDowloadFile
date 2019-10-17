# android 版本更新


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

          implementation 'com.github.zxyUncle:zxyDowloadFile:Tag'

# 使用：Kotlin版，当然也可以用java调用

 1. 获取写入权限,必须先获取写入权限，否则下载不会成功，获取权限可以使用插件 permissionsdispatcher（有写入权限的可以滤过此步骤）   

        //首先获取动态权限-写入权限,简单操作，可以看到效果，需要运行两边，有写入权限的只需要运行一遍，这里主要是下载功能
        if (Build.VERSION.SDK_INT >= 23) {
            var REQUEST_CODE_CONTACT = 101
            var permissions = mutableListOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            //验证是否许可权限
            if (checkSelfPermission(permissions.toString()) != PackageManager.PERMISSION_GRANTED) {
                //申请权限
                requestPermissions(permissions.toTypedArray(), REQUEST_CODE_CONTACT);
            }
        }

 2. 执行下载

        /**
         * 在获取的权限的回调之后执行，否则没有写入权限
         * 开始下载- 可以取消的对话框进度条
         * 1、apk下载路径
         * 2、this
         */
        DowloadFile().init("http://cdn.ferry10.com/packages/10/pinjamdong2/PinjamDong.apk", this)

**或者**   
    
        /**
         * 在获取的权限的回调之后执行，否则没有写入权限
         * 开始下载-不可以取消下载对话框
         * 1、apk下载路径
         * 2、this
         * 3、是否强制更新，下载对话框不会被取消，强制显示
         */
        DowloadFile().init("http://cdn.ferry10.com/packages/10/pinjamdong2/PinjamDong.apk", this,true)    
3. 如果感觉下载的对话框不好看，自己把项目下载了，将zxydowload（module）加入自己的项目，修改内部布局


#更新日志 1.1：    
1、修改9.0不能安装的BUg    
2、增加强制更新     
3、设置进度条颜色设置，防止不同机型颜色不一致     

#更新日志 1.3：     
1、修改去掉内容提供者，在module中执行     
2、修改一个手机只能有一个app使用这个module的bug    
