package me.pdthx;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import me.pdthx.Requests.ACHAccountSetupRequest;
import me.pdthx.Responses.ACHAccountSetupResponse;
import me.pdthx.Services.UserService;

public class ACHAccountSetupActivity extends BaseActivity {

	final private int SETUPACHACCOUNT_FAILED = 3;
	final private int USERREGISTRATION_ACHNUMBERMISMATCH = 8;
	private Button btnEnablePayments;
	private Button btnACHRemindMeLater;
	private ACHAccountSetupRequest request;
	private ACHAccountSetupResponse response;

	@Override
	public void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		
		progressDialog.dismiss();
		setContentView(R.layout.setup_achaccount_controller);

		btnEnablePayments = (Button)findViewById(R.id.btnSubmitACHAccount);
		btnACHRemindMeLater = (Button)findViewById(R.id.btnACHRemindMeLater);

		showSetupACHController();
	}


	Handler achSetupHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case(SETUPACHACCOUNT_FAILED):
				alertDialog = new AlertDialog.Builder(ACHAccountSetupActivity.this)
			.create();
			alertDialog.setTitle("Setup Failed");
			alertDialog
			.setMessage("There was an error setting up your ACH account: " + response.ReasonPhrase + " Please try again.");
			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});

			alertDialog.show();
			break;

			case(USERREGISTRATION_ACHNUMBERMISMATCH):
				alertDialog = new AlertDialog.Builder(ACHAccountSetupActivity.this)
			.create();
			alertDialog.setTitle("ACH Account Number Mismatch.");
			alertDialog
			.setMessage("The ACH account numbers you entered do not match. Please try again.");
			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});

			alertDialog.show();
			break;

			//	        	case(USERREGISTRATION_PHONENUMBERFORMATERROR):
			//	        		alertDialog = new AlertDialog.Builder(parent)
			//				.create();
			//				alertDialog.setTitle("Phone Number Format error.");
			//				alertDialog
			//						.setMessage("Phone number does not have 10 digits. Please try again.");
			//				alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			//					public void onClick(DialogInterface dialog, int which) {
			//						dialog.dismiss();
			//					}
			//				});
			//				
			//				alertDialog.show();
			//        		break;
			}	

		}

	};

	private void showSetupACHController() {
		
		btnACHRemindMeLater.setOnClickListener(new OnClickListener() {
			public void onClick(View argO) {
				setResult(RESULT_OK);
				finish();
			}
		});
		
		btnEnablePayments.setOnClickListener(new OnClickListener() {
			public void onClick(View argO) {

				EditText txtNameOnAccount = (EditText)findViewById(R.id.txtNameOnAccount);
				EditText txtRoutingNumber = (EditText)findViewById(R.id.txtRoutingNumber);
				EditText txtAccountNumber = (EditText)findViewById(R.id.txtAccountNumber);

				if (txtAccountNumber.getText().toString().trim().equals(((EditText)findViewById(R.id.txtConfirmAccountNumber)).getText().toString().trim())) {

					request = new ACHAccountSetupRequest();
					request.UserId = prefs.getString("userId", "");
					request.NameOnAccount = txtNameOnAccount.getText().toString().trim();
					request.RoutingNumber = txtRoutingNumber.getText().toString().trim();
					request.AccountNumber = txtAccountNumber.getText().toString().trim();
					request.AccountType = "Savings";

					progressDialog.setMessage("Setting up ACH Account...");
					progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					progressDialog.show();

					Thread thread = new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							try {
								response = UserService.setupACHAccount(request);
							} catch (Exception e) {
								e.printStackTrace();
							}
							progressDialog.dismiss();

							if(response.Success) {
								Editor editor = prefs.edit();

								editor.putString("paymentAccountId", response.PaymentAccountId);
								editor.commit();
								
								setResult(RESULT_OK);
								finish();

								//achSetupHandler.sendEmptyMessage(R.id.USERREGISTRATION_COMPLETE);
							}
							else {
								achSetupHandler.sendEmptyMessage(SETUPACHACCOUNT_FAILED);

							}
						}

					});
					thread.start();
				}
				else {
					achSetupHandler.sendEmptyMessage(USERREGISTRATION_ACHNUMBERMISMATCH);
				}
			}
		});
	}
}
