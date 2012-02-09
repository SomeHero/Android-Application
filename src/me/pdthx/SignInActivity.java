package me.pdthx;

import me.pdthx.Requests.UserSignInRequest;
import me.pdthx.Responses.UserSignInResponse;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public final class SignInActivity extends Activity {
	
	private SharedPreferences prefs = null;
	private String userName = "";
	private String password = "";
	
	private UserService userService = new UserService();
	private UserSignInResponse userSignInResponse = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signincontroller);

		prefs = PreferenceManager
				.getDefaultSharedPreferences(this);

		EditText txtUserName = (EditText) findViewById(R.id.txtUserName);
		txtUserName.setText(prefs.getString("mobileNumber", ""));
		userName = prefs.getString("mobileNumber", "");

		Button btn = (Button) findViewById(R.id.btnSignIn);
		btn.setOnClickListener(new OnClickListener() {
			public void onClick(View argO) {
				EditText txtPassword = (EditText) findViewById(R.id.txtPassword);

				password = txtPassword.getText().toString();
				
				showDialog(VALIDATEUSER_DIALOG);
			}
		});
	}

	final private int VALIDATEUSER_DIALOG = 0;
	final private int INVALIDUSERSIGNIN_DIALOG = 1;
	final private int UNHANDLEDEXCEPTION_DIALOG = 999;
	
	private Dialog dialog = null;
	private Handler dialogHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			dialog.dismiss();

			removeDialog(VALIDATEUSER_DIALOG);
			
			if (userSignInResponse.IsValid) {

				Editor editor = prefs.edit();
				editor.putString("userId", userSignInResponse.UserId);
				editor.putString("mobileNumber",
						userSignInResponse.MobileNumber);
				editor.putString("paymentAccountId",
						userSignInResponse.PaymentAccountId);

				editor.commit();

				launchPaymentScreen();
			} else {
				// else show alert dialog
				showDialog(INVALIDUSERSIGNIN_DIALOG);
				
				EditText txtPassword = (EditText) findViewById(R.id.txtPassword);
				
				txtPassword.setText("");
			}

		}
	};

	protected Dialog onCreateDialog(int id) {
		ProgressDialog progressDialog = null;
		AlertDialog alertDialog = null;
		switch(id) {
		case VALIDATEUSER_DIALOG:
			progressDialog = new ProgressDialog(this);

			progressDialog.setMessage("Validating User...");
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

			Thread thread = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						UserSignInRequest userSignInRequest = new UserSignInRequest();
						userSignInRequest.UserName = userName;
						userSignInRequest.Password = password;
						
						userSignInResponse = userService.SignInUser(userSignInRequest);

					} catch (Exception e) {
						e.printStackTrace();
					}
					dialogHandler.sendEmptyMessage(0);
				}

			});
			dialog = progressDialog;
			thread.start();

			return dialog;
		
			case INVALIDUSERSIGNIN_DIALOG:
				alertDialog = new AlertDialog.Builder(this).create();
				alertDialog.setTitle("Validation Failed");

				alertDialog
						.setMessage("Unable to validate user credentials.  Please try again.");
				alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();

					}
				});

				return alertDialog;
			case UNHANDLEDEXCEPTION_DIALOG:
				alertDialog = new AlertDialog.Builder(this).create();
				alertDialog.setTitle("Failed");

				alertDialog
						.setMessage("Unhandled Exception Occurred.");
				alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();

					}
				});

				return alertDialog;
		}
		return null;
	};

	protected void launchPaymentScreen() {
		Intent i = new Intent(this, CustomTabActivity.class);
		startActivity(i);
	}
}
