package com.connectapp.user.location;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;

public class FetchCordinates extends AsyncTask<String, Integer, String> {

    //private ProgressDialog progDailog = null;

    public double lati = 0.0;
    public double longi = 0.0;
    public Location mLocation = new Location("0");

    public LocationManager mLocationManager;

    public GPSLocationListener gpsLocationListener;

    private Context mContext;
    public LocationCallback mListener;

    public FetchCordinates asyncObject; // as CountDownTimer has similar method -> to prevent shadowing
    public CountDownTimer countDownTimer;
    public static boolean isFetchCancelled= false;

    //public Dialog dialog;

    public FetchCordinates(Context mContext) {

        this.mContext = mContext;
    }

    @Override
    protected void onPreExecute() {
        gpsLocationListener = new GPSLocationListener();
        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, gpsLocationListener);

        /*progDailog = new ProgressDialog(mContext);

        progDailog.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                FetchCordinates.this.cancel(true);
            }
        });

        progDailog.setMessage("Fetching your Location! Please wait...");
        progDailog.setIndeterminate(true);
        progDailog.setCancelable(false);*/


        /*progDailog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
			public void onClick(DialogInterface dialog, int which) {
				onCancelled();
			}
		});*/
        //progDailog.show();
        //showDialog();
        asyncObject = this;
        countDownTimer = new CountDownTimer(102000, 1000) {
            public void onTick(long millisUntilFinished) {
                // You can monitor the progress here as well by changing the onTick() time
                Log.d("TICK", "------->>> tick  <<-----------");
                Log.d("LOCATION", "Latitude: " + mLocation.getLatitude());
                 Log.d("AS", "Is cancelled: in tick " + isCancelled());
                Log.d("AS", "Is fetch cancelled: in tick " + isFetchCancelled);

            }

            public void onFinish() {
                Log.d("FINISH", "------->>> finish  <<-----------");
                // stop async task if not in progress
                if (asyncObject.getStatus() == AsyncTask.Status.RUNNING) {
                    System.out.println("Async task still running.");
                    /*asyncObject.cancel(true);
                    // Add any specific task you wish to do as your extended class variable works here as well.
					System.out.println("Cancelled by user!");*/
                    onPostExecute(null);

                }
            }
        }.start();
    }

    @Override
    protected void onCancelled() {
        countDownTimer.cancel();
        System.out.println("Cancelled by user!");
        Log.e("onCancelled", "Cancelled by user.");
        // progDailog.dismiss();
        mLocationManager.removeUpdates(gpsLocationListener);
        isFetchCancelled= isCancelled();
         Log.d("AS", "Is cancelled: " + isCancelled());
    }

    @Override
    protected void onPostExecute(String result) {
        countDownTimer.cancel();
        Log.e("onPostExecute", "onPostExecute");
        //progDailog.dismiss();
        if (this.mLocation.getLatitude() == 0.0) {
            mListener.getLocation(this.mLocation, false);
        } else {
            mListener.getLocation(this.mLocation, true);
        }
        //Toast.makeText(mContext, "Latitude :" + lati + "\nLongitude :" + longi, Toast.LENGTH_LONG).show();
        mLocationManager.removeUpdates(gpsLocationListener);

    }

    @Override
    protected String doInBackground(String... params) {

        do {
          //  Log.d("AS", "Is cancelled: " + isCancelled());
            if (isFetchCancelled) {
                Log.e("WhileLoop", "Is cancelled exit while loop.");
                break;
            }
        } while (mLocation.getLatitude() == 0.0);

        return null;
    }


    public class GPSLocationListener implements LocationListener {

        @SuppressWarnings("unused")
        @Override
        public void onLocationChanged(Location location) {

            int lat = (int) location.getLatitude(); // * 1E6);
            int log = (int) location.getLongitude(); // * 1E6);
            int acc = (int) (location.getAccuracy());

            String info = location.getProvider();
            try {

                lati = location.getLatitude();
                longi = location.getLongitude();
                Log.d("TAG", "Latitude: " + lati);
                mLocation = location;

            } catch (Exception e) {
                // progDailog.dismiss();
                // Toast.makeText(getApplicationContext(),"Unable to get Location", Toast.LENGTH_LONG).show();
            }

        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.i("OnProviderDisabled", "OnProviderDisabled");
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.i("onProviderEnabled", "onProviderEnabled");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.i("onStatusChanged", "onStatusChanged");

        }

    }


}
