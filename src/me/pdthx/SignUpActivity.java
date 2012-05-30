package me.pdthx;

import me.pdthx.Models.ACHAccountModel;
import me.pdthx.Models.UserRegistrationModel;
import me.pdthx.Requests.ACHAccountSetupRequest;
import me.pdthx.Requests.UserRegistrationRequest;
import me.pdthx.Responses.ACHAccountSetupResponse;
import me.pdthx.Responses.UserRegistrationResponse;
import me.pdthx.Services.UserService;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
//import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;


public class SignUpActivity extends BaseActivity {
	
	private UserRegistrationModel registrationModel;
	private ACHAccountModel achAccountModel;
	
	
	final private int USERSECURITYPIN_COMPLETE = 1;
	final private int USERSECURITYPIN_FAILED = 2;
	final private int SETUPACHACCOUNT_FAILED = 3;
	final private int USERREGISTRATION_FAILED = 4;
	final private int USERSECURITYPIN_INVALIDLENGTH = 5;
	final private int USERSECURITYPIN_CONFIRMMISMATCH = 6;
	final private int USERREGISTRATION_PASSWORDMISMATCH = 7;
	final private int USERREGISTRATION_ACHNUMBERMISMATCH = 8;
//	final private int USERREGISTRATION_PHONENUMBERFORMATERROR = 9;
	
	protected UserService userService = new UserService();
	protected UserRegistrationResponse userRegistrationResponse;
	protected ACHAccountSetupResponse achAccountSetupResponse;
	
	private Activity parent = null;
	private View signUpView = null;
	
	ProgressDialog progressDialog = null;
	AlertDialog alertDialog = null;
	
	private EditText txtEmailAddress;
	private EditText txtPassword;
//	private EditText txtMobileNumber;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		parent = this;
		registrationModel = new UserRegistrationModel();
		
