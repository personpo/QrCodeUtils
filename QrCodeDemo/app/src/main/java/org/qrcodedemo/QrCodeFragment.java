package org.qrcodedemo;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.QRCodePresenter;
import com.qrcode.view.QrCodePreView;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zbar.lib.CameraManager;

import org.qrcodedemo.utils.NoDoubleClickUtils;
import org.qrcodedemo.utils.RxBarUtils;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


/**
 * atiaotiao
 */
public class QrCodeFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "QRScanFragment";
    private ImageView mImgLight;
    //二维码presenter
    private QRCodePresenter mQRPresenter;
    private QrCodePreView mQrCodePreView;
    protected View mLayout;
    private Activity mContext;
    private Dialog mTipDialog;


    public QrCodeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mLayout != null) {
            return mLayout;
        } else {
            mLayout = inflater.inflate(R.layout.fragment_qr_code, container, false);
            return mLayout;
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext = getActivity();
        initView();
        initQR();
        RxBarUtils.transparencyBar2(mContext);
    }

    protected void initView() {
        mImgLight = (ImageView) mLayout.findViewById(R.id.img_light);
        mImgLight.setOnClickListener(this);
        mLayout.findViewById(R.id.tv_myalbum).setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        checkCameraPermission();
    }

    @Override
    public void onStop() {
        mQRPresenter.stopCamera();
        super.onStop();
    }

    private void initQR() {
        mQrCodePreView = (QrCodePreView) mLayout.findViewById(R.id.scanview);
        mQRPresenter = new QRCodePresenter(mContext, mQrCodePreView, new QRCodePresenter.QrCodeAnalysisListener() {
            @Override
            public void result(String result) {
                Log.d(TAG, "qrResult: " + result);
                if (mTipDialog != null && mTipDialog.isShowing()) mTipDialog.dismiss();
                if (!TextUtils.isEmpty(result)) {
                    mTipDialog = new AlertDialog.Builder(mContext).
                            setTitle("提示").
                            setMessage(result).
                            setIcon(com.qrcode.R.drawable.ic_launcher).
                            setPositiveButton("确定", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mTipDialog.dismiss();
                                }
                            }).create();
                    mTipDialog.show();
                } else {
                    Toast.makeText(mContext, "结果为空", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //将layout从父组件中移除
        if (mLayout != null) {
            ViewGroup parent = (ViewGroup) mLayout.getParent();
            parent.removeView(mLayout);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_myalbum:
                //从相册选择图片
                if (!NoDoubleClickUtils.isDoubleClick(1500)) {
                    mQRPresenter.choosePicFromFm(this);
                }
                break;
            case R.id.img_light:
                if (CameraManager.get().getFlash()) CameraManager.get().setFlash(false);
                else CameraManager.get().setFlash(true);
                break;


            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == mQRPresenter.CHOOSE_PICTURE && data != null) {
            mQRPresenter.analysisAblum(data.getData());
        }
    }

    /**
     * 获取相机和读取文件权限
     * 获取读取文件权限是因为需要通过图片选择二维码解析，如不需要该功能可去除该权限请求
     */
    public void checkCameraPermission() {
        new RxPermissions(mContext)
                .request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean value) {
                        Log.d(TAG, "call: " + value);
                        if (value) {
                            mQRPresenter.startCamera();
                        }
                    }


                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }

                });

    }
}
