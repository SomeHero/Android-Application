package me.pdthx;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import me.pdthx.Models.Friends;
import me.pdthx.Requests.UserChangeSecurityPinRequest;
import me.pdthx.Requests.UserMeCodeRequest;
import me.pdthx.Responses.UserChangeSecurityPinResponse;
import me.pdthx.Services.UserService;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.MotionEvent;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class BaseActivity extends Activity {
	protected SharedPreferences prefs;
	protected AlertDialog alertDialog;
	protected ProgressDialog progressDialog;
	
	private String message;

	final private int SETUPSECURITYPIN = 50;
	
	final private int USERSECURITYPIN_COMPLETE = 1;
	final private int USERSECURITYPIN_FAILED = 2;
	final private int USERSECURITYPIN_INVALIDLENGTH = 5;
	final private int USERSECURITYPIN_CONFIRMMISMATCH = 6;
	final private int INVALID_DOLLAR = 0;
//	final private int TESTING = 1;

	private UserChangeSecurityPinRequest request;
	private UserChangeSecurityPinResponse response;
	

	protected Facebook facebook = new Facebook("332189543469634");
	protected AsyncFacebookRunner mAsyncRunner = new AsyncFacebookRunner(
			facebook);
	protected static boolean signedInViaFacebook = false;
	protected static ArrayList<Friends> friendList = new ArrayList<Friends>();
	private static boolean contactListAdded = false;
	private ContactList contactList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		alertDialog = new AlertDialog.Builder(BaseActivity.this).create();
		progressDialog = new ProgressDialog(BaseActivity.this);
		
		if (!contactListAdded) {
			contactList = new ContactList(getBaseContext());
			friendList.addAll(contactList.getContacts());
			contactListAdded = true;
		}

		validateFBLogin();


	}
	
	private Handler BaseActivityHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
        	switch(msg.what) {
        	
        		case(USERSECURITYPIN_COMPLETE):
        			alertDialog.setTitle("Password changed");
				alertDialog.setMessage("Your passcode was successfully changed.");
				alertDialog.setButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int which) {
							showProfileSetup();
							dialog.dismiss();
						}
					});

				alertDialog.show();
        		break;
	        	case(INVALID_DOLLAR):
	        		alertDialog.setTitle("Invalid MeCode");
					alertDialog.setMessage("The meCode must begin with a '$'. Please try again.");
					alertDialog.setButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});

					alertDialog.show();
	        		break;
	        	case(USERSECURITYPIN_FAILED):
					alertDialog.setTitle("Setup Failed");
					alertDialog
							.setMessage("There was an error setting up your security pin: " + 
									message + " Please try again.");
					alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});

					alertDialog.show();
	        		break;

	        	case(USERSECURITYPIN_CONFIRMMISMATCH):
					alertDialog.setTitle("Security Pins Mismatch.");
					alertDialog
							.setMessage("The two security pins you just swiped don't match. Please try again.");
					alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});

					alertDialog.show();
	        		break;
        	}
        }
	};

	private void validateFBLogin() {
		String access_token = prefs.getString("access_token", null);
		long expires = prefs.getLong("access_expires", 0);
		if (access_token != null) {
			facebook.setAccessToken(access_token);
		}
		if (expires != 0) {
			facebook.setAccessExpires(expires);
		}
	}

	public void onResume() {
		super.onResume();
		facebook.extendAccessToken(this, null);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.main_menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		Editor editor = prefs.edit();

		switch (item.getItemId()) {
		case R.id.signOutMenuItem:
			if (!facebook.isSessionValid()) {
				signedInViaFacebook = false;
				editor.remove("userId");
				editor.commit();
			}
			else {
				signedInViaFacebook = false;
				facebookLogout();
			}

			OnSignOutComplete();
			break;
		case R.id.profileMenuItem:

			showProfileSetup();

			break;

		}
		return super.onOptionsItemSelected(item);
	}

	public void OnSignOutComplete() {
		//Do nothing.
		startActivity(new Intent(this, SignInActivity.class));
	}

	public void facebookLogout() {
		mAsyncRunner.logout(this, new RequestListener() {
			@Override
			public void onComplete(String response, Object state) {
				signedInViaFacebook = false;
				Editor editor = prefs.edit();
				editor.remove("userId");
				editor.commit();
			}

			@Override
			public void onIOException(IOException e, Object state) {
			}

			@Override
			public void onFileNotFoundException(FileNotFoundException e,
					Object state) {
			}

			@Override
			public void onMalformedURLException(MalformedURLException e,
					Object state) {
			}

			@Override
			public void onFacebookError(FacebookError e, Object state) {
			}

		});
	}

	public void showProfileSetup() {
		LayoutInflater inflator = getLayoutInflater();
		View view = inflator.inflate(R.layout.setup_profile, null, false);
		view.startAnimation(AnimationUtils.loadAnimation(getBaseContext(),
				R.anim.slide_left_out));
		setContentView(view);

		Button btnCreateProfile = (Button) findViewById(R.id.btnSaveChanges);

		btnCreateProfile.setOnClickListener(new OnClickListener() {
			public void onClick(View argO) {

				EditText txtMeCode = (EditText) findViewById(R.id.meCode);
				if (txtMeCode.getText().toString().charAt(0) != '$')
				{
					BaseActivityHandler.sendEmptyMessage(INVALID_DOLLAR);
				}
				else
				{
					progressDialog.setMessage("Adding Me Code..");
					progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					UserService userService = new UserService();
					UserMeCodeRequest userMeCodeRequest = new UserMeCodeRequest(
							prefs.getString("userId", ""), txtMeCode.getText().toString().trim());
					progressDialog.show();
					userService.createMeCode(userMeCodeRequest);
					txtMeCode.setText("");
					progressDialog.dismiss();
				}
			}
		});

		Button btnChangeSecurityPin = (Button) findViewById(R.id.btnChangeSecurityPin);
		TextView txtViewPin = (TextView) findViewById(R.id.txtViewPin);
		TextView txtNotePin = (TextView) findViewById(R.id.txtNotePin);

		if (prefs.getBoolean("setupSecurityPin", false)) {

		btnChangeSecurityPin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				request = new UserChangeSecurityPinRequest();
				response = new UserChangeSecurityPinResponse();
				changeSecurityPinCurrent();
			}

		});
		}
		else {

			txtViewPin.setText("Setup Security Pin");
			txtNotePin.setText("Click the button to setup your security pin.");
			btnChangeSecurityPin.setText("Setup Security Pin");
			btnChangeSecurityPin.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					startActivityForResult(new Intent(v.getContext(), 
							SecurityPinSetupActivity.class), SETUPSECURITYPIN);
				}

			});
		}
	}

	private void changeSecurityPinCurrent() {
		final Dialog d = new Dialog(this, R.style.CustomDialogTheme);
		d.setContentView(R.layout.security_dialog);

	 	d.getWindow().setLayout(400, 600);
		d.show();

		TextView txtConfirmHeader = (TextView)d.findViewById(R.id.txtConfirmHeader);
		TextView txtConfirmBody = (TextView)d.findViewById(R.id.txtConfirmBody);

		txtConfirmHeader.setText("Current Pin");
		txtConfirmBody.setText("To change your security pin, input your current security pin below.");


		final CustomLockView ctrlSecurityPin = (CustomLockView) d.findViewById(R.id.ctrlSecurityPin);
		ctrlSecurityPin.invalidate();
		ctrlSecurityPin.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				String passcode = ctrlSecurityPin.getPasscode();

				if (passcode.length() > 3) {
					request.CurrentSecurityPin = passcode;
					d.dismiss();

					changeSecurityPinNew();

				} else
					BaseActivityHandler.sendEmptyMessage(USERSECURITYPIN_INVALIDLENGTH);

				return false;

			}
		});

	}

	private void changeSecurityPinNew() {
		final Dialog d = new Dialog(this, R.style.CustomDialogTheme);
		d.setContentView(R.layout.security_dialog);

	 	d.getWindow().setLayout(400, 600);
		d.show();

		TextView txtConfirmHeader = (TextView)d.findViewById(R.id.txtConfirmHeader);
		TextView txtConfirmBody = (TextView)d.findViewById(R.id.txtConfirmBody);

		txtConfirmHeader.setText("New Security Pin");
		txtConfirmBody.setText("To change your security pin, input your new security pin below.");


		final CustomLockView ctrlSecurityPin = (CustomLockView) d.findViewById(R.id.ctrlSecurityPin);
		ctrlSecurityPin.invalidate();
		ctrlSecurityPin.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				String passcode = ctrlSecurityPin.getPasscode();

				if (passcode.length() > 3) {
					request.NewSecurityPin = passcode;
					d.dismiss();

					changeSecurityPinConfirmNew();

				} else
					BaseActivityHandler.sendEmptyMessage(USERSECURITYPIN_INVALIDLENGTH);

				return false;

			}
		});
	}

	private void changeSecurityPinConfirmNew() {
		final Dialog d = new Dialog(this, R.style.CustomDialogTheme);
		d.setContentView(R.layout.security_dialog);

	 	d.getWindow().setLayout(400, 600);
		d.show();

		TextView txtConfirmHeader = (TextView)d.findViewById(R.id.txtConfirmHeader);
		TextView txtConfirmBody = (TextView)d.findViewById(R.id.txtConfirmBody);

		txtConfirmHeader.setText("Confirm Security Pin");
		txtConfirmBody.setText("Put in your new security pin to confirm.");


		final CustomLockView ctrlSecurityPin = (CustomLockView) d.findViewById(R.id.ctrlSecurityPin);
		ctrlSecurityPin.invalidate();
		ctrlSecurityPin.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				String passcode = ctrlSecurityPin.getPasscode();

				if (passcode.length() > 3  && passcode.equals(request.NewSecurityPin)) {
					d.dismiss();

					progressDialog = new ProgressDialog(v.getContext());
					//ProgressDialog.Builder progressDialog = new ProgressDialog.Builder(parent);
					progressDialog.setMessage("Setting up your security pin...");
					progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					progressDialog.show();

					Thread thread = new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							try {
								UserService userService = new UserService();
								request.UserId = prefs.getString("userId", "");
								response = userService.changeSecurityPin(request);
							} catch (Exception e) {
								e.printStackTrace();
							}
							progressDialog.dismiss();

							if(response.Success) {

								BaseActivityHandler.sendEmptyMessage(USERSECURITYPIN_COMPLETE);
							}
							else {
								message = response.ReasonPhrase;
								BaseActivityHandler.sendEmptyMessage(USERSECURITYPIN_FAILED);

							}
						}

					});
					thread.start();

				} 
				else {
					BaseActivityHandler.sendEmptyMessage(USERSECURITYPIN_CONFIRMMISMATCH);
				}

				return false;
			}
		});
	}
}
