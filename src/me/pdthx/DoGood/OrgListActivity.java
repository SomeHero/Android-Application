package me.pdthx.DoGood;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import me.pdthx.BaseActivity;
import me.pdthx.FriendsListActivity;
import me.pdthx.R;
import me.pdthx.SendPaymentActivity;
import me.pdthx.Adapters.FriendAdapter;
import me.pdthx.Adapters.OrganizationAdapter;
import me.pdthx.Adapters.PaystreamAdapter;
import me.pdthx.Helpers.PhoneNumberFormatter;
import me.pdthx.Models.Friend;
import me.pdthx.Models.Organization;
import me.pdthx.Responses.OrganizationResponse;
import me.pdthx.Services.PaymentServices;

public class OrgListActivity extends BaseActivity {
	protected static final int CHANGE_LIST = 6;
	private static final int CHOSE_ORG = 5;
	private static final int ADDING_FRIEND = 2;
	private ListView theList;
	private ArrayList<Organization> orgList;
	private OrganizationAdapter adapter;
	private String currentNav;
	private Button submitSearch;
	private EditText searchBar;

	private ArrayList<Friend> allContacts;
	private FriendAdapter m_adapter;
	private final static int SETFRIENDIMAGE = 1;
	private static HashMap<String, Bitmap> pictureMap;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.donate_contacts);
		currentNav = "nonProfits";

		progressDialog.setMessage("Loading contacts... please wait.");
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.show();

		if (contactThread != null && contactThread.isAlive()) {
			try {
				contactThread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		pictureMap = new HashMap<String, Bitmap>();

		showOrgController();
	}

	private void showOrgController() {
		allContacts = new ArrayList<Friend>();
		allContacts.addAll(contactList);
		allContacts.addAll(friendsList);
		Collections.sort(allContacts);

		createFakeList();

		progressDialog.dismiss();

		Button backBtn = (Button) findViewById(R.id.btnBackBtn);
		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}

		});

		Button filterList = (Button) findViewById(R.id.donate_dropList);
		filterList.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(OrgListActivity.this,
						DoGoodDialog.class), CHANGE_LIST);
			}

		});

		submitSearch = (Button) findViewById(R.id.donate_submitSearch);
		submitSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				EditText ref = (EditText) findViewById(R.id.donate_searchOrg);
				String findRef = ref.getText().toString().trim();
				if (findRef.length() > 0) {
					ArrayList<Organization> change = searchList(findRef);
					adapter = new OrganizationAdapter(getApplicationContext(),
							R.layout.dogood_org_item, change);
					theList.setAdapter(adapter);
				} else {
					adapter = new OrganizationAdapter(getApplicationContext(),
							R.layout.dogood_org_item, orgList);
					theList.setAdapter(adapter);
				}
			}

		});

		searchBar = (EditText) findViewById(R.id.donate_searchOrg);
		searchBar.addTextChangedListener(new TextWatcher() {

			String current = "";

			@Override
			public void afterTextChanged(Editable s) {
				if (currentNav.equals("allContacts")) {

					ArrayList<Friend> searched = new ArrayList<Friend>();
					current = s.toString();

					for (int x = 0; x < friendsList.size(); x++) {

						Friend friend = friendsList.get(x);
						if (friend.masterSearch(current.toLowerCase())) {
							searched.add(friend);
						}
					}

					if (searchBar.getText().toString().length() == 0) {
						m_adapter = new FriendAdapter(OrgListActivity.this,
								R.layout.friend_item, friendsList);
					} else {
						if (searched.size() > 0) {
							m_adapter = new FriendAdapter(OrgListActivity.this,
									R.layout.friend_item, searched);
						} else {
							Log.d("No match found", "Maybe new person?");
							Friend friend = new Friend();

							friend.setName("'" + current + "' not found");
							friend.setPaypoint("Continue typing or check entry");

							if (current.matches("[0-9()-]+")) {
								String phone = current.replaceAll("[^0-9]", "");
								if (phone.length() == 10 || phone.length() == 7) {
									friend.setName("New Phone Contact");
									friend.setPaypoint(PhoneNumberFormatter
											.formatNumber(phone));
								}
							}

							if (current.contains("@") && current.contains(".")) {
								friend.setName("New Email Address");
								friend.setPaypoint(current);
							}

							if (current.charAt(0) == '$') {
								friend.setName("New MeCode");
								friend.setPaypoint(current);
							}

							ArrayList<Friend> newContact = new ArrayList<Friend>();
							newContact.add(friend);
							m_adapter = new FriendAdapter(OrgListActivity.this,
									R.layout.friend_item, newContact);
						}
					}
					theList.setAdapter(m_adapter);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

		});

		theList = (ListView) findViewById(R.id.donateContactsList);
		adapter = new OrganizationAdapter(this, R.layout.dogood_org_item,
				orgList);
		theList.setAdapter(adapter);
		theList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent data = new Intent();
				if (currentNav.equals("nonProfits")) {
					Organization chosenOrg = adapter.getItem(arg2);
					data.putExtra("org", chosenOrg.getName());
					data.putExtra("nav", "nonProfits");
				} else {
					Friend chosenFriend = m_adapter.getItem(arg2);

					if (!chosenFriend.getId().equals("")) {
						data.putExtra("id", chosenFriend.getId());
					} else {
						data.putExtra("paypoint", chosenFriend.getPaypoint());
					}
					data.putExtra("nav", "allContacts");
				}

				setResult(RESULT_OK, data);
				finish();
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == CHANGE_LIST) {
				Bundle bundle = data.getExtras();
				String result = bundle.getString("result");
				if (result.equals("allContacts")) {
					TextView txtEmptyList = (TextView) findViewById(R.id.txtEmptyList);
					if (allContacts.size() == 0) {
						theList.setVisibility(View.GONE);
						txtEmptyList.setVisibility(View.VISIBLE);
					} else {
						theList.setVisibility(View.VISIBLE);
						m_adapter = new FriendAdapter(getApplicationContext(),
								R.layout.friend_item, allContacts);
						theList.setAdapter(m_adapter);
						//m_adapter.notifyDataSetChanged();
						currentNav = "allContacts";
						submitSearch.setVisibility(View.GONE);
						searchBar.setText("");
						searchBar.setHint("Search all your contacts");
						txtEmptyList.setVisibility(View.GONE);
					}

				} else if (result.equals("phoneContacts")) {
					TextView txtEmptyList = (TextView) findViewById(R.id.txtEmptyList);
					if (contactList.size() == 0) {
						theList.setVisibility(View.GONE);
						txtEmptyList.setVisibility(View.VISIBLE);
					} else {
						theList.setVisibility(View.VISIBLE);
						m_adapter = new FriendAdapter(getApplicationContext(),
								R.layout.friend_item, contactList);
						m_adapter.notifyDataSetChanged();
						theList.setAdapter(m_adapter);
						currentNav = "phoneContacts";
						submitSearch.setVisibility(View.GONE);
						searchBar.setText("");
						searchBar.setHint("Search your phone contacts");
						txtEmptyList.setVisibility(View.GONE);
					}

				} else if (result.equals("fbContacts")) {
					TextView txtEmptyList = (TextView) findViewById(R.id.txtEmptyList);
					if (friendsList.size() == 0) {
						theList.setVisibility(View.GONE);
						txtEmptyList.setVisibility(View.VISIBLE);
					} else {
						theList.setVisibility(View.VISIBLE);
						txtEmptyList.setVisibility(View.GONE);
						m_adapter = new FriendAdapter(getApplicationContext(),
								R.layout.friend_item, friendsList);
						m_adapter.notifyDataSetChanged();
						theList.setAdapter(m_adapter);
						currentNav = "fbContacts";
						submitSearch.setVisibility(View.GONE);
						searchBar.setText("");
						searchBar.setHint("Search your Facebook contacts");
					}

				} else if (result.equals("nonProfits")) {
					TextView txtEmptyList = (TextView) findViewById(R.id.txtEmptyList);
					if (orgList.size() == 0) {
						theList.setVisibility(View.GONE);
						txtEmptyList.setVisibility(View.VISIBLE);
					} else {
						
						theList.setVisibility(View.VISIBLE);
						adapter = new OrganizationAdapter(getApplicationContext(),
								R.layout.dogood_org_item, orgList);
						theList.setAdapter(adapter);
						currentNav = "nonProfits";
						submitSearch.setVisibility(View.VISIBLE);
						searchBar.setText("");
						searchBar.setHint("Search for your organization");
						txtEmptyList.setVisibility(View.GONE);
					}
				} else if (result.equals("pubDirectory")) {
					//

				}
			}
			if (requestCode == CHOSE_ORG) {
				Bundle bundle = data.getExtras();
				Intent sendData = new Intent();
				sendData.putExtra("name", bundle.getString("name"));
				sendData.putExtra("amount", bundle.getString("amount"));

				setResult(RESULT_OK, sendData);
				finish();

			}
		}
	}

	private void createFakeList() {
		// GET FROM SERVER --->
		/*ArrayList<OrganizationResponse> nonProfitListResponse = PaymentServices.getOrgs("NonProfits");
		ArrayList<OrganizationResponse> organizationResponse = PaymentServices.getOrgs("Organizations");
		
		ArrayList<Organization> translateNonProfit = new ArrayList<Organization>();
		for(int i = 0; i < nonProfitListResponse.size(); i++)
		{
			Organization ref = new Organization();
			ref.setHeader("ALL CAUSES");
			ref.setImageUri(nonProfitListResponse.get(i).MerchantImageUri);
			ref.setName(nonProfitListResponse.get(i).Name);
			ref.setSlogan("");
			ref.setPreferredReceive(nonProfitListResponse.get(i).PreferredReceiveAccountId);
			ref.setPreferredSend(nonProfitListResponse.get(i).PreferredSendAccountId);
			ref.setInfo("");
			
			translateNonProfit.add(ref);
		}
		
		ArrayList<Organization> translateOrganization = new ArrayList<Organization>();
		for(int i = 0; i < organizationResponse.size(); i++)
		{
			Organization ref = new Organization();
			ref.setHeader("ALL CAUSES");
			ref.setImageUri(organizationResponse.get(i).MerchantImageUri);
			ref.setName(organizationResponse.get(i).Name);
			ref.setSlogan("");
			ref.setPreferredReceive(organizationResponse.get(i).PreferredReceiveAccountId);
			ref.setPreferredSend(organizationResponse.get(i).PreferredSendAccountId);
			ref.setInfo("");
			
			translateNonProfit.add(ref);
		}*/
		
		String path = "android.resource://me.pdthx/drawable/";
		// Uri path = Uri.parse("android.resource://me.pdthx/");
		String h = "ALL CAUSES";
		String r = "RECENT CAUSES";
		orgList = new ArrayList<Organization>();

		Organization acs = new Organization();
		acs.setHeader(r);
		acs.setImageUri(path + R.drawable.org_acs);
		acs.setName("American Cancer Society");
		acs.setSlogan("The official sponsor of birthdays");
		acs.setInfo("Visit us at www.cancer.org or call 1-800-227-2345 for help or questions.");

		orgList.add(acs);

		Organization ada = new Organization();
		ada.setHeader(h);
		ada.setImageUri(path + R.drawable.org_americandiabetes);
		ada.setName("American Diabetes Association");
		ada.setSlogan("");
		ada.setInfo("");

		orgList.add(ada);

		Organization aha = new Organization();
		aha.setHeader(h);
		aha.setImageUri(path + R.drawable.org_americanheart);
		aha.setName("American Heart Association");
		aha.setSlogan("Learn and Live");
		aha.setInfo("");

		orgList.add(aha);

		Organization beta = new Organization();
		beta.setHeader(h);
		beta.setImageUri(path + R.drawable.org_beta);
		beta.setName("Beta Theta Pi");
		beta.setSlogan("Men of principle");
		beta.setInfo("");

		orgList.add(beta);

		Organization bsa = new Organization();
		bsa.setHeader(r);
		bsa.setImageUri(path + R.drawable.org_bsa);
		bsa.setName("Boy Scouts of America");
		bsa.setSlogan("");
		bsa.setInfo("");

		orgList.add(bsa);

		Organization cs = new Organization();
		cs.setHeader(h);
		cs.setImageUri(path + R.drawable.org_childsavers);
		cs.setName("Child Savers");
		cs.setSlogan("Helping Greater Richmond's Children since 1924");
		cs.setInfo("To find out more ways to help, visit us at www.childsavers.org or contact us (804) 644-9590");

		orgList.add(cs);

		Organization cvsa = new Organization();
		cvsa.setHeader(h);
		cvsa.setImageUri(path + R.drawable.org_cvsa);
		cvsa.setName("Central Virginia Soccer Association");
		cvsa.setSlogan("35+ Years of the Best Adult Soccer in the Richmond Area");
		cvsa.setInfo("");
		orgList.add(cvsa);

		Organization goodwill = new Organization();
		goodwill.setHeader(h);
		goodwill.setImageUri(path + R.drawable.org_goodwill);
		goodwill.setName("Goodwill");
		goodwill.setSlogan("");
		goodwill.setInfo("Find your local Goodwill at http://m.goodwill.org");
		orgList.add(goodwill);

		Organization hostelling = new Organization();
		hostelling.setHeader(h);
		hostelling.setImageUri(path + R.drawable.org_hostelling);
		hostelling.setName("Hostelling International");
		hostelling.setSlogan("Travel with a Mission");
		hostelling
				.setInfo("Interested in becoming a part of this opportunity? Search for availible hostels at www.hihostels.com");
		orgList.add(hostelling);

		Organization mod = new Organization();
		mod.setHeader(r);
		mod.setImageUri(path + R.drawable.org_marchofdimes);
		mod.setName("March of Dimes");
		mod.setSlogan("Working together for stronger, healther babies");
		mod.setInfo("Learn more at www.marchofdimes.com");
		orgList.add(mod);

		Organization mda = new Organization();
		mda.setHeader(h);
		mda.setImageUri(path + R.drawable.org_mda);
		mda.setName("Muscular Dystrophy Association");
		mda.setSlogan("Fighting muscle disease");
		mda.setInfo("");
		orgList.add(mda);

		Organization nc = new Organization();
		nc.setHeader(h);
		nc.setImageUri(path + R.drawable.org_natureconserv);
		nc.setName("The Nature Conservancy");
		nc.setSlogan("Protecting Nature. Perserving Life");
		nc.setInfo("");
		orgList.add(nc);

		Organization op = new Organization();
		op.setHeader(h);
		op.setImageUri(path + R.drawable.org_obxparish);
		op.setName("Holy Redeemer Catholic Parish");
		op.setSlogan("");
		op.setInfo("");
		orgList.add(op);

		Organization rcs = new Organization();
		rcs.setHeader(h);
		rcs.setImageUri(path + R.drawable.org_rivercitysports);
		rcs.setName("River City Sports");
		rcs.setSlogan("The Premier Name for Sports Merchandise");
		rcs.setInfo("Order securely online or call our toll-free number: 1-80-950-8201");
		orgList.add(rcs);

		Organization rotary = new Organization();
		rotary.setHeader(h);
		rotary.setImageUri(path + R.drawable.org_rotary);
		rotary.setName("Rotary International");
		rotary.setSlogan("Looking into the future");
		rotary.setInfo("");
		orgList.add(rotary);

		Organization so = new Organization();
		so.setHeader(h);
		so.setImageUri(path + R.drawable.org_specialolympics);
		so.setName("Special Olympics");
		so.setSlogan("");
		so.setInfo("");
		orgList.add(so);

		Organization tfa = new Organization();
		tfa.setHeader(h);
		tfa.setImageUri(path + R.drawable.org_teach4amer);
		tfa.setName("Teach 4 America");
		tfa.setSlogan("Change Starts with You");
		tfa.setInfo("Want to join our mission? Go to www.teachforamerica.org to learn more.");
		orgList.add(tfa);

		Organization uturn = new Organization();
		uturn.setHeader(h);
		uturn.setImageUri(path + R.drawable.org_uturn);
		uturn.setName("U-Turn Sports Performance Academy");
		uturn.setSlogan("Training Champions for Life!");
		uturn.setInfo("");
		orgList.add(uturn);

		Organization wv = new Organization();
		wv.setHeader(h);
		wv.setImageUri(path + R.drawable.org_worldvision);
		wv.setName("World Vision");
		wv.setSlogan("Building a better world for children");
		wv.setInfo("");
		orgList.add(wv);

		Organization wwf = new Organization();
		wwf.setHeader(h);
		wwf.setImageUri(path + R.drawable.org_wwf);
		wwf.setName("World Wildlife Foundation");
		wwf.setSlogan("");
		wwf.setInfo("Find out more about who we are and what we do at www.worldwildlife.org");
		orgList.add(wwf);

		Organization zta = new Organization();
		zta.setHeader(h);
		zta.setImageUri(path + R.drawable.org_zta);
		zta.setName("Zeta Tau Alpha");
		zta.setSlogan("Seek the Noblest");
		zta.setInfo("");
		orgList.add(zta);

		sortList();
		removeDuplicateHeaders();
	}

	private void sortList() {
		int count = 0;
		for (int i = 0; i < orgList.size(); i++) {
			if (orgList.get(i).getHeader().equals("RECENT CAUSES")) {
				Organization ref = orgList.get(i);
				orgList.remove(i);
				orgList.add(count, ref);
				count++;
			}
		}
	}

	private void removeDuplicateHeaders() {
		String header = "";
		for (int i = 0; i < orgList.size(); i++) {
			if (orgList.get(i).getHeader().equals(header)) {
				orgList.get(i).setHeader("");
			} else {
				header = orgList.get(i).getHeader();
			}
		}
	}

	private ArrayList<Organization> searchList(String term) {
		ArrayList<Organization> list = new ArrayList<Organization>();
		for (int i = 0; i < orgList.size(); i++) {
			if (orgList.get(i).getName().toUpperCase()
					.contains(term.toUpperCase())) {
				list.add(orgList.get(i));
			}
		}

		return list;
	}
}
