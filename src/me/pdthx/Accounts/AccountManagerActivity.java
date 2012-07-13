package me.pdthx.Accounts;

import java.util.ArrayList;

import me.pdthx.BaseActivity;
import me.pdthx.R;
import me.pdthx.CustomViews.CustomLockView;
import me.pdthx.Requests.ACHAccountDeleteRequest;
import me.pdthx.Requests.ACHAccountDetailRequest;
import me.pdthx.Requests.ACHAccountUpdateRequest;
import me.pdthx.Responses.ACHAccountResponse;
import me.pdthx.Responses.Response;
import me.pdthx.Responses.UserSignInResponse;
import me.pdthx.Services.PaymentAcctService;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class AccountManagerActivity extends BaseActivity implements
		OnCheckedChangeListener {

	Button editAcct;
	Button addAcct;
	Button backAcctBtn;
	RadioGroup btnAcctType;
	Spinner spinnerReceiveAcct;
	Spinner spinnerSendAcct;

	private String preferredSend;
	private String preferredReceive;

	private boolean isCheckingAcct;
	private Response response;
	private ACHAccountResponse updateResponse;
	private Response sendResponse;
	private Response receiveResponse;
	private ACHAccountUpdateRequest updateRequest;
	private ACHAccountDeleteRequest deleteRequest;
	private UserSignInResponse userInfo;

	final private int USERREGISTRATION_ACHNUMBERMISMATCH = 15;
	final private int INVALID_PINCODE_LENGTH = 13;
	final private int USERDATA_FAILED = 10;
	final private int ACCOUNT_REG_SUCCESS = 1;
	final private int ACCOUNT_DEL_SUCCESS = 3;
	final private int ACCT_UPDATE_SUCCESS = 5;
	final private int USERSECURITYPIN_INVALIDLENGTH = 11;
	final private int USERSECURITYPIN_CONFIRMMISMATCH = 6;
	final private int UPDATEACCTMNG_SUCCESS = 18;
	final private int UPDATEACCTMNG_FAILED = 4;

	private String passcode = "";
	private int clickedBankId;
	private ArrayList<ACHAccountResponse> listofBanks;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		userInfo = new UserSignInResponse();
		userInfo.UserId = prefs.getString("userId", "");
		userInfo.PaymentAccountId = prefs.getString("paymentAccountId", "");
		showAccountScreen();
	}

	Handler achSetupHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			/*
			 * case (SETUPACHACCOUNT_FAILED): alertDialog = new
			 * AlertDialog.Builder( AccountManagerActivity.this).create();
			 * alertDialog.setTitle("Setup Failed"); alertDialog
			 * .setMessage("There was an error setting up your ACH account: " +
			 * response.ReasonPhrase + " Please try again.");
			 * alertDialog.setButton("OK", new DialogInterface.OnClickListener()
			 * { public void onClick(DialogInterface dialog, int which) {
			 * dialog.dismiss(); } });
			 *
			 * alertDialog.show(); break;
			 */
			case (USERREGISTRATION_ACHNUMBERMISMATCH):
				alertDialog = new AlertDialog.Builder(
						AccountManagerActivity.this).create();
				alertDialog.setTitle("ACH Account Number Mismatch.");
				alertDialog
						.setMessage("The ACH account numbers you entered must match and not be empty. Please try again.");
				alertDialog.setButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});

				alertDialog.show();
				break;

			case (INVALID_PINCODE_LENGTH):
				alertDialog = new AlertDialog.Builder(
						AccountManagerActivity.this).create();
				alertDialog.setTitle("Invalid pincode.");
				alertDialog
						.setMessage("Your pincode consists of 3 pins or greater. Please try again.");
				alertDialog.setButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});

				alertDialog.show();
				break;

			case (UPDATEACCTMNG_SUCCESS):
				alertDialog = new AlertDialog.Builder(
						AccountManagerActivity.this).create();
				alertDialog.setTitle("Updated Accounts.");
				alertDialog
						.setMessage("Your account settings have been updated.");
				alertDialog.setButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								finish();
							}
						});

				alertDialog.show();
				break;

			case (UPDATEACCTMNG_FAILED):
				alertDialog = new AlertDialog.Builder(
						AccountManagerActivity.this).create();
				alertDialog.setTitle("Failed to update.");
				alertDialog
						.setMessage("A problem occurred with updating your account settings. Please try again.");
				alertDialog.setButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								finish();
							}
						});

				alertDialog.show();
				break;

			case (USERSECURITYPIN_CONFIRMMISMATCH):
				alertDialog = new AlertDialog.Builder(
						AccountManagerActivity.this).create();
				alertDialog.setTitle("Security Pins Mismatch.");
				alertDialog
						.setMessage("The two security pins you just swiped don't match. Please try again.");
				alertDialog.setButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});

				alertDialog.show();
				break;

			case (ACCT_UPDATE_SUCCESS):
				alertDialog = new AlertDialog.Builder(
						AccountManagerActivity.this).create();
				alertDialog.setTitle("Account updated.");
				alertDialog.setMessage("Your account was successfully updated.");
				alertDialog.setButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								runOnUiThread(new Runnable() {
									public void run() {
										showAccountScreen();
									}
								});
							}
						});

				alertDialog.show();
				break;

			case (ACCOUNT_REG_SUCCESS):
				alertDialog = new AlertDialog.Builder(
						AccountManagerActivity.this).create();
				alertDialog.setTitle("Added new account.");
				alertDialog.setMessage("Your new account was created.");
				alertDialog.setButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								finish();
							}
						});

				alertDialog.show();
				break;

			case (ACCOUNT_DEL_SUCCESS):
				alertDialog = new AlertDialog.Builder(
						AccountManagerActivity.this).create();
				alertDialog.setTitle("Deleted your account.");
				alertDialog.setMessage("Your account was deleted.");
				alertDialog.setButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								runOnUiThread(new Runnable() {
									public void run() {
										showAccountScreen();
									}
								});
							}
						});

				alertDialog.show();
				break;

			case (USERDATA_FAILED):
				alertDialog = new AlertDialog.Builder(
						AccountManagerActivity.this).create();
				alertDialog.setTitle("Account data failed.");
				alertDialog
						.setMessage("There was an error with your data. Please try again.");
				alertDialog.setButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});

				alertDialog.show();
				break;

			case (USERSECURITYPIN_INVALIDLENGTH):
				alertDialog = new AlertDialog.Builder(
						AccountManagerActivity.this).create();
				alertDialog.setTitle("Invalid Length");
				alertDialog
						.setMessage("Your pincode must consist of a code of at least 3 inputs.");
				alertDialog.setButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});

				alertDialog.show();
				break;

			// case(USERREGISTRATION_PHONENUMBERFORMATERROR):
			// alertDialog = new AlertDialog.Builder(parent)
			// .create();
			// alertDialog.setTitle("Phone Number Format error.");
			// alertDialog
			// .setMessage("Phone number does not have 10 digits. Please try again.");
			// alertDialog.setButton("OK", new DialogInterface.OnClickListener()
			// {
			// public void onClick(DialogInterface dialog, int which) {
			// dialog.dismiss();
			// }
			// });
			//
			// alertDialog.show();
			// break;
			}

		}

	};

	public void showEditAccount() {
		setContentView(R.layout.achaccountsetup_controller);
		TextView title = (TextView) findViewById(R.id.txtACHTitle);
		title.setText("Edit Account");

		final ACHAccountResponse bankInfo = listofBanks.get(clickedBankId);

		// txtNameOnAccount
		final TextView nicknameonAccount = (TextView) findViewById(R.id.txtNicknameonAcct);
		if (!bankInfo.Nickname.equals("null")) {
			nicknameonAccount.setHint(bankInfo.Nickname);
		}
		final TextView nameonAccount = (TextView) findViewById(R.id.txtNameOnAccount);
		nameonAccount.setHint(bankInfo.NameOnAccount);
		// txtRoutingNumber
		final TextView routingNumber = (TextView) findViewById(R.id.txtRoutingNumber);
		routingNumber.setHint(bankInfo.RoutingNumber);
		// txtAccountNumber
		TextView accountNumber = (TextView) findViewById(R.id.txtAccountNumber);
		accountNumber.setHint("******" + bankInfo.AccountNumber);
		accountNumber.setEnabled(false);
		TextView confirm = (TextView) findViewById(R.id.txtConfirmAccountNumber);
		confirm.setVisibility(View.GONE);

		btnAcctType = (RadioGroup) findViewById(R.id.achBankCategories);
		btnAcctType.setOnCheckedChangeListener(this);
		if (bankInfo.AccountType.equals("Checking")) {
			RadioButton checkings = (RadioButton) findViewById(R.id.achCheckings);
			RadioButton savings = (RadioButton) findViewById(R.id.achSavings);
			checkings.setChecked(true);
			savings.setChecked(false);
		} else {
			RadioButton checkings = (RadioButton) findViewById(R.id.achCheckings);
			RadioButton savings = (RadioButton) findViewById(R.id.achSavings);
			checkings.setChecked(false);
			savings.setChecked(true);
		}

		Button remindLater = (Button) findViewById(R.id.btnRemindMeLater);
		remindLater.setVisibility(View.GONE);

		Button removeButton = (Button) findViewById(R.id.btnremoveACHAcct);
		removeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				deleteRequest = new ACHAccountDeleteRequest();
				deleteRequest.UserId = prefs.getString("userId", "");
				deleteRequest.BankId = bankInfo.BankId;

				// send pincode to server
				progressDialog.setMessage("Deleting Account...");
				progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				progressDialog.show();

				Thread thread = new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							response = PaymentAcctService
									.deleteAccount(deleteRequest);
						} catch (Exception e) {
							e.printStackTrace();
						}
						progressDialog.dismiss();

						if (response.Success) {
							setResult(RESULT_OK);
							achSetupHandler
									.sendEmptyMessage(ACCOUNT_DEL_SUCCESS);
						} else {
							achSetupHandler.sendEmptyMessage(USERDATA_FAILED);
						}
					}

				});
				thread.start();
			}

		});

		Button updateButton = (Button) findViewById(R.id.btnSubmitACHAccount);
		updateButton.setText("Update Account");
		updateButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				updateRequest = new ACHAccountUpdateRequest();
				if (nameonAccount.getText().length() > 0) {
					updateRequest.NameOnAccount = nameonAccount.getText()
							.toString().trim();
				} else {
					updateRequest.NameOnAccount = bankInfo.NameOnAccount;
				}
				if (routingNumber.getText().length() > 0) {
					updateRequest.RoutingNumber = routingNumber.getText()
							.toString().trim();
				} else {
					updateRequest.RoutingNumber = bankInfo.RoutingNumber;
				}

				if (nicknameonAccount.getText().length() > 0) {
					updateRequest.Nickname = nicknameonAccount.getText()
							.toString().trim();
				} else {
					updateRequest.Nickname = bankInfo.Nickname;
				}
				if (isCheckingAcct) {
					updateRequest.AccountType = "Checking";

				} else {

					updateRequest.AccountType = "Savings";
				}
				updateRequest.UserId = prefs.getString("userId", "");
				updateRequest.BankId = bankInfo.BankId;

				// send pincode to server
				progressDialog.setMessage("Updating Account...");
				progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				progressDialog.show();

				Thread thread = new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							updateResponse = PaymentAcctService
									.updateAccount(updateRequest);
						} catch (Exception e) {
							e.printStackTrace();
						}
						progressDialog.dismiss();

						if (updateResponse.Success) {
							setResult(RESULT_OK);
							achSetupHandler
									.sendEmptyMessage(ACCT_UPDATE_SUCCESS);
						} else {
							achSetupHandler.sendEmptyMessage(USERDATA_FAILED);
						}
					}

				});
				thread.start();
			}
		});


		LinearLayout bottom = (LinearLayout) findViewById(R.id.stepsToCompleteLayout);
		bottom.setVisibility(View.GONE);
	}

	public void showAccountScreen() {
		setContentView(R.layout.account_controller);

		listofBanks = PaymentAcctService.getAccounts(userInfo);

		LinearLayout list = (LinearLayout) findViewById(R.id.bankIDs);
		if (listofBanks != null) {
			for (int i = 0; i < listofBanks.size(); i++) {
				final ACHAccountResponse bank = listofBanks.get(i);

				// add image view then text view
				ImageView img = new ImageView(this);
				img.setImageResource(R.drawable.amt_icon);
				img.setMaxHeight(60);
				img.setMaxWidth(60);
				img.setAdjustViewBounds(true);

				LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);

				layoutParams.setMargins(10, 0, 0, 0);
				TextView bankTitle = new TextView(this);
				bankTitle.setWidth(150);
				if (bank.Nickname.equals("null") || bank.Nickname.equals("")) {
					bankTitle.setText(bank.NameOnAccount + " - "
							+ bank.AccountType + " - " + bank.AccountNumber);
				} else {
					bankTitle.setText(bank.Nickname);
				}

				LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);

				layoutParams2.setMargins(20, 0, 0, 0);
				Button editRemove = new Button(this);
				editRemove.setBackgroundDrawable(null);
				editRemove.setTextColor(Color.parseColor("#2FA7C2"));
				editRemove.setText("Edit/Remove");
				editRemove.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						for (int j = 0; j < listofBanks.size(); j++) {
							if (bank.AccountNumber == listofBanks.get(j).AccountNumber) {
								clickedBankId = j;
								showEditAccount();
							}
						}
					}

				});

				LinearLayout layout = new LinearLayout(this);
				LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);
				layoutParams3.setMargins(0, 5, 0, 5);
				layout.setLayoutParams(layoutParams3);
				layout.setOrientation(LinearLayout.HORIZONTAL);
				layout.setGravity(Gravity.CENTER);

				layout.addView(img);
				layout.addView(bankTitle, layoutParams);
				layout.addView(editRemove, layoutParams2);

				list.addView(layout);
			}
		}

		addAcct = (Button) findViewById(R.id.bankAddAcct);
		editAcct = (Button) findViewById(R.id.bankUpdateAcct);

		addAcct.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				startActivity(new Intent(getApplicationContext(), LinkAccountActivity.class));
				finish();

			}

		});

		// create list of bank names
		if (listofBanks != null) {
			ArrayList<String> bankNames = new ArrayList<String>();
			for (int i = 0; i < listofBanks.size(); i++) {
				ACHAccountResponse rep = listofBanks.get(i);
				if (rep.Nickname.equals("null") || rep.Nickname.equals("")) {
					bankNames.add(rep.NameOnAccount + " - " + rep.AccountType
							+ " - " + rep.AccountNumber);
				} else {
					bankNames.add(rep.Nickname);
				}
			}
			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
					getApplicationContext(),
					android.R.layout.simple_spinner_item, bankNames);
			dataAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

			spinnerSendAcct = (Spinner) findViewById(R.id.accountDefaultSendAcct);
			spinnerSendAcct.setAdapter(dataAdapter);

			ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(
					getApplicationContext(),
					android.R.layout.simple_spinner_item, bankNames);
			dataAdapter2
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinnerReceiveAcct = (Spinner) findViewById(R.id.accountDefaultReceiveAcct);
			spinnerReceiveAcct.setAdapter(dataAdapter2);
		}

		editAcct.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (listofBanks.size() > 0) {
					String refSend = String.valueOf(spinnerSendAcct
							.getSelectedItem());
					String refReceive = String.valueOf(spinnerReceiveAcct
							.getSelectedItem());
					for (int k = 0; k < listofBanks.size(); k++) {
						String ref = listofBanks.get(k).NameOnAccount + " - "
								+ listofBanks.get(k).AccountType + " - "
								+ listofBanks.get(k).AccountNumber;
						if (refSend.equals(ref)
								|| refSend.equals(listofBanks.get(k).Nickname)) {
							preferredSend = listofBanks.get(k).BankId;
						}
						if (refReceive.equals(ref)
								|| refReceive.equals(listofBanks.get(k).Nickname)) {
							preferredReceive = listofBanks.get(k).BankId;
						}
					}
					confirmPinUpdateAccts();
				} else {
					finish();
				}
			}

		});
	}

	public void confirmPinUpdateAccts() {
		setContentView(R.layout.setup_security_dialog);

		TextView header = (TextView) findViewById(R.id.setupSecurityHeader);
		header.setText("Confirm Your Pin");
		TextView body = (TextView) findViewById(R.id.setupSecurityBody);
		body.setText("Re-enter your security pin in order to continue account creation.");

		final CustomLockView ctrlSecurityPin = (CustomLockView) findViewById(R.id.ctrlSecurityPin);
		ctrlSecurityPin.invalidate();
		ctrlSecurityPin.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				String confirmPasscode = ctrlSecurityPin.getPasscode();

				if (confirmPasscode.length() > 3) {
					passcode = confirmPasscode;

					final ACHAccountDetailRequest requestPreferredSend = new ACHAccountDetailRequest();
					final ACHAccountDetailRequest requestPreferredReceive = new ACHAccountDetailRequest();

					requestPreferredSend.UserId = prefs.getString("userId", "");
					requestPreferredSend.AccountId = preferredSend;
					requestPreferredSend.SecurityPin = passcode;
					requestPreferredReceive.UserId = prefs.getString("userId",
							"");
					requestPreferredReceive.AccountId = preferredReceive;
					requestPreferredReceive.SecurityPin = passcode;

					progressDialog.setMessage("Sending Info...");
					progressDialog
							.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					progressDialog.show();

					Thread thread = new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							try {
								sendResponse = PaymentAcctService
										.updatePreferredSendAcct(requestPreferredSend);
								receiveResponse = PaymentAcctService
										.updatePreferredReceiveAcct(requestPreferredReceive);
							} catch (Exception e) {
								e.printStackTrace();
							}
							progressDialog.dismiss();

							if (sendResponse.Success && receiveResponse.Success) {
								achSetupHandler
										.sendEmptyMessage(UPDATEACCTMNG_SUCCESS);
							} else {
								achSetupHandler
										.sendEmptyMessage(UPDATEACCTMNG_FAILED);
							}
						}

					});
					thread.start();

				} else {
					achSetupHandler
							.sendEmptyMessage(USERSECURITYPIN_INVALIDLENGTH);
				}
				return false;
			}
		});

	}

	@Override
	public void onCheckedChanged(RadioGroup arg0, int arg1) {
		if (arg1 == R.id.achCheckings) {
			isCheckingAcct = true;
		}
		if (arg1 == R.id.achSavings) {
			isCheckingAcct = false;
		}
	}

	/*
	 * protected void SubmitPaymentRequest() {
	 *
	 * PaymentRequest paymentRequest = new PaymentRequest();
	 * paymentRequest.UserId = prefs.getString("userId", "");
	 * paymentRequest.SecurityPin = passcode; paymentRequest.SenderUri =
	 * prefs.getString("login", ""); paymentRequest.RecipientUri = recipientUri;
	 * paymentRequest.Amount = amount; paymentRequest.Comments = comments;
	 * paymentRequest.SenderAccountId = prefs.getString("paymentAccountId",
	 * "0");
	 *
	 * if (location != null) { paymentRequest.Latitude = location.getLatitude();
	 * paymentRequest.Longitude = location.getLongitude(); }
	 *
	 * paymentResponse = PaymentServices.sendMoney(paymentRequest);
	 *
	 * }
	 */
}