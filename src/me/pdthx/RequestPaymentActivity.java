package me.pdthx;

import java.text.NumberFormat;

import me.pdthx.CustomViews.CustomLockView;
import me.pdthx.Models.Friend;
import me.pdthx.Requests.PaymentRequest;
import me.pdthx.Responses.Response;
import me.pdthx.Services.PaymentServices;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RequestPaymentActivity extends BaseActivity {

	public static final String TAG = "RequestMoneyActivity";
	
	private String recipientUri = "";
	private double amount = 0;
	private String comments = "";
	private String errorMessage = "";
	
	private Location location = null;
	private LocationManager locationManager;
	private LocationListener locationListener;
	
	private Friend friend = null;

	private Button btnAddContacts;
	private EditText txtAmount;
	private EditText txtComments;
	private Button btnRequestMoney;
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

	private View requestMoneyView = null;
	private Response paymentResponse;
	
	Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

        	switch(msg.what) {
	    		case(R.id.USERSIGNIN_COMPLETE):
	    			setContentView(requestMoneyView);
	        		showSecurityPinDialog();
	    			break;
	    		case(R.id.USERREGISTRATION_COMPLETE):
	    			setContentView(requestMoneyView);
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

				if (paymentResponse != null
						&& paymentResponse.Success) {
					showDialog(SUBMITREQUESTSUCCESS_DIALOG);

				} else if (paymentResponse != null) {
					errorMessage = paymentResponse.ReasonPhrase;
					showDialog(SUBMITREQUESTFAILED_DIALOG);
				} else {
					showDialog(SUBMITREQUESTFAILED_DIALOG);
				}
				break;
			}
		}
	};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("Request Money");
	    
	    locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		locationListener = new LocationListener() {

			@Override
			public void onLocationChanged(Location arg0) {
				// TODO Auto-generated method stub
				if (isBetterLocation(arg0, location)) {
					location = arg0;
				}
			}

			@Override
			public void onProviderDisabled(String arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				// TODO Auto-generated method stub

			}

		};

		launchRequestMoneyView();
	}
	
	@Override
	public void onResume() {
		super.onResume();

		locationManager.requestLocationUpdates(
				LocationManager.GPS_PROVIDER, 0, 0, locationListener);
		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);


	}

	@Override
	public void onPause() {
		super.onPause();

		locationManager.removeUpdates(locationListener);
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
			alertDialog = new AlertDialog.Builder(RequestPaymentActivity.this)
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
			alertDialog = new AlertDialog.Builder(RequestPaymentActivity.this)
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
			alertDialog = new AlertDialog.Builder(RequestPaymentActivity.this)
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
			alertDialog = new AlertDialog.Builder(RequestPaymentActivity.this)
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

		requestMoneyView = View.inflate(this, R.layout.requestmoney_controller, null);
		setContentView(requestMoneyView);

		txtAmount = (EditText) findViewById(R.id.txtRequestMoneyAmount);
		txtComments = (EditText) findViewById(R.id.txtRequestMoneyComments);
		btnRequestMoney = (Button) findViewById(R.id.btnSubmit);
		
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
				if (arg0.length() == 14) {
					arg0.replace(13, 14, "");
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				
			}
		});
		
		btnAddContacts = (Button) findViewById(R.id.addRecipient);
		
		btnAddContacts.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(RequestPaymentActivity.this, FriendsListActivity.class), ADDING_FRIEND);			
			}
			
		});
		
		btnRequestMoney.setOnClickListener(new OnClickListener() {

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
					
					Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

					if (isBetterLocation(lastKnownLocation, location)) {
						location = lastKnownLocation;
					}
					
					if(prefs.getString("userId", "").length() == 0) {
						startActivityForResult(new Intent(RequestPaymentActivity.this, SignInActivity.class), 1);
					} else {
						showSecurityPinDialog();
					}
				}
			}
		});

		btnRequestMoney.setVisibility(View.VISIBLE);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == ADDING_FRIEND) {
				Bundle bundle = data.getExtras();
				friend = friendsList.get(bundle.getInt("index"));
				
				if (friend.getType().equals("Facebook")) {
					recipientUri = "fb_" + friend.getId();
					btnAddContacts.setText(friend.getName() + ": " + friend.getId());
				}
				else {
					recipientUri = "" + friend.getPhoneNumber();
					btnAddContacts.setText(friend.getName() + ": " + friend.getPhoneNumber());
				}
				
				
			}
			else {
				launchRequestMoneyView();
			}
		}
		else {
			finish();
		}
	}

	protected void showSecurityPinDialog() {
		final Dialog d = new Dialog(RequestPaymentActivity.this, R.style.CustomDialogTheme);
		d.setContentView(R.layout.security_dialog);

	 	d.getWindow().setLayout(400, 600);
		d.show();

		TextView txtConfirmHeader = (TextView)d.findViewById(R.id.txtConfirmHeader);
		TextView txtConfirmBody = (TextView)d.findViewById(R.id.txtConfirmBody);
	
		txtConfirmHeader.setText("Confirm Your Request");
		txtConfirmBody.setText(
				String.format("To confirm your request for %s from %s, swipe you pin below.", 
						txtAmount.getText(), friend.getName()));
		
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
		paymentRequest.RecipientUri = recipientUri;
		paymentRequest.Amount = amount;
		paymentRequest.Comments = comments;
		paymentRequest.SecurityPin = passcode;
		
		if (location != null) {
			paymentRequest.Latitude = location.getLatitude();
			paymentRequest.Longitude = location.getLongitude();
		}

		paymentResponse = PaymentServices.requestMoney(paymentRequest);
	}
	
	/** Determines whether one Location reading is better than the current Location fix
	 * @param location  The new Location that you want to evaluate
	 * @param currentBestLocation  The current Location fix, to which you want to compare the new one
	 */
	private boolean isBetterLocation(Location location, Location currentBestLocation) {
		final int TWO_MINUTES = 1000 * 60 * 2;

		if (location != null) {

			if (currentBestLocation == null) {
				// A new location is always better than no location
				return true;
			}

			// Check whether the new location fix is newer or older
			long timeDelta = location.getTime() - currentBestLocation.getTime();
			boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
			boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
			boolean isNewer = timeDelta > 0;

			// If it's been more than two minutes since the current location, use the new location
			// because the user has likely moved
			if (isSignificantlyNewer) {
				return true;
				// If the new location is more than two minutes older, it must be worse
			} else if (isSignificantlyOlder) {
				return false;
			}

			// Check whether the new location fix is more or less accurate
			int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
			boolean isLessAccurate = accuracyDelta > 0;
			boolean isMoreAccurate = accuracyDelta < 0;
			boolean isSignificantlyLessAccurate = accuracyDelta > 200;

			// Check if the old and new location are from the same provider
			boolean isFromSameProvider = isSameProvider(location.getProvider(),
					currentBestLocation.getProvider());

			// Determine location quality using a combination of timeliness and accuracy
			if (isMoreAccurate) {
				return true;
			} else if (isNewer && !isLessAccurate) {
				return true;
			} else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
				return true;
			}
		}
		return false;
	}

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}




}