package me.pdthx;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.*;
import com.facebook.android.Facebook.DialogListener;

import me.pdthx.Models.Friends;
import me.pdthx.Requests.UserFBSignInRequest;
import me.pdthx.Requests.UserSignInRequest;
import me.pdthx.Responses.UserSignInResponse;
import me.pdthx.Services.UserService;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class SignInActivity extends BaseActivity {

	private String login = "";
	private String password = "";
	private String firstName = "";
	private String lastName = "";
	private String email = "";
	
	private UserSignInResponse userSignInResponse = null;

	private final int ACCOUNT_SETUP = 1;
	private final int FACEBOOK_SETUP = 2;
	private final int ACHACCOUNT_SETUP = 3;	

	final private int USERSIGNIN_INVALID = 0;
	final private int USERSIGNIN_FAILED = 4;

	// private Handler _handler = null;
	// private Activity parent = this;

	// private SharedPreferences _prefs;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(View.inflate(this, R.layout.signin_controller, null));

		showSignInActivity();
	}

	Handler signInHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case(USERSIGNIN_INVALID):
				alertDialog = new AlertDialog.Builder(SignInActivity.this)
			.create();
			alertDialog.setTitle("Invalid Account Credentials.");
			alertDialog
			.setMessage("The username and password you entered were invalid. Please try again.");
			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});

			alertDialog.show();
			break;

			case(USERSIGNIN_FAILED):
				alertDialog = new AlertDialog.Builder(SignInActivity.this)
			.create();
			alertDialog.setTitle("Account Creation Failed.");
			alertDialog
			.setMessage("Account creation failed. Please try again.");
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

	// public SignInActivity(Activity parentActivity, Handler handler,
	// SharedPreferences prefs) {
	// parent = parentActivity;
	// _handler = handler;
	// _prefs = prefs;
	// }

	public void showSignInActivity() {

		// deviceId = Secure.getString(getBaseContext().getContentResolver(),
		// Secure.ANDROID_ID);

		Button btnFBSignIn = (Button) findViewById(R.id.btnFBSignIn);

		btnFBSignIn.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {

				if (!signedInViaFacebook) {
					String[] permissions = { "email", "read_friendlists" };

					facebook.authorize(SignInActivity.this, permissions, 2,
							new DialogListener() {
						public void onComplete(Bundle values) {
							Editor editor = prefs.edit();
							editor.putString("access_token",
									facebook.getAccessToken());
							editor.putLong("access_expires",
									facebook.getAccessExpires());
							editor.commit();
						}

						public void onFacebookError(FacebookError error) {
						}

						public void onError(DialogError e) {
						}

						public void onCancel() {
						}
					});
				}

			}
		});

		Button btnSignIn = (Button) findViewById(R.id.btnSignInSubmit);

		btnSignIn.setOnClickListener(new OnClickListener() {
			public void onClick(View argO) {
				EditText txtUserName = (EditText) findViewById(R.id.txtUserName);
				EditText txtPassword = (EditText) findViewById(R.id.txtPassword);

				login = txtUserName.getText().toString();
				password = txtPassword.getText().toString();

				signInRunner();
			}
		});

		Button btnSetupAccount = (Button) findViewById(R.id.btnCreateAnAccount);

		btnSetupAccount.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				startActivityForResult(new Intent(view.getContext(),
						SignUpActivity.class), 1);
			}
		});

	}

	private void signInRunner() {
		Thread thread = null;
		//		progressDialog.setMessage("Authenticating...");
		//		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		//		progressDialog.show();

		thread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					signInUser();
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (userSignInResponse.IsValid) {

					Editor editor = prefs.edit();
					editor.putString("userId", userSignInResponse.UserId);
					editor.putString("mobileNumber", userSignInResponse.MobileNumber);
					editor.putString("paymentAccountId", userSignInResponse.PaymentAccountId);
					editor.putBoolean("setupSecurityPin", userSignInResponse.SetupSecurityPin);
					editor.putInt("upperLimit", userSignInResponse.UpperLimit);
					
					if (signedInViaFacebook) {
						editor.putString("login", "fb_" + login);
					}
					else {
						editor.putString("login", login);
					}
					
					editor.commit();
					progressDialog.dismiss();


					if (userSignInResponse.PaymentAccountId.equals("")) {
						startActivityForResult(new Intent(SignInActivity.this, 
								ACHAccountSetupActivity.class), ACHACCOUNT_SETUP);
					}
					else {
						setResult(RESULT_OK);
						finish();
					}
				}
				else {
					progressDialog.dismiss();
					signInHandler.sendEmptyMessage(USERSIGNIN_INVALID);
				}
			}

		});
		thread.start();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == Activity.RESULT_OK) {
			
			if (requestCode == ACCOUNT_SETUP) {
				login = data.getStringExtra("email");
				password = data.getStringExtra("password");
				
				signInRunner();
			}
			else if (requestCode == ACHACCOUNT_SETUP) {
				setResult(RESULT_OK);
				finish();
			}
			else if (requestCode == FACEBOOK_SETUP)
			{
				facebook.authorizeCallback(requestCode, resultCode, data);
				progressDialog.setMessage("Authenticating...");
				progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				progressDialog.show();

				mAsyncRunner.request("me", new RequestListener(){

					@Override
					public void onComplete(String response, Object state) {
						// TODO Auto-generated method stub
						JSONObject result;

						try {
							result = new JSONObject(response);
							login = result.getString("id");
							firstName = result.getString("first_name");
							lastName = result.getString("last_name");
							email = result.getString("email");
							signedInViaFacebook = true;
							signInRunner();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
					@Override
					public void onIOException(IOException e, Object state) {
						// TODO Auto-generated method stub	
					}
					@Override
					public void onFileNotFoundException(
							FileNotFoundException e, Object state) {
						// TODO Auto-generated method stub						
					}
					@Override
					public void onMalformedURLException(
							MalformedURLException e, Object state) {
						// TODO Auto-generated method stub
					}
					@Override
					public void onFacebookError(FacebookError e, Object state) {
						// TODO Auto-generated method stub	
					}

				});

				facebook.authorizeCallback(requestCode, resultCode, data);
				mAsyncRunner.request("me/friends", new RequestListener(){

					@Override
					public void onComplete(String response, Object state) {

						try {
							JSONObject json = new JSONObject(response);
							JSONArray d = json.getJSONArray("data");
							int l = (d != null ? d.length() : 0);
							Log.d("Facebook-Example-Friends Request", "d.length(): " + l);
							for (int i=0; i<l; i++) {
								JSONObject o = d.getJSONObject(i);
								String n = o.getString("name");
								String id = o.getString("id");
								Friends f = new Friends();
								f.id = id;
								f.name = n;
								f.type = "Facebook";
								friendList.add(f);
								Log.d(f.name + ": " + f.id, "Facebook Friends");			//SWEEETTNNEEESESSS

							}
							
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();      
						}
					}

					@Override
					public void onIOException(IOException e, Object state) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onFileNotFoundException(FileNotFoundException e,
							Object state) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onMalformedURLException(MalformedURLException e,
							Object state) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onFacebookError(FacebookError e, Object state) {
						// TODO Auto-generated method stub

					}

				});
			}
		}
		else {
			if (requestCode == ACCOUNT_SETUP && requestCode == FACEBOOK_SETUP) {
				progressDialog.dismiss();
				signInHandler.sendEmptyMessage(USERSIGNIN_FAILED);
			}
		}
	}

	private void signInUser() {
		UserService userService = new UserService();
		
		
		if (!signedInViaFacebook) {
			UserSignInRequest userSignInRequest = new UserSignInRequest();
			userSignInRequest.Login = login;
			userSignInRequest.Password = password;
			userSignInResponse = userService.SignInUser(userSignInRequest);
		} else {
			UserFBSignInRequest userFBSignInRequest = new UserFBSignInRequest();
			userFBSignInRequest.IDNumber = login;
			userFBSignInRequest.FirstName = firstName;
			userFBSignInRequest.LastName = lastName;
			userFBSignInRequest.Email = email;
			userSignInResponse = userService.SignInUser(userFBSignInRequest);
		}
	}
}
