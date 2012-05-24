package me.pdthx;


import me.pdthx.Models.ACHAccountModel;
import me.pdthx.Models.UserRegistrationModel;
import me.pdthx.Requests.ACHAccountSetupRequest;
import me.pdthx.Requests.UserRegistrationRequest;
import me.pdthx.Requests.UserSignInRequest;
import me.pdthx.Responses.ACHAccountSetupResponse;
import me.pdthx.Responses.UserRegistrationResponse;
import me.pdthx.Responses.UserSignInResponse;
import me.pdthx.Services.UserService;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;

public class SignInActivity {

	private static final String APIKEY = "bda11d91-7ade-4da1-855d-24adfe39d174";
	private String deviceId= "";
	private String userName = "";
	private String password = "";

	private UserRegistrationModel registrationModel;
	private ACHAccountModel achAccountModel;

	protected UserService userService = new UserService();
	protected UserSignInResponse userSignInResponse = null;
	protected UserRegistrationResponse userRegistrationResponse;
	protected ACHAccountSetupResponse achAccountSetupResponse;

	ProgressDialog progressDialog = null;
	AlertDialog alertDialog = null;

	final private int USERSIGNIN_INVALID = 0;
	final private int USERSECURITYPIN_COMPLETE = 1;
	final private int USERSECURITYPIN_FAILED = 2;
	final private int SETUPACHACCOUNT_FAILED = 3;
	final private int USERREGISTRATION_FAILED = 4;
	final private int USERSECURITYPIN_INVALIDLENGTH = 5;
	final private int USERSECURITYPIN_CONFIRMMISMATCH = 6;
	final private int USERREGISTRATION_PASSWORDMISMATCH = 7;

	private Handler _handler = null;
	private Activity parent = null;
	private View signInView = null;
	private SharedPreferences _prefs;

