package me.pdthx;

import com.zubhium.ZubhiumSDK;
import com.zubhium.ZubhiumSDK.CrashReportingMode;

import android.app.Activity;
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

public class BaseActivity extends Activity{
	protected SharedPreferences prefs;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		prefs = PreferenceManager.getDefaultSharedPreferences(this);

	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.main_menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		Intent intent = null;

		Editor editor = prefs.edit();

		switch (item.getItemId()) {
		case R.id.signOutMenuItem:
			editor.remove("userId");
			editor.commit();

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
	
}
