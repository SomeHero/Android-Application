package me.pdthx;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class CustomTabActivity extends TabActivity {

	private TabHost mTabHost;

	private void setupTabHost() {
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// construct the tabhost
		setContentView(R.layout.main);

		setupTabHost();
		mTabHost.getTabWidget().setDividerDrawable(R.drawable.tab_divider);

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);

		Intent in1 = new Intent(mTabHost.getContext(), MakePaymentActivity.class);
		setupTab(new TextView(this), "Send Money", in1);
		
		if(prefs.getString("paymentAccountId", "").length() == 0) {
			Intent in2 = new Intent(mTabHost.getContext(), GetMoneyActivity.class);
			setupTab(new TextView(this), "Get Money", in2);
		}
		
		Intent in3 = new Intent(mTabHost.getContext(), RequestMoneyActivity.class);
		setupTab(new TextView(this), "Request Money", in3);
	}

	private void setupTab(final View view, final String tag, final Intent intent) {
		View tabview = createTabView(mTabHost.getContext(), tag);

		TabSpec setContent = mTabHost.newTabSpec(tag).setIndicator(tabview);
		setContent.setContent(intent);
		
		mTabHost.addTab(setContent);

	}

	private static View createTabView(final Context context, final String text) {
		View view = LayoutInflater.from(context).inflate(R.layout.tabs_bg, null);
		TextView tv = (TextView) view.findViewById(R.id.tabsText);
		tv.setText(text);
		return view;
	}
}