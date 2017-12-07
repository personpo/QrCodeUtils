package com.qrcode.view;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.qrcode.R;
import com.qrcode.utils.RxAnimationUtils;
import com.qrcode.utils.ScreenUtils;
import com.zbar.lib.CameraManager;

import java.io.IOException;

import static com.qrcode.R.id.capture_crop_layout;


/**
 * Created by atiao on 2017/7/30.
 */
public class QrCodePreView extends RelativeLayout {

    private View mLayout;
    private int x = 0;
    private int y = 0;
    private int cropWidth = 0;
    private int cropHeight = 0;
    private RelativeLayout mContainer = null;
    private RelativeLayout mCropLayout = null;
    private SurfaceHolder mSurfaceHolder;
    private int animTime = 2000;
    private int[] scanline = {R.drawable.code_xian_ic_type0, R.drawable.code_xian_ic_type1, R.drawable.code_xian_ic_type2,
            R.drawable.code_xian_ic_type3, R.drawable.code_xian_ic_type4, R.drawable.code_xian_ic_type5
            , R.drawable.code_xian_ic_type6, R.drawable.code_xian_ic_type7, R.drawable.code_xian_ic_type8
            , R.drawable.code_xian_ic_type9, R.drawable.code_xian_ic_type10, R.drawable.code_xian_ic_type11
            , R.drawable.code_xian_ic_type12, R.drawable.code_xian_ic_type13, R.drawable.code_xian_ic_type14};
    private int[] scanframe = {R.drawable.code_frame_ic_type0, R.drawable.code_frame_ic_type1
            , R.drawable.code_frame_ic_type2, R.drawable.code_frame_ic_type3, R.drawable.code_frame_ic_type4
            , R.drawable.code_frame_ic_type5, R.drawable.code_frame_ic_type6, R.drawable.code_frame_ic_type7
            , R.drawable.code_frame_ic_type8, R.drawable.code_frame_ic_type9, R.drawable.code_frame_ic_type10
            , R.drawable.code_frame_ic_type11, R.drawable.code_frame_ic_type12, R.drawable.code_frame_ic_type13
            , R.drawable.code_frame_ic_type14};
    private ImageView mQrLineView;

    public QrCodePreView(Context context) {
        super(context);
        init();
    }

    public QrCodePreView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mLayout = View.inflate(getContext(), R.layout.activity_scaner_code, null);
        initScanerAnimation();
        addView(mLayout);
    }

    /**
     * 初始化动画
     */
    private void initScanerAnimation() {
        mQrLineView = (ImageView) mLayout.findViewById(R.id.capture_scan_line);
//        RxAnimationUtils.ScaleUpDowm(mQrLineView);
        View view = mLayout.findViewById(capture_crop_layout);
        int height = ((View) view.getParent()).getTop() + view.getTop() + view.getHeight();
        RxAnimationUtils.animUpAndDown(mQrLineView, ScreenUtils.dip2px(getContext(), 245), animTime);
    }

    public SurfaceHolder getSurfaceHolder() {
        if (mSurfaceHolder == null) {
            SurfaceView surfaceView = (SurfaceView) mLayout.findViewById(R.id.capture_preview);
            mSurfaceHolder = surfaceView.getHolder();
        }
        return mSurfaceHolder;
    }

    /**
     * 动画类型
     *
     * @param type
     */
    public void setScanFrameType(int type) {
        if (type < 0 || type >= scanline.length) type = 0;
        mQrLineView.setImageResource(scanline[type]);
        mCropLayout.setBackgroundResource(scanframe[type]);
    }

    /**
     * @param animTime
     */
    public void setAnimTime(int animTime) {
        this.animTime = animTime;
        if (mQrLineView.getAnimation() != null) mQrLineView.getAnimation().setDuration(animTime);
    }


    public void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
            Point point = CameraManager.get().getCameraResolution();
            int width = point.y;
            int height = point.x;
            if (mContainer == null)
                mContainer = (RelativeLayout) mLayout.findViewById(R.id.capture_containter);
            if (mCropLayout == null)
                mCropLayout = (RelativeLayout) mLayout.findViewById(capture_crop_layout);
            int x = mCropLayout.getLeft() * width / mContainer.getWidth();
            int y = mCropLayout.getTop() * height / mContainer.getHeight();
            int cropWidth = mCropLayout.getWidth() * width
                    / mContainer.getWidth();
            int cropHeight = mCropLayout.getHeight() * height
                    / mContainer.getHeight();
            setX(x);
            setY(y);
            setCropWidth(cropWidth);
            setCropHeight(cropHeight);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }

    }

    public int getScanX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getScanY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getCropWidth() {
        return cropWidth;
    }

    public void setCropWidth(int cropWidth) {
        this.cropWidth = cropWidth;
    }

    public int getCropHeight() {
        return cropHeight;
    }

    public void setCropHeight(int cropHeight) {
        this.cropHeight = cropHeight;
    }


}
