package me.pdthx;

import com.zubhium.ZubhiumSDK;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public final class GetMoneyActivity extends Activity {
	SharedPreferences prefs;

	ZubhiumSDK sdk ;
	
	private Dialog dialog = null;
	
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

			intent = new Intent(GetMoneyActivity.this, SignInActivity.class);
			startActivity(intent);

			break;
		case R.id.forgetMeMenuItem:

			editor.clear();
			editor.commit();

			intent = new Intent(GetMoneyActivity.this,
					GetMoneyActivity.class);
			startActivity(intent);

			break;

		}
		return super.onOptionsItemSelected(item);
	}
	
}
