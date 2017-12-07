[TOC]
# QrCodeUtils
可能是东半球最难用的Android二维码扫描工具

## 添加依赖
```java
compile 'com.atiao:qrcode:1.0'
```

## 初始化
布局文件添加com.qrcode.view.QrCodePreView自定义控件
将控件传入
```java
mQRPresenter.startCamera();
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
