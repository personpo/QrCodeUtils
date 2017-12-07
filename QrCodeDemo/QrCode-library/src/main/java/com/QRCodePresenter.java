package com;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;

import com.qrcode.R;
import com.qrcode.view.QrCodePreView;
import com.qrdecode.CaptureHandler;
import com.qrdecode.QRCodeDecoder;
import com.zbar.lib.CameraManager;

import static android.content.Context.VIBRATOR_SERVICE;

/**
 * atiaotiao
 */
public class QRCodePresenter implements SurfaceHolder.Callback {
    private static final String TAG = "QRCodePresenter";
    public final int CHOOSE_PICTURE = 800;
    private Activity mContext;
    private QrCodePreView mQrCodePreView;
    private CaptureHandler mCaptureHandler;
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            // 连续扫描，不发送此消息扫描一次结束后就不能再次扫描
            mCaptureHandler.sendEmptyMessage(R.id.restart_preview);
        }
    };
    private boolean hasSurface;
    private QrCodeAnalysisListener listener;

    public QRCodePresenter(Activity context, QrCodePreView qrCodePreView, QrCodeAnalysisListener l) {
        CameraManager.init(context);//初始化 CameraManager
        this.mContext = context;
        this.mQrCodePreView = qrCodePreView;
        this.listener = l;
    }


    public void startCamera() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initStartScan();
            }
        }, 200);
    }

    private void initStartScan() {
        if (hasSurface) {
            mQrCodePreView.initCamera(mQrCodePreView.getSurfaceHolder());//Camera初始化
            if (mCaptureHandler == null)
                mCaptureHandler = new CaptureHandler(QRCodePresenter.this);
        } else {
            mQrCodePreView.getSurfaceHolder().addCallback(QRCodePresenter.this);
            mQrCodePreView.getSurfaceHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            if (!hasSurface) {
                hasSurface = true;
                mQrCodePreView.initCamera(mQrCodePreView.getSurfaceHolder());
                if (mCaptureHandler == null) {
                    mCaptureHandler = new CaptureHandler(QRCodePresenter.this);
                }
            }
        }
    }

    public void stopCamera() {
        if (mCaptureHandler != null) {
            mCaptureHandler.removeCallbacks(mRunnable);
            if (mCaptureHandler != null) {
                mCaptureHandler.quitSynchronously();
                mCaptureHandler = null;
            }
        }
        CameraManager.get().closeDriver();
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    public QrCodePreView getQrCodePreView() {
        return mQrCodePreView;
    }

    public Handler getHandler() {
        return mCaptureHandler;
    }

    public void analysisAblum(final Uri uri) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                return QRCodeDecoder.syncDecodeQRCode(Utils.getRealFilePath(mContext, uri));
            }

            @Override
            protected void onPostExecute(String result) {
                if (!TextUtils.isEmpty(result) && listener != null) listener.result(result);
                else {
//                    if (ContextCompat.checkSelfPermission(mContext
//                            , Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    listener.result(null);
//                    } else
//                        Toast.makeText(mContext, "非正常二维码或缺少访问文件权限", Toast.LENGTH_SHORT).show();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    public void handleDecode(String result) {
        Vibrator vibrator = (Vibrator) mContext.getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200L);
        Log.v("二维码/条形码 扫描结果", result);
        if (!TextUtils.isEmpty(result) && listener != null) listener.result(result);
        mCaptureHandler.postDelayed(mRunnable, 1000);
    }

    public void setScanFrameType(int type) {
        getQrCodePreView().setScanFrameType(type);
    }

    public void setAnimTime(int time) {
        getQrCodePreView().setAnimTime(time);
    }

    /**
     * activity调用
     */
    public void choosePicFromAc() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
        } else {
            intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        mContext.startActivityForResult(intent, CHOOSE_PICTURE);
    }


    /**
     * fragment调用
     */
    public void choosePicFromFm(Fragment fm) {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
        } else {
            intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        fm.startActivityForResult(intent, CHOOSE_PICTURE);
    }

    /**
     * 解析结果回调接口
     * 注意，结果有可能为null，如图片解析为非二维码即返回null
     */
    public interface QrCodeAnalysisListener {
        void result(String result);
    }


}
