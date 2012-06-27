package me.pdthx.Setup;

import me.pdthx.ACHAccountSetupActivity;
import me.pdthx.BaseActivity;
import me.pdthx.CustomTabActivity;
import me.pdthx.R;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class EnableActivity extends BaseActivity {
	TextView userName;
	TextView amountWaiting;
	ImageView incomingPaymentUserImg;
	TextView incomingPaymentUserName;
	TextView incomingPaymentUserDate;
	TextView incomingPaymentUserCmmt;
	Button addBtn;
	Button remindLater;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setupEnable();
	}

	private void setupEnable() {
		setContentView(R.layout.enable_payments);
		
		userName = (TextView) findViewById(R.id.enable_username);
		amountWaiting = (TextView) findViewById(R.id.enable_amountWaiting);
		incomingPaymentUserImg = (ImageView) findViewById(R.id.enable_incoming_UserImg);
		incomingPaymentUserName = (TextView) findViewById(R.id.enable_incomingUser);
		incomingPaymentUserDate = (TextView) findViewById(R.id.enable_incomingDate);
		incomingPaymentUserCmmt = (TextView) findViewById(R.id.enable_incomingComment);
		addBtn = (Button) findViewById(R.id.enable_addAcct);
		addBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
				startActivity(new Intent(getApplicationContext(), ACHAccountSetupActivity.class));
			}

		});
		remindLater = (Button) findViewById(R.id.enable_rmdLater);

		remindLater.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
				startActivity(new Intent(getApplicationContext(), CustomTabActivity.class));
			}

		});
	}

}
