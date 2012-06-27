package me.pdthx;


import java.text.NumberFormat;

import me.pdthx.Requests.UserRequest;
import me.pdthx.Responses.UserResponse;
import me.pdthx.Services.UserService;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public final class HomeActivity extends BaseActivity {

	public static final String TAG = "HomeActivity";
	private String userId = "";

	private String SENT = "SMS_SENT";
    private String DELIVERED = "SMS_DELIVERED";
    private String phoneNumber = "12892100266";

    private PendingIntent sentPI;
    private PendingIntent deliveredPI;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);  

		tracker.trackPageView("Home");

		if(prefs.getString("userId", "").length() == 0) {
			startActivityForResult(new Intent(this, SignInActivity.class), 1);
		}
		else {
			showHomeController();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			showHomeController();
		}
		else {
			finish();
		}
	}
	public void switchTabInActivity(int indexTabToSwitchTo){
		CustomTabActivity ParentActivity;
		ParentActivity = (CustomTabActivity) this.getParent();
		ParentActivity.switchTab(indexTabToSwitchTo);
	}

	public void setupSMS() {
		sentPI = PendingIntent.getBroadcast(this, 0,
	            new Intent(SENT), 0);

	    deliveredPI = PendingIntent.getBroadcast(this, 0,
	            new Intent(DELIVERED), 0);

	    //---when the SMS has been sent---
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
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

        //---when the SMS has been delivered---
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
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


	private void showHomeController() {
		userId = prefs.getString("userId", "");

		UserRequest userRequest = new UserRequest();
		userRequest.UserId = userId;

		UserResponse userResponse = UserService.getUser(userRequest);

		if(userResponse == null)
		{
		    logout();
		}
		else {
			if (userResponse.DeviceToken == null ||
					userResponse.DeviceToken.equals("null") ||
					userResponse.RegistrationId.equals("null")) {
				registerPushNotifications();
			}

			if (userResponse.MobileNumber != null &&
					userResponse.MobileNumber.equals("null")) {

				setupSMS();
				String message = userResponse.UserId;

				SmsManager sms = SmsManager.getDefault();

    	        try {
    	        	sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
    	        }
    	        catch(Exception e)
    	        {
    	        	e.printStackTrace();
    	        }
			}


			Editor editor = prefs.edit();
			editor.putInt("upperLimit", userResponse.UpperLimit);

			if (userResponse.UserName != null &&
					userResponse.UserName.contains("fb_")) {
				editor.putBoolean("signedInViaFacebook", true);
			}
			editor.commit();

			setContentView(R.layout.home_controller);

			NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();

			TextView txtUserName = (TextView)findViewById(R.id.txtUserName);

			if (!userResponse.FirstName.equals("null") && !userResponse.LastName.equals("null") ) {
				txtUserName.setText(userResponse.FirstName + " " + userResponse.LastName);
			}
			else if (!userResponse.EmailAddress.equals("null")) {
				txtUserName.setText(userResponse.EmailAddress);
			}
			else if (!userResponse.MobileNumber.equals("null")) {
				txtUserName.setText(userResponse.MobileNumber);
			}
			else {
				txtUserName.setText("PaidThx User");
			}


			TextView txtTotalMoneySent = (TextView)findViewById(R.id.txtTotalMoneySent);
			txtTotalMoneySent.setText(currencyFormatter.format(userResponse.TotalMoneySent));

			TextView txtTotalMoneyReceived = (TextView)findViewById(R.id.txtTotalMoneyReceived);
			txtTotalMoneyReceived.setText(currencyFormatter.format(userResponse.TotalMoneyReceived));

			Button btnSendMoney = (Button)findViewById(R.id.btnQuickLinkSent);
			btnSendMoney.setOnClickListener(new OnClickListener() {
				public void onClick(View argO) {

					switchTabInActivity(1);
				}
			});
			Button btnRequestMoney = (Button)findViewById(R.id.btnQuickLinkRequest);

			btnRequestMoney.setOnClickListener(new OnClickListener() {
				public void onClick(View argO) {

					switchTabInActivity(2);
				}
			});
		}
	}
}
