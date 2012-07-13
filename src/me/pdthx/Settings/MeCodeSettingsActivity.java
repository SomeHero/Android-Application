package me.pdthx.Settings;

import me.pdthx.BaseActivity;
import me.pdthx.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MeCodeSettingsActivity extends BaseActivity {
	private LinearLayout setupMeCode;

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
						MeCodeSetupActivity.class));
			}

		});


		LinearLayout background = (LinearLayout)findViewById(R.id.settings_ExtraBtn2BG);
		background.setVisibility(View.GONE);
	}
}
