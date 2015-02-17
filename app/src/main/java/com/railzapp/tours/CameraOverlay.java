package com.railzapp.tours;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.location.Location;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

public class CameraOverlay extends View implements SensorEventListener {

    private static final Integer rectSize = 30;
    Paint paint;
    private Location LocationObj;

    public CameraOverlay(Context context) {
        super(context);
        paint = new Paint();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);

        int height = canvas.getHeight();
        int width = canvas.getWidth();

        //canvas.drawLine(width / 2, 0, width / 2, height, paint);
        //canvas.drawLine(0, height / 2, width, height / 2, paint);


        Bitmap arrowBitmap = BitmapFactory.decodeResource(
                getResources(),
                R.drawable.arrow);
        canvas.drawBitmap(arrowBitmap, height / 4, width / 2, null);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // If we don't have a Location, we break out
        if (LocationObj == null) return;

        float azimuth = event.values[0];
        float baseAzimuth = azimuth;

        GeomagneticField geoField = new GeomagneticField(Double
                .valueOf(LocationObj.getLatitude()).floatValue(), Double
                .valueOf(LocationObj.getLongitude()).floatValue(),
                Double.valueOf(LocationObj.getAltitude()).floatValue(),
                System.currentTimeMillis());

        azimuth -= geoField.getDeclination(); // converts magnetic north into true north

        // Store the bearingTo in the bearTo variable
        Location dest = new Location("Destination");
        dest.setLatitude(10);
        dest.setLongitude(20);
        float bearTo = LocationObj.bearingTo(dest); //TODO

        // If the bearTo is smaller than 0, add 360 to get the rotation clockwise.
        if (bearTo < 0) {
            bearTo = bearTo + 360;
        }

        //This is where we choose to point it
        float direction = bearTo - azimuth;

        // If the direction is smaller than 0, add 360 to get the rotation clockwise.
        if (direction < 0) {
            direction = direction + 360;
        }

        rotateImageView(null, R.drawable.arrow, direction);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //TODO
    }

    private void rotateImageView(ImageView imageView, int drawable, float rotate) {

        // Decode the drawable into a bitmap
        Bitmap bitmapOrg = BitmapFactory.decodeResource(getResources(),
                drawable);

        // Get the width/height of the drawable
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        int width = bitmapOrg.getWidth(), height = bitmapOrg.getHeight();

        // Initialize a new Matrix
        Matrix matrix = new Matrix();

        // Decide on how much to rotate
        rotate = rotate % 360;

        // Actually rotate the image
        matrix.postRotate(rotate, width, height);

        // recreate the new Bitmap via a couple conditions
        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0, width, height, matrix, true);
        //BitmapDrawable bmd = new BitmapDrawable( rotatedBitmap );

        //imageView.setImageBitmap( rotatedBitmap );
        imageView.setImageDrawable(new BitmapDrawable(getResources(), rotatedBitmap));
        imageView.setScaleType(ImageView.ScaleType.CENTER);
    }

}
