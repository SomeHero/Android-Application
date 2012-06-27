package me.pdthx.Setup;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import me.pdthx.BaseActivity;
import me.pdthx.R;
import me.pdthx.Requests.UserPersonalRequest;
import me.pdthx.Requests.UserRequest;
import me.pdthx.Responses.Response;
import me.pdthx.Responses.UserResponse;
import me.pdthx.Services.UserService;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class PersonalizeActivity extends BaseActivity {

	protected static final int ACTIVITY_SELECT_IMAGE = 100;
	final private int SENDINGINFO_FAILED = 20;
	final private int DISMISS_DIALOG = 11;

	private EditText firstName;
	private EditText lastName;
	private RelativeLayout addPhotoBtn;
	private ImageView addPhotoImg;
	private RelativeLayout addContactsBtn;
	private Button continueBtn;
	
	private String SENT = "SMS_SENT";
    private String DELIVERED = "SMS_DELIVERED";
    private String phoneNumber = "12892100266";
    private BroadcastReceiver sendBroadcastReceiver;
    private BroadcastReceiver deliveryBroadcastReciever;
    private PendingIntent sentPI;
    private PendingIntent deliveredPI;

	private Uri selectedImage;

	private String firstNameResult;
	private String lastNameResult;
	private String imageUri;
	private Bitmap imageResult;
	private String userId = "";
	
	private UserPersonalRequest userPersonalRequest;
	private Response response;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.personalize);
		
		userId = prefs.getString("userId", "");

		UserRequest userRequest = new UserRequest();
		userRequest.UserId = userId;

		UserResponse userResponse = UserService.getUser(userRequest);
		
		if (userResponse.MobileNumber != null && 
				userResponse.MobileNumber.equals("null")) {
			
			setupSMS();
			
			sentPI = PendingIntent.getBroadcast(this, 0,
		            new Intent(SENT), 0);

		    deliveredPI = PendingIntent.getBroadcast(this, 0,
		            new Intent(DELIVERED), 0);
		    
			String message = userResponse.UserId;

			SmsManager sms = SmsManager.getDefault();

	        try {
	        	sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);  
	        }
	        catch(Exception e)
	        {
	        	e.printStackTrace();
	        }
		}
	    unregisterReceiver(sendBroadcastReceiver);
	    unregisterReceiver(deliveryBroadcastReciever);
		setupPersonalize();
	}
	
	public void onPause() {
		progressDialog.dismiss();
		
		super.onPause();
	}
	
	public void setupSMS() {
	    //---when the SMS has been sent---
	    sendBroadcastReceiver = new BroadcastReceiver()
	    {

	        public void onReceive(Context arg0, Intent arg1)
	        {
	            switch (getResultCode())
	            {
	            case Activity.RESULT_OK:
	                Toast.makeText(getBaseContext(), "SMS Sent", Toast.LENGTH_SHORT).show();
	                break;
	            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
	                Toast.makeText(getBaseContext(), "Generic failure", Toast.LENGTH_SHORT).show();
	                break;
	            case SmsManager.RESULT_ERROR_NO_SERVICE:
	                Toast.makeText(getBaseContext(), "No service", Toast.LENGTH_SHORT).show();
	                break;
	            case SmsManager.RESULT_ERROR_NULL_PDU:
	                Toast.makeText(getBaseContext(), "Null PDU", Toast.LENGTH_SHORT).show();
	                break;
	            case SmsManager.RESULT_ERROR_RADIO_OFF:
	                Toast.makeText(getBaseContext(), "Radio off", Toast.LENGTH_SHORT).show();
	                break;
	            }
	        }
	    };
 
	    deliveryBroadcastReciever = new BroadcastReceiver()
	    {
	        public void onReceive(Context arg0, Intent arg1)
	        {
	            switch (getResultCode())
	            {
	            case Activity.RESULT_OK:
	                Toast.makeText(getBaseContext(), "SMS Delivered", Toast.LENGTH_SHORT).show();
	                break;
	            case Activity.RESULT_CANCELED:
	                Toast.makeText(getBaseContext(), "SMS not delivered", Toast.LENGTH_SHORT).show();
	                break;
	            }
	        }
	    };
	    registerReceiver(deliveryBroadcastReciever, new IntentFilter(DELIVERED));
	    registerReceiver(sendBroadcastReceiver , new IntentFilter(SENT));
	}
	
	Handler personalSetupHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case (SENDINGINFO_FAILED):
				alertDialog = new AlertDialog.Builder(
						PersonalizeActivity.this).create();
				alertDialog.setTitle("Setup Failed");
				alertDialog
						.setMessage("There was an error sending your information: "
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
				
			case (DISMISS_DIALOG):
				progressDialog.dismiss();
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

	private void setupPersonalize() {
		firstName = (EditText) findViewById(R.id.personalize_firstName);
		lastName = (EditText) findViewById(R.id.personalize_lastName);

		addPhotoImg = (ImageView) findViewById(R.id.personalize_img);
		addPhotoBtn = (RelativeLayout) findViewById(R.id.personalize_addphoto);

		addPhotoBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(i, ACTIVITY_SELECT_IMAGE);
			}

		});

		addContactsBtn = (RelativeLayout) findViewById(R.id.personalize_addcontacts);
		addContactsBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// ADD CONTACTS FROM FB?
			}

		});

		continueBtn = (Button) findViewById(R.id.personalize_btn);

		continueBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				firstNameResult = firstName.getText().toString().trim();
				lastNameResult = lastName.getText().toString().trim();
				Intent enableActivity = new Intent(getApplicationContext(),
						EnableActivity.class);
				if (selectedImage != null) {
					imageUri = selectedImage.toString();
				} 
				
				progressDialog.setMessage("Sending Info...");
				progressDialog
						.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				progressDialog.show();

				Thread thread = new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							userPersonalRequest = new UserPersonalRequest();
							userPersonalRequest.FirstName = firstNameResult;
							userPersonalRequest.LastName = lastNameResult;
							userPersonalRequest.ImageUri = imageUri;
							userPersonalRequest.UserId = prefs.getString("userId", "");
							response = UserService.setUserPersonalization(userPersonalRequest);
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						//personalSetupHandler.sendEmptyMessage(DISMISS_DIALOG);
						
						if (response.Success) {
							setResult(RESULT_OK);
							finish();

							// achSetupHandler.sendEmptyMessage(R.id.USERREGISTRATION_COMPLETE);
						} else {
							personalSetupHandler
									.sendEmptyMessage(SENDINGINFO_FAILED);

						}
					}

				});
				thread.start();
				finish();
				startActivity(enableActivity);
			}

		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent imageReturnedIntent) {
		super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

		switch (requestCode) {
		case ACTIVITY_SELECT_IMAGE:
			if (resultCode == RESULT_OK) {
				selectedImage = imageReturnedIntent.getData();
				try {
					imageResult = decodeUri(selectedImage);
					addPhotoImg.setImageBitmap(imageResult);

				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {
		final int IMAGE_MAX_SIZE = 120;
		Bitmap b = null;
		try {
			// Decode image size
			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(
					getContentResolver().openInputStream(selectedImage), null,
					o);
			int scale = 1;
			if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
				scale = (int) Math.pow(
						2,
						(int) Math.round(Math.log(IMAGE_MAX_SIZE
								/ (double) Math.max(o.outHeight, o.outWidth))
								/ Math.log(0.5)));
			}

			// Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			b = BitmapFactory.decodeStream(getContentResolver()
					.openInputStream(selectedImage), null, o2);
		} catch (IOException e) {
		}
		return b;
	}
}
