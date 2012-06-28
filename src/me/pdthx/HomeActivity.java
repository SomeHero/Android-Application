package me.pdthx;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import me.pdthx.Requests.UserRequest;
import me.pdthx.Responses.UserResponse;
import me.pdthx.Services.UserService;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public final class HomeActivity extends BaseActivity {

	public static final String TAG = "HomeActivity";
	private String userId = "";

//	private String SENT = "SMS_SENT";
//    private String DELIVERED = "SMS_DELIVERED";
//    private String phoneNumber = "12892100266";
//
//    private PendingIntent sentPI;
//    private PendingIntent deliveredPI;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		tracker.trackPageView("Home");

		if(prefs.getString("userId", "").length() == 0) {
			logout();
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

//	public void setupSMS() {
//		sentPI = PendingIntent.getBroadcast(this, 0,
//	            new Intent(SENT), 0);
//
//	    deliveredPI = PendingIntent.getBroadcast(this, 0,
//	            new Intent(DELIVERED), 0);
//
//	    //---when the SMS has been sent---
//        registerReceiver(new BroadcastReceiver(){
//            @Override
//            public void onReceive(Context arg0, Intent arg1) {
//                switch (getResultCode())
//                {
//                    case Activity.RESULT_OK:
//                        Toast.makeText(getBaseContext(), "SMS sent",
//                                Toast.LENGTH_SHORT).show();
//                        break;
//                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
//                        Toast.makeText(getBaseContext(), "Generic failure",
//                                Toast.LENGTH_SHORT).show();
//                        break;
//                    case SmsManager.RESULT_ERROR_NO_SERVICE:
//                        Toast.makeText(getBaseContext(), "No service",
//                                Toast.LENGTH_SHORT).show();
//                        break;
//                    case SmsManager.RESULT_ERROR_NULL_PDU:
//                        Toast.makeText(getBaseContext(), "Null PDU",
//                                Toast.LENGTH_SHORT).show();
//                        break;
//                    case SmsManager.RESULT_ERROR_RADIO_OFF:
//                        Toast.makeText(getBaseContext(), "Radio off",
//                                Toast.LENGTH_SHORT).show();
//                        break;
//                }
//            }
//        }, new IntentFilter(SENT));
//
//        //---when the SMS has been delivered---
//        registerReceiver(new BroadcastReceiver(){
//            @Override
//            public void onReceive(Context arg0, Intent arg1) {
//                switch (getResultCode())
//                {
//                    case Activity.RESULT_OK:
//                        Toast.makeText(getBaseContext(), "SMS delivered",
//                                Toast.LENGTH_SHORT).show();
//                        break;
//                    case Activity.RESULT_CANCELED:
//                        Toast.makeText(getBaseContext(), "SMS not delivered",
//                                Toast.LENGTH_SHORT).show();
//                        break;
//                }
//            }
//        }, new IntentFilter(DELIVERED));
//	}


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
			if (userResponse.RegistrationId.equals("null")) {
				registerPushNotifications();
			}

			/*if (userResponse.MobileNumber != null &&
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
			}*/


			Editor editor = prefs.edit();
			editor.putInt("upperLimit", userResponse.UpperLimit);

			if (userResponse.UserName != null &&
					userResponse.UserName.contains("fb_")) {
				editor.putBoolean("signedInViaFacebook", true);
			}
			editor.commit();

			setContentView(R.layout.homescreen);

			ImageView imgUserName =  (ImageView)findViewById(R.id.home_userImg);
			if (imgUserName != null) {
				URL url;
				try {
					if (!userResponse.ImageUrl.equals("null")) {
						url = new URL(userResponse.ImageUrl);
						Bitmap mIcon = BitmapFactory.decodeStream(url
								.openConnection().getInputStream());
						imgUserName.setImageBitmap(mIcon);
					} else {
						imgUserName
								.setImageResource(R.drawable.avatar_unknown);
					}
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			TextView txtUserName = (TextView)findViewById(R.id.home_username);
			if (!userResponse.FirstName.equals("null") && !userResponse.LastName.equals("null") ) {
				txtUserName.setText(userResponse.FirstName + " " + userResponse.LastName);
			}
			else {
				txtUserName.setText("PaidThx User");
			}

			TextView txtPhoneNumber = (TextView)findViewById(R.id.home_phonenumber);
			if(!userResponse.MobileNumber.equals("null"))
			{
				txtPhoneNumber.setText(userResponse.MobileNumber);
			}
			else
			{
				txtPhoneNumber.setText(" ");
			}

			TextView txtEmail = (TextView)findViewById(R.id.home_email);
			if(!userResponse.EmailAddress.equals("null"))
			{
				txtEmail.setText(userResponse.EmailAddress);
			}
			else
			{
				txtEmail.setText(" ");
			}

			Button btnSendMoney = (Button)findViewById(R.id.home_sendmoneyBtn);
			btnSendMoney.setOnClickListener(new OnClickListener() {
				public void onClick(View argO) {

					switchTabInActivity(1);
				}
			});

			Button btnRequestMoney = (Button)findViewById(R.id.home_requestmoneyBtn);

			btnRequestMoney.setOnClickListener(new OnClickListener() {
				public void onClick(View argO) {

					switchTabInActivity(2);
				}
			});
			/*NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();

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
			});*/
		}
	}
}
