package com.qrdecode;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.QRCodePresenter;
import com.qrcode.R;
import com.zbar.lib.CameraManager;



public final class CaptureHandler extends Handler {
    private static final String TAG = "CaptureHandler";
    DecodeThread decodeThread = null;
    QRCodePresenter mQrCodePresenter = null;
    private State state;

    private enum State {
        PREVIEW, SUCCESS, DONE
    }

    public CaptureHandler(QRCodePresenter qrCodePresenter) {
        this.mQrCodePresenter = qrCodePresenter;
        decodeThread = new DecodeThread(mQrCodePresenter);
        decodeThread.start();
        state = State.SUCCESS;
        CameraManager.get().startPreview();
        restartPreviewAndDecode();
    }

    @Override
    public void handleMessage(Message message) {

        if (message.what == R.id.auto_focus) {
            if (state == State.PREVIEW) {
                try {
                    CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
                } catch (Exception e) {
                    Log.d("handleMessage error", "" + e.getMessage());
                }
            }

        } else if (message.what == R.id.restart_preview) {
            restartPreviewAndDecode();

        } else if (message.what == R.id.decode_succeeded) {
            state = State.SUCCESS;
            mQrCodePresenter.handleDecode((String) message.obj);// 解析成功，回调

        } else if (message.what == R.id.decode_failed) {
            state = State.PREVIEW;
            CameraManager.get().requestPreviewFrame(decodeThread.getHandler(),
                    R.id.decode);

        }

    }

    public void quitSynchronously() {
        state = State.DONE;
        CameraManager.get().stopPreview();
        removeMessages(R.id.decode_succeeded);
        removeMessages(R.id.decode_failed);
        removeMessages(R.id.decode);
        removeMessages(R.id.auto_focus);
    }

    private void restartPreviewAndDecode() {
        if (state == State.SUCCESS) {
            state = State.PREVIEW;
            CameraManager.get().requestPreviewFrame(decodeThread.getHandler(),
                    R.id.decode);
            try {
                CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
            } catch (Exception e) {
                Log.d(TAG, "restartPreviewAndDecode: " + e.toString());
            }
        }
    }

}
