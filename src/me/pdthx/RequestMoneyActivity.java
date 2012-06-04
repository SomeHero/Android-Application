package me.pdthx;

import java.text.NumberFormat;

import com.zubhium.ZubhiumSDK;
import com.zubhium.ZubhiumSDK.CrashReportingMode;

import me.pdthx.Models.Friends;
import me.pdthx.Requests.PaymentRequest;
import me.pdthx.Responses.PaymentResponse;
import me.pdthx.Services.RequestPaymentService;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RequestMoneyActivity extends BaseActivity {

	RequestPaymentService paymentRequestService = new RequestPaymentService();
	
	private String deviceId;
	private String recipientUri = "";
	private double amount = 0;
	private String comments = "";
	private String errorMessage = "";
	
	private Friends friend = null;

	private EditText txtAmount;
	private EditText txtComments;
	private Button btnSendMoney;
	private Button btnAddContacts;
	private String passcode = "";

	
	final private int ADDING_FRIEND = 6;
	final private int SUBMITREQUEST_DIALOG = 0;
	final private int NORECIPIENTSPECIFIED_DIALOG = 1;
	final private int NOAMOUNTSPECIFIED_DIALOG = 2;
	final private int SUBMITREQUESTFAILED_DIALOG = 3;
	final private int SUBMITREQUESTSUCCESS_DIALOG = 4;
	final private int INVALIDPASSCODELENGTH_DIALOG = 5;
	final private int PAYMENTEXCEEDSLIMIT_DIALOG = 6;

	final private int SUBMITREQUEST_ACTION = 1;

	private View sendRequestView = null;
	private SharedPreferences prefs;
	private PaymentResponse paymentRequestResponse;

	ZubhiumSDK sdk ;
	public static final String TAG = "RequestMoneyActivity";
	
	Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

        	switch(msg.what) {
	    		case(R.id.USERSIGNIN_COMPLETE):
	    			setContentView(sendRequestView);
	        	showSecurityPinDialog();
	    			break;
	    		case(R.id.USERREGISTRATION_COMPLETE):
	    			setContentView(sendRequestView);
	        		showSecurityPinDialog();
	    			break;
	    	}
        }
        
	};

	private Dialog dialog = null;
	private Handler dialogHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			dialog.dismiss();
			switch (msg.what) {
			case SUBMITREQUEST_ACTION:
				removeDialog(SUBMITREQUEST_DIALOG);

				if (paymentRequestResponse != null
						&& paymentRequestResponse.Success) {
					showDialog(SUBMITREQUESTSUCCESS_DIALOG);

				} else {
					errorMessage = paymentRequestResponse.ReasonPhrase;
					showDialog(SUBMITREQUESTFAILED_DIALOG);
				}
				break;
			}
		}
	};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sdk = ZubhiumSDK.getZubhiumSDKInstance(RequestMoneyActivity.this, getString(R.string.secret_key));
		
	    if(sdk != null){
	    	sdk.setCrashReportingMode(CrashReportingMode.SILENT);
	    }
	    prefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
	    
		setTitle("Request Money");

		deviceId = Secure.getString(getBaseContext().getContentResolver(),
				Secure.ANDROID_ID);

		launchRequestMoneyView();
	}

	protected android.app.Dialog onCreateDialog(int id) {
		AlertDialog alertDialog = null;
		ProgressDialog progressDialog = null;
		Thread thread = null;
		switch (id) {
		case SUBMITREQUEST_DIALOG:
			progressDialog = new ProgressDialog(this);

			progressDialog.setMessage("Submitting Request...");
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

			thread = new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						SubmitRequest();

					} catch (Exception e) {
						e.printStackTrace();
					}
					dialogHandler.sendEmptyMessage(SUBMITREQUEST_ACTION);
				}

			});
			dialog = progressDialog;
			thread.start();

			return dialog;
		case SUBMITREQUESTFAILED_DIALOG:
			alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.setTitle("Failed");

			alertDialog
					.setMessage(errorMessage);
			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();

				}
			});

			return alertDialog;
		case SUBMITREQUESTSUCCESS_DIALOG:
			alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.setTitle("Request Sumitted");
			NumberFormat nf = NumberFormat.getCurrencyInstance();

			alertDialog.setMessage(String.format(
					"Your request for %s was sent to %s.", nf.format(amount),
					recipientUri));

			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					removeDialog(4);
					dialog.dismiss();

					friend = null;
					btnAddContacts.setText("Add recipient");
					txtAmount.setText("$0.00");
					txtComments.setText("");
				}
			});
			return alertDialog;
		case NORECIPIENTSPECIFIED_DIALOG:
			alertDialog = new AlertDialog.Builder(RequestMoneyActivity.this)
					.create();
			alertDialog.setTitle("Invalid Recipient");
			alertDialog
					.setMessage("Your recipient does not have a valid paypoint.");

			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});

			return alertDialog;
		case NOAMOUNTSPECIFIED_DIALOG:
			alertDialog = new AlertDialog.Builder(RequestMoneyActivity.this)
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
			alertDialog = new AlertDialog.Builder(RequestMoneyActivity.this)
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
			alertDialog = new AlertDialog.Builder(RequestMoneyActivity.this)
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

	protected void launchRequestMoneyView() {

		sendRequestView = View.inflate(this, R.layout.requestmoney_controller, null);
		setContentView(sendRequestView);

//		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,																	//CURRENTLY WON'T PULL FB CONTACTS
//				android.R.layout.simple_dropdown_item_1line, contactList.getContacts().toArray(
//						new String[0]));
//
//	
//		txtRequestMoneyRecipient = (AutoCompleteTextView) findViewById(R.id.txtRequestMoneyRecipient);
//		txtRequestMoneyRecipient.setAdapter(adapter);
//		for (int x = 0; x < contactList.getContacts().size(); x++)
//		{
//			Log.d(contactList.getContacts().get(x), "Phone Contacts");
//		}
		txtAmount = (EditText) findViewById(R.id.txtRequestMoneyAmount);
		txtAmount.addTextChangedListener(new TextWatcher() {
			String current = "";

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (!s.toString().equals(current)) {
					EditText txtAmount = (EditText) findViewById(R.id.txtRequestMoneyAmount);
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
				
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				
			}
		});
		txtComments = (EditText) findViewById(R.id.txtRequestMoneyComments);
		btnSendMoney = (Button) findViewById(R.id.btnSubmit);
		btnAddContacts = (Button) findViewById(R.id.addRecipient);
		
		btnAddContacts.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(RequestMoneyActivity.this, FriendListActivity.class), ADDING_FRIEND);			
			}
			
		});
		btnAddContacts.setVisibility(View.VISIBLE);
		
		btnSendMoney.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				boolean isValid = true;

				amount = Double.parseDouble(txtAmount.getText().toString()
						.replaceAll("[$,]*", ""));
				comments = txtComments.getText().toString();

				if (isValid & recipientUri.length() == 0) {
					showDialog(NORECIPIENTSPECIFIED_DIALOG);
					isValid = false;
				}
				if (isValid & amount == 0) {
					showDialog(NOAMOUNTSPECIFIED_DIALOG);
					isValid = false;
				}
				if (isValid && amount > prefs.getInt("upperLimit", 0)) {
					showDialog(PAYMENTEXCEEDSLIMIT_DIALOG);
					isValid = false;
				}
				if (isValid) {
					if(prefs.getString("userId", "").length() == 0) {
						startActivityForResult(new Intent(RequestMoneyActivity.this, SignInActivity.class), 1);
					} else {
						showSecurityPinDialog();
					}
				}
			}
		});

		btnSendMoney.setVisibility(View.VISIBLE);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == ADDING_FRIEND) {
				Bundle bundle = data.getExtras();
				friend = friendList.get(bundle.getInt("index"));
				
				if (friend.type.equals("Facebook")) {
					recipientUri = "fb_" + friend.id; 
				}
				else {
					recipientUri = "" + friend.phoneNumber;
				}
				
				btnAddContacts.setText(friend.name + ": " + friend.id);
			}
		}
	}

	protected void showSecurityPinDialog() {
		final Dialog d = new Dialog(RequestMoneyActivity.this, R.style.CustomDialogTheme);
		d.setContentView(R.layout.security_dialog);

	 	d.getWindow().setLayout(400, 600);
		d.show();

		TextView txtConfirmHeader = (TextView)d.findViewById(R.id.txtConfirmHeader);
		TextView txtConfirmBody = (TextView)d.findViewById(R.id.txtConfirmBody);
	
		txtConfirmHeader.setText("Confirm Your Request");
		txtConfirmBody.setText(String.format("To confirm your request for %s from %s, swipe you pin below.", txtAmount.getText(), friend.name));
		
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
					
					amount = Double.parseDouble(txtAmount.getText().toString()
							.replaceAll("[$,]*", ""));
					comments = txtComments.getText().toString();
					passcode = ctrlSecurityPin.getPasscode();

					d.dismiss();
					
					showDialog(SUBMITREQUEST_DIALOG);
				} else
					showDialog(INVALIDPASSCODELENGTH_DIALOG);

				return false;
			}
		});
	}

	protected void SubmitRequest() {
		
		PaymentRequest paymentRequest = new PaymentRequest();
		paymentRequest.SenderAccountId = prefs.getString("paymentAccountId", "");
		paymentRequest.SenderUri = prefs.getString("login", "");
		paymentRequest.DeviceId = deviceId;
		paymentRequest.RecipientUri = recipientUri;
		paymentRequest.Amount = amount;
		paymentRequest.Comments = comments;
		paymentRequest.SecurityPin = passcode;

		paymentRequestResponse = paymentRequestService.SendPaymentRequest(paymentRequest);
	}



}
