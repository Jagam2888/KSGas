package ks.com.ksgas.common;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GPSTracker extends Service implements LocationListener {

	private final Activity mContext;

	// flag for GPS status
	boolean isGPSEnabled = false;

	// flag for network status
	boolean isNetworkEnabled = false;

	// flag for GPS status
	boolean canGetLocation = false;

	Location location; // location
	double latitude; // latitude
	double longitude; // longitude

	// The minimum distance to change Updates in meters
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

	// The minimum time between updates in milliseconds
	private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

	// Declaring a Location Manager
	protected LocationManager locationManager;
    Geocoder geocoder;
    List<Address> localAddress;
    private final int REQUEST_PERMISSION_LOCATION_STATE=1;

	public GPSTracker(Activity context) {
		this.mContext = context;
        getLocation();
	}

	public Location getLocation() {
		try {

			locationManager = (LocationManager) mContext
					.getSystemService(LOCATION_SERVICE);

			// getting GPS status
			isGPSEnabled = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);

			// getting network status
			isNetworkEnabled = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (!isGPSEnabled && !isNetworkEnabled) {
				// no network provider is enabled
			} else {
				this.canGetLocation = true;
				if (isNetworkEnabled) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (mContext.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(mContext,"Allow to track Location",Toast.LENGTH_LONG).show();
                        }
                        }
                            locationManager.requestLocationUpdates(
                                    LocationManager.NETWORK_PROVIDER,
                                    MIN_TIME_BW_UPDATES,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);


					Log.d("Network", "Network");
					if (locationManager != null) {
						location = locationManager
								.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						if (location != null) {
							latitude = location.getLatitude();
							longitude = location.getLongitude();
						}
					}
				}
				// if GPS Enabled get lat/long using GPS Services
				if (isGPSEnabled) {
					if (location == null) {

                                locationManager.requestLocationUpdates(
                                        LocationManager.GPS_PROVIDER,
                                        MIN_TIME_BW_UPDATES,
                                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
						Log.d("GPS Enabled", "GPS Enabled");
						if (locationManager != null) {
							location = locationManager
									.getLastKnownLocation(LocationManager.GPS_PROVIDER);
							if (location != null) {
								latitude = location.getLatitude();
								longitude = location.getLongitude();
							}
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return location;
	}


	
	/**
	 * Stop using GPS listener
	 * Calling this function will stop using GPS in your app
	 * */
	public void stopUsingGPS(){
		if(locationManager != null){
			if(Build.VERSION.SDK_INT >= 23) {
				if (mContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
						|| mContext.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
					locationManager.removeUpdates(GPSTracker.this);
				}
			}else {
				locationManager.removeUpdates(GPSTracker.this);
			}

		}		
	}
	
	/**
	 * Function to get latitude
	 * */
	public double getLatitude(){
		if(location != null){
			latitude = location.getLatitude();
		}
		
		// return latitude
		return latitude;
	}
	
	/**
	 * Function to get longitude
	 * */
	public double getLongitude(){
		if(location != null){
			longitude = location.getLongitude();
		}
		
		// return longitude
		return longitude;
	}
	
	/**
	 * Function to check GPS/wifi enabled
	 * @return boolean
	 * */
	public boolean canGetLocation() {
		return this.canGetLocation;
	}
	
	/**
	 * Function to show settings alert dialog
	 * On pressing Settings button will lauch Settings Options
	 * */
	public void showSettingsAlert(){
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
   	 
        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");
 
        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
 
        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            	mContext.startActivity(intent);
            }
        });
 
        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
            }
        });
 
        // Showing Alert Message
        alertDialog.show();
	}

    public void getAddress(EditText address, EditText city,EditText state) {
        geocoder = new Geocoder(mContext, Locale.getDefault());
        try {
            localAddress = geocoder.getFromLocation(getLatitude(), getLongitude(), 1);
            if (localAddress.size() > 0)
            {
                String yourAddress = localAddress.get(0).getAddressLine(0);
                String yourAddress1 = localAddress.get(0).getAddressLine(1);
                String yourCity = localAddress.get(0).getLocality();
                String yourState = localAddress.get(0).getAdminArea();
                city.setText(yourCity);
                state.setText(yourState);
				if(yourAddress1 == null) {
					yourAddress1 = "";
				}
                address.setText(yourAddress+", "+yourAddress1);
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }


	@Override
	public void onLocationChanged(Location location) {
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}
