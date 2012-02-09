/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.pdthx;

import java.text.NumberFormat;
import java.util.ArrayList;

import me.pdthx.Requests.ACHAccountSetupRequest;
import me.pdthx.Requests.SubmitPaymentRequest;
import me.pdthx.Requests.UserRegistrationRequest;
import me.pdthx.Requests.UserSetupPasswordRequest;
import me.pdthx.Requests.UserSetupSecurityPinRequest;
import me.pdthx.Requests.UserVerifyMobileDeviceRequest;
import me.pdthx.Responses.ACHAccountSetupResponse;
import me.pdthx.Responses.SubmitPaymentResponse;
import me.pdthx.Responses.UserRegistrationResponse;
import me.pdthx.Responses.UserSetupPasswordResponse;
import me.pdthx.Responses.UserSetupSecurityPinResponse;
import me.pdthx.Responses.UserVerifyMobileDeviceResponse;
import me.pdthx.Services.PaymentServices;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public final class MakePaymentActivity extends Activity {

	public static final String TAG = "ContactManager";
	private String deviceId;
	private String mobileNumber;
	private String recipientUri = "";
	private Double amount = (double) 0;
	private String comments = "";
	private String userId = "";
	private String paymentAccountId = "";
	private String securityPin = "";
	private String verificationCode1 = "";
	private String verificationCode2 = "";
	private String errorMessage = "";

	private UserService userService = new UserService();

	private AutoCompleteTextView txtRecipientUri;
	private EditText txtAmount;
	private EditText txtComments;
	private ContactList contactList;
	private Button btnSendMoney;
	private CustomLockView ctrlSecurityPin;
	private String passcode = "";
	private CustomLockView ctrlSetupSecurityPin;
	private String setupPasscode = "";
	private CustomLockView ctrlConfirmSecurityPin;
	private String confirmPasscode = "";
	private String nameOnAccount = "";
	private String routingNumber = "";
	private String accountNumber = "";
	private String confirmAccountNumber = "";
	private String accountType = "Checking";
	private String password = "";

	final private int SUBMITPAYMENT_DIALOG = 0;
	final private int NORECIPIENTSPECIFIED_DIALOG = 1;
	final private int NOAMOUNTSPECIFIED_DIALOG = 2;
	final private int SUBMITPAYMENTFAILED_DIALOG = 3;
	final private int SUBMITPAYMENTSUCCESS_DIALOG = 4;
	final private int SETUPSECURITYPIN_DIALOG = 5;
	final private int CONFIRMSECURITYPIN_DIALOG = 6;
	final private int REGISTERUSER_DIALOG = 7;
	final private int VERIFYMOBILEDEVICE_DIALOG = 8;
	final private int INVALIDMOBILEDEVICE_DIALOG = 9;
	final private int SETUPACHACCOUNT_DIALOG = 10;
	final private int ACHACCOUNTSETUPFAILED_DIALOG = 11;
	final private int INVALIDPASSCODELENGTH_DIALOG = 12;
	final private int SETUPPASSWORD_DIALOG = 13;
	final private int SETUPPASSWORDFAILED_DIALOG = 14;
	final private int SETUPSECURITYPINFAILED_DIALOG = 15;
	final private int REGISTERUSERFAILED_DIALOG = 16;
	final private int SETUPACHACCOUNTFAILED_DIALOG = 17;
	final private int MISMATCHSECURITYPIN_DIALOG = 18;

	final private int SUBMITPAYMENT_ACTION = 0;
	final private int SETUPSECURITYPIN_ACTION = 1;
	final private int CONFIRMSECURITYPIN_ACTION = 2;
	final private int REGISTERUSER_ACTION = 3;
	final private int VERIFYMOBILEDEVICE_ACTION = 4;
	final private int SETUPACHACCOUNT_ACTION = 5;
	final private int SETUPPASSWORD_ACTION = 6;

	private SharedPreferences prefs;
	private UserRegistrationResponse userRegistrationResponse;
	private UserSetupSecurityPinResponse userSetupSecurityPinResponse;
	private SubmitPaymentResponse submitPaymentResponse;
	private UserVerifyMobileDeviceResponse userVerifyMobileDeviceResponse;
	private ACHAccountSetupResponse achAccountSetupResponse;
	private UserSetupPasswordResponse userSetupPasswordResponse;

	private Dialog dialog = null;
	private Handler dialogHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			dialog.dismiss();

			switch (msg.what) {
			case SUBMITPAYMENT_ACTION:
				removeDialog(SUBMITPAYMENT_DIALOG);

				if (submitPaymentResponse != null
						&& submitPaymentResponse.Success) {
					showDialog(SUBMITPAYMENTSUCCESS_DIALOG);
					launchSendMoneyView();
				} else if (submitPaymentResponse != null) {
					errorMessage = submitPaymentResponse.Message;
					showDialog(SUBMITPAYMENTFAILED_DIALOG);
				} else {
					showDialog(SUBMITPAYMENTFAILED_DIALOG);
				}
				break;
			case SETUPSECURITYPIN_ACTION:
				launchConfirmSecurityPin();
				break;

			case CONFIRMSECURITYPIN_ACTION:
				if (userSetupSecurityPinResponse.Success) {
					Editor editor = prefs.edit();
					editor.putBoolean("setupSecurityPin", true);
					editor.commit();

					launchACHAccountSetupActivity();
				} else {
					showDialog(SETUPSECURITYPINFAILED_DIALOG);
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
					showDialog(REGISTERUSERFAILED_DIALOG);
				}
				break;
			case VERIFYMOBILEDEVICE_ACTION:

				if (userVerifyMobileDeviceResponse != null
						&& userVerifyMobileDeviceResponse.Success) {
					launchSetupSecurityPin();
				} else {
					showDialog(VERIFYMOBILEDEVICE_ACTION);
				}
				break;
			case SETUPACHACCOUNT_ACTION:

				if (achAccountSetupResponse != null
						&& achAccountSetupResponse.Success) {
					Editor editor = prefs.edit();
					editor.putString("paymentAccountId",
							achAccountSetupResponse.PaymentAccountId);
					editor.commit();

					launchPaymentConfirmationActivity();
				} else {
					showDialog(SETUPACHACCOUNTFAILED_DIALOG);
				}
				break;
			case SETUPPASSWORD_ACTION:
				removeDialog(SETUPPASSWORD_DIALOG);

				if (userSetupPasswordResponse != null
						&& userSetupPasswordResponse.Success) {
					Editor editor = prefs.edit();
					editor.putBoolean("setupPassword", true);
					editor.commit();

					launchPaymentConfirmationActivity();
				} else {
					showDialog(SUBMITPAYMENTFAILED_DIALOG);
				}
				break;
			}

		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "Activity State: onCreate()");

		super.onCreate(savedInstanceState);
		setTitle("Send Money");

		deviceId = Secure.getString(getBaseContext().getContentResolver(),
				Secure.ANDROID_ID);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		userId = prefs.getString("userId", "");
		mobileNumber = prefs.getString("mobileNumber", "");
		contactList = new ContactList(getBaseContext());

		launchSendMoneyView();

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

		Editor editor = prefs.edit();

		switch (item.getItemId()) {
		case R.id.signOutMenuItem:
			editor.remove("userId");
			editor.commit();

			intent = new Intent(MakePaymentActivity.this, SignInActivity.class);
			startActivity(intent);

			break;
		case R.id.forgetMeMenuItem:

			editor.clear();
			editor.commit();

			intent = new Intent(MakePaymentActivity.this,
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
		case SUBMITPAYMENT_DIALOG:
			progressDialog = new ProgressDialog(this);

			progressDialog.setMessage("Submitting Request...");
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

			thread = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						SubmitPaymentRequest();

					} catch (Exception e) {
						e.printStackTrace();
					}
					dialogHandler.sendEmptyMessage(SUBMITPAYMENT_ACTION);
				}

			});
			dialog = progressDialog;
			thread.start();

			return dialog;
		case SUBMITPAYMENTFAILED_DIALOG:
			alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.setTitle("Failed");

			alertDialog.setMessage(errorMessage);
			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();

				}
			});

			return alertDialog;
		case SUBMITPAYMENTSUCCESS_DIALOG:
			alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.setTitle("Payment Sumitted");
			NumberFormat nf = NumberFormat.getCurrencyInstance();

			alertDialog.setMessage(String.format(
					"Your payment for %s was sent to %s.", nf.format(amount),
					recipientUri));

			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();

					txtRecipientUri.setText("");
					txtRecipientUri.requestFocus();
					txtAmount.setText("$0.00");
					txtComments.setText("");
				}
			});
			return alertDialog;
		case NORECIPIENTSPECIFIED_DIALOG:
			alertDialog = new AlertDialog.Builder(MakePaymentActivity.this)
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
			alertDialog = new AlertDialog.Builder(MakePaymentActivity.this)
					.create();
			alertDialog.setTitle("Please Specify an Amount");
			alertDialog.setMessage("You must specify the amount to send.");

			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});

			return alertDialog;
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
			alertDialog = new AlertDialog.Builder(MakePaymentActivity.this)
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
		case INVALIDPASSCODELENGTH_DIALOG:
			alertDialog = new AlertDialog.Builder(MakePaymentActivity.this)
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
		case SETUPPASSWORD_DIALOG:
			progressDialog = new ProgressDialog(this);

			progressDialog.setMessage("Setting up Your Password...");
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

			thread = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						SetupPassword();

					} catch (Exception e) {
						e.printStackTrace();
					}
					dialogHandler.sendEmptyMessage(SETUPPASSWORD_ACTION);
				}

			});
			dialog = progressDialog;
			thread.start();

			return dialog;
		case MISMATCHSECURITYPIN_DIALOG:
			alertDialog = new AlertDialog.Builder(MakePaymentActivity.this)
					.create();
			alertDialog.setTitle("Security Pins Mismatch");
			alertDialog
					.setMessage("Your confirmation swipe does not match the security pin you setup. Please try again.");

			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});

			return alertDialog;
		}

		return null;
	}

	protected void launchACHAccountSetupActivity() {
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
				confirmAccountNumber = txtConfirmAccountNumber.getText()
						.toString();

				showDialog(SETUPACHACCOUNT_DIALOG);

			}
		});
	}

	protected void launchPaymentConfirmationActivity() {
		setContentView(R.layout.paymentconfirmation_controller);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		TextView txtRecipient = (TextView) findViewById(R.id.txtConfirmRecipient);
		txtRecipient.setText(recipientUri);

		TextView txtAmount = (TextView) findViewById(R.id.txtConfirmAmount);
		txtAmount.setText(amount.toString());

		TextView txtComments = (TextView) findViewById(R.id.txtConfirmComment);
		txtComments.setText(comments);

		final CustomLockView ctrlConfirmPaymentSecurityPin = (CustomLockView) findViewById(R.id.ctrlConfirmPaymentSecurityPin);
		ctrlConfirmPaymentSecurityPin.setOnTouchListener(new OnTouchListener() {

			// .setOnClickListener(new OnClickListener() {
			public boolean onTouch(View v, MotionEvent event) {

				passcode = ctrlConfirmPaymentSecurityPin.getPasscode();

				if (passcode.length() > 3)
					showDialog(SUBMITPAYMENT_DIALOG);
				else
					showDialog(INVALIDPASSCODELENGTH_DIALOG);
				return false;
			}

		});
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
				String confirmPasscode = ctrlConfirmSecurityPin.getPasscode();

				if (!confirmPasscode.equalsIgnoreCase(passcode)) {
					showDialog(MISMATCHSECURITYPIN_DIALOG);
					
					return false;
				}
				if (passcode.length() > 3)
					showDialog(CONFIRMSECURITYPIN_DIALOG);
				else
					showDialog(INVALIDPASSCODELENGTH_DIALOG);
				return false;
			}

		});

	}

	protected void launchSendMoneyView() {
		setContentView(R.layout.contactmanager);

		txtRecipientUri = (AutoCompleteTextView) findViewById(R.id.txtSendMoneyRecipient);
		txtAmount = (EditText) findViewById(R.id.txtAmount);
		txtComments = (EditText) findViewById(R.id.txtComments);
		ctrlSecurityPin = (CustomLockView) findViewById(R.id.ctrlSecurityPin);

		btnSendMoney = (Button) findViewById(R.id.btnSubmitPaymentRequest);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.list_item, contactList.getContacts().toArray(
						new String[0]));
		txtRecipientUri.setAdapter(adapter);

		txtAmount.addTextChangedListener(new TextWatcher() {
			String current = "";

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (!s.toString().equals(current)) {
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
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}
		});

		ctrlSecurityPin.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				recipientUri = txtRecipientUri.getText().toString();
				amount = Double.parseDouble(txtAmount.getText().toString()
						.replace("$", ""));
				comments = txtComments.getText().toString();
				passcode = ctrlSecurityPin.getPasscode();

				if (passcode.length() > 3) {
					if (prefs.getBoolean("setupPassword", false) == true)
						showDialog(SUBMITPAYMENT_DIALOG);
					else
						launchSetupPasswordScreen();
				} else
					showDialog(INVALIDPASSCODELENGTH_DIALOG);

				return false;
			}

		});
		btnSendMoney.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				boolean isValid = true;

				recipientUri = txtRecipientUri.getText().toString();
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
						launchSetupSecurityPin();
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

	protected void launchSetupPasswordScreen() {
		setContentView(R.layout.setupassword_controller);

		final EditText txtUserName = (EditText) findViewById(R.id.txtSetupPasswordUserName);
		txtUserName.setText(mobileNumber);

		Button btn = (Button) findViewById(R.id.btnSubmitPassword);
		btn.setOnClickListener(new OnClickListener() {
			public void onClick(View argO) {
				final EditText txtPassword = (EditText) findViewById(R.id.txtSetupPasswordPassword);

				password = txtPassword.getText().toString();

				showDialog(SETUPPASSWORD_DIALOG);

			};
		});
	}

	protected void SubmitPaymentRequest() {

		Boolean isValid = true;

		if (isValid && recipientUri.length() == 0) {
			showDialog(NORECIPIENTSPECIFIED_DIALOG);
			isValid = false;
		}
		if (isValid && amount == (double) 0) {
			showDialog(NOAMOUNTSPECIFIED_DIALOG);
			isValid = false;
		}
		if (isValid) {

			if (!prefs.contains("userId")) {
				showDialog(REGISTERUSER_DIALOG);
			} else if (prefs.getString("paymentAccountId", "").length() == 0) {
				launchACHAccountSetupActivity();
			} else {

				userId = prefs.getString("userId", "");
				paymentAccountId = prefs.getString("paymentAccountId", "0");

				PaymentServices paymentServices = new PaymentServices();
				SubmitPaymentRequest submitPaymentRequest = new SubmitPaymentRequest();
				submitPaymentRequest.UserId = userId;
				submitPaymentRequest.SecurityPin = passcode;
				submitPaymentRequest.FromMobileNumber = mobileNumber;
				submitPaymentRequest.Recipient = recipientUri;
				submitPaymentRequest.Amount = amount;
				submitPaymentRequest.Comments = comments;
				submitPaymentRequest.PaymentAccountId = paymentAccountId;

				submitPaymentResponse = paymentServices
						.SubmitPayment(submitPaymentRequest);

			}
		}
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

	protected void setupSecurityPin() {
		UserSetupSecurityPinRequest userSetupSecurityPin = new UserSetupSecurityPinRequest();
		userSetupSecurityPin.UserId = prefs.getString("userId", "");
		userSetupSecurityPin.DeviceId = deviceId;
		userSetupSecurityPin.SecurityPin = passcode;

		userSetupSecurityPinResponse = userService
				.SetupSecurityPin(userSetupSecurityPin);
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
