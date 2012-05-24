package me.pdthx;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;

public class BaseActivity extends Activity {
	protected SharedPreferences prefs;
	AlertDialog alertDialog = null;
	
	final private int INVALID_DOLLAR = 0;
	final private int TESTING = 1;
	
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
		case R.id.profileMenuItem:

			showProfileSetup();

			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void OnSignOutComplete() {
		// TODO Auto-generated method stub

	}
	public void showProfileSetup()
	{
		LayoutInflater inflator = getLayoutInflater();
		View view = inflator.inflate(R.layout.setup_profile, null, false);
		view.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_left_out));
		setContentView(view);

		Button btnCreateProfile = (Button) findViewById(R.id.btnCreateProfile);
		
		btnCreateProfile.setOnClickListener(new OnClickListener() {
			public void onClick(View argO) {

				EditText txtMeCode = (EditText) findViewById(R.id.meCode);
				if (txtMeCode.getText().toString().charAt(0) != '$')
				{
					AlertDialog alertDialog = new AlertDialog.Builder(BaseActivity.this).create();
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
				}
				else
				{
					AlertDialog alertDialog = new AlertDialog.Builder(BaseActivity.this).create();
					alertDialog.setTitle("Under Construction");
					alertDialog.setMessage("The profile page is under construction.");
					alertDialog.setButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							});

					alertDialog.show();
				}
			}
		});
	}
}
