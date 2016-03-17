package com.mini.emergency.app;

import net.viralpatel.android.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final int RESULT_SETTINGS = 1;

	ImageView imageView;
	String address = "";
	SharedPreferences sharedPrefs;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		imageView = (ImageView) findViewById(R.id.panic);

		sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(this);

		imageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//String address = "";
				GPSService mGPSService = new GPSService(getApplicationContext());
				mGPSService.getLocation();

				if (mGPSService.isLocationAvailable == false) {

					// Here you can ask the user to try again, using return; for that
					Toast.makeText(getApplicationContext(), "Your location is not available, please try again.", Toast.LENGTH_SHORT).show();
					return;

					// Or you can continue without getting the location, remove the return; above and uncomment the line given below
					// address = "Location not available";
				} else {

					// Getting location co-ordinates
					double latitude = mGPSService.getLatitude();
					double longitude = mGPSService.getLongitude();
					Toast.makeText(getApplicationContext(), "Latitude:" + latitude + " | Longitude: " + longitude, Toast.LENGTH_LONG).show();

					address = mGPSService.getLocationAddress();
				}

				Toast.makeText(getApplicationContext(), "Your address is: " + address, Toast.LENGTH_SHORT).show();

				// make sure you close the gps after using it. Save user's battery power
				mGPSService.closeGPS();

				sendMessage(sharedPrefs);
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				phoneCall(sharedPrefs);
			}
		});

		showUserSettings();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.menu_settings:
			Intent i = new Intent(this, UserSettingActivity.class);
			startActivityForResult(i, RESULT_SETTINGS);
			break;

		}

		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case RESULT_SETTINGS:
			showUserSettings();
			break;
		}
	}

	private void showUserSettings() {

		StringBuilder builder = new StringBuilder();

		builder.append("\n Ambulance Number: "
				+ sharedPrefs.getString("ambulanceNumber", "NULL"));

		builder.append("\n Police Number:"
				+ sharedPrefs.getString("policeNumber", "NULL"));

		TextView settingsTextView = (TextView) findViewById(R.id.textUserSettings);

		settingsTextView.setText(builder.toString());

	}

	private void phoneCall(SharedPreferences sharedPrefs) {

		String firstNumber = sharedPrefs.getString("firstNumber", "NULL");
		Intent phoneIntent = new Intent(Intent.ACTION_CALL);

		phoneIntent.setData(Uri.parse("tel:+91"+firstNumber));

		try
		{
			startActivity(phoneIntent);
			finish();
			Log.i("Finish making phone call", "");

		}
		catch(android.content.ActivityNotFoundException ex)
		{
			Toast.makeText(MainActivity.this, "Call failed", Toast.LENGTH_LONG).show();
		}

	}

	private void sendMessage(SharedPreferences sharedPrefs) {

		String firstNumber = sharedPrefs.getString("firstNumber", "NULL");
		String secondNumber = sharedPrefs.getString("secondNumber", "NULL");
		String thirdNumber = sharedPrefs.getString("secondNumber", "NULL");

		if(firstNumber != null || !firstNumber.equals("")){
			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage(firstNumber.toString(), null, "I am in an emergency Situation, Please do help me \n"+address, null, null);

			//Toast.makeText(getApplicationContext(), "Message Sent!", Toast.LENGTH_LONG).show();
		}
		if(secondNumber != null || !secondNumber.equals("")){
			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage(secondNumber.toString(), null, "I am in an emergency Situation, Please do help me \n"+address, null, null);

			Toast.makeText(getApplicationContext(), "Message Sent!", Toast.LENGTH_LONG).show();
		}
		if(thirdNumber != null || !thirdNumber.equals("")){
			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage(thirdNumber.toString(), null, "I am in an emergency Situation, Please do help me \n"+address, null, null);

			Toast.makeText(getApplicationContext(), "Message Sent!", Toast.LENGTH_LONG).show();
		}

	}

}
