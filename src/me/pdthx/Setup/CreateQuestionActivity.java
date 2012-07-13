package me.pdthx.Setup;

import android.content.SharedPreferences.Editor;
import android.util.Log;
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

public class CreateQuestionActivity extends BaseActivity {
	
	private String[] questions;
	private int currentId = 1;
	
	final private int USERDATA_FAILED = 2;
	final private int SETUPACHACCOUNT_FAILED = 3;
	final private int USERREGISTRATION_FAILED = 4;
	final private int USERSECURITYPIN_CONFIRMMISMATCH = 6;
	final private int USERREGISTRATION_PASSWORDMISMATCH = 7;
	final private int USERREGISTRATION_ACHNUMBERMISMATCH = 8;

	protected ACHAccountSetupRequest request;
	protected ACHAccountSetupResponse response;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_security_question);

		final TextView txtAnswer = (TextView) findViewById(R.id.security_question_selected);

		final WheelView list = (WheelView) findViewById(R.id.security_question_list);

		int curr = 0;
		final ArrayList<SecurityQuestionResponse> securityQuestions = UserService
				.getSecurityQuestions();
		questions = new String[securityQuestions.size()];
		for (int i = 0; i < securityQuestions.size(); i++) {
		    Log.d("Adding security question: " + i, securityQuestions.get(i).Question);
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
				currentId = newValue + 1;
			}

		});

		Button confirmBtn = (Button) findViewById(R.id.securityquestion_confirmbtn);
		confirmBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				EditText userAnswer = (EditText)findViewById(R.id.setupSecurityTxtAnswer);
				String answer = userAnswer.getText().toString().trim();

				request = getIntent().getExtras().getParcelable("achAccountObject");

				request.SecurityAnswer = answer;
				request.SecurityQuestionId = currentId;

				progressDialog.setMessage("Sending Info...");
				progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				progressDialog.show();

				Thread thread = new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							response = UserService
									.setupACHAccount(request);
						} catch (Exception e) {
							e.printStackTrace();
						}
						progressDialog.dismiss();

						if (response.Success) {

						    Editor edit = prefs.edit();
						    edit.putBoolean("hasACHAccount", true);
						    edit.putString("paymentAccountId", response.PaymentAccountId);
						    edit.commit();

							setResult(RESULT_OK);
							finish();

							Intent intent = new Intent(getApplicationContext(),
                                CustomTabActivity.class);
							intent.putExtra("tab", request.tab);
							startActivity(intent);
						} else {
							signUpHandler.sendEmptyMessage(USERDATA_FAILED);
						}
					}

				});
				thread.start();
			}

		});
	}

	private Handler signUpHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case (USERDATA_FAILED):
				alertDialog.setTitle("Setup Failed");
				alertDialog.setMessage(response.ReasonPhrase);
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

}