package me.pdthx;

import me.pdthx.Requests.UserRegistrationRequest;
import me.pdthx.Responses.UserRegistrationResponse;
import me.pdthx.Services.UserService;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
//import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class SignUpActivity extends BaseActivity {
	
	final private int USERREGISTRATION_FAILED = 4;
	final private int USERREGISTRATION_PASSWORDMISMATCH = 7;
	private String emailAddress;
	private String password;
	
	private UserRegistrationRequest request;
	private UserRegistrationResponse response;
	
	private String SENT = "SMS_SENT";
    private String DELIVERED = "SMS_DELIVERED";
    private String phoneNumber = "12892100266";
	
    private PendingIntent sentPI;
    private PendingIntent deliveredPI;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
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
	    
		if (!signedInViaFacebook) {
		setContentView(View.inflate(this, R.layout.setup_account, null));
		
		showSignUpActivity(); 
		}
		else {
			finish();
		}
	}
	
	private Handler signUpHandler = new Handler() {

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

									String message = response.UserId;
									
									SmsManager sms = SmsManager.getDefault();
					    	        
					    	        try {
					    	        	sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);  
					    	        }
					    	        catch(Exception e)
					    	        {
					    	        	e.printStackTrace();
					    	        }

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
