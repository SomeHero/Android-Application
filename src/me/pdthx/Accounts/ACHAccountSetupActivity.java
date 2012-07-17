package me.pdthx.Accounts;

import java.io.FileInputStream;
import me.pdthx.Requests.UserSetupSecurityPinRequest;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;
import me.pdthx.BaseActivity;
import me.pdthx.PhotoCaptureExample;
import me.pdthx.R;
import me.pdthx.CustomViews.CustomLockView;
import me.pdthx.Requests.ACHAccountDeleteRequest;
import me.pdthx.Requests.ACHAccountDetailRequest;
import me.pdthx.Requests.ACHAccountSetupRequest;
import me.pdthx.Requests.ACHAccountUpdateRequest;
import me.pdthx.Responses.ACHAccountResponse;
import me.pdthx.Responses.ACHAccountSetupResponse;
import me.pdthx.Responses.Response;
import me.pdthx.Responses.SecurityQuestionResponse;
import me.pdthx.Responses.UserSignInResponse;
import me.pdthx.Services.PaymentAcctService;
import me.pdthx.Services.UserService;
import me.pdthx.Widget.OnWheelChangedListener;
import me.pdthx.Widget.WheelView;
import me.pdthx.Widget.Adapters.ArrayWheelAdapter;

public class ACHAccountSetupActivity extends BaseActivity implements OnCheckedChangeListener{
	Button editAcct;
	Button addAcct;
	Button backAcctBtn;
	RadioGroup btnAcctType;
	Spinner spinnerReceiveAcct;
	Spinner spinnerSendAcct;

	private String nameOnAccount;
	private String routingNumber;
	private String accountNumber;
	private String accountType;
	private String questions[];
	private String passcodeCreation;
	private String nickname;

	private String preferredSend;
	private String preferredReceive;

	private int currentId;
	private boolean isCheckingAcct;
	private ACHAccountSetupRequest request;
	private ACHAccountSetupResponse setupResponse;
	private Response response;
	private ACHAccountResponse updateResponse;
	private Response sendResponse;
	private Response receiveResponse;
	private ACHAccountUpdateRequest updateRequest;
	private ACHAccountDeleteRequest deleteRequest;

	private String paymentAccountId;

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
	final private int CAMERA = 20;

