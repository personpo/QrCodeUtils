package com.qrdecode;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.QRCodePresenter;
import com.dtr.zbar.build.ZBarDecoder;
import com.qrcode.R;
import com.zbar.lib.ZbarManager;


/**
 * 描述: 接受消息后解码
 */
public final class DecodeHandler extends Handler {

	QRCodePresenter mQrCodePresenter = null;

	DecodeHandler(QRCodePresenter qrCodePresenter) {
		this.mQrCodePresenter = qrCodePresenter;
	}

	@Override
	public void handleMessage(Message message) {
		if (message.what == R.id.decode) {
			decode((byte[]) message.obj, message.arg1, message.arg2);

		} else if (message.what == R.id.quit) {
			Looper.myLooper().quit();

		}
	}

	private void decode(byte[] data, int width, int height) {
		byte[] rotatedData = new byte[data.length];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++)
				rotatedData[x * height + height - y - 1] = data[x + y * width];
		}
		int tmp = width;// Here we are swapping, that's the difference to #11
		width = height;
		height = tmp;

		ZbarManager manager = new ZbarManager();
		String result = manager.decode(rotatedData, width, height, true,
				mQrCodePresenter.getQrCodePreView().getScanX(), mQrCodePresenter.getQrCodePreView().getScanY()
				, mQrCodePresenter.getQrCodePreView().getCropWidth(),
				mQrCodePresenter.getQrCodePreView().getCropHeight());
		ZBarDecoder zBarDecoder = new ZBarDecoder();
		String result_line = zBarDecoder.decodeRaw(rotatedData,width,height);
		if (result != null) {
			if(null != mQrCodePresenter.getHandler()){
				Message msg = new Message();
				msg.obj = ""+result;
				msg.what = R.id.decode_succeeded;
				mQrCodePresenter.getHandler().sendMessage(msg);
			}
		} else {
			if(result_line != null){
				if(null != mQrCodePresenter.getHandler()){
					Message msg = new Message();
					msg.obj = ""+result_line;
					msg.what = R.id.decode_succeeded;
					mQrCodePresenter.getHandler().sendMessage(msg);
				}
			}else{
				if (null != mQrCodePresenter.getHandler()) {
					mQrCodePresenter.getHandler().sendEmptyMessage(R.id.decode_failed);
				}
			}
			//Toast.makeText(activity, result_line, 0).show();
		}
	}

}
