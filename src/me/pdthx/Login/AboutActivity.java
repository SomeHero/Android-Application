package me.pdthx.Login;

import me.pdthx.BaseActivity;
import me.pdthx.R;
import android.os.Bundle;
import android.view.View;

public class AboutActivity extends BaseActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(View.inflate(this, R.layout.about, null));
	}
}
