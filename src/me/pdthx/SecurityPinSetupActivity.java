package me.pdthx;

import me.pdthx.Requests.SecurityPinSetupRequest;
import me.pdthx.Responses.SecurityPinSetupResponse;
import me.pdthx.Services.UserService;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;

public class SecurityPinSetupActivity extends BaseActivity {
	
	private Button btnSetupPin;
	private SecurityPinSetupRequest request;
	private SecurityPinSetupResponse response;
	
	final private int USERSECURITYPIN_COMPLETE = 1;
	final private int USERSECURITYPIN_FAILED = 2;
	final private int USERSECURITYPIN_INVALIDLENGTH = 5;
	final private int USERSECURITYPIN_CONFIRMMISMATCH = 6;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		setContentView(R.layout.setup_security_controller);
		
		btnSetupPin = (Button)findViewById(R.id.btnSetupSecurityPin);
		
		btnSetupPin.setOnClickListener(new OnClickListener() {
			public void onClick(View argO) {
				showSecurityPinDialog();
			}
		});
	}
	
	Handler securityPinSetupHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
        	switch(msg.what) {
	        	case(USERSECURITYPIN_COMPLETE):
	        		//TODO: Start HomeActivity.
	        		break;
	        	case(USERSECURITYPIN_FAILED):
	        		alertDialog = new AlertDialog.Builder(SecurityPinSetupActivity.this)
					.create();
					alertDialog.setTitle("Setup Failed");
					alertDialog
							.setMessage("There was an error setting up your security pin: " + 
									response.ReasonPhrase + " Please try again.");
					alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
					
					alertDialog.show();
	        		break;
	        		
	        	case(USERSECURITYPIN_CONFIRMMISMATCH):
	        		alertDialog = new AlertDialog.Builder(SecurityPinSetupActivity.this)
					.create();
					alertDialog.setTitle("Security Pins Mismatch.");
					alertDialog
							.setMessage("The two security pins you just swiped don't match. Please try again.");
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
	
	private void showSecurityPinDialog() {
		final Dialog d = new Dialog(this, R.style.CustomDialogTheme);
		d.setContentView(R.layout.setup_security_dialog);

	 	d.getWindow().setLayout(400, 600);
		d.show();


		final CustomLockView ctrlSecurityPin = (CustomLockView) d.findViewById(R.id.ctrlSecurityPin);
		ctrlSecurityPin.invalidate();
		ctrlSecurityPin.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				String passcode = ctrlSecurityPin.getPasscode();

				if (passcode.length() > 3) {
					request = new SecurityPinSetupRequest(prefs.getString("userId", ""), passcode);
					d.dismiss();
					
					showConfirmSecurityPinDialog();
				} else
					securityPinSetupHandler.sendEmptyMessage(USERSECURITYPIN_INVALIDLENGTH);
				
				return false;

			}
		});
	}
	
	private void showConfirmSecurityPinDialog() {
		final Dialog d = new Dialog(this, R.style.CustomDialogTheme);
		d.setContentView(R.layout.confirm_security_dialog);

	 	d.getWindow().setLayout(400, 600);
		d.show();


		final CustomLockView ctrlSecurityPin = (CustomLockView) d.findViewById(R.id.ctrlSecurityPin);
		ctrlSecurityPin.invalidate();
		ctrlSecurityPin.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				String passcode = ctrlSecurityPin.getPasscode();

				if (passcode.length() > 3  && passcode.equals(request.getSecurityPin())) {
					d.dismiss();
					
					progressDialog = new ProgressDialog(v.getContext());
					//ProgressDialog.Builder progressDialog = new ProgressDialog.Builder(parent);
					progressDialog.setMessage("Setting up your security pin...");
					progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					progressDialog.show();
					
					Thread thread = new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							try {
								UserService userService = new UserService();
								response = userService.setupSecurityPin(request);
							} catch (Exception e) {
								e.printStackTrace();
							}
							progressDialog.dismiss();
							
							if(response.Success) {
								Editor editor = prefs.edit();

								editor.putBoolean("setupSecurityPin", true);
								editor.commit();
								
								setResult(RESULT_OK);
								finish();

								//securityPinSetupHandler.sendEmptyMessage(USERSECURITYPIN_COMPLETE);
							}
							else {
								securityPinSetupHandler.sendEmptyMessage(USERSECURITYPIN_FAILED);
								
							}
						}

					});
					thread.start();
					
				} else
					securityPinSetupHandler.sendEmptyMessage(USERSECURITYPIN_CONFIRMMISMATCH);
				
				return false;
			}
		});
	}
}
