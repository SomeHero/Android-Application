package me.pdthx;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import me.pdthx.Requests.ACHAccountSetupRequest;
import me.pdthx.Responses.ACHAccountSetupResponse;
import me.pdthx.Services.UserService;
import me.pdthx.Setup.CreatePinActivity;

public class ACHAccountSetupActivity extends BaseActivity implements
		OnCheckedChangeListener {

	final private int SETUPACHACCOUNT_FAILED = 3;
	final private int USERREGISTRATION_ACHNUMBERMISMATCH = 8;
	private Button btnAddAccount;
	private Button btnRemoveAccount;
	private Button btnUpdateAccount;
	private Button btnBack;
	private RadioGroup btnAcctType;
	private boolean isCheckingAcct;
	private int tab;

	private ACHAccountSetupRequest request;
	private ACHAccountSetupResponse response;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tracker.trackPageView("ACHAccountSetUpActivity");

		progressDialog.dismiss();
		setContentView(R.layout.achaccountsetup_controller);

		tab = getIntent().getExtras().getInt("tab");

		btnAddAccount = (Button) findViewById(R.id.btnSubmitACHAccount);
		btnAddAccount.setText("Add Account");
		btnRemoveAccount = (Button) findViewById(R.id.btnremoveACHAcct);
		btnRemoveAccount.setVisibility(View.GONE);
		btnUpdateAccount = (Button) findViewById(R.id.btnRemindMeLater);
		btnUpdateAccount.setVisibility(View.GONE);
		btnBack = (Button) findViewById(R.id.btnACHBack);
		btnAcctType = (RadioGroup) findViewById(R.id.achBankCategories);
		btnAcctType.setOnCheckedChangeListener(this);
		showSetupACHController();


	}

	Handler achSetupHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case (SETUPACHACCOUNT_FAILED):
				alertDialog = new AlertDialog.Builder(
						ACHAccountSetupActivity.this).create();
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

			case (USERREGISTRATION_ACHNUMBERMISMATCH):
				alertDialog = new AlertDialog.Builder(
						ACHAccountSetupActivity.this).create();
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

	private void showSetupACHController() {

		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}

		});

		btnAddAccount.setOnClickListener(new OnClickListener() {
			public void onClick(View argO) {

				EditText txtNameOnAccount = (EditText) findViewById(R.id.txtNameOnAccount);
				EditText txtRoutingNumber = (EditText) findViewById(R.id.txtRoutingNumber);
				EditText txtAccountNumber = (EditText) findViewById(R.id.txtAccountNumber);
				EditText txtNickname = (EditText)findViewById(R.id.txtNicknameonAcct);

				if (txtAccountNumber
						.getText()
						.toString()
						.trim()
						.equals(((EditText) findViewById(R.id.txtConfirmAccountNumber))
								.getText().toString().trim())) {

					Intent createPin = new Intent(getApplicationContext(), CreatePinActivity.class);
					createPin.putExtra("nameOnAccount", txtNameOnAccount.getText().toString().trim());
					createPin.putExtra("routingNumber", txtRoutingNumber.getText().toString().trim());
					createPin.putExtra("accountNumber", txtAccountNumber.getText().toString().trim());
					createPin.putExtra("nickname", txtNickname.getText().toString().trim());
					createPin.putExtra("tab", tab);

					if(isCheckingAcct)
					{
						createPin.putExtra("accountType", "Checking");
					}
					else
					{
						createPin.putExtra("accountType", "Savings");
					}
					finish();
					startActivity(createPin);
				} else {
					achSetupHandler
							.sendEmptyMessage(USERREGISTRATION_ACHNUMBERMISMATCH);
				}
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
}
