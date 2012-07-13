package me.pdthx.Setup;

import me.pdthx.BaseActivity;
import me.pdthx.R;
import me.pdthx.CustomViews.CustomLockView;
import me.pdthx.Requests.ACHAccountSetupRequest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;

public class CreatePinActivity extends BaseActivity {

	final private int USERSECURITYPIN_FAILED = 2;
	final private int USERSECURITYPIN_INVALIDLENGTH = 5;
	protected ACHAccountSetupRequest request;

	private Activity parent = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setup_security_controller);

		Button btnSetupSecurityPin = (Button)findViewById(R.id.btnSetupSecurityPin);
		btnSetupSecurityPin.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				showPinController();
			}

		});
	}

	public void showPinController()
	{
		setContentView(R.layout.setup_security_dialog);
		parent = this;

		request = getIntent().getExtras().getParcelable("achAccountObject");

		final CustomLockView ctrlSecurityPin = (CustomLockView)findViewById(R.id.ctrlSecurityPin);
		ctrlSecurityPin.invalidate();
		ctrlSecurityPin.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				String passcode = ctrlSecurityPin.getPasscode();

				if (passcode.length() > 3) {
					Intent confirmAcct = new Intent(getApplicationContext(), ConfirmPinActivity.class);

					request.SecurityPin = passcode;

					confirmAcct.putExtra("achAccountObject", request);

					finish();
					startActivity(confirmAcct);
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
			case (USERSECURITYPIN_FAILED):
				alertDialog = new AlertDialog.Builder(parent).create();
				alertDialog.setTitle("Setup Failed");
				alertDialog
						.setMessage("There was an error setting up your security pin. Please try again.");
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
				alertDialog = new AlertDialog.Builder(parent).create();
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
