package me.pdthx.Login;

import android.widget.TextView;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.android.FacebookError;
import com.facebook.android.AsyncFacebookRunner.RequestListener;

import me.pdthx.ACHAccountSetupActivity;
import me.pdthx.BaseActivity;
import me.pdthx.CustomTabActivity;
import me.pdthx.R;
import me.pdthx.Requests.UserFBSignInRequest;
import me.pdthx.Requests.UserSignInRequest;
import me.pdthx.Responses.UserSignInResponse;
import me.pdthx.Services.UserService;
import android.app.Activity;
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

public class SignInUIActivity extends BaseActivity {
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
    final private int FACEBOOK_SIGNIN = 5;
    final private int AUTHENTICATE = 6;
    final private int CLOSEDIALOG = 7;

    private Button facebookButton;
    private Button emailacctButton;
    private TextView forgotInfo;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		tracker.trackPageView("Sign On");
        setContentView(R.layout.signin);

        showSignInActivity();
    }

    private Handler signInHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case(USERSIGNIN_INVALID):
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
                alertDialog.setTitle("Sign In Failed.");
                alertDialog
                .setMessage("Sign In failed. Please try again.");
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alertDialog.show();
                break;

                case(FACEBOOK_SIGNIN):
                    signInRunner();
                break;

                case(AUTHENTICATE):
                    progressDialog.setMessage("Logging into PaidThx...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();
                break;

                case(CLOSEDIALOG):
                    progressDialog.dismiss();
                break;
            }

        }

    };

    public void showSignInActivity() {

        facebookButton = (Button) findViewById(R.id.signin_facebook);
        // emailacctButton = (Button)findViewById(R.id.signin_email);
        // enterID = (EditText)findViewById(R.id.enterEmail);
        // enterPW = (EditText)findViewById(R.id.enterPassword);

        facebookButton.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {

                if (!signedInViaFacebook)
                {
                    if (!facebook.isSessionValid())
                    {
                        String[] permissions = { "email", "read_friendlists" };
                        signInWithFacebook(permissions);
                    }
                    else {
                        requestUserInfo();
                    }
                }

            }
        });

        emailacctButton = (Button) findViewById(R.id.signin_email);
        emailacctButton.setOnClickListener(new OnClickListener() {
            public void onClick(View argO) {
                EditText txtUserName = (EditText) findViewById(R.id.enterEmail);
                EditText txtPassword = (EditText) findViewById(R.id.enterPassword);

                login = txtUserName.getText().toString();
                password = txtPassword.getText().toString();

                signInRunner();
            }
        });

        forgotInfo = (TextView) findViewById(R.id.forgotInfo);
        forgotInfo.setOnClickListener(new OnClickListener() {
           public void onClick(View arg0) {
               startActivity(new Intent(SignInUIActivity.this, ForgotPasswordActivity.class));
           }
        });
    }

    private void signInRunner() {
        Thread thread = null;
        signInHandler.sendEmptyMessage(AUTHENTICATE);

        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    signInUser();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (userSignInResponse.Success) {

                    Editor editor = prefs.edit();
                    editor.putString("userId", userSignInResponse.UserId);
                    editor.putString("mobileNumber",
                        userSignInResponse.MobileNumber);
                    editor.putString("paymentAccountId",
                        userSignInResponse.PaymentAccountId);
                    editor.putBoolean("setupSecurityPin",
                        userSignInResponse.SetupSecurityPin);
                    editor.putInt("upperLimit", userSignInResponse.UpperLimit);
                    editor.putBoolean("hasACHAccount", userSignInResponse.hasACHAccount);

                    if (signedInViaFacebook) {
                        editor.putString("login", "fb_" + login);
                    } else {
                        editor.putString("login", login);
                    }

                    editor.commit();
                    progressDialog.dismiss();

                    if (userSignInResponse.PaymentAccountId.equals("")) {
                        startActivityForResult(new Intent(SignInUIActivity.this,
                            ACHAccountSetupActivity.class),
                            ACHACCOUNT_SETUP);
                    } else {
                        finish();
                        startActivity(new Intent(getApplicationContext(),
                            CustomTabActivity.class));
                    }
                } else {
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
                finish();
                startActivity(new Intent(getApplicationContext(), CustomTabActivity.class));
            }
            else if (requestCode == FACEBOOK_SETUP)
            {
                progressDialog.setMessage("Requesting information from Facebook...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();

                facebook.authorizeCallback(requestCode, resultCode, data);
                requestUserInfo();
            }
        }
        else {
            if (requestCode == ACCOUNT_SETUP && requestCode == FACEBOOK_SETUP) {
                progressDialog.dismiss();
                signInHandler.sendEmptyMessage(USERSIGNIN_FAILED);
            }
        }
    }

    private void requestUserInfo() {
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

                    if (!facebookFriendsAdded) {
                        requestFacebookFriends();
                    }

                    signInHandler.sendEmptyMessage(FACEBOOK_SIGNIN);

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    Editor edit = prefs.edit();
                    edit.remove("access_token");
                    edit.remove("access_expires");
                    edit.commit();
                    facebook.setAccessExpires(0);
                    facebook.setAccessToken(null);
                    signInHandler.sendEmptyMessage(CLOSEDIALOG);
                    signInHandler.sendEmptyMessage(USERSIGNIN_FAILED);
                    e.printStackTrace();
                }
            }

            @Override
            public void onIOException(IOException e, Object state) {
                // TODO Auto-generated method stub
                Log.e("Facebook Error", e.toString());
            }
            @Override
            public void onFileNotFoundException(
                FileNotFoundException e, Object state) {
                // TODO Auto-generated method stub
                Log.e("Facebook Error", e.toString());
            }
            @Override
            public void onMalformedURLException(
                MalformedURLException e, Object state) {
                // TODO Auto-generated method stub
                Log.e("Facebook Error", e.toString());
            }
            @Override
            public void onFacebookError(FacebookError e, Object state) {
                // TODO Auto-generated method stub
                Log.e("Facebook Error", e.toString());
            }

        });
    }

    private void signInUser() {


        if (!signedInViaFacebook) {
            UserSignInRequest userSignInRequest = new UserSignInRequest();
            userSignInRequest.Login = login;
            userSignInRequest.Password = password;
            userSignInResponse = UserService.signInUser(userSignInRequest);
        } else {
            UserFBSignInRequest userFBSignInRequest = new UserFBSignInRequest();
            userFBSignInRequest.IDNumber = login;
            userFBSignInRequest.FirstName = firstName;
            userFBSignInRequest.LastName = lastName;
            userFBSignInRequest.Email = email;
            userFBSignInRequest.DeviceToken = prefs.getString("deviceToken", "");
            userSignInResponse = UserService.signInUser(userFBSignInRequest);
        }
    }
}
