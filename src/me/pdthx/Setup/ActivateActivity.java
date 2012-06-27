package me.pdthx.Setup;

import me.pdthx.BaseActivity;
import me.pdthx.R;
import me.pdthx.Requests.UserRequest;
import me.pdthx.Responses.UserResponse;
import me.pdthx.Services.UserService;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class ActivateActivity extends BaseActivity {

	private String userId = "";
	private String SENT = "SMS_SENT";
	private String DELIVERED = "SMS_DELIVERED";
	private String phoneNumber = "12892100266";

	private PendingIntent sentPI;
	private PendingIntent deliveredPI;

	private Button activateBtn;
	private Button remindLater;
	
	private UserResponse userResponse;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activate);

		userId = prefs.getString("userId", "");

		UserRequest userRequest = new UserRequest();
		userRequest.UserId = userId;

		userResponse = UserService.getUser(userRequest);

		setupActivate();
	}

	private void setupActivate() {
		activateBtn = (Button) findViewById(R.id.activate_btn);
		remindLater = (Button) findViewById(R.id.activate_rmdLater);

		activateBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setupSMS();
				String message = userResponse.UserId;

				SmsManager sms = SmsManager.getDefault();

				try {
					sms.sendTextMessage(phoneNumber, null, message, sentPI,
							deliveredPI);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		});

		remindLater.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(),
						PersonalizeActivity.class));
			}

		});
	}

	public void setupSMS() {
		sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);

		deliveredPI = PendingIntent.getBroadcast(this, 0,
				new Intent(DELIVERED), 0);

		// ---when the SMS has been sent---
		registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					Toast.makeText(getBaseContext(), "SMS sent",
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					Toast.makeText(getBaseContext(), "Generic failure",
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_NO_SERVICE:
					Toast.makeText(getBaseContext(), "No service",
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_NULL_PDU:
					Toast.makeText(getBaseContext(), "Null PDU",
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_RADIO_OFF:
					Toast.makeText(getBaseContext(), "Radio off",
							Toast.LENGTH_SHORT).show();
					break;
				}
			}
		}, new IntentFilter(SENT));

		// ---when the SMS has been delivered---
		registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					Toast.makeText(getBaseContext(), "SMS delivered",
							Toast.LENGTH_SHORT).show();
					break;
				case Activity.RESULT_CANCELED:
					Toast.makeText(getBaseContext(), "SMS not delivered",
							Toast.LENGTH_SHORT).show();
					break;
				}
			}
		}, new IntentFilter(DELIVERED));
	}
}