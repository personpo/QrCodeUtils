package com.zbar.lib;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.util.List;

/**
 *
 * 描述: 相机管理
 */
public final class CameraManager {
	private static CameraManager cameraManager;

	static final int SDK_INT;
	static {
		int sdkInt;
		try {
			sdkInt = android.os.Build.VERSION.SDK_INT;
		} catch (NumberFormatException nfe) {
			sdkInt = 10000;
		}
		SDK_INT = sdkInt;
	}

	private final CameraConfigurationManager configManager;
	private Camera camera;
	private boolean initialized;
	private boolean previewing;
	private final boolean useOneShotPreviewCallback;
	private final PreviewCallback previewCallback;
	private final AutoFocusCallback autoFocusCallback;
	private Parameters parameter;

	public static void init(Context context) {
		if (cameraManager == null) {
			cameraManager = new CameraManager(context);
		}
	}

	public static CameraManager get() {
		return cameraManager;
	}

	public void setFlash(boolean flag) {
		if (camera != null && isFlashSupported(camera)) {
			Parameters parameters = camera.getParameters();
			if (flag) {
				if (parameters.getFlashMode().equals(Parameters.FLASH_MODE_TORCH)) {
					return;
				}
				parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
			} else {
				if (parameters.getFlashMode().equals(Parameters.FLASH_MODE_OFF)) {
					return;
				}
				parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
			}
			camera.setParameters(parameters);
		}
	}


	public boolean getFlash() {
		if (camera != null && isFlashSupported(camera)) {
			Parameters parameters = camera.getParameters();
			if (parameters.getFlashMode().equals(Parameters.FLASH_MODE_TORCH)) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	public static boolean isFlashSupported(Camera camera) {
        /* Credits: Top answer at http://stackoverflow.com/a/19599365/868173 */
		if (camera != null) {
			Parameters parameters = camera.getParameters();

			if (parameters.getFlashMode() == null) {
				return false;
			}

			List<String> supportedFlashModes = parameters.getSupportedFlashModes();
			if (supportedFlashModes == null || supportedFlashModes.isEmpty() || supportedFlashModes.size() == 1 && supportedFlashModes.get(0).equals(Parameters.FLASH_MODE_OFF)) {
				return false;
			}
		} else {
			return false;
		}

		return true;
	}

	private CameraManager(Context context) {
		this.configManager = new CameraConfigurationManager(context);

		useOneShotPreviewCallback = SDK_INT > 3;
		previewCallback = new PreviewCallback(configManager, useOneShotPreviewCallback);
		autoFocusCallback = new AutoFocusCallback();
	}

	public void openDriver(SurfaceHolder holder) throws IOException {
		if (camera == null) {
			camera = Camera.open();
			if (camera == null) {
				throw new IOException();
			}
			camera.setPreviewDisplay(holder);

			if (!initialized) {
				initialized = true;
				configManager.initFromCameraParameters(camera);
			}
			configManager.setDesiredCameraParameters(camera);
			FlashlightManager.enableFlashlight();
		}
	}

	public Point getCameraResolution() {
		return configManager.getCameraResolution();
	}

	public void closeDriver() {
		if (camera != null) {
			FlashlightManager.disableFlashlight();
			camera.release();
			camera = null;
		}
	}

	public void startPreview() {
		if (camera != null && !previewing) {
			camera.startPreview();
			previewing = true;
		}
	}

	public void stopPreview() {
		if (camera != null && previewing) {
			if (!useOneShotPreviewCallback) {
				camera.setPreviewCallback(null);
			}
			camera.stopPreview();
			previewCallback.setHandler(null, 0);
			autoFocusCallback.setHandler(null, 0);
			previewing = false;
		}
	}

	public void requestPreviewFrame(Handler handler, int message) {
		if (camera != null && previewing) {
			previewCallback.setHandler(handler, message);
			if (useOneShotPreviewCallback) {
				camera.setOneShotPreviewCallback(previewCallback);
			} else {
				camera.setPreviewCallback(previewCallback);
			}
		}
	}

	public void requestAutoFocus(Handler handler, int message) {
		if (camera != null && previewing) {
			autoFocusCallback.setHandler(handler, message);
			camera.autoFocus(autoFocusCallback);
		}
	}

	public void openLight() {
		if (camera != null) {
			parameter = camera.getParameters();
			parameter.setFlashMode(Parameters.FLASH_MODE_TORCH);
			camera.setParameters(parameter);
		}
	}

	public void offLight() {
		if (camera != null) {
			parameter = camera.getParameters();
			parameter.setFlashMode(Parameters.FLASH_MODE_OFF);
			camera.setParameters(parameter);
		}
	}

	public void setCameraScale(float scale) {
		if (camera != null) {
			Camera.Parameters parameters = camera.getParameters();
			boolean isSuportZoom = parameters.isZoomSupported();
//        int scale2;//相机zoom不支持float类型，只好根据float倍数四舍五入
			float baseScale = 1;
			if (scale <= 1) {//临时数据，没有时间精雕细琢
				if (parameters.getZoom() <= 2) baseScale = 0;//2倍及以下缩放直接回归0
				else baseScale = scale * 0.85f;
			} else if (scale > 1 && scale <= 1.3) {
				baseScale = 1.45f;
			} else if (scale > 1.3 && scale <= 1.5) {
				baseScale = 1.3f;
			} else if (scale > 1.5 && scale <= 1.8) {
				baseScale = 1.1f;
			} else {
				baseScale = 1f;
			}
			int scale2 = Math.round(parameters.getZoom() == 0 ? baseScale : parameters.getZoom() * scale);
			if (isSuportZoom) {
				int max = parameters.getMaxZoom();
				if (scale2 > max) scale2 = max;
				if (scale2 <= 0) scale2 = 0;
			} else {
				scale2 = 0;
			}
			Log.e("cameraScale", "" + scale + "和scale2:" + scale2);
			parameters.setZoom(scale2);
//        parameters.setPreviewSize(cameraResolution.x*scale2, cameraResolution.y*scale2);
			camera.setParameters(parameters);
		}
	}

	public Camera getCamera(){
		if(camera == null) return null;
		return camera;
	}
}
