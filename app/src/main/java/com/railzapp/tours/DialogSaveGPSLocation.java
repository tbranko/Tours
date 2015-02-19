package com.railzapp.tours;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

@SuppressLint("ValidFragment")
public class DialogSaveGPSLocation extends DialogFragment {
	private LatLng latLng;
	private String home_text = "N/A";
	private boolean addressFound;

	public DialogSaveGPSLocation(LatLng point) {
		this.latLng = point;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		try {
			Geocoder geo = new Geocoder(this.getActivity().getApplicationContext());
			List<Address> addresses = geo.getFromLocation(this.latLng.latitude, this.latLng.longitude, 1);
			if (addresses.isEmpty()) {
				home_text = "Address not available";
				this.addressFound = false;
			}
			else {
				if (addresses.size() > 0) {
					this.addressFound = true;
					home_text = addresses.get(0).getAddressLine(0) + ", " + addresses.get(0).getLocality() + ", " + addresses.get(0).getCountryName();
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace(); // getFromLocation() may sometimes fail
			home_text = "ERROR: " + e.getMessage() + " " +e.toString();
		}
		
		builder
			.setMessage(home_text)
			.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					// User cancelled the dialog
				}
			});

		if (this.addressFound) {
			builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					// Save LatLng and Address text to the shared prefs
					SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = sharedPref.edit();
					editor.putString(getString(R.string.address_text), home_text);
					editor.putString(getString(R.string.pref_lat), String.valueOf(latLng.latitude));
					editor.putString(getString(R.string.pref_lng), String.valueOf(latLng.longitude));
					editor.commit();
					Log.e("t10", sharedPref.getString(getString(R.string.address_text), "NO_TXT"));
				}
			});
		}
			
		// Create the AlertDialog object and return it
		return builder.create();
	}


}
