package me.pdthx.Accounts;

import me.pdthx.BaseActivity;
import me.pdthx.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class LinkAccountActivity extends BaseActivity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// userInfo = new UserSignInResponse();
		// userInfo.UserId = prefs.getString("userId", "");
		// userInfo.PaymentAccountId = prefs.getString("paymentAccountId", "");
		showLinkScreen();
	}

	private void showLinkScreen() {
		setContentView(R.layout.account_bank_select);


		Button btnAccountNumber = (Button) findViewById(R.id.btn_enterAcctNumber);
		btnAccountNumber.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(),
						ACHAccountSetupActivity.class));
			}

		});

		/**
		 * BANKS: MAYBE POPULATE A LIST INSTEAD OF HARDCODED
		 */

		LinearLayout bankofAmerica = (LinearLayout) findViewById(R.id.ach_bankofAmericaLogin);
		bankofAmerica.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				runOnUiThread(new Runnable() {
					public void run() {
						setContentView(R.layout.ach_link_acct);
						showBankofAmericaScreen();
					}
				});
			}

		});

		LinearLayout chase = (LinearLayout) findViewById(R.id.ach_chaseLogin);
		chase.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				runOnUiThread(new Runnable() {
					public void run() {
						setContentView(R.layout.ach_link_acct);
						showChaseScreen();
					}
				});
			}

		});

		LinearLayout citibank = (LinearLayout) findViewById(R.id.ach_citibankLogin);
		citibank.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				runOnUiThread(new Runnable() {
					public void run() {
						setContentView(R.layout.ach_link_acct);
						showCitibankScreen();
					}
				});
			}

		});

		LinearLayout capitalOne = (LinearLayout) findViewById(R.id.ach_capitalOneLogin);
		capitalOne.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				runOnUiThread(new Runnable() {
					public void run() {
						setContentView(R.layout.ach_link_acct);
						showCapitalOneScreen();
					}
				});
			}

		});
	}

	private void showBankofAmericaScreen() {

		Button linkAutomatically = (Button)findViewById(R.id.btnloginLinkInstantly);
		linkAutomatically.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				EditText usernameTxtBox = (EditText) findViewById(R.id.achAdd_enterID);
				EditText passwordTxtBox = (EditText)findViewById(R.id.achAdd_enterPasscode);

				String username = usernameTxtBox.getText().toString().trim();
				String password = passwordTxtBox.getText().toString().trim();

				// web service calls?
			}

		});
	}

	private void showChaseScreen() {

		ImageView bankLogo = (ImageView) findViewById(R.id.achAdd_imgofBank);
		bankLogo.setImageResource(R.drawable.chase_logo);


		Button linkAutomatically = (Button)findViewById(R.id.btnloginLinkInstantly);
		linkAutomatically.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				EditText usernameTxtBox = (EditText) findViewById(R.id.achAdd_enterID);
				EditText passwordTxtBox = (EditText)findViewById(R.id.achAdd_enterPasscode);

				String username = usernameTxtBox.getText().toString().trim();
				String password = passwordTxtBox.getText().toString().trim();

				// web service calls?
			}

		});
	}

	private void showCitibankScreen() {

		ImageView bankLogo = (ImageView) findViewById(R.id.achAdd_imgofBank);
		bankLogo.setImageResource(R.drawable.citibank_logo);


		Button linkAutomatically = (Button)findViewById(R.id.btnloginLinkInstantly);
		linkAutomatically.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				EditText usernameTxtBox = (EditText) findViewById(R.id.achAdd_enterID);
				EditText passwordTxtBox = (EditText)findViewById(R.id.achAdd_enterPasscode);

				String username = usernameTxtBox.getText().toString().trim();
				String password = passwordTxtBox.getText().toString().trim();

				// web service calls?
			}

		});
	}

	private void showCapitalOneScreen() {

		ImageView bankLogo = (ImageView) findViewById(R.id.achAdd_imgofBank);
		bankLogo.setImageResource(R.drawable.capitalone_logo);


		Button linkAutomatically = (Button)findViewById(R.id.btnloginLinkInstantly);
		linkAutomatically.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				EditText usernameTxtBox = (EditText) findViewById(R.id.achAdd_enterID);
				EditText passwordTxtBox = (EditText)findViewById(R.id.achAdd_enterPasscode);

				String username = usernameTxtBox.getText().toString().trim();
				String password = passwordTxtBox.getText().toString().trim();

				// web service calls?
			}

		});
	}
}
