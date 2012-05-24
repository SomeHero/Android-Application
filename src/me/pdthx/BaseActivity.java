package me.pdthx;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import com.facebook.android.*;
import com.facebook.android.AsyncFacebookRunner.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;

public class BaseActivity extends Activity{
	protected SharedPreferences prefs;
	private AlertDialog alertDialog = null;
	
	final private int INVALID_DOLLAR = 0;
	final private int TESTING = 1;
	protected Facebook facebook = new Facebook("332189543469634");
	protected AsyncFacebookRunner mAsyncRunner = new AsyncFacebookRunner(facebook);
	protected static boolean signedInViaFacebook = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		validateFBLogin();


	}

	private void validateFBLogin() {
		String access_token = prefs.getString("access_token", null);
		long expires = prefs.getLong("access_expires", 0);
		if(access_token != null) {
			facebook.setAccessToken(access_token);
		}
		if(expires != 0) {
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
				editor.remove("userId");
				editor.commit();
			}
			else {
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
		// TODO Auto-generated method stub

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
			public void onIOException(IOException e, Object state) {}

			@Override
			public void onFileNotFoundException(FileNotFoundException e,
					Object state) {}

			@Override
			public void onMalformedURLException(MalformedURLException e,
					Object state) {}

			@Override
			public void onFacebookError(FacebookError e, Object state) {}
		});
	}
	
	public void showProfileSetup()
	{
		LayoutInflater inflator = getLayoutInflater();
		View view = inflator.inflate(R.layout.setup_profile, null, false);
		view.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_left_out));
		setContentView(view);

//		Button btnCreateProfile = (Button) findViewById(R.id.btnCreateProfile);
//		
//		btnCreateProfile.setOnClickListener(new OnClickListener() {
//			public void onClick(View argO) {
//
//				EditText txtMeCode = (EditText) findViewById(R.id.meCode);
//				if (txtMeCode.getText().toString().charAt(0) != '$')
//				{
//					AlertDialog alertDialog = new AlertDialog.Builder(BaseActivity.this).create();
//					alertDialog.setTitle("Invalid MeCode");
//					alertDialog.setMessage("The meCode must begin with a '$'. Please try again.");
//					alertDialog.setButton("OK",
//							new DialogInterface.OnClickListener() {
//								public void onClick(DialogInterface dialog,
//										int which) {
//									dialog.dismiss();
//								}
//							});
//
//					alertDialog.show();
//				}
//				else
//				{
//					AlertDialog alertDialog = new AlertDialog.Builder(BaseActivity.this).create();
//					alertDialog.setTitle("Under Construction");
//					alertDialog.setMessage("The profile page is under construction.");
//					alertDialog.setButton("OK",
//							new DialogInterface.OnClickListener() {
//								public void onClick(DialogInterface dialog,
//										int which) {
//									dialog.dismiss();
//								}
//							});
//
//					alertDialog.show();
//				}
//			}
//		});
	}

}
