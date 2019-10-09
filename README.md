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

##使用：

 1. 获取写入权限

            //首先获取动态权限-写入权限,简单操作，也可以用第三方动态权限,处理拒绝权限时等其他处理
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

            //开始下载
        DowloadFile().init("http://cdn.ferry10.com/packages/10/pinjamdong2/PinjamDong.apk", this)
        


 3. 如果感觉下载的对话框不好看，自己把module下载了，将zxydowload（module）加入自己的项目

