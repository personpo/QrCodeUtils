[TOC]
# QrCodeUtils
Android二维码扫描工具，支持选择图片解析。欢迎提bug或需求给我。
联系QQ：97663202

## 使用
```java
allprojects {
    repositories {
        maven { url "https://raw.githubusercontent.com/personpo/QrCodeUtils/master" }
    }
}


compile 'com.atiao:qrcode:1.0'
```

## 初始化
布局文件添加com.qrcode.view.QrCodePreView自定义控件
```java
<com.qrcode.view.QrCodePreView
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/scanview"
    android:layout_width="match_parent"
    android:layout_height="match_parent">
    
 </com.qrcode.view.QrCodePreView>
```

将控件传入
```java
mQRPresenter = new QRCodePresenter(mContext, mQrCodePreView, new QRCodePresenter.QrCodeAnalysisListener() {
            @Override
            public void result(String result) {
                //扫描或通过选择图片的解析结果回调
                Log.d(TAG, "qrResult: " + result);
                } 
            }
        });
```

## 扫一扫预览及关闭预览
```java
mQRPresenter.startCamera();

mQRPresenter.stopCamera();
```