	Handler signInHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
        	switch(msg.what) {
	        	case(USERSIGNIN_INVALID):
	        		alertDialog = new AlertDialog.Builder(parent)
					.create();
					alertDialog.setTitle("Invalid Account Credentials.");
					alertDialog
							.setMessage("The username and password you entered were invalid. Please try again.");
					alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});

					alertDialog.show();
					break;
	        	case(USERSECURITYPIN_COMPLETE):
	        		showSetupACHController();
	        		break;
	        	case(USERSECURITYPIN_FAILED):
	        		alertDialog = new AlertDialog.Builder(parent)
					.create();
					alertDialog.setTitle("Setup Failed");
					alertDialog
							.setMessage("There was an error setting up your security pin. Please try again.");
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
							.setMessage("There was an error setting up your ACH account. Please try again.");
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
        	}

        }

	};

	public SignInActivity(Activity parentActivity, Handler handler, SharedPreferences prefs) {
		parent = parentActivity;
		_handler = handler;
		_prefs = prefs;
	}

    public void showSignInActivity() {
    	signInView = View.inflate(parent, R.layout.signin_controller, null);
        parent.setContentView(signInView);

		//deviceId = Secure.getString(getBaseContext().getContentResolver(),
				//Secure.ANDROID_ID);

		Button btnSignIn = (Button)parent.findViewById(R.id.btnSignInSubmit);

		btnSignIn.setOnClickListener(new OnClickListener() {
			public void onClick(View argO) {
				EditText txtUserName = (EditText) parent.findViewById(R.id.txtUserName);
				EditText txtPassword = (EditText) parent.findViewById(R.id.txtPassword);

				userName = txtUserName.getText().toString();
				password = txtPassword.getText().toString();

				//ProgressDialog progressDialog = null;
				Thread thread = null;

				progressDialog = new ProgressDialog(parent);
				//ProgressDialog.Builder progressDialog = new ProgressDialog.Builder(parent);
				progressDialog.setMessage("Authenticating...");
				progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				progressDialog.show();

				thread = new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							signInUser();
						} catch (Exception e) {
							e.printStackTrace();
						}
						progressDialog.dismiss();

						if(userSignInResponse.IsValid) {
							Editor editor = _prefs.edit();

							editor.putString("userId", userSignInResponse.UserId);
							editor.putString("mobileNumber", userSignInResponse.MobileNumber);
							editor.putString("paymentAccountId", userSignInResponse.PaymentAccountId);
							editor.putBoolean("setupPassword", userSignInResponse.SetupPassword);
							editor.putBoolean("setupSecurityPin", userSignInResponse.SetupSecurityPin);
							editor.commit();

							_handler.sendEmptyMessage(R.id.USERSIGNIN_COMPLETE);
						}
						else {
							signInHandler.sendEmptyMessage(USERSIGNIN_INVALID);

						}
					}

				});
				thread.start();
			}
		});

		Button btnSetupAccount = (Button)parent.findViewById(R.id.btnCreateAnAccount);

		btnSetupAccount.setOnClickListener(new OnClickListener() {
			public void onClick(View argO) {
				showSetupAccountController();
			}
		});
	}
	private void showSetupAccountController() {
		parent.setContentView(R.layout.setup_account);

		Button btnCreateAccount = (Button)parent.findViewById(R.id.btnCreateAnAccount);

		btnCreateAccount.setOnClickListener(new OnClickListener() {
			public void onClick(View argO) {

				EditText txtEmailAddress = (EditText)parent.findViewById(R.id.txtEmailAddress);
				EditText txtMobileNumber = (EditText)parent.findViewById(R.id.txtMobileNumber);
				EditText txtPassword = (EditText)parent.findViewById(R.id.txtPassword);
				EditText txtConfirmPassword = (EditText)parent.findViewById(R.id.txtConfirmPassword);

				if(txtPassword.getText().toString().equals(txtConfirmPassword.getText().toString())) {
					registrationModel = new UserRegistrationModel();
					registrationModel.setEmailAddress(txtEmailAddress.getText().toString());

					registrationModel.setMobileNumber(txtMobileNumber.getText().toString());
					registrationModel.setPassword(txtPassword.getText().toString());

					showSetupSecurityPinController();
				} else {
					signInHandler.sendEmptyMessage(USERREGISTRATION_PASSWORDMISMATCH);
				}
			}
		});
	}
	private void showSetupSecurityPinController() {
		parent.setContentView(R.layout.setup_security_controller);

		Button btnSetupPin = (Button)parent.findViewById(R.id.btnSetupSecurityPin);

		btnSetupPin.setOnClickListener(new OnClickListener() {
			public void onClick(View argO) {
				showSecurityPinDialog();
			}
		});
	}
	private void showSecurityPinDialog() {
		final Dialog d = new Dialog(parent, R.style.CustomDialogTheme);
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
					signInHandler.sendEmptyMessage(USERSECURITYPIN_INVALIDLENGTH);

				return false;

			}
		});
	}

	private void showConfirmSecurityPinDialog() {
		final Dialog d = new Dialog(parent, R.style.CustomDialogTheme);
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

					progressDialog = new ProgressDialog(parent);
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
								Editor editor = _prefs.edit();

								editor.putString("userId", userRegistrationResponse.UserId);
								editor.putString("mobileNumber", registrationModel.getMobileNumber());
								editor.putString("paymentAccountId", "");
								editor.putBoolean("setupPassword", true);
								editor.putBoolean("setupSecurityPin", true);

								editor.commit();

								signInHandler.sendEmptyMessage(USERSECURITYPIN_COMPLETE);
							}
							else {
								signInHandler.sendEmptyMessage(USERSECURITYPIN_FAILED);

							}
						}

					});
					thread.start();

				} else
					signInHandler.sendEmptyMessage(USERSECURITYPIN_CONFIRMMISMATCH);

				return false;
			}
		});
	}
	private void showSetupACHController() {
		parent.setContentView(R.layout.setup_achaccount_controller);

		Button btnEnablePayments = (Button)parent.findViewById(R.id.btnSubmitACHAccount);
		btnEnablePayments.setOnClickListener(new OnClickListener() {
			public void onClick(View argO) {

				EditText txtNameOnAccount = (EditText)parent.findViewById(R.id.txtNameOnAccount);
				EditText txtRoutingNumber = (EditText)parent.findViewById(R.id.txtRoutingNumber);
				EditText txtAccountNumber = (EditText)parent.findViewById(R.id.txtAccountNumber);
				EditText txtConfirmAccountNumber = (EditText)parent.findViewById(R.id.txtConfirmAccountNumber);

				achAccountModel = new ACHAccountModel();
				achAccountModel.NameOnAccount = txtNameOnAccount.getText().toString();
				achAccountModel.RoutingNumber = txtRoutingNumber.getText().toString();
				achAccountModel.AccountNumber = txtAccountNumber.getText().toString();
				achAccountModel.AccountType = "Savings";

				progressDialog = new ProgressDialog(parent);
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
							Editor editor = _prefs.edit();

							editor.putString("paymentAccountId", achAccountSetupResponse.PaymentAccountId);
							editor.commit();

							_handler.sendEmptyMessage(R.id.USERREGISTRATION_COMPLETE);
						}
						else {
							signInHandler.sendEmptyMessage(SETUPACHACCOUNT_FAILED);

						}
					}

				});
				thread.start();
			}
		});
	}
	private void signInUser() {
		UserSignInRequest userSignInRequest = new UserSignInRequest();
		userSignInRequest.UserName = userName;
		userSignInRequest.Password = password;
		userSignInResponse = userService.SignInUser(userSignInRequest);
	}
	private void registerUser() {
		UserRegistrationRequest request = new UserRegistrationRequest();
		request.ApiKey = APIKEY;
		request.DeviceId = deviceId;
		request.UserName = registrationModel.getEmailAddress();
		request.EmailAddress = registrationModel.getEmailAddress();
		request.MobileNumber = registrationModel.getMobileNumber();
		request.Password = registrationModel.getPassword();
		request.SecurityPin = registrationModel.getSecurityPin();
		request.RegistrationMethodId = "2";

		userRegistrationResponse = userService.RegisterUser(request);
	}
	private void setupACHAccount() {
		ACHAccountSetupRequest request = new ACHAccountSetupRequest();

		request.UserId = _prefs.getString("userId", "");
		request.AccountNumber = achAccountModel.AccountNumber;
		request.AccountType = achAccountModel.AccountType;
		request.NameOnAccount = achAccountModel.NameOnAccount;
		request.RoutingNumber = achAccountModel.RoutingNumber;

		achAccountSetupResponse = userService.SetupACHAccount(request);
	}
}
