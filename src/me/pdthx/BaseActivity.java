package me.pdthx;

import android.content.pm.ActivityInfo;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import me.pdthx.Login.TabUIActivity;
import me.pdthx.Models.Friend;
import me.pdthx.Settings.SettingsActivity;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.zubhium.ZubhiumSDK;
import com.zubhium.ZubhiumSDK.CrashReportingMode;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class BaseActivity extends Activity {

	public static final String TAG = "BaseActivity";
	protected SharedPreferences prefs;
	protected AlertDialog alertDialog;
	protected ProgressDialog progressDialog;

	protected Facebook facebook = new Facebook("332189543469634");
	protected AsyncFacebookRunner mAsyncRunner = new AsyncFacebookRunner(
			facebook);
	protected static boolean signedInViaFacebook = false;
	protected static ArrayList<Friend> friendsList = new ArrayList<Friend>();
	private static boolean contactListAdded = false;
	protected static boolean facebookFriendsAdded = false;
	private ContactList contactList;
	protected static Thread contactThread;

	protected int RETURNFROM_PROFILESETUP = 10;

	protected GoogleAnalyticsTracker tracker;
	private ZubhiumSDK sdk;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		tracker = GoogleAnalyticsTracker.getInstance();
		tracker.startNewSession("UA-30208011-10", 5, this);
		setContentView(R.layout.main);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		signedInViaFacebook = prefs.getBoolean("signedInViaFacebook", false);

		alertDialog = new AlertDialog.Builder(BaseActivity.this).create();
		progressDialog = new ProgressDialog(BaseActivity.this);

		Editor editor = prefs.edit();
		editor.putString("deviceToken", Secure.getString(getBaseContext().getContentResolver(),
				Secure.ANDROID_ID));
		editor.commit();

		sdk = ZubhiumSDK.getZubhiumSDKInstance(this, getString(R.string.secret_key));

		if(sdk != null){
			sdk.setCrashReportingMode(CrashReportingMode.SILENT);
		}

		validateFBLogin();

		if (signedInViaFacebook && !facebookFriendsAdded)
		{
			if (friendsList.size() == 0)
			{
				requestFacebookFriends();
			}
			else
			{
				facebookFriendsAdded = true;
			}
		}

		if (contactList == null || contactList.getContacts().size() == 0)
		{
			Runnable run = new Runnable() {
				public void run() {
					contactList = new ContactList(getBaseContext());
					friendsList.addAll(contactList.getContacts());
				}
			};

			if (!contactListAdded)
			{
				contactListAdded = true;
				contactThread = new Thread(run);
				contactThread.start();
			}
		}
		else
		{
			friendsList.addAll(contactList.getContacts());
			contactListAdded = true;
		}

	}

	public void registerPushNotifications() {
		Intent registrationIntent = new Intent("com.google.android.c2dm.intent.REGISTER");
		registrationIntent.putExtra("app", PendingIntent.getBroadcast(this, 1, new Intent(), 0));
		registrationIntent.putExtra("sender", "android.paidthx@gmail.com");
		startService(registrationIntent);
	}


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

	public void signInWithFacebook(String[] permissions) {
		if (!facebook.isSessionValid())
		{
			facebook.authorize(this, permissions, 2,
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
					Log.d(error.toString(), error.toString());

				}

				public void onError(DialogError e) {
					Log.d(e.toString(), e.toString());
				}

				public void onCancel() {
					Log.d("Canceled", "Canceled");
				}
			});
		}
	}

	private void requestFacebookFriends() {

		if (signedInViaFacebook && !facebookFriendsAdded) {
			mAsyncRunner.request("me/friends", new RequestListener(){

				@Override
				public void onComplete(String response, Object state) {

					try {
						JSONObject json = new JSONObject(response);
						JSONArray d = json.getJSONArray("data");
						int l = (d != null ? d.length() : 0);
						Log.d("Requesting Friends, BaseActivity", "d.length(): " + l);
						for (int i=0; i<l; i++) {
							JSONObject o = d.getJSONObject(i);
							String n = o.getString("name");
							String id = o.getString("id");
							Friend f = new Friend();
							f.setId(id);
							f.setName(n);
							f.setFBContact(true);
							friendsList.add(f);
							Log.d(f.getName() + ": " + f.getId(), "Facebook Friends");			//SWEEETTNNEEESESSS

						}

						facebookFriendsAdded = true;

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

	public void onResume() {
		super.onResume();
		facebook.extendAccessTokenIfNeeded(this, null);
	}

	@Override
	public void onBackPressed()
	{
	    if (!progressDialog.isShowing())
	    {
	        finish();
	    }
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		if (prefs.getString("userId", "").length() != 0) {
			MenuInflater menuInflater = getMenuInflater();
			menuInflater.inflate(R.menu.main_menu, menu);

			return true;
		}

		return false;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.signOutMenuItem:

			logout();

			break;
		case R.id.profileMenuItem:

			startActivity(new Intent(this, SettingsActivity.class));
			break;
		}

		return super.onOptionsItemSelected(item);
	}


	public void logout() {

		facebookFriendsAdded = false;
		friendsList.clear();

		contactListAdded = false;
		signedInViaFacebook = false;
		Editor editor = prefs.edit();
		editor.remove("userId");
		editor.remove("signedInViaFacebook");
		editor.remove("facebookFriendsAdded");
		editor.commit();


		if (!facebook.isSessionValid()) {
			startActivityForResult(new Intent(this, TabUIActivity.class), 1);
		}
		else {

			mAsyncRunner.logout(this, new RequestListener() {
				@Override
				public void onComplete(String response, Object state) {
					startActivityForResult(new Intent(BaseActivity.this, TabUIActivity.class), 1);
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
	}
}
