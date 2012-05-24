package me.pdthx;

import java.text.NumberFormat;

import com.zubhium.ZubhiumSDK;
import com.zubhium.ZubhiumSDK.CrashReportingMode;

import me.pdthx.Requests.ACHAccountSetupRequest;
import me.pdthx.Requests.UserRegistrationRequest;
import me.pdthx.Requests.UserSetupSecurityPinRequest;
import me.pdthx.Requests.UserVerifyMobileDeviceRequest;
import me.pdthx.Responses.ACHAccountSetupResponse;
import me.pdthx.Responses.SubmitPaymentResponse;
import me.pdthx.Responses.UserRegistrationResponse;
import me.pdthx.Responses.UserSetupSecurityPinResponse;
import me.pdthx.Responses.UserVerifyMobileDeviceResponse;
import me.pdthx.Services.UserService;
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
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

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
		/*case R.id.forgetMeMenuItem:

			editor.clear();
			editor.commit();

			intent = new Intent(GetMoneyActivity.this,
					GetMoneyActivity.class);
			startActivity(intent);

			break;
			*/

		}
		return super.onOptionsItemSelected(item);
	}
	
}
