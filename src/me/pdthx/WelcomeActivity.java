package me.pdthx;

import me.pdthx.Requests.UserAcknowledgementRequest;
import me.pdthx.Responses.UserAcknowledgementResponse;
import me.pdthx.Services.UserService;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public final class WelcomeActivity extends Activity {
	private String mobileNumber = "";
	private String deviceId ="";
	private Boolean isLoggedIn = false;
	private SharedPreferences prefs = null;

	private UserService userService = new UserService();
	private UserAcknowledgementResponse userAcknowledgementResponse = null;

	final private int ACKNOWLEDGEDEVICE_DIALOG = 0;
	final private int UNHANDLEDEXCEPTION_DIALOG = 999;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		deviceId = Secure.getString(this.getContentResolver(),
	            Secure.ANDROID_ID);
		setContentView(R.layout.acknowledgedevice_controller);

		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		if (prefs.getString("userId", "").length() > 0
				&& prefs.getString("mobileNumber", "").length() > 0) {
			isLoggedIn = true;
		}
		if (prefs.getString("mobileNumber", "").length() > 0) {
			mobileNumber = prefs.getString("mobileNumber", "");
			showDialog(ACKNOWLEDGEDEVICE_DIALOG);
		} else {

			
			EditText txtMobileNumber = (EditText) findViewById(R.id.txtYourMobileNumber);
			txtMobileNumber
					.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

			Button btn = (Button) findViewById(R.id.btnSubmitYourMobileNumber);
			btn.setOnClickListener(new OnClickListener() {
				public void onClick(View argO) {

					EditText txtMobileNumber = (EditText) findViewById(R.id.txtYourMobileNumber);
					mobileNumber = txtMobileNumber.getText().toString();

					showDialog(ACKNOWLEDGEDEVICE_DIALOG);

				}
			});
		}
	}

	private Dialog dialog = null;
	private Handler dialogHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			dialog.dismiss();

			Editor editor = prefs.edit();

			String userId = prefs.getString("userId", "");
			
			if (userAcknowledgementResponse != null) {
				if (userAcknowledgementResponse.IsMobileNumberRegistered
						&& userAcknowledgementResponse.SetupPassword
						&& isLoggedIn  
						&& userId.equals(userAcknowledgementResponse.UserId)) {
					editor.putString("userId",
							userAcknowledgementResponse.UserId);
					editor.putString("mobileNumber", mobileNumber);
					editor.putString("paymentAccountId",
							userAcknowledgementResponse.PaymentAccountId);
					editor.putBoolean("setupSecurityPin", userAcknowledgementResponse.SetupSecurityPin);
					editor.putBoolean("setupPassword", true);
					editor.commit();

					launchPaymentScreen();

				} else if (userAcknowledgementResponse.IsMobileNumberRegistered
						&& userAcknowledgementResponse.SetupPassword
						&& !isLoggedIn  
						&& userId.equals(userAcknowledgementResponse.UserId)) {
					editor.putString("userId",
							userAcknowledgementResponse.UserId);
					editor.putString("mobileNumber", mobileNumber);
					editor.putString("paymentAccountId",
							userAcknowledgementResponse.PaymentAccountId);
					editor.putBoolean("setupSecurityPin", userAcknowledgementResponse.SetupSecurityPin);
					editor.putBoolean("setupPassword", true);
					editor.commit();

					launchSignInScreen(mobileNumber);

				} 
				else if (userAcknowledgementResponse.IsMobileNumberRegistered) {
					editor.putString("userId",
							userAcknowledgementResponse.UserId);
					editor.putString("mobileNumber", mobileNumber);
					editor.putString("paymentAccountId",
							userAcknowledgementResponse.PaymentAccountId);
					editor.putBoolean("setupSecurityPin", userAcknowledgementResponse.SetupSecurityPin);
					editor.putBoolean("setupPassword", false);
					editor.commit();

					launchPaymentScreen();

				} else {
					editor.clear();
					editor.commit();
					
					editor.putString("mobileNumber", mobileNumber);
					editor.commit();

					launchPaymentScreen();
				}
			} else {
				showDialog(UNHANDLEDEXCEPTION_DIALOG);
			}
		}
	};

	@Override
	protected Dialog onCreateDialog(int id) {
		ProgressDialog progressDialog = null;
		AlertDialog alertDialog = null;
		switch (id) {
		case ACKNOWLEDGEDEVICE_DIALOG:
			progressDialog = new ProgressDialog(this);

			progressDialog.setMessage("Authenticating...");
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

			Thread thread = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						acknowledgeDevice();

					} catch (Exception e) {
						e.printStackTrace();
					}
					dialogHandler.sendEmptyMessage(0);
				}

			});
			dialog = progressDialog;
			thread.start();

			return dialog;
		case UNHANDLEDEXCEPTION_DIALOG:
			alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.setTitle("Exception Occurred");

			alertDialog
					.setMessage("Unhandled Exception Occurred.  Please try again.");
			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();

				}
			});
			
			return alertDialog;
		}
		return super.onCreateDialog(id);
	}

	protected void launchPaymentScreen() {
		Intent i = new Intent(this, CustomTabActivity.class);
		startActivity(i);
	}

	protected void launchSignInScreen(String mobileNumber) {
		Intent i = new Intent(this, SignInActivity.class);
		startActivity(i);
	}

	private void acknowledgeDevice() {
		UserAcknowledgementRequest userAcknowledgementRequest = new UserAcknowledgementRequest();
		userAcknowledgementRequest.DeviceId = deviceId;
		userAcknowledgementRequest.MobileNumber = mobileNumber;

		try {
			userAcknowledgementResponse = userService.AcknowledgeUser(userAcknowledgementRequest);	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
