package me.pdthx;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import com.facebook.android.*;
import com.facebook.android.AsyncFacebookRunner.*;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class BaseActivity extends Activity{
	protected SharedPreferences prefs;
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
		case R.id.forgetMeMenuItem:

			editor.clear();
			editor.commit();

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

}
