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
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener {

    // define the display assembly compass picture
    private ImageView mImageView;

    // GPS class is helper for all Location based stuff
    private GPS gps;

    // record the compass picture angle turned
    private float mCurrentDegree = 0f;

    // Destination LatLng
    private Location mDestination;

    // device sensor manager
    private SensorManager mSensorManager;


    private Camera mCamera;
    private CameraPreview mPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = (ImageView) findViewById(R.id.imageViewCompass);

        // initialize your android device sensor capabilities
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

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
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_UI);

        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        double shared_lat = Double.valueOf(sharedPref.getString(getString(R.string.pref_lat), "0.00"));
        double shared_lng = Double.valueOf(sharedPref.getString(getString(R.string.pref_lng), "0.00"));
        this.mDestination = new Location("mDestination");
        mDestination.setLatitude(shared_lat);
        mDestination.setLongitude(shared_lng);
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

    @Override
    public void onSensorChanged(SensorEvent event) {

        gps.updateGPSCoordinates();
        Location currentLoc = gps.getLocation();
//        Location mDestination = new Location("");
//        this.mDestination.setLatitude(50.827955);
//        this.mDestination.setLongitude(4.377515);
        float degree = currentLoc.bearingTo(mDestination);

        float azimuth = event.values[0];
        GeomagneticField geoField = new GeomagneticField(
                Double.valueOf(currentLoc.getLatitude()).floatValue(),
                Double.valueOf(currentLoc.getLongitude()).floatValue(),
                Double.valueOf( currentLoc.getAltitude() ).floatValue(),
                System.currentTimeMillis() );

        azimuth -= geoField.getDeclination(); // converts magnetic north into true north
        // Store the bearingTo in the bearTo variable
        float bearTo = currentLoc.bearingTo(this.mDestination);

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
        // TODO Find out why we have to add 45 to direction to fix it, and is it going to work
        // in all situations
        degree = direction;

        // create a rotation animation (reverse turn degree degrees)
        RotateAnimation ra = new RotateAnimation(
                mCurrentDegree,
                degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);

        // how long the animation will take place
        ra.setDuration(210);

        // set the animation after the end of the reservation status
        ra.setFillAfter(true);

        // Start the animation
        mImageView.startAnimation(ra);
        mCurrentDegree = degree;
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