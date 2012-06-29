package me.pdthx.Settings;

import me.pdthx.BaseActivity;
import me.pdthx.ProfileSetupActivity;
import me.pdthx.R;
import me.pdthx.Requests.UserChangeSecurityPinRequest;
import me.pdthx.Responses.Response;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MeCodeSettingsActivity extends BaseActivity {
	private LinearLayout setupMeCode;
	private Button backBtn;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_continued);

		setupMeCode = (LinearLayout) findViewById(R.id.settings_ExtraBtn1);
		TextView title = (TextView)findViewById(R.id.settingsExtraTitle1);
		title.setText("Create a MeCode");
		setupMeCode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(),
						ProfileSetupActivity.class));
			}

		});

		backBtn = (Button) findViewById(R.id.settings_BackButton);
		backBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}

		});

		LinearLayout background = (LinearLayout)findViewById(R.id.settings_ExtraBtn2BG);
		background.setVisibility(View.GONE);
	}
}
