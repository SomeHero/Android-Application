package me.pdthx.Accounts;

import me.pdthx.BaseActivity;
import me.pdthx.R;
import me.pdthx.CustomViews.CustomLockView;
import me.pdthx.Requests.ACHAccountSetupRequest;
import me.pdthx.Setup.CreateQuestionActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

public class EnterPinActivity extends BaseActivity {
	final private int USERDATA_FAILED = 2;
	final private int INVALID_LENGTH = 14;
	
	private ACHAccountSetupRequest request;
	// final private int USERREGISTRATION_PHONENUMBERFORMATERROR = 9;

	private Activity parent = null;

	// private EditText txtMobileNumber;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		parent = this;
		setContentView(R.layout.setup_security_dialog);

		request = getIntent().getExtras().getParcelable("achAccountObject");
		
		TextView header = (TextView) findViewById(R.id.setupSecurityHeader);
		header.setText("Enter your pin");
		TextView body = (TextView) findViewById(R.id.setupSecurityBody);
		body.setText("Enter your security pin to continue your new bank account creation.");
		
		final CustomLockView ctrlSecurityPin = (CustomLockView) findViewById(R.id.ctrlSecurityPin);
		ctrlSecurityPin.invalidate();
		ctrlSecurityPin.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				String confirmPasscode = ctrlSecurityPin.getPasscode();

				if (confirmPasscode.length() > 3) {
					Intent createQuestion = new Intent(getApplicationContext(), CreateQuestionActivity.class);
					request.SecurityPin = confirmPasscode;
					
					createQuestion.putExtra("achAccountObject", request);
					
					startActivity(createQuestion);
					finish();
				} else {
					signUpHandler.sendEmptyMessage(INVALID_LENGTH);
				}
				return false;
			}
		});

	}

	Handler signUpHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case (USERDATA_FAILED):
				alertDialog = new AlertDialog.Builder(parent).create();
				alertDialog.setTitle("Setup Failed");
				alertDialog.setMessage("There was an error sending the data.");
				alertDialog.setButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});

				alertDialog.show();
				break;

			case (INVALID_LENGTH):
				alertDialog = new AlertDialog.Builder(parent).create();
				alertDialog.setTitle("Invalid pin.");
				alertDialog
						.setMessage("Your security pin consists of 3 pins or more. Please try again.");
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