	private String passcode = "";
	private int clickedBankId;
	private ArrayList<ACHAccountResponse> listofBanks;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		paymentAccountId = prefs.getString("paymentAccountId", "");
		showSetupACHController();
	}
	Handler achSetupHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			/*
			 * case (SETUPACHACCOUNT_FAILED): alertDialog = new
			 * AlertDialog.Builder( ACHAccountSetupActivity.this).create();
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
						ACHAccountSetupActivity.this).create();
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
						ACHAccountSetupActivity.this).create();
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
						ACHAccountSetupActivity.this).create();
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
						ACHAccountSetupActivity.this).create();
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
						ACHAccountSetupActivity.this).create();
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
						ACHAccountSetupActivity.this).create();
			alertDialog.setTitle("Account updated.");
			alertDialog.setMessage("Your account was successfully updated.");
			alertDialog.setButton("OK",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
						int which) {
					dialog.dismiss();
					runOnUiThread(new Runnable() {
						public void run() {
							finish();
						}
					});
				}
			});

			alertDialog.show();
			break;

			case (ACCOUNT_REG_SUCCESS):
				alertDialog = new AlertDialog.Builder(
						ACHAccountSetupActivity.this).create();
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
						ACHAccountSetupActivity.this).create();
			alertDialog.setTitle("Deleted your account.");
			alertDialog.setMessage("Your account was deleted.");
			alertDialog.setButton("OK",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
						int which) {
					dialog.dismiss();
					runOnUiThread(new Runnable() {
						public void run() {
							finish();
						}
					});
				}
			});

			alertDialog.show();
			break;

			case (USERDATA_FAILED):
				alertDialog = new AlertDialog.Builder(
						ACHAccountSetupActivity.this).create();
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
						ACHAccountSetupActivity.this).create();
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

	private void showSetupACHController() {

		setContentView(R.layout.achaccountsetup_controller);
		TextView title = (TextView) findViewById(R.id.txtACHTitle);
		title.setText("Add Account");

		btnAcctType = (RadioGroup) findViewById(R.id.achBankCategories);
		btnAcctType.setOnCheckedChangeListener(this);

		Button remindLater = (Button) findViewById(R.id.btnRemindMeLater);
		remindLater.setVisibility(View.GONE);
		Button removeButton = (Button) findViewById(R.id.btnremoveACHAcct);
		removeButton.setVisibility(View.GONE);
		LinearLayout bottom = (LinearLayout) findViewById(R.id.stepsToCompleteLayout);
		bottom.setVisibility(View.GONE);

		Button addAccount = (Button) findViewById(R.id.btnSubmitACHAccount);
		addAccount.setOnClickListener(new OnClickListener() {
			public void onClick(View argO) {
				EditText txtNameOnAccount = (EditText) findViewById(R.id.txtNameOnAccount);
				EditText txtRoutingNumber = (EditText) findViewById(R.id.txtRoutingNumber);
				EditText txtAccountNumber = (EditText) findViewById(R.id.txtAccountNumber);
				EditText txtNickname = (EditText) findViewById(R.id.txtNicknameonAcct);

				if (txtAccountNumber
						.getText()
						.toString()
						.trim()
						.equals(((EditText) findViewById(R.id.txtConfirmAccountNumber))
								.getText().toString().trim())
								&& (txtAccountNumber.getText().toString().length() != 0)) {

					nameOnAccount = txtNameOnAccount.getText().toString()
							.trim();
					routingNumber = txtRoutingNumber.getText().toString()
							.trim();
					accountNumber = txtAccountNumber.getText().toString()
							.trim();
					nickname = txtNickname.getText().toString().trim();
					if (isCheckingAcct) {
						accountType = "Checking";
					} else {
						accountType = "Savings";
					}

					if (paymentAccountId.length() != 0
							|| (listofBanks != null && listofBanks.size() > 0)) {
						// user has security pin
						// 1.) confirm pin
						// 2.) confirm question
						confirmPin();
					} else {
						// user does not have security pin
						// 1.) show setup pin creation screen
						// 2.) show create question screen
						confirmCreation();
					}
				} else {
					achSetupHandler
					.sendEmptyMessage(USERREGISTRATION_ACHNUMBERMISMATCH);
				}
			}
		});

		LinearLayout btnCheckImage = (LinearLayout)findViewById(R.id.takePhotoBtn);
		btnCheckImage.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(getApplicationContext(), PhotoCaptureExample.class), CAMERA);

			}

		});

	}

	public void confirmCreation() {
		setContentView(R.layout.setup_security_controller);

		Button btnSetupSecurityPin = (Button) findViewById(R.id.btnSetupSecurityPin);
		btnSetupSecurityPin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				createSecurityPin();
			}

		});
	}

	public void createSecurityPin() {
		setContentView(R.layout.setup_security_dialog);

		final CustomLockView ctrlSecurityPin = (CustomLockView) findViewById(R.id.ctrlSecurityPin);
		ctrlSecurityPin.invalidate();
		ctrlSecurityPin.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				passcode = ctrlSecurityPin.getPasscode();

				if (passcode.length() > 3) {
					confirmPin();
				} else
					achSetupHandler
					.sendEmptyMessage(USERSECURITYPIN_INVALIDLENGTH);

				return false;

			}
		});
	}

	public void confirmPin() {
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
					if (paymentAccountId.length() != 0
							|| (listofBanks != null && listofBanks.size() > 0)) {
						passcode = confirmPasscode;
						createQuestion();
					} else {

						if (confirmPasscode.equals(passcode)) {
							final UserSetupSecurityPinRequest setupSecurityPin = new UserSetupSecurityPinRequest();
							setupSecurityPin.SecurityPin = passcode;
							setupSecurityPin.UserId = prefs.getString("userId",
									"");

							// send pincode to server
							progressDialog.setMessage("Sending Info...");
							progressDialog
							.setProgressStyle(ProgressDialog.STYLE_SPINNER);
							progressDialog.show();

							Thread thread = new Thread(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									try {
										response = UserService
												.setupSecurityPin(setupSecurityPin);
									} catch (Exception e) {
										e.printStackTrace();
									}
									progressDialog.dismiss();

									if (response.Success) {
										runOnUiThread(new Runnable() {
											public void run() {

												createQuestion();

											}
										});
									} else {
										achSetupHandler
										.sendEmptyMessage(USERDATA_FAILED);
									}
								}

							});
							thread.start();

						} else {
							achSetupHandler
							.sendEmptyMessage(USERSECURITYPIN_CONFIRMMISMATCH);
						}
					}
				} else
					achSetupHandler
					.sendEmptyMessage(USERSECURITYPIN_INVALIDLENGTH);

				return false;

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

	public void createQuestion() {
		setContentView(R.layout.create_security_question);

		if (paymentAccountId.length() != 0
				|| (listofBanks != null && listofBanks.size() > 0)) {
			TextView body = (TextView) findViewById(R.id.securityQuestionBody);
			body.setText("Choose your previously created security question and answer it in order to confirm your new account creation.");
		}

		final TextView txtAnswer = (TextView) findViewById(R.id.security_question_selected);

		final WheelView list = (WheelView) findViewById(R.id.security_question_list);

		int curr = 1;
		final ArrayList<SecurityQuestionResponse> securityQuestions = UserService
				.getSecurityQuestions();
		questions = new String[securityQuestions.size()];
		for (int i = 0; i < securityQuestions.size(); i++) {
			questions[i] = securityQuestions.get(i).Question;
		}
		ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<String>(this,
				questions);
		adapter.setTextSize(14);
		list.setViewAdapter(adapter);
		list.setCurrentItem(curr);

		list.addChangingListener(new OnWheelChangedListener() {

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				txtAnswer.setText(questions[newValue]);
				currentId = newValue;
			}

		});

		Button confirmBtn = (Button) findViewById(R.id.securityquestion_confirmbtn);
		confirmBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				EditText userAnswer = (EditText) findViewById(R.id.setupSecurityTxtAnswer);
				String answer = userAnswer.getText().toString().trim();

				request = new ACHAccountSetupRequest();
				request.AccountNumber = accountNumber;
				request.AccountType = accountType;
				request.NameOnAccount = nameOnAccount;
				request.RoutingNumber = routingNumber;
				request.SecurityPin = passcode;
				request.SecurityAnswer = answer;
				request.SecurityQuestionId = currentId;
				request.Nickname = nickname;
				request.UserId = prefs.getString("userId", "");

				progressDialog.setMessage("Sending Info...");
				progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				progressDialog.show();

				Thread thread = new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							setupResponse = UserService
									.setupACHAccount(request);
						} catch (Exception e) {
							e.printStackTrace();
						}
						progressDialog.dismiss();

						if (setupResponse.Success) {
							if (paymentAccountId.length() != 0
									|| (listofBanks != null && listofBanks
									.size() > 0)) {
								runOnUiThread(new Runnable() {
									public void run() {
										Editor editor = prefs.edit();
										editor.remove("paymentAccountId");
										editor.putString("paymentAccountId",
												setupResponse.PaymentAccountId);
										editor.commit();
									}
								});
							}
							achSetupHandler
							.sendEmptyMessage(ACCOUNT_REG_SUCCESS);
						} else {
							achSetupHandler.sendEmptyMessage(USERDATA_FAILED);
						}
					}

				});
				thread.start();
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

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if(requestCode == CAMERA)
			{
				try{
					String path = (String) data.getExtras().get("index");
					FileInputStream in = new FileInputStream(path);

					Bitmap thumbnail = null;

					//					cameraImage.setImageResource(R.drawable.bg_pop3);
					//					thumbnail = BitmapFactory.decodeStream(in);
					//					cameraImage.setImageBitmap(thumbnail);
					in.close();
				}
				catch (Exception e)
				{
					Log.d("Error", e.getMessage());
				}
			}
		}
		else {
			if (requestCode != CAMERA) {
				finish();
			}
		}
	}


}
