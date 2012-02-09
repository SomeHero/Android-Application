package me.pdthx;

import me.pdthx.Requests.UserAcknowledgementRequest;
import me.pdthx.Responses.UserAcknowledgementResponse;
import me.pdthx.Services.UserService;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class AcknowledgeDevice extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.acknowledgedevice_controller);
		
		EditText txtMobileNumber = (EditText) findViewById(R.id.txtYourMobileNumber);
		txtMobileNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
		
		Button btn = (Button) findViewById(R.id.btnSubmitYourMobileNumber);
		btn.setOnClickListener(new OnClickListener() {
			public void onClick(View argO) {
				ProgressDialog mDialog = new ProgressDialog(argO.getContext(),
						ProgressDialog.STYLE_SPINNER);
				mDialog.setMessage("Please wait...");
				mDialog.setCancelable(false);
				mDialog.show();
				
				EditText txtMobileNumber = (EditText) findViewById(R.id.txtYourMobileNumber);
				final String mobileNumber = txtMobileNumber.getText()
						.toString();
				
				UserService userService = new UserService();
				UserAcknowledgementRequest userAcknowledgementRequest = new UserAcknowledgementRequest();
				userAcknowledgementRequest.MobileNumber = mobileNumber;
				
				UserAcknowledgementResponse userAcknowledgementResponse =
						userService.AcknowledgeUser(userAcknowledgementRequest);
				
				SharedPreferences prefs = PreferenceManager
						.getDefaultSharedPreferences(argO.getContext());
				Editor editor = prefs.edit();

				if(userAcknowledgementResponse.IsMobileNumberRegistered & userAcknowledgementResponse.SetupPassword)
				{
					editor.putString("userId", userAcknowledgementResponse.UserId);
					editor.putString("mobileNumber", mobileNumber);
					editor.putString("paymentAccountId", userAcknowledgementResponse.PaymentAccountId);
					editor.putBoolean("setupPassword", false);
					editor.commit();
					
					mDialog.dismiss();
					launchSignInScreen(mobileNumber);
							
				}
				else if(userAcknowledgementResponse.IsMobileNumberRegistered)
				{
					editor.putString("userId", userAcknowledgementResponse.UserId);
					editor.putString("mobileNumber", mobileNumber);
					editor.putString("paymentAccountId", userAcknowledgementResponse.PaymentAccountId);
					editor.putBoolean("setupPassword", true);
					editor.commit();
					
					mDialog.dismiss();
					launchPaymentScreen(mobileNumber);
							
				}
				else {
					editor.clear();
					editor.commit();
					
					editor.putString("mobileNumber", mobileNumber);
					editor.commit();
					
					mDialog.dismiss();
					launchPaymentScreen(mobileNumber);
				}

			}
		});
	}
	protected void launchPaymentScreen(String mobileNumber) {
		Intent i = new Intent(this, CustomTabActivity.class);
		startActivity(i);
	}
	protected void launchSignInScreen(String mobileNumber) {
		Intent i = new Intent(this, SignInActivity.class);
		startActivity(i);
	}
}
