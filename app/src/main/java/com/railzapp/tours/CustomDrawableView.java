package com.railzapp.tours;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import java.io.IOException;

public class CustomDrawableView extends ImageView implements SurfaceHolder.Callback {

    private long updateInterval = 3000000000L; //1000000000 nanoseconds = 1sec
    private long lastUpdateNanosecods;
    private CameraActivity cameraActivity;
    SurfaceHolder mHolder;
    Paint paint = new Paint();
    LinearInterpolator linear_interpolator = new LinearInterpolator();
    RotateAnimation rotate = null;
    Float azimuth;
    Float new_azimuth;

    public CustomDrawableView(CameraActivity cameraActivity, Context context, Camera mCamera) {
        super(context);
        this.cameraActivity = cameraActivity;
        this.azimuth = 0.0f;
        this.new_azimuth = 0.0f;
        this.lastUpdateNanosecods = Long.valueOf("0");
        paint.setColor(0xff00ff00);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        paint.setAntiAlias(true);
    }

    public void reDraw() {
Log.e("Debug", "called reDraw method");
        long difference = System.nanoTime() - this.lastUpdateNanosecods;

Log.e("SystemNanotime", String.valueOf(System.nanoTime()));
Log.e("lastUpdateNanosecods", String.valueOf(this.lastUpdateNanosecods));
Log.e("UpdateInterval", String.valueOf(updateInterval));
Log.e("Difference", String.valueOf(difference));

        if (difference > updateInterval
//                && this.new_azimuth != null
//                && this.azimuth != null
//                && this.azimuth != this.new_azimuth
//                && Math.abs(this.azimuth - this.new_azimuth) > 3
                ) {
            this.lastUpdateNanosecods = System.nanoTime();
            this.invalidate();
        }
    }

    protected void onDraw(Canvas canvas) {
Log.e("onDraw1", "onDraw started");
Log.e("OldAzimuth", String.valueOf(this.azimuth));
Log.e("NewAzimuth", String.valueOf(this.new_azimuth));

        int width = getWidth();
        int height = getHeight();
        int centerx = width / 2;
        int centery = height / 2;

        paint.setColor(0xff0000ff);
        Bitmap arrowBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.arrow);
        canvas.drawBitmap(arrowBitmap, height / 4, width / 2, null);

        // Rotate the canvas with the azimuth
        if (this.new_azimuth != null
                && this.azimuth != null
                && this.azimuth != this.new_azimuth
                && Math.abs(this.azimuth - this.new_azimuth) > 1
            ) {
            rotate = new RotateAnimation(this.azimuth, this.new_azimuth, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotate.setInterpolator(linear_interpolator);
            rotate.setDuration(500);
            //rotate.setFillAfter(true);
            this.startAnimation(rotate);
            this.azimuth = this.new_azimuth;

        }
//      else if (
//                this.new_azimuth != null
//                        && this.azimuth != null
//                        && Math.abs(this.azimuth - this.new_azimuth) < 2
//                ) {
//            // Wait for more change
//            //TODO
//        }
//        else {
//            this.azimuth = this.new_azimuth;
//        }


        canvas.drawLine(centerx, 0, centerx, height, paint);
        canvas.drawLine(0, centery, width, centery, paint);
        // Add bitmap


//        canvas.drawLine(centerx, -1000, centerx, +1000, paint);
//        canvas.drawLine(-1000, centery, 1000, centery, paint);
//        canvas.drawText("N", centerx + 5, centery - 10, paint);
//        canvas.drawText("S", centerx - 10, centery + 15, paint);
Log.e("onDraw2", "onDraw completed");
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
//        try {
//            cameraActivity.mCamera.setPreviewDisplay(holder);
//            cameraActivity.mCamera.setDisplayOrientation(90);
//            cameraActivity.mCamera.startPreview();
//        } catch (IOException e) {
//            Log.d("ERROR in CameraPreview", "Error setting camera preview: " + e.getMessage());
//        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (this.mHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
//            cameraActivity.mCamera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
//            cameraActivity.mCamera.setPreviewDisplay(this.mHolder);
//            cameraActivity.mCamera.startPreview();

        } catch (Exception e) {
            Log.d("ERROR in CameraPreview", "Error starting camera preview: " + e.getMessage());
        }
    }
}
