package me.pdthx.Setup;

import java.util.ArrayList;
import java.util.Calendar;

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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class CreateQuestionActivity extends BaseActivity {

	private String nameOnAccount;
	private String routingNumber;
	private String accountNumber;
	private String nickname;
	private String accountType;
	private String questions[];
	private String passcode;
	private int currentId;

	private Activity parent = null;

	final private int USERDATA_FAILED = 2;
	final private int SETUPACHACCOUNT_FAILED = 3;
	final private int USERREGISTRATION_FAILED = 4;
	final private int USERSECURITYPIN_CONFIRMMISMATCH = 6;
	final private int USERREGISTRATION_PASSWORDMISMATCH = 7;
	final private int USERREGISTRATION_ACHNUMBERMISMATCH = 8;

	protected ACHAccountSetupRequest achAccountSetupRequest;
	protected ACHAccountSetupResponse achAccountSetupResponse;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_security_question);

		parent = this;

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
				nickname = extras.getString("nickname");

				achAccountSetupRequest = new ACHAccountSetupRequest();
				achAccountSetupRequest.AccountNumber = accountNumber;
				achAccountSetupRequest.AccountType = accountType;
				achAccountSetupRequest.NameOnAccount = nameOnAccount;
				achAccountSetupRequest.RoutingNumber = routingNumber;
				achAccountSetupRequest.SecurityPin = passcode;
				achAccountSetupRequest.SecurityAnswer = answer;
				achAccountSetupRequest.Nickname = nickname;
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
							startActivity(new Intent(getApplicationContext(),
									CustomTabActivity.class));
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