package me.pdthx;

import java.text.NumberFormat;

import me.pdthx.Requests.ACHAccountSetupRequest;
import me.pdthx.Requests.PaymentRequestRequest;
import me.pdthx.Requests.UserRegistrationRequest;
import me.pdthx.Requests.UserSetupPasswordRequest;
import me.pdthx.Requests.UserSetupSecurityPinRequest;
import me.pdthx.Requests.UserVerifyMobileDeviceRequest;
import me.pdthx.Responses.PaymentRequestResponse;
import me.pdthx.Responses.UserRegistrationResponse;
import me.pdthx.Responses.UserSetupPasswordResponse;
import me.pdthx.Responses.UserSetupSecurityPinResponse;
import me.pdthx.Responses.UserVerifyMobileDeviceResponse;
import me.pdthx.Services.PaymentRequestService;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

public class RequestMoneyActivity extends Activity {

	PaymentRequestService paymentRequestService = new PaymentRequestService();
	private UserService userService = new UserService();
	
	private static final String APIKEY = "bda11d91-7ade-4da1-855d-24adfe39d174";
	private String recipientUri = "";
	private double amount = (double) 0;
	private String comments = "";
	private String userId = "";
	private String deviceId = "";
	private String passcode = "";
	private String mobileNumber = "";
	private String password = "";
	private String verificationCode1;
	private String verificationCode2;

	private AutoCompleteTextView txtRequestMoneyRecipient;
	private EditText txtAmount;
	private EditText txtComments;
	private Button btnSendMoney;
	private CustomLockView ctrlSetupSecurityPin;
	private CustomLockView ctrlConfirmSecurityPin;

	final private int SUBMITREQUEST_DIALOG = 0;
	final private int NORECIPIENTSPECIFIED_DIALOG = 1;
	final private int NOAMOUNTSPECIFIED_DIALOG = 2;
	final private int SUBMITREQUESTFAILED_DIALOG = 3;
	final private int SUBMITREQUESTSUCCESS_DIALOG = 4;
	final private int INVALIDPASSCODELENGTH_DIALOG = 5;
	final private int SUBMITPASSWORD_DIALOG = 6;
	final private int SUBMITPASSWORDSUCCESS_DIALOG = 7;
	final private int SUBMITPASSWORDFAILED_DIALOG = 8;
	final private int VERIFYMOBILEDEVICE_DIALOG = 9;
	final private int VERIFYMOBILEDEVICEFAILED_DIALOG = 10;
	final private int CONFIRMSECURITYPIN_DIALOG = 11;
	final private int REGISTERUSER_DIALOG = 12;
	final private int SETUPSECURITYPIN_DIALOG = 13;
	final private int CONFIRMSECURITYPIN_ACTION = 14;

	final private int SUBMITREQUEST_ACTION = 1;
	final private int SUBMITPASSWORD_ACTION = 2;
	final private int VERIFYMOBILEDEVICE_ACTION = 3;
	final private int REGISTERUSER_ACTION = 4;
	final private int SETUPSECURITYPIN_ACTION = 5;

	private SharedPreferences prefs;

	private CustomLockView ctrlSecurityPin;
	private PaymentRequestResponse paymentRequestResponse;
	private UserVerifyMobileDeviceResponse userVerifyMobileDeviceResponse;
	private UserSetupPasswordResponse userSetupPasswordResponse;
	private UserRegistrationResponse userRegistrationResponse;
	private UserSetupSecurityPinResponse userSetupSecurityPinResponse;

	private ContactList contactList = null;

