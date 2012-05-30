/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.pdthx;

import java.text.NumberFormat;

import com.zubhium.ZubhiumSDK;
import com.zubhium.ZubhiumSDK.CrashReportingMode;

import me.pdthx.Responses.PaymentResponse;
import me.pdthx.Services.PaymentServices;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import me.pdthx.Requests.SubmitPaymentRequest;

public final class SubmitPaymentActivity extends BaseActivity {

	public static final String TAG = "MakePaymentActivity";
	private String deviceId;
	private String mobileNumber;
	private String recipientUri = "";
	private Double amount = (double) 0;
	private String comments = "";
	private String userId = "";
	private String senderAccountId = "";
	private String errorMessage = "";

	private AutoCompleteTextView txtRecipientUri;
	private EditText txtAmount;
	private EditText txtComments;
	private ContactList contactList;
	private Button btnSendMoney;
	private String passcode = "";

	final private int SUBMITPAYMENT_DIALOG = 0;
	final private int NORECIPIENTSPECIFIED_DIALOG = 1;
	final private int NOAMOUNTSPECIFIED_DIALOG = 2;
	final private int PAYMENTEXCEEDSLIMIT_DIALOG = 5;
	final private int SUBMITPAYMENTFAILED_DIALOG = 3;
	final private int SUBMITPAYMENTSUCCESS_DIALOG = 4;
	final private int INVALIDPASSCODELENGTH_DIALOG = 12;

	final private int SUBMITPAYMENT_ACTION = 0;

	private View sendMoneyView = null;
	private PaymentResponse paymentResponse;
	
	Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

