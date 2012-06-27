package me.pdthx.Accounts;

import java.util.ArrayList;

import me.pdthx.BaseActivity;
import me.pdthx.CustomTabActivity;
import me.pdthx.R;
import me.pdthx.Requests.ACHAccountSetupRequest;
import me.pdthx.Responses.ACHAccountSetupResponse;
import me.pdthx.Responses.SecurityQuestionResponse;
import me.pdthx.Services.UserService;
import me.pdthx.Widget.OnWheelChangedListener;
import me.pdthx.Widget.WheelView;
import me.pdthx.Widget.Adapters.ArrayWheelAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class EnterAnswerActivity extends BaseActivity{
	private Activity parent = null;
	
	private String nameOnAccount;
	private String routingNumber;
	private String accountNumber;
	private String accountType;
	private String questions[];
	private String passcode;
	private int currentId;
	
	final private int USERDATA_FAILED = 2;
	final private int SETUPACHACCOUNT_FAILED = 3;
	final private int USERREGISTRATION_FAILED = 4;
	final private int USERSECURITYPIN_INVALIDLENGTH = 5;
	final private int USERSECURITYPIN_CONFIRMMISMATCH = 6;
	final private int USERREGISTRATION_PASSWORDMISMATCH = 7;
	final private int USERREGISTRATION_ACHNUMBERMISMATCH = 8;

	protected ACHAccountSetupRequest achAccountSetupRequest;
	protected ACHAccountSetupResponse achAccountSetupResponse;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		parent = this;
		setContentView(R.layout.create_security_question);
		
		TextView header = (TextView)findViewById(R.id.setupSecurityQuestionHeader);
		header.setText("Confirm Security Question");
		
		TextView body = (TextView)findViewById(R.id.setupSecurityBody);
		body.setText("Pick your account's security question and enter the answer below in order" +
				"to confirm your bank account creation.");
		
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
				EditText userAnswer = (EditText)findViewById(R.id.setupSecurityTxtAnswer);
				String answer = userAnswer.getText().toString().trim();

				Bundle extras = getIntent().getExtras();
				nameOnAccount = extras.getString("nameOnAccount");
				routingNumber = extras.getString("routingNumber");
				accountNumber = extras.getString("accountNumber");
				accountType = extras.getString("accountType");
				passcode = extras.getString("securityPin");

				achAccountSetupRequest = new ACHAccountSetupRequest();
				achAccountSetupRequest.AccountNumber = accountNumber;
				achAccountSetupRequest.AccountType = accountType;
				achAccountSetupRequest.NameOnAccount = nameOnAccount;
				achAccountSetupRequest.RoutingNumber = routingNumber;
				achAccountSetupRequest.SecurityPin = passcode;
				achAccountSetupRequest.SecurityAnswer = answer;
				achAccountSetupRequest.SecurityQuestionId = currentId;
				achAccountSetupRequest.UserId = prefs.getString("userId", "");

				progressDialog.setMessage("Sending Info...");
				progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				progressDialog.show();

				Thread thread = new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							achAccountSetupResponse = UserService
									.setupACHAccount(achAccountSetupRequest);
						} catch (Exception e) {
							e.printStackTrace();
						}
						progressDialog.dismiss();

						if (achAccountSetupResponse.Success) {
							setResult(RESULT_OK);
							finish();
						} else {
							signUpHandler.sendEmptyMessage(USERDATA_FAILED);
						}
					}

				});
				thread.start();
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
			case (SETUPACHACCOUNT_FAILED):
				alertDialog = new AlertDialog.Builder(parent).create();
				alertDialog.setTitle("Setup Failed");
				alertDialog
						.setMessage("There was an error setting up your ACH account: "
								+ achAccountSetupResponse.ReasonPhrase + " Please try again.");
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
				alertDialog = new AlertDialog.Builder(parent).create();
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
				alertDialog = new AlertDialog.Builder(parent).create();
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
				alertDialog = new AlertDialog.Builder(parent).create();
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
				alertDialog = new AlertDialog.Builder(parent).create();
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

	
	
}

/**
request.UserId = prefs.getString("userId", "");

// send pincode to server
progressDialog.setMessage("Sending New Account...");
progressDialog
		.setProgressStyle(ProgressDialog.STYLE_SPINNER);
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
			achSetupHandler
					.sendEmptyMessage(ACCOUNT_REG_SUCCESS);
			finish();
		} else {
			achSetupHandler
					.sendEmptyMessage(USERDATA_FAILED);
		}
	}

});
thread.start();**/