package com.railzapp.tours;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class MainActivity extends Activity implements SensorEventListener {

    private static final String TAG = "MainActivity";

    // define the display assembly resources
    private ImageView mImageViewArrow;
    private ImageView mImageViewRedCircle;

    // GPS class is helper for all Location based stuff
    private GPS gps;

    // record the compass picture angle turned
    private float mCurrentDegree = 0f;

    // Current Location
    private Location mCurrentLocation;

    // Destination Location
    private Location mDestination;

    // device sensor manager
    private SensorManager mSensorManager;

    private Camera mCamera;
    private CameraPreview mPreview;

    // Max offsets for mImageViewRedCircle, thus for viewPoint in general
    // TODO This must be calculated based on screen resolution, size and dpi
    private static final int VIEWPOINT_MIN_Y = 100;
    private static final int VIEWPOINT_MAX_Y = 800;

    Sensor accelerometer;
    Sensor magnetometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageViewArrow = (ImageView) findViewById(R.id.imageViewArrow);
        mImageViewRedCircle = (ImageView) findViewById(R.id.imageViewRedCircle);

        // initialize your android device sensor capabilities
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        //DB PLAYGROUND
//        Intent intent_test_db = new Intent(this, TestDatabaseActivity.class);
//        startActivity(intent_test_db);
    }

    @Override
    protected void onResume() {
        super.onResume();

        gps = new GPS(this);

        if (mCamera == null) {
            mCamera = getCameraInstance();
            mCamera.setDisplayOrientation(90);

           // TODO For orientation, check http://stackoverflow.com/questions/20800630/java-lang-runtimeexception-method-called-after-release

            // Create our Preview view and set it as the content of our activity.
            mPreview = new CameraPreview(this, mCamera);
            FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
            preview.addView(mPreview);
        }

        // for the system's orientation sensor registered listeners
//        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_UI);
        //mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), 5000000); // Value is in microseconds
        // TODO Figure out why it doesn't work with accelerometer/magnetometer
        // Need to replace this TYPE_ORIENTATION
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);


        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        double shared_lat = Double.valueOf(sharedPref.getString(getString(R.string.pref_lat), "0.00"));
        double shared_lng = Double.valueOf(sharedPref.getString(getString(R.string.pref_lng), "0.00"));
        this.mDestination = new Location("mDestination");
        mDestination.setLatitude(shared_lat);
        mDestination.setLongitude(shared_lng);


        // TEST
        TranslateAnimation ta;
        int horizontalShift = 0;
        int[] mImageViewRedCircleXY = new int[2];
        mImageViewRedCircle.getLocationOnScreen(mImageViewRedCircleXY);
        Log.e(TAG, "X: " + mImageViewRedCircleXY[0] + ", Y: " + mImageViewRedCircleXY[1] );
        Log.e(TAG, "Top: " + mImageViewRedCircle.getTop() + ", Left: " + mImageViewRedCircle.getLeft() );

    }

    @Override
    protected void onPause() {
        // to stop the listener and save battery
        mSensorManager.unregisterListener(this);

        if (mCamera != null) {
            mCamera = null;
        }

        gps.stopUsingGPS();

        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.map:
                Intent intent_maps = new Intent(this, MapsActivity.class);
                startActivity(intent_maps);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    float[] mGravity;
    float[] mGeomagnetic;
    long lastUpdate = 0l;
    long updateInterval = 500000000l; // Nanoseconds
    @Override
    public void onSensorChanged(SensorEvent event) {
        // There needs to be some way of managing how frequent this method is going to be called
        // Sensor Frequency is not usable for that, there needs to be external controller
        if (lastUpdate + updateInterval < System.nanoTime()) {
            lastUpdate = System.nanoTime();

            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) mGravity = event.values;
            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) mGeomagnetic = event.values;
            if (mGravity != null && mGeomagnetic != null
                    && (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER || event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)) {

                // Get current location
                gps.updateGPSCoordinates();
                Location currentLoc = gps.getLocation();

                // Get azimuth
                float azimuth = getAzimuth(currentLoc);
                // Store the bearingTo in the bearTo variable
                float bearTo = currentLoc.bearingTo(this.mDestination);

                // If the bearTo is smaller than 0, add 360 to get the rotation clockwise.
                float adjustedBearTo = bearTo;
                // TODO Test if it works better with this or without
//                if (bearTo < 0) {
//                    adjustedBearTo = bearTo + 360;
//                }

                //This is where we choose to point it
                float degree = adjustedBearTo - azimuth;

                // TODO Test if it works better with this or without
                // If the direction is smaller than 0, add 360 to get the rotation clockwise.
                //if (degree < 0) {
                //    degree = degree + 360;
                //}

                moveRedCircle(bearTo, degree);

                rotateArrow(degree);
            }
        }
    }

    private void moveRedCircle(float bearTo, float degree) {
        // Move red circle
        int horizontalShift = Math.round(bearTo);

Log.e(TAG, "Degree: " + degree);
Log.e(TAG, "bear to: " + bearTo);
Log.e(TAG, "mCurrentDegree: " + mCurrentDegree);
Log.e(TAG, "horizontalShift: " + horizontalShift);

        TranslateAnimation ta;
        int[] mImageViewRedCircleXY = new int[2];
        mImageViewRedCircle.getLocationOnScreen(mImageViewRedCircleXY);

        Log.e(TAG, "X: " + mImageViewRedCircleXY[0] + ", Y: " + mImageViewRedCircleXY[1]);

        if (mCurrentDegree < degree) { // go right
            horizontalShift = Math.round(mImageViewRedCircleXY[0] + horizontalShift);
            horizontalShift = horizontalShift > VIEWPOINT_MAX_Y ? VIEWPOINT_MAX_Y : horizontalShift;
        } else { //go left
            horizontalShift = Math.round(mImageViewRedCircleXY[0] - horizontalShift);
            horizontalShift = horizontalShift < VIEWPOINT_MIN_Y ? VIEWPOINT_MIN_Y : horizontalShift;
        }
        ta = new TranslateAnimation(
                Animation.ABSOLUTE, mImageViewRedCircleXY[0],
                Animation.ABSOLUTE, horizontalShift,
                Animation.ABSOLUTE, 0.0f,
                Animation.ABSOLUTE, 0.0f

        );
        ta.setDuration(200);
        ta.setFillAfter(true);
        mImageViewRedCircle.startAnimation(ta);
        //mImageViewRedCircle.setLeft(horizontalShift);
    }

    private void rotateArrow(float degree) {
        // create a rotation animation for arrow (reverse turn degree degrees)
        RotateAnimation ra = new RotateAnimation(
                mCurrentDegree,
                degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        // how long the animation will take place
        ra.setDuration(210);
        // set the animation after the end of the reservation status
        ra.setFillAfter(true);
        // Start the animation
        mImageViewArrow.startAnimation(ra);
        mCurrentDegree = degree;
    }

    private float getAzimuth(Location currentLoc) {
        float azimuth = 0.0f;
        float R[] = new float[9];
        float I[] = new float[9];
        boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
        if (success) {
            float orientation[] = new float[3];
            SensorManager.getOrientation(R, orientation);
            azimuth = (float)Math.toDegrees(orientation[0]); // orientation contains: azimuth, pitch and roll, in RADIANS
        }


        GeomagneticField geoField = new GeomagneticField(
                Double.valueOf(currentLoc.getLatitude()).floatValue(),
                Double.valueOf(currentLoc.getLongitude()).floatValue(),
                Double.valueOf(currentLoc.getAltitude()).floatValue(),
                System.currentTimeMillis());

        azimuth -= geoField.getDeclination(); // converts magnetic north into true north
        return azimuth;
    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not in use
    }
}