        	switch(msg.what) {
	    		case(R.id.USERSIGNIN_COMPLETE):
	    			setContentView(sendMoneyView);
	        	showSecurityPinDialog();
	    			break;
	    		case(R.id.USERREGISTRATION_COMPLETE):
	    			setContentView(sendMoneyView);
	        		showSecurityPinDialog();
	    			break;
	    	}
        }
        
	};

	private Dialog dialog = null;
	ZubhiumSDK sdk ;

	private Handler dialogHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			dialog.dismiss();

			switch (msg.what) {
			
			case SUBMITPAYMENT_ACTION:
				removeDialog(SUBMITPAYMENT_DIALOG);

				if (paymentResponse != null
						&& paymentResponse.Success) {
					showDialog(SUBMITPAYMENTSUCCESS_DIALOG);
					launchSendMoneyView();
				} else if (paymentResponse != null) {
					errorMessage = paymentResponse.ReasonPhrase;
					showDialog(SUBMITPAYMENTFAILED_DIALOG);
				} else {
					showDialog(SUBMITPAYMENTFAILED_DIALOG);
				}
				break;

			}

		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "Activity State: onCreate()");

		super.onCreate(savedInstanceState);
		setTitle("Send Money");
		
		sdk = ZubhiumSDK.getZubhiumSDKInstance(SubmitPaymentActivity.this, getString(R.string.secret_key));
		
	    if(sdk != null){
	    	sdk.setCrashReportingMode(CrashReportingMode.SILENT);
	    }

	    deviceId = Secure.getString(getBaseContext().getContentResolver(),
				Secure.ANDROID_ID);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		userId = prefs.getString("userId", "");
		mobileNumber = prefs.getString("mobileNumber", "");
		contactList = new ContactList(getBaseContext());

		
		launchSendMoneyView();

	}

	protected android.app.Dialog onCreateDialog(int id) {
		AlertDialog alertDialog = null;
		ProgressDialog progressDialog = null;
		Thread thread = null;
		switch (id) {
		case SUBMITPAYMENT_DIALOG:
			progressDialog = new ProgressDialog(this);

			progressDialog.setMessage("Submitting Request...");
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

			thread = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						SubmitPaymentRequest();

					} catch (Exception e) {
						e.printStackTrace();
					}
					dialogHandler.sendEmptyMessage(SUBMITPAYMENT_ACTION);
				}

			});
			dialog = progressDialog;
			thread.start();

			return dialog;
		case SUBMITPAYMENTFAILED_DIALOG:
			alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.setTitle("Failed");

			alertDialog.setMessage(errorMessage);
			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();

				}
			});

			return alertDialog;
		case SUBMITPAYMENTSUCCESS_DIALOG:
			alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.setTitle("Payment Sumitted");
			NumberFormat nf = NumberFormat.getCurrencyInstance();

			alertDialog.setMessage(String.format(
					"Your payment for %s was sent to %s.", nf.format(amount),
					recipientUri));

			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					removeDialog(4);
					dialog.dismiss();

					txtRecipientUri.setText("");
					txtRecipientUri.requestFocus();
					txtAmount.setText("$0.00");
					txtComments.setText("");
				}
			});

			return alertDialog;
		case NORECIPIENTSPECIFIED_DIALOG:
			alertDialog = new AlertDialog.Builder(SubmitPaymentActivity.this)
					.create();
			alertDialog.setTitle("Please Specify a Recipient");
			alertDialog
					.setMessage("You must specify the recipient's mobile number.");

			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});

			return alertDialog;
		case NOAMOUNTSPECIFIED_DIALOG:
			alertDialog = new AlertDialog.Builder(SubmitPaymentActivity.this)
					.create();
			alertDialog.setTitle("Please Specify an Amount");
			alertDialog.setMessage("You must specify the amount to send.");

			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});

			return alertDialog;
		
		case INVALIDPASSCODELENGTH_DIALOG:
			alertDialog = new AlertDialog.Builder(SubmitPaymentActivity.this)
					.create();
			alertDialog.setTitle("Invalid Passcode");
			alertDialog
					.setMessage("Your passcode is atleast 4 buttons. Please try again.");

			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});

			return alertDialog;
			
			
		case PAYMENTEXCEEDSLIMIT_DIALOG:
			alertDialog = new AlertDialog.Builder(SubmitPaymentActivity.this)
			.create();
			alertDialog.setTitle("Exceeds Limit");
			alertDialog
			.setMessage("The payment exceeds your upper limit. Please try again.");

			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});

			return alertDialog;

		}

		return null;
	}

	protected void launchSendMoneyView() {
		sendMoneyView = View.inflate(this, R.layout.contactmanager, null);
		setContentView(sendMoneyView);
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, contactList.getContacts().toArray(
						new String[0]));
		txtRecipientUri = (AutoCompleteTextView) findViewById(R.id.txtRecipientUri);
		txtRecipientUri.setAdapter(adapter);
		
		txtAmount = (EditText) findViewById(R.id.txtAmount);
		txtComments = (EditText) findViewById(R.id.txtComments);
		btnSendMoney = (Button) findViewById(R.id.btnSubmitPaymentRequest);

		txtAmount.addTextChangedListener(new TextWatcher() {
			String current = "";

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (!s.toString().equals(current)) {
					txtAmount.removeTextChangedListener(this);

					String cleanString = s.toString().replaceAll("[$,.]", "");

					double parsed = Double.parseDouble(cleanString);
					String formatted = NumberFormat.getCurrencyInstance()
							.format((parsed / 100));

					current = formatted;
					txtAmount.setText(formatted);
					txtAmount.setSelection(formatted.length());

					txtAmount.addTextChangedListener(this);
				}
			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}
		});

		btnSendMoney.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				boolean isValid = true;

				recipientUri = txtRecipientUri.getText().toString();
				amount = Double.parseDouble(txtAmount.getText().toString()
						.replace("$", ""));
				comments = txtComments.getText().toString();

				if (isValid && recipientUri.length() == 0) {
					showDialog(NORECIPIENTSPECIFIED_DIALOG);
					isValid = false;
				}
				if (isValid && amount == (double) 0) {
					showDialog(NOAMOUNTSPECIFIED_DIALOG);
					isValid = false;
				}
				if (isValid && amount > prefs.getInt("upperLimit", 0)) {
					showDialog(PAYMENTEXCEEDSLIMIT_DIALOG);
					isValid = false;
				}
				if (isValid) {
		
					if(prefs.getString("userId", "").length() == 0 || prefs.getString("mobileNumber", "").length() == 0)		{
//					    SignInActivity signInActivity = new SignInActivity(SubmitPaymentActivity.this, mHandler, prefs);
//					    signInActivity.showSignInActivity();
						startActivityForResult(new Intent(SubmitPaymentActivity.this, SignInActivity.class), 1);
					} else {
						showSecurityPinDialog();
					}
				}
			}
		});

		btnSendMoney.setVisibility(View.VISIBLE);

	}

	protected void showSecurityPinDialog() {
		final Dialog d = new Dialog(SubmitPaymentActivity.this, R.style.CustomDialogTheme);
		d.setContentView(R.layout.security_dialog);

	 	d.getWindow().setLayout(400, 600);
		d.show();

		TextView txtConfirmHeader = (TextView)d.findViewById(R.id.txtConfirmHeader);
		TextView txtConfirmBody = (TextView)d.findViewById(R.id.txtConfirmBody);
		
		txtConfirmHeader.setText("Confirm Your Payment");
		txtConfirmBody.setText(String.format("To confirm your payment of %s to %s, swipe you pin below.", txtAmount.getText(), txtRecipientUri.getText()));
		
		Button btnCancel = (Button) d.findViewById(R.id.btnCancelSendMoney);
		btnCancel.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		        d.dismiss();
		    }
		});
	    
		final CustomLockView ctrlSecurityPin = (CustomLockView) d.findViewById(R.id.ctrlSecurityPin);
		ctrlSecurityPin.invalidate();
		ctrlSecurityPin.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				passcode = ctrlSecurityPin.getPasscode();

				if (passcode.length() > 3) {
					recipientUri = txtRecipientUri.getText()
							.toString();
					amount = Double.parseDouble(txtAmount.getText().toString()
							.replace("$", ""));
					comments = txtComments.getText().toString();
					passcode = ctrlSecurityPin.getPasscode();

					d.dismiss();
					
					showDialog(SUBMITPAYMENT_DIALOG);
				} else
					showDialog(INVALIDPASSCODELENGTH_DIALOG);

				return false;
			}
		});
	}

	protected void SubmitPaymentRequest() {

		Boolean isValid = true;

		if (isValid && recipientUri.length() == 0) {
			showDialog(NORECIPIENTSPECIFIED_DIALOG);
			isValid = false;
		}
		if (isValid && amount == (double) 0) {
			showDialog(NOAMOUNTSPECIFIED_DIALOG);
			isValid = false;
		}
		if (isValid) {

			userId = prefs.getString("userId", "");
			senderAccountId = prefs.getString("paymentAccountId", "0");

			PaymentServices paymentServices = new PaymentServices();
			SubmitPaymentRequest submitPaymentRequest = new SubmitPaymentRequest();
			submitPaymentRequest.UserId = userId;
			submitPaymentRequest.SecurityPin = passcode;
			submitPaymentRequest.SenderUri = mobileNumber;
			submitPaymentRequest.RecipientUri = recipientUri;
			submitPaymentRequest.Amount = amount;
			submitPaymentRequest.Comments = comments;
			submitPaymentRequest.SenderAccountId = senderAccountId;

			paymentResponse = paymentServices
					.SubmitPayment(submitPaymentRequest);

		}
	}

}
