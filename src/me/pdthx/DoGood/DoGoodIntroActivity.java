package me.pdthx.DoGood;

import me.pdthx.AddMoneyActivity;
import me.pdthx.BaseActivity;
import me.pdthx.FriendsListActivity;
import me.pdthx.R;
import me.pdthx.Models.Friend;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DoGoodIntroActivity extends BaseActivity {

	final private int DONATE_ADD_MONEY = 8;
	final private int DONATE_ADD_ORG = 4;
	private static final int PLEDGE_ADD_MONEY = 8;
	private static final int PLEDGE_ADDING_FRIEND = 7;
	protected static final int PLEDGE_ADD_ORG = 0;
	private LinearLayout addAmount;
	private LinearLayout addContactBtn;
	private LinearLayout addMoneyBtn;
	private LinearLayout addOrgBtn;

	private String recipientUri = "";
	private String comments = "";
	private String errorMessage = "";
	private Friend friend;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dogood_intro);
		setupIntroController();
	}

	public void setupIntroController() {
		Button clickCause = (Button) findViewById(R.id.donate_clickCauseBtn);
		clickCause.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				runOnUiThread(new Runnable() {
					public void run() {
						setContentView(R.layout.dogood_controller);
					}
				});
				showDonateActivity();
			}

		});
		Button clickPledge = (Button) findViewById(R.id.donate_clickPledgeBtn);
		clickPledge.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				runOnUiThread(new Runnable() {
					public void run() {
						setContentView(R.layout.pledge_controller);
					}
				});
				showPledge();
			}

		});
	}

	public void showDonateActivity() {
		Button backBtn = (Button) findViewById(R.id.btnBackBtn);
		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				runOnUiThread(new Runnable() {
					public void run() {
						setContentView(R.layout.dogood_intro);
					}
				});
				setupIntroController();
			}

		});

		addOrgBtn = (LinearLayout) findViewById(R.id.donate_enterOrg);
		addOrgBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(DoGoodIntroActivity.this,
						OrgListActivity.class), DONATE_ADD_ORG);
			}

		});

		addMoneyBtn = (LinearLayout) findViewById(R.id.donate_chooseAmount);
		addMoneyBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(DoGoodIntroActivity.this,
						AddMoneyActivity.class), DONATE_ADD_MONEY);
			}

		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == DONATE_ADD_ORG) {
				Bundle bundle = data.getExtras();
				String amount = bundle.getString("amount");
				if (amount != null) {
					TextView theAmount = (TextView) findViewById(R.id.donate_amountResult);
					theAmount.setText("$"+amount);
					TextView orgResult = (TextView) findViewById(R.id.donate_OrgResult);
					orgResult.setText(bundle.getString("name"));
				} else {
					String currentnav = bundle.getString("nav");

					if (currentnav.equals("nonProfits")) {
						String result = bundle.getString("org");
						TextView orgResult = (TextView) findViewById(R.id.donate_OrgResult);
						orgResult.setText(result);
					} else if (currentnav.equals("allContacts")) {
						addingContactDonate(bundle.getString("id"),
								bundle.getString("paypoint"));
					}
				}
			} else if (requestCode == DONATE_ADD_MONEY) {
				Bundle bundle = data.getExtras();
				String amount = bundle.getString("index");
				TextView theResult = (TextView) findViewById(R.id.donate_amountResult);
				theResult.setText("$"+amount);
			} else if (requestCode == PLEDGE_ADDING_FRIEND) {
				Bundle bundle = data.getExtras();
				addingContact(bundle.getString("id"),
						bundle.getString("paypoint"));
			} else if (requestCode == PLEDGE_ADD_MONEY) {
				Bundle bundle = data.getExtras();
				String amount = bundle.getString("index");
				TextView theResult = (TextView) findViewById(R.id.donate_amountResult);
				theResult.setText("$" + amount);
			} else if (requestCode == PLEDGE_ADD_ORG) {
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

	public void showPledge() {
		Button backBtn = (Button) findViewById(R.id.btnBackBtn);
		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				runOnUiThread(new Runnable() {
					public void run() {
						setContentView(R.layout.dogood_intro);
					}
				});
				setupIntroController();
			}

		});

		addOrgBtn = (LinearLayout) findViewById(R.id.donate_chooseOrg);
		addOrgBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(DoGoodIntroActivity.this,
						OrgListActivity.class), PLEDGE_ADD_ORG);
			}
		});

		addAmount = (LinearLayout) findViewById(R.id.donate_chooseAmount);
		addAmount.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(DoGoodIntroActivity.this,
						AddMoneyActivity.class), PLEDGE_ADD_MONEY);
			}

		});

		addContactBtn = (LinearLayout) findViewById(R.id.donate_chooseContact);
		addContactBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(DoGoodIntroActivity.this,
						FriendsListActivity.class), PLEDGE_ADDING_FRIEND);
			}

		});

	}

	private void addingContact(String id, String paypoint) {
		Friend chosenContact = new Friend();
		if (!id.equals("")) {
			chosenContact.setId(id);
			friend = friendsList.get(friendsList.indexOf(chosenContact));

			if (friend.isFBContact()) {
				recipientUri = "fb_" + friend.getId();
				TextView contactResult = (TextView) findViewById(R.id.donate_contactResult);
				contactResult.setText(friend.getName() + ": " + friend.getId());
			} else {
				recipientUri = "" + friend.getPaypoint();
				TextView contactResult = (TextView) findViewById(R.id.donate_contactResult);
				contactResult.setText(friend.toString());
			}
		} else {
			chosenContact.setName("New Contact");
			chosenContact.setPaypoint(paypoint);
			friend = chosenContact;
			recipientUri = "" + paypoint;
			TextView contactResult = (TextView) findViewById(R.id.donate_contactResult);
			contactResult.setText("New contact: " + paypoint);
		}
	}
}
