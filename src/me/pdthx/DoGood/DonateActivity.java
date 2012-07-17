package me.pdthx.DoGood;

import me.pdthx.AddMoneyActivity;
import me.pdthx.BaseActivity;
import me.pdthx.R;
import me.pdthx.SendPaymentActivity;
import me.pdthx.Models.Friend;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DonateActivity extends BaseActivity {
	final private int ADD_MONEY = 8;
	final private int ADD_ORG = 4;
	private LinearLayout addMoneyBtn;
	private LinearLayout addOrgBtn;

	private String recipientUri = "";
	private String comments = "";
	private String errorMessage = "";
	private Friend friend;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dogood_controller);
		showDonateActivity();
	}

	public void showDonateActivity() {
		Button backBtn = (Button) findViewById(R.id.btnBackBtn);
		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}

		});

		addOrgBtn = (LinearLayout) findViewById(R.id.donate_enterOrg);
		addOrgBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(DonateActivity.this,
						OrgListActivity.class), ADD_ORG);
			}

		});

		addMoneyBtn = (LinearLayout) findViewById(R.id.donate_chooseAmount);
		addMoneyBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(DonateActivity.this,
						AddMoneyActivity.class), ADD_MONEY);
			}

		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == ADD_ORG) {
				Bundle bundle = data.getExtras();
				String currentnav = bundle.getString("nav");

				if (currentnav.equals("nonProfits")) {
					String result = bundle.getString("org");
					TextView orgResult = (TextView) findViewById(R.id.donate_OrgResult);
					orgResult.setText(result);
				} else if (currentnav.equals("allContacts")) {
					addingContactDonate(bundle.getString("id"),
							bundle.getString("paypoint"));
				}
			} else if (requestCode == ADD_MONEY) {
				Bundle bundle = data.getExtras();
				String amount = bundle.getString("index");
				TextView theResult = (TextView) findViewById(R.id.donate_amountResult);
				theResult.setText(amount);
			}
		} else {
			if (requestCode != ADD_MONEY && requestCode != ADD_ORG) {
				finish();
			}
		}
	}

	private void addingContactDonate(String id, String paypoint) {
		Friend chosenContact = new Friend();
		if (!id.equals("")) {
			chosenContact.setId(id);
			friend = friendsList.get(friendsList.indexOf(chosenContact));

			if (friend.isFBContact()) {
				recipientUri = "fb_" + friend.getId();
				TextView contactResult = (TextView) findViewById(R.id.donate_OrgResult);
				contactResult.setText(friend.getName() + ": " + friend.getId());
			} else {
				recipientUri = "" + friend.getPaypoint();
				TextView contactResult = (TextView) findViewById(R.id.donate_OrgResult);
				contactResult.setText(friend.toString());
			}
		} else {
			chosenContact.setName("New Contact");
			chosenContact.setPaypoint(paypoint);
			friend = chosenContact;
			recipientUri = "" + paypoint;
			TextView contactResult = (TextView) findViewById(R.id.donate_OrgResult);
			contactResult.setText("New contact: " + paypoint);
		}
	}

}
