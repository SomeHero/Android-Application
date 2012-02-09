package me.pdthx;

import java.text.NumberFormat;

import me.pdthx.Requests.ACHAccountSetupRequest;
import me.pdthx.Requests.UserRegistrationRequest;
import me.pdthx.Requests.UserSetupSecurityPinRequest;
import me.pdthx.Requests.UserVerifyMobileDeviceRequest;
import me.pdthx.Responses.ACHAccountSetupResponse;
import me.pdthx.Responses.SubmitPaymentResponse;
import me.pdthx.Responses.UserRegistrationResponse;
import me.pdthx.Responses.UserSetupSecurityPinResponse;
import me.pdthx.Responses.UserVerifyMobileDeviceResponse;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public final class GetMoneyActivity extends Activity {
	private UserService userService = new UserService();
	private String securityPin = "";
	private String mobileNumber = "";
	private String deviceId = "";
	private String userId = "";
	private String nameOnAccount = "";
	private String routingNumber = "";
	private String accountNumber = "";
	private String accountType = "";
	private String verificationCode1 = "";
	private String verificationCode2= "";
	SharedPreferences prefs;
	
	private UserRegistrationResponse userRegistrationResponse;
	private UserSetupSecurityPinResponse userSetupSecurityPinResponse;
	private UserVerifyMobileDeviceResponse userVerifyMobileDeviceResponse;
	private ACHAccountSetupResponse achAccountSetupResponse;
	
	final private int REGISTERUSER_DIALOG = 0;
	final private int VERIFYMOBILEDEVICE_DIALOG = 1;
	final private int INVALIDMOBILEDEVICE_DIALOG = 2;
	final private int SETUPACHACCOUNT_DIALOG = 3;
	final private int VERIFYMOBILEDEVICEFAILED_DIALOG = 4;
	
	final private int REGISTERUSER_ACTION = 0;
	final private int VERIFYMOBILEDEVICE_ACTION = 1;
	final private int SETUPACHACCOUNT_ACTION = 2;
	
	private Dialog dialog = null;
	private Handler dialogHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			dialog.dismiss();
			switch (msg.what) {
			
			case REGISTERUSER_ACTION:
				if (userRegistrationResponse != null
						&& userRegistrationResponse.Success) {
					userId = userRegistrationResponse.UserId;

					Editor editor = prefs.edit();
					editor.putString("userId", userId);
					editor.commit();

					launchVerifyMobileDevice();
				} else {
					//displayFailure();
				}
				break;
			case VERIFYMOBILEDEVICE_ACTION:
				removeDialog(VERIFYMOBILEDEVICE_DIALOG);

				if (userVerifyMobileDeviceResponse != null
						&& userVerifyMobileDeviceResponse.Success) {
					launchSetupACHAccount();
				} else {
					showDialog(VERIFYMOBILEDEVICEFAILED_DIALOG);
				}
				break;
			case SETUPACHACCOUNT_ACTION:
				break;
			}
		}
	};
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.main_menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		Intent intent = null;

		Editor editor = prefs.edit();

		switch (item.getItemId()) {
		case R.id.signOutMenuItem:
			editor.remove("userId");
			editor.commit();

			intent = new Intent(GetMoneyActivity.this, SignInActivity.class);
			startActivity(intent);

			break;
		case R.id.forgetMeMenuItem:

			editor.clear();
			editor.commit();

			intent = new Intent(GetMoneyActivity.this,
					AcknowledgeDevice.class);
			startActivity(intent);

			break;

		}
		return super.onOptionsItemSelected(item);
	}

	protected android.app.Dialog onCreateDialog(int id) {
		AlertDialog alertDialog = null;
		ProgressDialog progressDialog = null;
		Thread thread = null;
		switch (id) {
		
		case REGISTERUSER_DIALOG:
			progressDialog = new ProgressDialog(this);

			progressDialog.setMessage("Setting Up Your Account...");
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

			thread = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {

						RegisterUser();

					} catch (Exception e) {
						e.printStackTrace();
					}
					dialogHandler.sendEmptyMessage(REGISTERUSER_ACTION);
				}

			});
			dialog = progressDialog;
			thread.start();

			return dialog;
		case VERIFYMOBILEDEVICE_DIALOG:
			progressDialog = new ProgressDialog(this);

			progressDialog.setMessage("Verifying Your Device...");
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

			thread = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {

						VerifyMobileDevice();

					} catch (Exception e) {
						e.printStackTrace();
					}
					dialogHandler.sendEmptyMessage(VERIFYMOBILEDEVICE_ACTION);
				}

			});
			dialog = progressDialog;
			thread.start();

			return dialog;

		case INVALIDMOBILEDEVICE_DIALOG:
			alertDialog = new AlertDialog.Builder(GetMoneyActivity.this)
					.create();
			alertDialog.setTitle("Validation Failed");

			alertDialog
					.setMessage("The validation codes you submitted are incorrect.  Please try again.");
			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();

					// txtVerificationCode1.setText("");
					// txtVerificationCode2.setText("");
				}
			});
			alertDialog.show();
			
			return alertDialog;

		case SETUPACHACCOUNT_DIALOG:
			progressDialog = new ProgressDialog(this);

			progressDialog.setMessage("Setting Up Your ACH Account...");
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

			thread = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {

						SetupACHAccount();

					} catch (Exception e) {
						e.printStackTrace();
					}
					dialogHandler.sendEmptyMessage(SETUPACHACCOUNT_ACTION);
				}

			});
			dialog = progressDialog;
			thread.start();

			return dialog;
		}

		return null;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		deviceId = Secure.getString(this
				.getContentResolver(), Secure.ANDROID_ID);
		mobileNumber =  prefs.getString(
				"mobileNumber", "");
				
		setContentView(R.layout.getmoney_controller);
		
		Button btn = (Button) findViewById(R.id.btnVerifyMobileNumber);
		btn.setOnClickListener(new OnClickListener() {
			public void onClick(View argO) {
				showDialog(REGISTERUSER_DIALOG);
			}
				
		});
		
	}
	
	protected void launchVerifyMobileDevice() {
		setContentView(R.layout.verifymobiledevice_controller);
		
		Button btn = (Button) findViewById(R.id.btnSubmitVerificationCodes);
		btn.setOnClickListener(new OnClickListener() {
			public void onClick(View argO) {

				final EditText txtVerificationCode1 = (EditText) findViewById(R.id.txtVerificationCode1);
				final EditText txtVerificationCode2 = (EditText) findViewById(R.id.txtVerificationCode2);

				verificationCode1 = txtVerificationCode1.getText().toString();
				verificationCode2 = txtVerificationCode2.getText().toString();
				
				showDialog(VERIFYMOBILEDEVICE_DIALOG);
			}
		});
		
	}
	
	protected void launchSetupACHAccount() {
		setContentView(R.layout.achaccountsetup_controller);

		Button btn = (Button) findViewById(R.id.btnSubmitACHAccount);
		btn.setOnClickListener(new OnClickListener() {
			public void onClick(View argO) {

				EditText txtNameOnAccount = (EditText) findViewById(R.id.txtNameOnAccount);
				EditText txtRoutingNumber = (EditText) findViewById(R.id.txtRoutingNumber);
				EditText txtAccountNumber = (EditText) findViewById(R.id.txtAccountNumber);
				EditText txtConfirmAccountNumber = (EditText) findViewById(R.id.txtConfirmAccountNumber);

				nameOnAccount = txtNameOnAccount.getText().toString();
				routingNumber = txtRoutingNumber.getText().toString();
				accountNumber = txtAccountNumber.getText().toString();
				String confirmAccountNumber = txtConfirmAccountNumber.getText().toString();
				
				if(accountNumber == confirmAccountNumber)
					showDialog(SETUPACHACCOUNT_DIALOG);
				
				//else
				//showfailure
			}
		});
	}
	protected void RegisterUser() {

		UserRegistrationRequest userRegistrationRequest = new UserRegistrationRequest();
		userRegistrationRequest.MobileNumber = mobileNumber;
		userRegistrationRequest.UserName = mobileNumber;
		userRegistrationRequest.SecurityPin = "";
		userRegistrationRequest.DeviceId = deviceId;

		userRegistrationResponse = userService
				.RegisterUser(userRegistrationRequest);
	}

	protected void VerifyMobileDevice() {
		UserVerifyMobileDeviceRequest userVerifyMobileDeviceRequest = new UserVerifyMobileDeviceRequest();
		userVerifyMobileDeviceRequest.UserId = userId;
		userVerifyMobileDeviceRequest.MobileNumber = mobileNumber;
		userVerifyMobileDeviceRequest.DeviceId = deviceId;
		userVerifyMobileDeviceRequest.VerificationCode1 = verificationCode1;
		userVerifyMobileDeviceRequest.VerificationCode2 = verificationCode2;

		userVerifyMobileDeviceResponse = userService
				.VerifyMobileDevice(userVerifyMobileDeviceRequest);

	}

	protected void SetupACHAccount() {
		ACHAccountSetupRequest achAccountSetupRequest = new ACHAccountSetupRequest();
		achAccountSetupRequest.UserId = prefs.getString("userId", "");
		achAccountSetupRequest.NameOnAccount = nameOnAccount;
		achAccountSetupRequest.RoutingNumber = routingNumber;
		achAccountSetupRequest.AccountNumber = accountNumber;
		achAccountSetupRequest.AccountType = accountType;

		achAccountSetupResponse = userService
				.SetupACHAccount(achAccountSetupRequest);
	}

}
