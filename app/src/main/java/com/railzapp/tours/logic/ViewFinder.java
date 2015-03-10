package com.railzapp.tours.logic;

import android.location.Location;

/**
 * Created by branko on 3/2/15.
 */
public class ViewFinder {
    // TODO Find out, based on screensize and orientation real max angles
    private static final double HORIZONTAL_VIEWFINDER_ANGLE = 30.0;
    private static final double VERTICAL_VIEWFINDER_ANGLE = 30.0;


    public ViewFinder() {}

    public float distanceBetween(Location user, Location poi) {
        return user.distanceTo(poi);
    }

    public float bearingTo(Location user, Location poi) {
        return user.bearingTo(poi);
    }

}
