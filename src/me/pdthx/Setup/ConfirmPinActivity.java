package me.pdthx.Setup;

import me.pdthx.Requests.UserSetupSecurityPinRequest;
import me.pdthx.BaseActivity;
import me.pdthx.R;
import me.pdthx.CustomViews.CustomLockView;
import me.pdthx.Requests.ACHAccountSetupRequest;
import me.pdthx.Responses.Response;
import me.pdthx.Services.UserService;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

public class ConfirmPinActivity extends BaseActivity {
	final private int USERDATA_FAILED = 2;
	final private int SETUPACHACCOUNT_FAILED = 3;
	final private int USERREGISTRATION_FAILED = 4;
	final private int USERSECURITYPIN_INVALIDLENGTH = 5;
	final private int USERSECURITYPIN_CONFIRMMISMATCH = 6;
	final private int USERREGISTRATION_PASSWORDMISMATCH = 7;
	final private int USERREGISTRATION_ACHNUMBERMISMATCH = 8;
	// final private int USERREGISTRATION_PHONENUMBERFORMATERROR = 9;

	private UserSetupSecurityPinRequest request;
	private ACHAccountSetupRequest achAccountSetupRequest;

	private Response response;
	// private EditText txtMobileNumber;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setup_security_dialog);

		TextView header = (TextView) findViewById(R.id.setupSecurityHeader);
		header.setText("Confirm Your Pin");
		TextView body = (TextView) findViewById(R.id.setupSecurityBody);
		body.setText("Re-enter your security pin to confirm your new pin code.");

		achAccountSetupRequest = getIntent().getExtras().getParcelable("achAccountObject");

		final CustomLockView ctrlSecurityPin = (CustomLockView) findViewById(R.id.ctrlSecurityPin);
		ctrlSecurityPin.invalidate();
		ctrlSecurityPin.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				String confirmPasscode = ctrlSecurityPin.getPasscode();

				if (confirmPasscode.length() > 3) {
					if (confirmPasscode.equals(achAccountSetupRequest.SecurityPin)) {
						request = new UserSetupSecurityPinRequest();
						request.SecurityPin = achAccountSetupRequest.SecurityPin;
						request.UserId = prefs.getString("userId", "");

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
											.setupSecurityPin(request);
								} catch (Exception e) {
									e.printStackTrace();
								}
								progressDialog.dismiss();

								if (response.Success) {
									Intent createQuestion = new Intent(getApplicationContext(), CreateQuestionActivity.class);

									createQuestion.putExtra("achAccountObject", achAccountSetupRequest);


									setResult(RESULT_OK);
									finish();
									startActivity(createQuestion);
								} else {
									signUpHandler
											.sendEmptyMessage(USERDATA_FAILED);
								}
							}

						});
						thread.start();

					} else {
						signUpHandler
								.sendEmptyMessage(USERSECURITYPIN_CONFIRMMISMATCH);
					}
				} else
					signUpHandler
							.sendEmptyMessage(USERSECURITYPIN_INVALIDLENGTH);

				return false;

			}
		});

	}

	Handler signUpHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case (USERDATA_FAILED):
				alertDialog.setTitle("Setup Failed");
				alertDialog
						.setMessage("There was an error sending the data.");
				alertDialog.setButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});

				alertDialog.show();
				break;
			case (SETUPACHACCOUNT_FAILED):
				alertDialog.setTitle("Setup Failed");
				alertDialog
						.setMessage("There was an error setting up your ACH account: "
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
			case (USERREGISTRATION_FAILED):
				alertDialog.setTitle("User Registration Failed.");
				alertDialog
						.setMessage("There was an error completing your registration. Please try again.");
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

			case (USERREGISTRATION_PASSWORDMISMATCH):
				alertDialog.setTitle("Passwords don't Match.");
				alertDialog
						.setMessage("The password you entered do not match. Please try again.");
				alertDialog.setButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});

				alertDialog.show();
				break;

			case (USERREGISTRATION_ACHNUMBERMISMATCH):
				alertDialog.setTitle("ACH Account Number Mismatch.");
				alertDialog
						.setMessage("The ACH account numbers you entered do not match. Please try again.");
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

	/*
	 * progressDialog.setMessage("Setting up ACH Account..."); progressDialog
	 * .setProgressStyle(ProgressDialog.STYLE_SPINNER); progressDialog.show();
	 *
	 * Thread thread = new Thread(new Runnable() {
	 *
	 * @Override public void run() { // TODO Auto-generated method stub try {
	 * response = UserService.setupACHAccount(request); } catch (Exception e) {
	 * e.printStackTrace(); } progressDialog.dismiss();
	 *
	 * if (response.Success) { Editor editor = prefs.edit();
	 *
	 * editor.putString("paymentAccountId", response.PaymentAccountId);
	 * editor.commit();
	 *
	 * setResult(RESULT_OK); finish();
	 *
	 * startActivity(new Intent( getApplicationContext(),
	 * CreatePinActivity.class)); } else { achSetupHandler
	 * .sendEmptyMessage(SETUPACHACCOUNT_FAILED); } }
	 *
	 * }); thread.start();
	 */
}
