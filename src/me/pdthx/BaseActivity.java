package me.pdthx;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import me.pdthx.Models.Friend;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.zubhium.ZubhiumSDK;
import com.zubhium.ZubhiumSDK.CrashReportingMode;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class BaseActivity extends Activity {
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
	
	private ZubhiumSDK sdk;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		signedInViaFacebook = prefs.getBoolean("signedInViaFacebook", false);

		alertDialog = new AlertDialog.Builder(BaseActivity.this).create();
		progressDialog = new ProgressDialog(BaseActivity.this);

		sdk = ZubhiumSDK.getZubhiumSDKInstance(this, getString(R.string.secret_key));
		
	    if(sdk != null){
	    	sdk.setCrashReportingMode(CrashReportingMode.SILENT);
	    }
		
		if (!contactListAdded && friendsList.size() == 0) {
			contactList = new ContactList(getBaseContext());
			friendsList.addAll(contactList.getContacts());
			contactListAdded = true;
		}

		validateFBLogin();

		if (signedInViaFacebook && !facebookFriendsAdded) {
			requestFacebookFriends();
		}

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

	private void requestFacebookFriends() {

		if (signedInViaFacebook && !facebookFriendsAdded) {
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
							Friend f = new Friend();
							f.setId(id);
							f.setName(n);
							f.setType("Facebook");
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
			if (prefs.getString("userId", "").length() != 0) {
				contactListAdded = false;
				facebookFriendsAdded = false;
				friendsList.clear();
				
				signedInViaFacebook = false;
				editor.remove("userId");
				editor.remove("signedInViaFacebook");
				editor.commit();
				

				if (!facebook.isSessionValid()) {
					startActivityForResult(new Intent(this, SignInActivity.class), 1);
				}
				else {
					facebookLogout();
					
				}

			}
			break;
		case R.id.profileMenuItem:

			startActivity(new Intent(this, ProfileSetupActivity.class));

			break;

		}
		return super.onOptionsItemSelected(item);
	}


	public void facebookLogout() {
		mAsyncRunner.logout(this, new RequestListener() {
			@Override
			public void onComplete(String response, Object state) {
				startActivityForResult(new Intent(BaseActivity.this, SignInActivity.class), 1);
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
