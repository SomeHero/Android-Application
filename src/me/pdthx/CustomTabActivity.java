package me.pdthx;

import me.pdthx.DoGood.DoGoodIntroActivity;

import com.zubhium.ZubhiumSDK;
import com.zubhium.ZubhiumSDK.CrashReportingMode;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class CustomTabActivity extends TabActivity {

	private TabHost mTabHost;
	private ZubhiumSDK sdk;

	// private void setupTabHost() {
	// mTabHost = (TabHost) findViewById(android.R.id.tabhost);
	// mTabHost.setup();
	// }

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Resources res = getResources();

		mTabHost = getTabHost(); // The activity TabHost
		mTabHost.getTabWidget().setDividerDrawable(R.drawable.tab_divider);

		Intent intent; // Reusable Intent for each tab

		sdk = ZubhiumSDK.getZubhiumSDKInstance(CustomTabActivity.this,
				getString(R.string.secret_key));

		if (sdk != null) {
			sdk.setCrashReportingMode(CrashReportingMode.SILENT);
		}

		intent = new Intent(mTabHost.getContext(), HomeActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		setupTab(res.getDrawable(R.drawable.tab_home_selector), "Home", intent);

		intent = new Intent(mTabHost.getContext(), RequestPaymentActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		setupTab(res.getDrawable(R.drawable.tab_request_selector), "Req $", intent);

		intent = new Intent(mTabHost.getContext(), SendPaymentActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		setupTab(res.getDrawable(R.drawable.tab_send_selector), "Send $", intent);

		intent = new Intent(mTabHost.getContext(), DoGoodIntroActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		setupTab(res.getDrawable(R.drawable.tab_dogood_selector), "Do Good", intent);

		intent = new Intent(mTabHost.getContext(), PaystreamActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		setupTab(res.getDrawable(R.drawable.tab_stream_selector), "Stream", intent);

		mTabHost.setCurrentTab(getIntent().getIntExtra("tab", 0));
	}

	public void switchTab(int tab) {
		mTabHost.setCurrentTab(tab);
	}

	private void setupTab(Drawable icon, final String tag, final Intent intent) {
		View tabview = createTabView(mTabHost.getContext(), icon, tag);

		// tabview.setBackgroundColor(Color.rgb(93, 182, 204));
		// tabview.getLayoutParams().width= 50;

		TabSpec setContent = mTabHost.newTabSpec(tag).setIndicator(tabview)
				.setContent(intent);

		mTabHost.addTab(setContent);

	}

	private static View createTabView(final Context context,
			final Drawable icon, final String text) {
		View view = LayoutInflater.from(context)
				.inflate(R.layout.tabs_bg, null);
		ImageView iv = (ImageView) view.findViewById(R.id.tabsIcon);
		iv.setImageDrawable(icon);

		TextView tv = (TextView) view.findViewById(R.id.tabsText);
		tv.setText(text);
		return view;
	}

//	private OnTabChangeListener myOnTabChangeListener = new OnTabChangeListener() {
//
//		@Override
//		public void onTabChanged(String tabId) {
//			for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {
//				TextView tv = (TextView) mTabHost.getTabWidget().getChildAt(i)
//						.findViewById(android.R.id.title); // Unselected Tabs
//				tv.setTextColor(Color.parseColor("#424242"));
//				mTabHost.getTabWidget().getChildAt(i)
//						.setBackgroundResource(R.drawable.tab_bg_unselected);
//			}
//			mTabHost.getTabWidget().getChildAt(mTabHost.getCurrentTab())
//					.setBackgroundResource(R.drawable.tab_bg_selected);
//			TextView tv = (TextView) mTabHost.getTabWidget()
//					.getChildAt(mTabHost.getCurrentTab())
//					.findViewById(android.R.id.title);
//			tv.setTextColor(Color.parseColor("#A3A2A2"));
//		}
//	};
}
