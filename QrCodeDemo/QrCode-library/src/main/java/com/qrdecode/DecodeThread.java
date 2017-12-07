package com.qrdecode;

import android.os.Handler;
import android.os.Looper;

import com.QRCodePresenter;

import java.util.concurrent.CountDownLatch;

/**
 *
 * 描述: 解码线程
 */
public final class DecodeThread extends Thread {

	QRCodePresenter mQrCodePresenter;
	private Handler handler;
	private final CountDownLatch handlerInitLatch;

	DecodeThread(QRCodePresenter qrCodePresenter) {
		this.mQrCodePresenter = qrCodePresenter;
		handlerInitLatch = new CountDownLatch(1);
	}

	Handler getHandler() {
		try {
			handlerInitLatch.await();
		} catch (InterruptedException ie) {
			// continue?
		}
		return handler;
	}

	@Override
	public void run() {
		Looper.prepare();
		handler = new DecodeHandler(mQrCodePresenter);
		handlerInitLatch.countDown();
		Looper.loop();
	}

}
