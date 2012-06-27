package me.pdthx;

import android.text.Editable;
import android.text.TextWatcher;
import me.pdthx.Accounts.AccountManagerActivity;
import me.pdthx.CustomViews.CustomLockView;
import me.pdthx.Requests.UserChangeSecurityPinRequest;
import me.pdthx.Requests.UserMeCodeRequest;
import me.pdthx.Responses.Response;
import me.pdthx.Services.UserService;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ProfileSetupActivity extends BaseActivity {

	final private int SETUPSECURITYPIN = 50;

	final private int USERSECURITYPIN_COMPLETE = 1;
	final private int USERSECURITYPIN_FAILED = 2;
	final private int USERSECURITYPIN_INVALIDLENGTH = 5;
	final private int USERSECURITYPIN_CONFIRMMISMATCH = 6;

	final private int INVALID_DOLLAR = 0;
	final private int INVALID_MECODE = 3;
	final private int SUCCESS_MECODE = 4;

	private UserChangeSecurityPinRequest request;
	private Response response;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		showProfileSetup();

	}

	private Handler profileSetupHandler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {

			case (INVALID_MECODE):
				alertDialog.setTitle("Invalid MeCode");
				alertDialog
						.setMessage("There was a problem setting up your MeCode: "
								+ response.ReasonPhrase + ". Please try again.");
				alertDialog.setButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});

				alertDialog.show();
				break;

			case (SUCCESS_MECODE):
				alertDialog.setTitle("MeCode Success");
				alertDialog.setMessage("MeCode setup successful");
				alertDialog.setButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});

				alertDialog.show();
				break;

			case (INVALID_DOLLAR):
				alertDialog.setTitle("Invalid MeCode");
				alertDialog
						.setMessage("The meCode must begin with a '$'. Please try again.");
				alertDialog.setButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});

				alertDialog.show();
				break;

			case (USERSECURITYPIN_COMPLETE):
				alertDialog.setTitle("Password changed");
				alertDialog
						.setMessage("Your passcode was successfully changed.");
				alertDialog.setButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								showProfileSetup();
								dialog.dismiss();
							}
						});

				alertDialog.show();
				break;

			case (USERSECURITYPIN_FAILED):
				alertDialog.setTitle("Setup Failed");
				alertDialog
						.setMessage("There was an error setting up your security pin: "
								+ response.ReasonPhrase + " Please try again.");
				alertDialog.setButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});

				alertDialog.show();
				break;

			case (USERSECURITYPIN_CONFIRMMISMATCH):
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
			}
		}
	};

	public void showProfileSetup() {
		View view = View.inflate(ProfileSetupActivity.this,
				R.layout.setup_profile, null);
		setContentView(view);

		Button btnCreateProfile = (Button) findViewById(R.id.btnSaveChanges);

		btnCreateProfile.setOnClickListener(new OnClickListener() {
			public void onClick(View argO) {

				EditText txtMeCode = (EditText) findViewById(R.id.meCode);
				txtMeCode.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void afterTextChanged(Editable s)
                    {
                        if (s.toString().charAt(0) != '$')
                        {
                            s.insert(0, "$");
                        }

                    }

                    @Override
                    public void beforeTextChanged(
                        CharSequence s,
                        int start,
                        int count,
                        int after)
                    {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onTextChanged(
                        CharSequence s,
                        int start,
                        int before,
                        int count)
                    {
                        // TODO Auto-generated method stub

                    }

				});
				if (txtMeCode.getText().toString().charAt(0) != '$') {
					profileSetupHandler.sendEmptyMessage(INVALID_DOLLAR);
				} else {
					progressDialog.setMessage("Adding Me Code..");
					progressDialog
							.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					UserMeCodeRequest userMeCodeRequest = new UserMeCodeRequest(
							prefs.getString("userId", ""), txtMeCode.getText()
									.toString().trim());
					progressDialog.show();
					response = UserService.createMeCode(userMeCodeRequest);

					if (response.Success) {
						txtMeCode.setText("");
						progressDialog.dismiss();
						profileSetupHandler.sendEmptyMessage(SUCCESS_MECODE);
					} else {
						progressDialog.dismiss();
						profileSetupHandler.sendEmptyMessage(INVALID_MECODE);
					}
				}
			}
		});

		Button btnManageAccounts = (Button) findViewById(R.id.btnManageAccounts);
		btnManageAccounts.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(v.getContext(),
						AccountManagerActivity.class));
			}

		});

		Button btnChangeSecurityPin = (Button) findViewById(R.id.btnChangeSecurityPin);
		TextView txtViewPin = (TextView) findViewById(R.id.txtViewPin);
		TextView txtNotePin = (TextView) findViewById(R.id.txtNotePin);

		if (prefs.getBoolean("setupSecurityPin", false)) {

			btnChangeSecurityPin.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					request = new UserChangeSecurityPinRequest();
					response = new Response();
					changeSecurityPinCurrent();
				}

			});
		} else {

			txtViewPin.setText("Setup Security Pin");
			txtNotePin.setText("Click the button to setup your security pin.");
			btnChangeSecurityPin.setText("Setup Security Pin");
			btnChangeSecurityPin.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					startActivityForResult(new Intent(v.getContext(),
							SecurityPinSetupActivity.class), SETUPSECURITYPIN);
				}

			});
		}
	}

	private void changeSecurityPinCurrent() {
		final Dialog d = new Dialog(this, R.style.CustomDialogTheme);
		d.setContentView(R.layout.security_dialog);

		d.getWindow().setLayout(400, 600);
		d.show();

		TextView txtConfirmHeader = (TextView) d
				.findViewById(R.id.txtConfirmHeader);
		TextView txtConfirmBody = (TextView) d
				.findViewById(R.id.txtConfirmBody);

		txtConfirmHeader.setText("Current Pin");
		txtConfirmBody
				.setText("To change your security pin, input your current security pin below.");

		final CustomLockView ctrlSecurityPin = (CustomLockView) d
				.findViewById(R.id.ctrlSecurityPin);
		ctrlSecurityPin.invalidate();
		ctrlSecurityPin.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				String passcode = ctrlSecurityPin.getPasscode();

				if (passcode.length() > 3) {
					request.CurrentSecurityPin = passcode;
					d.dismiss();

					changeSecurityPinNew();

				} else
					profileSetupHandler
							.sendEmptyMessage(USERSECURITYPIN_INVALIDLENGTH);

				return false;

			}
		});

	}

	private void changeSecurityPinNew() {
		final Dialog d = new Dialog(this, R.style.CustomDialogTheme);
		d.setContentView(R.layout.security_dialog);

		d.getWindow().setLayout(400, 600);
		d.show();

		TextView txtConfirmHeader = (TextView) d
				.findViewById(R.id.txtConfirmHeader);
		TextView txtConfirmBody = (TextView) d
				.findViewById(R.id.txtConfirmBody);

		txtConfirmHeader.setText("New Security Pin");
		txtConfirmBody
				.setText("To change your security pin, input your new security pin below.");

		final CustomLockView ctrlSecurityPin = (CustomLockView) d
				.findViewById(R.id.ctrlSecurityPin);
		ctrlSecurityPin.invalidate();
		ctrlSecurityPin.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				String passcode = ctrlSecurityPin.getPasscode();

				if (passcode.length() > 3) {
					request.NewSecurityPin = passcode;
					d.dismiss();

					changeSecurityPinConfirmNew();

				} else
					profileSetupHandler
							.sendEmptyMessage(USERSECURITYPIN_INVALIDLENGTH);

				return false;

			}
		});
	}

	private void changeSecurityPinConfirmNew() {
		final Dialog d = new Dialog(this, R.style.CustomDialogTheme);
		d.setContentView(R.layout.security_dialog);

		d.getWindow().setLayout(400, 600);
		d.show();

		TextView txtConfirmHeader = (TextView) d
				.findViewById(R.id.txtConfirmHeader);
		TextView txtConfirmBody = (TextView) d
				.findViewById(R.id.txtConfirmBody);

		txtConfirmHeader.setText("Confirm Security Pin");
		txtConfirmBody.setText("Put in your new security pin to confirm.");

		final CustomLockView ctrlSecurityPin = (CustomLockView) d
				.findViewById(R.id.ctrlSecurityPin);
		ctrlSecurityPin.invalidate();
		ctrlSecurityPin.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				String passcode = ctrlSecurityPin.getPasscode();

				if (passcode.length() > 3
						&& passcode.equals(request.NewSecurityPin)) {
					d.dismiss();

					progressDialog = new ProgressDialog(v.getContext());
					// ProgressDialog.Builder progressDialog = new
					// ProgressDialog.Builder(parent);
					progressDialog
							.setMessage("Setting up your security pin...");
					progressDialog
							.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					progressDialog.show();

					Thread thread = new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							try {
								request.UserId = prefs.getString("userId", "");
								response = UserService
										.changeSecurityPin(request);
							} catch (Exception e) {
								e.printStackTrace();
							}
							progressDialog.dismiss();

							if (response.Success) {

								profileSetupHandler
										.sendEmptyMessage(USERSECURITYPIN_COMPLETE);
							} else {
								profileSetupHandler
										.sendEmptyMessage(USERSECURITYPIN_FAILED);
							}
						}

					});
					thread.start();

				} else {
					profileSetupHandler
							.sendEmptyMessage(USERSECURITYPIN_CONFIRMMISMATCH);
				}

				return false;
			}
		});
	}
}
