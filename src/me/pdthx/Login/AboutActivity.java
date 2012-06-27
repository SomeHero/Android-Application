package me.pdthx.Login;

import me.pdthx.BaseActivity;
import me.pdthx.R;
import android.os.Bundle;

public class AboutActivity extends BaseActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tracker.trackPageView("About");
		setContentView(R.layout.about);
	}
}
