package com.romerock.modules.android.currencylocation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class SplashScreen extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
	public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
	public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
	protected final static String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
	protected final static String LOCATION_KEY = "location-key";
	protected final static String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";
	protected int _splashTime = 5000;
	protected GoogleApiClient mGoogleApiClient;
	protected LocationRequest mLocationRequest;
	protected Location mCurrentLocation;
	protected Boolean mRequestingLocationUpdates;
	protected String mLastUpdateTime;
	private Thread splashTread;
	private boolean isAllShoppingsIntetn = false;
	private String action = null;
	private Currency currency;
	private String countryCodeLocale = null;
	private Bundle savedInstanceState;
	private SharedPreferences sharedPrefs;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.splash);
		//************************************************

		sharedPrefs = getSharedPreferences(getString(R.string.preferences_name), MODE_PRIVATE);
		if (!sharedPrefs.contains(getString(R.string.preferences_currency_symbol))) {
			updateValuesFromBundle();
			buildGoogleApiClient();
		} else {
			splashProcess();
		}
	}

	protected void splashProcess() {
		// ************* verify preferences *************
		//*************************** Detect Location **********
		// Detect language
		SharedPreferences sharedPrefs = getSharedPreferences(getString(R.string.preferences_name), MODE_PRIVATE);
		SharedPreferences.Editor ed = sharedPrefs.edit();

		//*************************** Detect Location **********
		Locale locale = null;
		if (!sharedPrefs.contains(getString(R.string.preferences_currency_symbol))) {
			ed.putBoolean("firstTimeOpen",true);
			if (countryCodeLocale == null) {
				if (!Locale.getDefault().getCountry().toString().equals(""))
					currency = Currency.getInstance(Locale.getDefault());
				else {
					locale = new Locale("en", "US");
					currency = Currency.getInstance(locale);
				}
			} else {
				locale = new Locale("", countryCodeLocale);
				currency = Currency.getInstance(locale);
			}
			ed.putString(getString(R.string.preferences_currency_symbol), currency.getSymbol());
		if (!sharedPrefs.contains(getString(R.string.preferences_currency_country_money_symbol))) {
			ed.putString(getString(R.string.preferences_currency_country_money_symbol), "USD");
		}

		String country = "";
		Locale current = getResources().getConfiguration().locale;
		if (locale == null) {
			country = current.getCountry();
		} else {
			country = locale.getCountry();
		}
		ed.putString(getString(R.string.preferences_currency_country_code), country);
		String name = "";
		if (android.os.Build.VERSION.SDK_INT >= 19) {
			name = currency.getDisplayName() + " ";
		}
		name += currency.getSymbol();
		ed.putString(getString(R.string.preferences_currency_name_money), name);
		ed.putString(getString(R.string.preferences_currency_country_money_symbol), currency.getCurrencyCode());
	}else{
			ed.putBoolean("firstTimeOpen",false);
		}
		ed.commit();
		//****************************************
		final SplashScreen sPlashScreen = this;
		splashTread = new Thread() {
			@Override
			public void run() {
				try {
					synchronized (this) {
						wait(_splashTime);
					}

				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					Intent intent = new Intent();
					intent.setClass(sPlashScreen, MainActivity.class);
					startActivity(intent);
					finish();
				}
			}
		};
		splashTread.start();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
		}
		splashProcess();
		return true;
	}

	private void updateValuesFromBundle() {
		if (savedInstanceState != null) {
			if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
				mRequestingLocationUpdates = savedInstanceState.getBoolean(
						REQUESTING_LOCATION_UPDATES_KEY);
			}

			if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
				mCurrentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
			}

			if (savedInstanceState.keySet().contains(LAST_UPDATED_TIME_STRING_KEY)) {
				mLastUpdateTime = savedInstanceState.getString(LAST_UPDATED_TIME_STRING_KEY);
			}
			updateUI();
		}
	}

	protected synchronized void buildGoogleApiClient() {
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API)
				.build();
		createLocationRequest();
	}

	protected void createLocationRequest() {
		mLocationRequest = new LocationRequest();
		mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
		mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	}

	protected void startLocationUpdates() {
		if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			if (ActivityCompat.shouldShowRequestPermissionRationale(this,
					android.Manifest.permission.ACCESS_FINE_LOCATION)) {

			} else {
				ActivityCompat.requestPermissions(this,
						new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
						100);
			}
		}else
			LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
	}

	private void updateUI() {
		if (mCurrentLocation != null) {
			countryCodeLocale = getAddress(SplashScreen.this, mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
		}
		splashProcess();
	}

	protected void stopLocationUpdates() {
		LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (mGoogleApiClient != null)
			mGoogleApiClient.connect();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mGoogleApiClient != null)
			if (mGoogleApiClient.isConnected()) {
				startLocationUpdates();
			}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mGoogleApiClient != null)
			if (mGoogleApiClient.isConnected()) {
				stopLocationUpdates();
			}
	}

	@Override
	protected void onStop() {
		if (mGoogleApiClient != null)
			mGoogleApiClient.disconnect();
		super.onStop();
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		if (mCurrentLocation == null) {
			if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				if (ActivityCompat.shouldShowRequestPermissionRationale(this,
						android.Manifest.permission.ACCESS_FINE_LOCATION)) {

				} else {
					ActivityCompat.requestPermissions(this,
							new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
							100);
				}
			} else {
				mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
				mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
				updateUI();
			}
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		switch (requestCode) {
			case 100: {
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
					}
					mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
					mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
					updateUI();
				} else {
					updateUI();
				}
				return;
			}
		}
	}


	@Override
	public void onLocationChanged(Location location) {
		mCurrentLocation = location;
		mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
	}

	@Override
	public void onConnectionSuspended(int cause) {
		mGoogleApiClient.connect();
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		updateUI();
	}

	public static String getAddress(Context ctx, double latitude, double longitude) {
		String region_code = null;
		Geocoder geocoder = new Geocoder(ctx, Locale.getDefault());
		List<Address> addresses = null;
		try {
			addresses = geocoder.getFromLocation(latitude, longitude, 1);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (addresses.size() > 0) {
			Address address = addresses.get(0);
			region_code = address.getCountryCode();
		}
		return region_code;
	}


}