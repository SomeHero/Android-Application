package me.pdthx;

import me.pdthx.Requests.UserRegistrationRequest;
import me.pdthx.Responses.UserRegistrationResponse;
import me.pdthx.Services.UserService;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;


public class SignUpActivity extends BaseActivity {
	
	final private int USERREGISTRATION_FAILED = 4;
	final private int USERREGISTRATION_PASSWORDMISMATCH = 7;
	
	private String emailAddress;
	private String password;
	
	private UserRegistrationRequest request;
	private UserRegistrationResponse response;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (!signedInViaFacebook) {
		setContentView(View.inflate(this, R.layout.setup_account, null));
		
		showSignUpActivity(); 
		}
		else {
			finish();
		}
	}
	
	Handler signUpHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
        	switch(msg.what) {
	        	case(USERREGISTRATION_FAILED):
	        		alertDialog = new AlertDialog.Builder(SignUpActivity.this)
					.create();
					alertDialog.setTitle("User Registration Failed.");
					alertDialog
							.setMessage("There was an error completing your registration: " + response.ReasonPhrase + " Please try again.");
					alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
					
					alertDialog.show();
	        		break;
	        		
	        	case(USERREGISTRATION_PASSWORDMISMATCH):
	        		alertDialog = new AlertDialog.Builder(SignUpActivity.this)
				.create();
				alertDialog.setTitle("Passwords don't Match.");
				alertDialog
						.setMessage("The password you entered do not match. Please try again.");
				alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				
				alertDialog.show();
        		break;
        	}	
			
        }
        
	};
	
	public void showSignUpActivity() {

		Button btnCreateAccount = (Button)findViewById(R.id.btnCreateAnAccount);

		btnCreateAccount.setOnClickListener(new OnClickListener() {
			public void onClick(View argO) {

				emailAddress = ((EditText) findViewById(R.id.txtEmailAddress)).getText().toString().trim();
				password = ((EditText) findViewById(R.id.txtPassword)).getText().toString().trim();
				String confirmPassword = ((EditText) findViewById(R.id.txtConfirmPassword)).getText().toString().trim();

				if(password.equals(confirmPassword)) {
					request = new UserRegistrationRequest(emailAddress, password, "");
					
					progressDialog.setMessage("Setting up account...");
					progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					progressDialog.show();

					Thread thread = new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							try {
								UserService userService = new UserService();
								response = userService.RegisterUser(request);
							} catch (Exception e) {
								e.printStackTrace();
							}
							progressDialog.dismiss();

							if(response.Success) {
								
								Intent data = new Intent();
								data.putExtra("email", emailAddress);
								data.putExtra("password", password);
								
								setResult(RESULT_OK, data);
								finish();

								//signUpHandler.sendEmptyMessage(R.id.USERREGISTRATION_COMPLETE);
							}
							else {
								signUpHandler.sendEmptyMessage(USERREGISTRATION_FAILED);
							}
						}

					});
					thread.start();
				} 
				else {
					signUpHandler.sendEmptyMessage(USERREGISTRATION_PASSWORDMISMATCH);
				}
			}
		});
	}

}
