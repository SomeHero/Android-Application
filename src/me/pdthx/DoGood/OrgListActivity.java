package me.pdthx.DoGood;

import java.util.ArrayList;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import me.pdthx.BaseActivity;
import me.pdthx.R;
import me.pdthx.Adapters.OrganizationAdapter;
import me.pdthx.Models.Organization;

public class OrgListActivity extends BaseActivity {
	private static final int CHOSE_ORG = 5;
	private ListView theList;
	private OrganizationAdapter adapter;
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
				data.putExtra("id", adapter.getItem(arg2).getId());

				setResult(RESULT_OK, data);
				finish();
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == CHOSE_ORG) {
				Bundle bundle = data.getExtras();
				Intent sendData = new Intent();
				sendData.putExtra("id", bundle.getString("id"));
				sendData.putExtra("amount", bundle.getString("amount"));

				setResult(RESULT_OK, sendData);
				finish();

			}
		}
	}
}