	private Dialog dialog = null;
	private Handler dialogHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			dialog.dismiss();
			switch (msg.what) {
			case SUBMITREQUEST_ACTION:
				removeDialog(SUBMITREQUEST_DIALOG);

				if (paymentRequestResponse != null
						&& paymentRequestResponse.Success) {
					showDialog(SUBMITREQUESTSUCCESS_DIALOG);

				} else {
					showDialog(SUBMITREQUESTFAILED_DIALOG);
				}
				break;
			case SUBMITPASSWORD_ACTION:
				removeDialog(SUBMITPASSWORD_DIALOG);

				if (userSetupPasswordResponse != null
						&& userSetupPasswordResponse.Success) {
					Editor editor = prefs.edit();
					editor.putBoolean("setupPassword", true);
					editor.commit();

					showDialog(SUBMITREQUEST_DIALOG);
				} else {
					showDialog(SUBMITPASSWORDFAILED_DIALOG);
				}
				break;
			case REGISTERUSER_ACTION:
				if (userRegistrationResponse != null
						&& userRegistrationResponse.Success) {
					userId = userRegistrationResponse.UserId;

					Editor editor = prefs.edit();
					editor.putString("userId", userId);
					editor.commit();

					launchVerifyMobileDeviceView();
				} else {
					//displayFailure();
				}
				break;
			case VERIFYMOBILEDEVICE_ACTION:
				removeDialog(VERIFYMOBILEDEVICE_DIALOG);

				if (userVerifyMobileDeviceResponse != null
						&& userVerifyMobileDeviceResponse.Success) {
					launchSetupSecurityPin();
				} else {
					showDialog(VERIFYMOBILEDEVICEFAILED_DIALOG);
				}
				break;
			case SETUPSECURITYPIN_ACTION:
				launchConfirmSecurityPin();
				break;

			case CONFIRMSECURITYPIN_ACTION:
				if (userSetupSecurityPinResponse != null && userSetupSecurityPinResponse.Success) {
					Editor editor = prefs.edit();
					editor.putBoolean("setupPassword", true);
					editor.commit();
					
					showDialog(SUBMITREQUEST_DIALOG);
				} else {
					//displayFailure();
				}
				break;
			}
		}
	};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("Request Money");

		prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

		userId = prefs.getString("userId", "");
		mobileNumber = prefs.getString("mobileNumber", "");
		deviceId = Secure.getString(getBaseContext().getContentResolver(),
				Secure.ANDROID_ID);
		contactList = new ContactList(getBaseContext());

		launchRequestMoneyView();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.main_menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		Intent intent = null;

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		Editor editor = prefs.edit();

		switch (item.getItemId()) {
		case R.id.signOutMenuItem:
			editor.remove("userId");
			editor.commit();

			intent = new Intent(RequestMoneyActivity.this, SignInActivity.class);
			startActivity(intent);

			break;
		case R.id.forgetMeMenuItem:

			editor.clear();
			editor.commit();

			intent = new Intent(RequestMoneyActivity.this,
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
		case SUBMITREQUEST_DIALOG:
			progressDialog = new ProgressDialog(this);

			progressDialog.setMessage("Submitting Request...");
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

			thread = new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						SubmitRequest();

					} catch (Exception e) {
						e.printStackTrace();
					}
					dialogHandler.sendEmptyMessage(SUBMITREQUEST_ACTION);
				}

			});
			dialog = progressDialog;
			thread.start();

			return dialog;
		case SUBMITREQUESTFAILED_DIALOG:
			alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.setTitle("Failed");

			alertDialog
					.setMessage("Unable to submit your request at this time.  Please try again.");
			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();

				}
			});

			return alertDialog;
		case SUBMITREQUESTSUCCESS_DIALOG:
			alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.setTitle("Request Sumitted");
			NumberFormat nf = NumberFormat.getCurrencyInstance();

			alertDialog.setMessage(String.format(
					"Your request for %s was sent to %s.", nf.format(amount),
					recipientUri));

			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();

					txtRequestMoneyRecipient.setText("");
					txtRequestMoneyRecipient.requestFocus();
					txtAmount.setText("$0.00");
					txtComments.setText("");
				}
			});
			return alertDialog;
		case NORECIPIENTSPECIFIED_DIALOG:
			alertDialog = new AlertDialog.Builder(RequestMoneyActivity.this)
					.create();
			alertDialog.setTitle("Please Specify a Recipient");
			alertDialog
					.setMessage("You must specify the recipient's mobile number.");

			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});

			return alertDialog;
		case NOAMOUNTSPECIFIED_DIALOG:
			alertDialog = new AlertDialog.Builder(RequestMoneyActivity.this)
					.create();
			alertDialog.setTitle("Please Specify an Amount");
			alertDialog.setMessage("You must specify the amount to send.");

			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});

			return alertDialog;

		case INVALIDPASSCODELENGTH_DIALOG:
			alertDialog = new AlertDialog.Builder(RequestMoneyActivity.this)
					.create();
			alertDialog.setTitle("Invalid Passcode");
			alertDialog
					.setMessage("Your passcode is atleast 4 buttons. Please try again.");

			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});

			return alertDialog;

		case SUBMITPASSWORD_DIALOG:
			progressDialog = new ProgressDialog(this);

			progressDialog.setMessage("Submitting Request...");
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

			thread = new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						SetupPassword();

					} catch (Exception e) {
						e.printStackTrace();
					}
					dialogHandler.sendEmptyMessage(SUBMITPASSWORD_ACTION);
				}

			});
			dialog = progressDialog;
			thread.start();

			return dialog;
		case SUBMITPASSWORDFAILED_DIALOG:
			alertDialog = new AlertDialog.Builder(RequestMoneyActivity.this)
					.create();
			alertDialog.setTitle("Failure");
			alertDialog
					.setMessage("Unable to setup password. Please try again.");

			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});

			return alertDialog;

		case VERIFYMOBILEDEVICE_DIALOG:
			progressDialog = new ProgressDialog(this);

			progressDialog.setMessage("Verifying Device...");
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

			thread = new Thread(new Runnable() {

				@Override
				public void run() {
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
		case VERIFYMOBILEDEVICEFAILED_DIALOG:
			alertDialog = new AlertDialog.Builder(RequestMoneyActivity.this)
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
		case SETUPSECURITYPIN_DIALOG:
			progressDialog = new ProgressDialog(this);

			progressDialog.setMessage("Creating Security Pin...");
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

			thread = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						Thread.sleep(1000);
					} catch (Exception e) {
						e.printStackTrace();
					}
					dialogHandler.sendEmptyMessage(SETUPSECURITYPIN_ACTION);
				}

			});
			dialog = progressDialog;
			thread.start();

			return dialog;
		case CONFIRMSECURITYPIN_DIALOG:
			progressDialog = new ProgressDialog(this);

			progressDialog.setMessage("Validating Security Pin...");
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

			thread = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {

						setupSecurityPin();

					} catch (Exception e) {
						e.printStackTrace();
					}
					dialogHandler.sendEmptyMessage(CONFIRMSECURITYPIN_ACTION);
				}
			});
			dialog = progressDialog;
			thread.start();

			return dialog;
		}

		return null;
	}

	protected void launchRequestMoneyView() {

		setContentView(R.layout.requestmoney_controller);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.list_item, contactList.getContacts().toArray(
						new String[0]));

		txtRequestMoneyRecipient = (AutoCompleteTextView) findViewById(R.id.txtRequestMoneyRecipient);
		txtRequestMoneyRecipient.setAdapter(adapter);

		txtAmount = (EditText) findViewById(R.id.txtRequestMoneyAmount);
		txtAmount.addTextChangedListener(new TextWatcher() {
			String current = "";

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (!s.toString().equals(current)) {
					EditText txtAmount = (EditText) findViewById(R.id.txtRequestMoneyAmount);
					txtAmount.removeTextChangedListener(this);

					String cleanString = s.toString().replaceAll("[$,.]", "");

					double parsed = Double.parseDouble(cleanString);
					String formatted = NumberFormat.getCurrencyInstance()
							.format((parsed / 100));

					current = formatted;
					txtAmount.setText(formatted);
					txtAmount.setSelection(formatted.length());

					txtAmount.addTextChangedListener(this);
				}
			}

			@Override
			public void afterTextChanged(Editable arg0) {
				
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				
			}
		});
		txtComments = (EditText) findViewById(R.id.txtRequestMoneyComments);

		ctrlSecurityPin = (CustomLockView) findViewById(R.id.ctrlSecurityPin);
		ctrlSecurityPin.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				passcode = ctrlSecurityPin.getPasscode();

				if (passcode.length() > 3) {
					recipientUri = txtRequestMoneyRecipient.getText()
							.toString();
					amount = Double.parseDouble(txtAmount.getText().toString()
							.replace("$", ""));
					comments = txtComments.getText().toString();

					showDialog(SUBMITREQUEST_DIALOG);
				} else
					showDialog(INVALIDPASSCODELENGTH_DIALOG);

				return false;
			}
		});
		
		btnSendMoney = (Button) findViewById(R.id.btnSubmit);
		btnSendMoney.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				boolean isValid = true;

				recipientUri = txtRequestMoneyRecipient.getText().toString();
				amount = Double.parseDouble(txtAmount.getText().toString()
						.replace("$", ""));
				comments = txtComments.getText().toString();

				if (isValid & recipientUri.length() == 0) {
					showDialog(NORECIPIENTSPECIFIED_DIALOG);
					isValid = false;
				}
				if (isValid & amount == (double) 0) {
					showDialog(NOAMOUNTSPECIFIED_DIALOG);
					isValid = false;
				}
				if (isValid) {
					if (prefs.getString("userId", "").length() == 0) {
						showDialog(REGISTERUSER_DIALOG);
					} else {
						showDialog(SUBMITREQUEST_DIALOG);
					}
				}
			}
		});

		if (prefs.getBoolean("setupSecurityPin", false) == true) {
			ctrlSecurityPin.setVisibility(View.VISIBLE);
			btnSendMoney.setVisibility(View.INVISIBLE);
		} else {
			ctrlSecurityPin.setVisibility(View.INVISIBLE);
			btnSendMoney.setVisibility(View.VISIBLE);
		}
	}

	protected void launchVerifyMobileDeviceView() {
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

	protected void launchSetupSecurityPin() {
		setContentView(R.layout.setupsecuritypin_controller);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		ctrlSetupSecurityPin = (CustomLockView) findViewById(R.id.ctrlSetupSecurityPin);

		ctrlSetupSecurityPin.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				passcode = ctrlSetupSecurityPin.getPasscode();

				if (passcode.length() > 3)
					showDialog(SETUPSECURITYPIN_DIALOG);
				else
					showDialog(INVALIDPASSCODELENGTH_DIALOG);
				return false;
			}

		});
	}

	protected void launchConfirmSecurityPin() {
		setContentView(R.layout.confirmsecuritypin_controller);

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		ctrlConfirmSecurityPin = (CustomLockView) findViewById(R.id.ctrlConfirmSecurityPin);

		ctrlConfirmSecurityPin.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				passcode = ctrlConfirmSecurityPin.getPasscode();

				if (passcode.length() > 3)
					showDialog(CONFIRMSECURITYPIN_DIALOG);
				else
					showDialog(INVALIDPASSCODELENGTH_DIALOG);
				return false;
			}

		});

	}

	protected void launchSetupPasswordView() {
		setContentView(R.layout.setupassword_controller);

		final EditText txtUserName = (EditText) findViewById(R.id.txtSetupPasswordUserName);
		txtUserName.setText(mobileNumber);

		Button btn = (Button) findViewById(R.id.btnSubmitPassword);
		btn.setOnClickListener(new OnClickListener() {
			public void onClick(View argO) {
				final EditText txtPassword = (EditText) findViewById(R.id.txtSetupPasswordPassword);
				password = txtPassword.getText().toString();

				showDialog(SUBMITPASSWORD_DIALOG);

			};
		});
	}

	protected void SubmitRequest() {
		final SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());

		PaymentRequestRequest paymentRequest = new PaymentRequestRequest();
		paymentRequest.ApiKey = APIKEY;
		paymentRequest.UserId = prefs.getString("userId", "");
		paymentRequest.DeviceId = deviceId;
		paymentRequest.RecipientUri = recipientUri;
		paymentRequest.Amount = amount;
		paymentRequest.Comments = comments;

		paymentRequestResponse = paymentRequestService.SendPaymentRequest(paymentRequest);
	}

	protected void SetupPassword() {
		UserSetupPasswordRequest userSetupPasswordRequest = new UserSetupPasswordRequest();

		userSetupPasswordRequest.UserId = userId;
		userSetupPasswordRequest.DeviceId = deviceId;
		userSetupPasswordRequest.UserName = mobileNumber;
		userSetupPasswordRequest.Password = password;

		userSetupPasswordResponse = userService
				.SetupPassword(userSetupPasswordRequest);
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
	protected void RegisterUser() {

		UserRegistrationRequest userRegistrationRequest = new UserRegistrationRequest();
		userRegistrationRequest.MobileNumber = mobileNumber;
		userRegistrationRequest.UserName = mobileNumber;
		userRegistrationRequest.SecurityPin = "";
		userRegistrationRequest.DeviceId = deviceId;

		userRegistrationResponse = userService
				.RegisterUser(userRegistrationRequest);
	}
	protected void setupSecurityPin() {
		UserSetupSecurityPinRequest userSetupSecurityPin = new UserSetupSecurityPinRequest();
		userSetupSecurityPin.UserId = prefs.getString("userId",
				"");
		userSetupSecurityPin.DeviceId = deviceId;
		userSetupSecurityPin.SecurityPin = passcode;

		userSetupSecurityPinResponse = userService
				.SetupSecurityPin(userSetupSecurityPin);
	}

}
