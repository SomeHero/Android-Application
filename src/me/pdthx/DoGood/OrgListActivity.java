package me.pdthx.DoGood;

import java.util.ArrayList;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import me.pdthx.BaseActivity;
import me.pdthx.R;
import me.pdthx.Adapters.OrganizationAdapter;
import me.pdthx.Models.Organization;

public class OrgListActivity extends BaseActivity {
	private static final int CHANGE_LIST = 6;
	private static final int CHOSE_ORG = 5;
	private ListView theList;
	private OrganizationAdapter adapter;
	private Button submitSearch;
	private EditText searchBar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.donate_contacts);
		showOrgController();
	}

	private void showOrgController() {

		searchBar = (EditText) findViewById(R.id.donate_searchOrg);
		searchBar.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {

				if (s.length() != 0)
				{
				    ArrayList<Organization> tempList = new ArrayList<Organization>();
				    for (int i = 0; i < adapter.getCount(); i++)
				    {
				        Organization org = adapter.getItem(i);
				        if (org.search(s.toString()))
				        {
				            tempList.add(adapter.getItem(i));
				        }
				    }

				    if (tempList.size() > 0)
                    {
                        adapter = new OrganizationAdapter(OrgListActivity.this, R.layout.dogood_org_item,
                            tempList);
                    }
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
				nonProfitsList);
		theList.setAdapter(adapter);
		theList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent data = new Intent();
//				if (currentNav.equals("nonProfits")) {
//					Organization chosenOrg = adapter.getItem(arg2);
//					data.putExtra("org", chosenOrg.getName());
//					data.putExtra("nav", "nonProfits");
//				} else {
//					Friend chosenFriend = m_adapter.getItem(arg2);
//
//					if (!chosenFriend.getId().equals("")) {
//						data.putExtra("id", chosenFriend.getId());
//					} else {
//						data.putExtra("paypoint", chosenFriend.getPaypoints().get(0));
//					}
//					data.putExtra("nav", "allContacts");
//				}

				setResult(RESULT_OK, data);
				finish();
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == CHANGE_LIST) {
//				Bundle bundle = data.getExtras();
//				String result = bundle.getString("result");
//				if (result.equals("allContacts")) {
//					TextView txtEmptyList = (TextView) findViewById(R.id.txtEmptyList);
//					if (combinedContactList.isEmpty()) {
//						theList.setVisibility(View.GONE);
//						txtEmptyList.setVisibility(View.VISIBLE);
//					} else {
//						theList.setVisibility(View.VISIBLE);
//						m_adapter = new FriendAdapter(OrgListActivity.this,
//								R.layout.friend_item, combinedContactList);
//						theList.setAdapter(m_adapter);
//						//m_adapter.notifyDataSetChanged();
//						currentNav = "allContacts";
//						submitSearch.setVisibility(View.GONE);
//						searchBar.setText("");
//						searchBar.setHint("Search all your contacts");
//						txtEmptyList.setVisibility(View.GONE);
//					}
//
//				} else if (result.equals("phoneContacts")) {
//					TextView txtEmptyList = (TextView) findViewById(R.id.txtEmptyList);
//					if (contactList.size() == 0) {
//						theList.setVisibility(View.GONE);
//						txtEmptyList.setVisibility(View.VISIBLE);
//					} else {
//						theList.setVisibility(View.VISIBLE);
//						m_adapter = new FriendAdapter(OrgListActivity.this,
//								R.layout.friend_item, contactList);
//						m_adapter.notifyDataSetChanged();
//						theList.setAdapter(m_adapter);
//						currentNav = "phoneContacts";
//						submitSearch.setVisibility(View.GONE);
//						searchBar.setText("");
//						searchBar.setHint("Search your phone contacts");
//						txtEmptyList.setVisibility(View.GONE);
//					}
//
//				} else if (result.equals("fbContacts")) {
//					TextView txtEmptyList = (TextView) findViewById(R.id.txtEmptyList);
//					if (friendsList.size() == 0) {
//						theList.setVisibility(View.GONE);
//						txtEmptyList.setVisibility(View.VISIBLE);
//					} else {
//						theList.setVisibility(View.VISIBLE);
//						txtEmptyList.setVisibility(View.GONE);
//						m_adapter = new FriendAdapter(OrgListActivity.this,
//								R.layout.friend_item, friendsList);
//						m_adapter.notifyDataSetChanged();
//						theList.setAdapter(m_adapter);
//						currentNav = "fbContacts";
//						submitSearch.setVisibility(View.GONE);
//						searchBar.setText("");
//						searchBar.setHint("Search your Facebook contacts");
//					}
//
//				} else if (result.equals("nonProfits")) {
//					TextView txtEmptyList = (TextView) findViewById(R.id.txtEmptyList);
//					if (nonProfitsList.size() == 0) {
//						theList.setVisibility(View.GONE);
//						txtEmptyList.setVisibility(View.VISIBLE);
//					} else {
//
//						theList.setVisibility(View.VISIBLE);
//						adapter = new OrganizationAdapter(OrgListActivity.this,
//								R.layout.dogood_org_item, nonProfitsList);
//						theList.setAdapter(adapter);
//						currentNav = "nonProfits";
//						submitSearch.setVisibility(View.VISIBLE);
//						searchBar.setText("");
//						searchBar.setHint("Search for your organization");
//						txtEmptyList.setVisibility(View.GONE);
//					}
//				} else if (result.equals("pubDirectory")) {
//				    TextView txtEmptyList = (TextView) findViewById(R.id.txtEmptyList);
//                    if (organizationsList.size() == 0) {
//                        theList.setVisibility(View.GONE);
//                        txtEmptyList.setVisibility(View.VISIBLE);
//                    } else {
//
//                        theList.setVisibility(View.VISIBLE);
//                        adapter = new OrganizationAdapter(OrgListActivity.this,
//                                R.layout.dogood_org_item, organizationsList);
//                        theList.setAdapter(adapter);
//                        currentNav = "pubDirectory";
//                        submitSearch.setVisibility(View.VISIBLE);
//                        searchBar.setText("");
//                        searchBar.setHint("Search for your organization");
//                        txtEmptyList.setVisibility(View.GONE);
//                    }
//
//				}
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

	/*private void createFakeList() {

		Organization acs = new Organization();
		acs.setImageUri(path + R.drawable.org_acs);
		acs.setName("American Cancer Society");
		acs.setSlogan("The official sponsor of birthdays");
		acs.setInfo("Visit us at www.cancer.org or call 1-800-227-2345 for help or questions.");

		orgList.add(acs);

		Organization ada = new Organization();
		ada.setImageUri(path + R.drawable.org_americandiabetes);
		ada.setName("American Diabetes Association");
		ada.setSlogan("");
		ada.setInfo("");

		orgList.add(ada);

		Organization aha = new Organization();
		aha.setImageUri(path + R.drawable.org_americanheart);
		aha.setName("American Heart Association");
		aha.setSlogan("Learn and Live");
		aha.setInfo("");

		orgList.add(aha);

		Organization beta = new Organization();
		beta.setImageUri(path + R.drawable.org_beta);
		beta.setName("Beta Theta Pi");
		beta.setSlogan("Men of principle");
		beta.setInfo("");

		orgList.add(beta);

		Organization bsa = new Organization();
		bsa.setImageUri(path + R.drawable.org_bsa);
		bsa.setName("Boy Scouts of America");
		bsa.setSlogan("");
		bsa.setInfo("");

		orgList.add(bsa);

		Organization cs = new Organization();
		cs.setImageUri(path + R.drawable.org_childsavers);
		cs.setName("Child Savers");
		cs.setSlogan("Helping Greater Richmond's Children since 1924");
		cs.setInfo("To find out more ways to help, visit us at www.childsavers.org or contact us (804) 644-9590");

		orgList.add(cs);

		Organization cvsa = new Organization();
		cvsa.setImageUri(path + R.drawable.org_cvsa);
		cvsa.setName("Central Virginia Soccer Association");
		cvsa.setSlogan("35+ Years of the Best Adult Soccer in the Richmond Area");
		cvsa.setInfo("");
		orgList.add(cvsa);

		Organization goodwill = new Organization();
		goodwill.setImageUri(path + R.drawable.org_goodwill);
		goodwill.setName("Goodwill");
		goodwill.setSlogan("");
		goodwill.setInfo("Find your local Goodwill at http://m.goodwill.org");
		orgList.add(goodwill);

		Organization hostelling = new Organization();
		hostelling.setImageUri(path + R.drawable.org_hostelling);
		hostelling.setName("Hostelling International");
		hostelling.setSlogan("Travel with a Mission");
		hostelling
				.setInfo("Interested in becoming a part of this opportunity? Search for availible hostels at www.hihostels.com");
		orgList.add(hostelling);

		Organization mod = new Organization();
		mod.setImageUri(path + R.drawable.org_marchofdimes);
		mod.setName("March of Dimes");
		mod.setSlogan("Working together for stronger, healther babies");
		mod.setInfo("Learn more at www.marchofdimes.com");
		orgList.add(mod);

		Organization mda = new Organization();
		mda.setImageUri(path + R.drawable.org_mda);
		mda.setName("Muscular Dystrophy Association");
		mda.setSlogan("Fighting muscle disease");
		mda.setInfo("");
		orgList.add(mda);

		Organization nc = new Organization();
		nc.setImageUri(path + R.drawable.org_natureconserv);
		nc.setName("The Nature Conservancy");
		nc.setSlogan("Protecting Nature. Perserving Life");
		nc.setInfo("");
		orgList.add(nc);

		Organization op = new Organization();
		op.setImageUri(path + R.drawable.org_obxparish);
		op.setName("Holy Redeemer Catholic Parish");
		op.setSlogan("");
		op.setInfo("");
		orgList.add(op);

		Organization rcs = new Organization();
		rcs.setImageUri(path + R.drawable.org_rivercitysports);
		rcs.setName("River City Sports");
		rcs.setSlogan("The Premier Name for Sports Merchandise");
		rcs.setInfo("Order securely online or call our toll-free number: 1-80-950-8201");
		orgList.add(rcs);

		Organization rotary = new Organization();
		rotary.setImageUri(path + R.drawable.org_rotary);
		rotary.setName("Rotary International");
		rotary.setSlogan("Looking into the future");
		rotary.setInfo("");
		orgList.add(rotary);

		Organization so = new Organization();
		so.setImageUri(path + R.drawable.org_specialolympics);
		so.setName("Special Olympics");
		so.setSlogan("");
		so.setInfo("");
		orgList.add(so);

		Organization tfa = new Organization();
		tfa.setImageUri(path + R.drawable.org_teach4amer);
		tfa.setName("Teach 4 America");
		tfa.setSlogan("Change Starts with You");
		tfa.setInfo("Want to join our mission? Go to www.teachforamerica.org to learn more.");
		orgList.add(tfa);

		Organization uturn = new Organization();
		uturn.setImageUri(path + R.drawable.org_uturn);
		uturn.setName("U-Turn Sports Performance Academy");
		uturn.setSlogan("Training Champions for Life!");
		uturn.setInfo("");
		orgList.add(uturn);

		Organization wv = new Organization();
		wv.setImageUri(path + R.drawable.org_worldvision);
		wv.setName("World Vision");
		wv.setSlogan("Building a better world for children");
		wv.setInfo("");
		orgList.add(wv);

		Organization wwf = new Organization();
		wwf.setImageUri(path + R.drawable.org_wwf);
		wwf.setName("World Wildlife Foundation");
		wwf.setSlogan("");
		wwf.setInfo("Find out more about who we are and what we do at www.worldwildlife.org");
		orgList.add(wwf);

		Organization zta = new Organization();
		zta.setImageUri(path + R.drawable.org_zta);
		zta.setName("Zeta Tau Alpha");
		zta.setSlogan("Seek the Noblest");
		zta.setInfo("");
		orgList.add(zta);

		sortList();
		removeDuplicateHeaders();
	}*/
}