		if (!signedInViaFacebook) {
		signUpView = View.inflate(this, R.layout.setup_account, null);
		setContentView(signUpView);
		
		txtEmailAddress = (EditText)findViewById(R.id.txtEmailAddress);
		txtPassword = (EditText)findViewById(R.id.txtPassword);
//		txtMobileNumber = (EditText)findViewById(R.id.txtMobileNumber);
//		txtMobileNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
		
		showSignUpActivity(); 
		}
		else {
			showSetupSecurityPinController();
		}
	}
	
	Handler signUpHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
        	switch(msg.what) {
	        	case(USERSECURITYPIN_COMPLETE):
	        		showSetupACHController();
	        		break;
	        	case(USERSECURITYPIN_FAILED):
	        		alertDialog = new AlertDialog.Builder(parent)
					.create();
					alertDialog.setTitle("Setup Failed");
					alertDialog
							.setMessage("There was an error setting up your security pin: " + userRegistrationResponse.ReasonPhrase + " Please try again.");
					alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
					
					alertDialog.show();
	        		break;
	        	case(SETUPACHACCOUNT_FAILED):
	        		alertDialog = new AlertDialog.Builder(parent)
					.create();
					alertDialog.setTitle("Setup Failed");
					alertDialog
							.setMessage("There was an error setting up your ACH account: " + achAccountSetupResponse.ReasonPhrase + " Please try again.");
					alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
					
					alertDialog.show();
	        		break;
	        	case(USERREGISTRATION_FAILED):
	        		alertDialog = new AlertDialog.Builder(parent)
					.create();
					alertDialog.setTitle("User Registration Failed.");
					alertDialog
							.setMessage("There was an error completing your registration. Please try again.");
					alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
					
					alertDialog.show();
	        		break;
	        	case(USERSECURITYPIN_CONFIRMMISMATCH):
	        		alertDialog = new AlertDialog.Builder(parent)
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
	        		
	        	case(USERREGISTRATION_PASSWORDMISMATCH):
	        		alertDialog = new AlertDialog.Builder(parent)
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
        		
	        	case(USERREGISTRATION_ACHNUMBERMISMATCH):
	        		alertDialog = new AlertDialog.Builder(parent)
				.create();
				alertDialog.setTitle("ACH Account Number Mismatch.");
				alertDialog
						.setMessage("The ACH account numbers you entered do not match. Please try again.");
				alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				
				alertDialog.show();
        		break;
        		
//	        	case(USERREGISTRATION_PHONENUMBERFORMATERROR):
//	        		alertDialog = new AlertDialog.Builder(parent)
//				.create();
//				alertDialog.setTitle("Phone Number Format error.");
//				alertDialog
//						.setMessage("Phone number does not have 10 digits. Please try again.");
//				alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int which) {
//						dialog.dismiss();
//					}
//				});
//				
//				alertDialog.show();
//        		break;
        	}	
			
        }
        
	};
	
	public void showSignUpActivity() {

		Button btnCreateAccount = (Button)findViewById(R.id.btnCreateAnAccount);

		btnCreateAccount.setOnClickListener(new OnClickListener() {
			public void onClick(View argO) {

				EditText txtConfirmPassword = (EditText) findViewById(R.id.txtConfirmPassword);

//				if(txtMobileNumber.getText().toString().matches("[0-9]{3}-[0-9]{3}-[0-9]{4}")) {

					if(txtPassword.getText().toString().trim().equals(txtConfirmPassword.getText().toString().trim())) {
						registrationModel.setEmailAddress(txtEmailAddress.getText().toString().trim());
//						registrationModel.setMobileNumber(txtMobileNumber.getText().toString().trim());
						registrationModel.setPassword(txtPassword.getText().toString());
						showSetupSecurityPinController();
					} 
					else {
						signUpHandler.sendEmptyMessage(USERREGISTRATION_PASSWORDMISMATCH);
					}
//				}
//				else {
//					signUpHandler.sendEmptyMessage(USERREGISTRATION_PHONENUMBERFORMATERROR);
//				}
			}
		});
	}
	private void showSetupSecurityPinController() {
		setContentView(R.layout.setup_security_controller);
		
		Button btnSetupPin = (Button)findViewById(R.id.btnSetupSecurityPin);
		
		btnSetupPin.setOnClickListener(new OnClickListener() {
			public void onClick(View argO) {
				showSecurityPinDialog();
			}
		});
	}
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
					registrationModel.setSecurityPin(passcode);
					d.dismiss();
					
					showConfirmSecurityPinDialog();
				} else
					signUpHandler.sendEmptyMessage(USERSECURITYPIN_INVALIDLENGTH);
				
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

				if (passcode.length() > 3  && passcode.equals(registrationModel.getSecurityPin())) {
					d.dismiss();
					
					progressDialog = new ProgressDialog(v.getContext());
					//ProgressDialog.Builder progressDialog = new ProgressDialog.Builder(parent);
					progressDialog.setMessage("Setting up your PaidThx Account...");
					progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					progressDialog.show();
					
					Thread thread = new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							try {
								registerUser();
							} catch (Exception e) {
								e.printStackTrace();
							}
							progressDialog.dismiss();
							
							if(userRegistrationResponse.Success) {
								Editor editor = prefs.edit();

								editor.putString("userId", userRegistrationResponse.UserId);
								editor.putString("mobileNumber", registrationModel.getMobileNumber());
								editor.putString("paymentAccountId", "");
								editor.putBoolean("setupPassword", true);
								editor.putBoolean("setupSecurityPin", true);
								
								editor.commit();

								signUpHandler.sendEmptyMessage(USERSECURITYPIN_COMPLETE);
							}
							else {
								signUpHandler.sendEmptyMessage(USERSECURITYPIN_FAILED);
								
							}
						}

					});
					thread.start();
					
				} else
					signUpHandler.sendEmptyMessage(USERSECURITYPIN_CONFIRMMISMATCH);
				
				return false;
			}
		});
	}	
	private void showSetupACHController() {
		setContentView(R.layout.setup_achaccount_controller);
		
		Button btnEnablePayments = (Button)findViewById(R.id.btnSubmitACHAccount);
		btnEnablePayments.setOnClickListener(new OnClickListener() {
			public void onClick(View argO) {
				
				EditText txtNameOnAccount = (EditText)findViewById(R.id.txtNameOnAccount);
				EditText txtRoutingNumber = (EditText)findViewById(R.id.txtRoutingNumber);
				EditText txtAccountNumber = (EditText)findViewById(R.id.txtAccountNumber);
				
				if (txtAccountNumber.getText().toString().trim().equals(((EditText)findViewById(R.id.txtConfirmAccountNumber)).getText().toString().trim())) {
				
				achAccountModel = new ACHAccountModel();
				achAccountModel.NameOnAccount = txtNameOnAccount.getText().toString().trim();
				achAccountModel.RoutingNumber = txtRoutingNumber.getText().toString().trim();
				achAccountModel.AccountNumber = txtAccountNumber.getText().toString().trim();
				achAccountModel.AccountType = "Savings";
				
				progressDialog = new ProgressDialog(argO.getContext());
				//ProgressDialog.Builder progressDialog = new ProgressDialog.Builder(parent);
				progressDialog.setMessage("Setting up ACH Account...");
				progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				progressDialog.show();
				
				Thread thread = new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							setupACHAccount();
						} catch (Exception e) {
							e.printStackTrace();
						}
						progressDialog.dismiss();
						
						if(achAccountSetupResponse.Success) {
							Editor editor = prefs.edit();

							editor.putString("paymentAccountId", achAccountSetupResponse.PaymentAccountId);
							editor.commit();

							signUpHandler.sendEmptyMessage(R.id.USERREGISTRATION_COMPLETE);
							Intent data = new Intent();
							data.putExtra("email", txtEmailAddress.getText().toString().trim());
							data.putExtra("password", txtPassword.getText().toString().trim());
							
							setResult(Activity.RESULT_OK, data);
							finish();
						}
						else {
							signUpHandler.sendEmptyMessage(SETUPACHACCOUNT_FAILED);
							
						}
					}

				});
				thread.start();
				}
				else {
					signUpHandler.sendEmptyMessage(USERREGISTRATION_ACHNUMBERMISMATCH);
				}
			}
		});
	}
	
	//Fix this.
	private void registerUser() {
		
		if (signedInViaFacebook) {
			userRegistrationResponse = new UserRegistrationResponse();
			userRegistrationResponse.UserId = prefs.getString("userId", "");
			userRegistrationResponse.Success = true;
		}
		else {
		
		UserRegistrationRequest request = new UserRegistrationRequest();
		request.DeviceToken = "";
		request.UserName = registrationModel.getEmailAddress();
		request.Password = registrationModel.getPassword();
		
		userRegistrationResponse = userService.RegisterUser(request);
		}
	}
	private void setupACHAccount() {
		ACHAccountSetupRequest request = new ACHAccountSetupRequest();
		
		request.UserId = prefs.getString("userId", "");
		request.AccountNumber = achAccountModel.AccountNumber;
		request.AccountType = achAccountModel.AccountType;
		request.NameOnAccount = achAccountModel.NameOnAccount;
		request.RoutingNumber = achAccountModel.RoutingNumber;
		
		achAccountSetupResponse = userService.setupACHAccount(request);
	}
}
