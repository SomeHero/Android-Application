package me.pdthx;

import com.zubhium.ZubhiumSDK;
import com.zubhium.ZubhiumSDK.CrashReportingMode;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class CustomTabActivity extends TabActivity {

	private TabHost mTabHost;
	ZubhiumSDK sdk;
	
//	private void setupTabHost() {
//		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
//		mTabHost.setup();
//	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		Resources res = getResources(); 
		
		mTabHost = getTabHost();  // The activity TabHost
		mTabHost.getTabWidget().setDividerDrawable(R.drawable.tab_divider);
		
		Intent intent;  // Reusable Intent for each tab

		sdk = ZubhiumSDK.getZubhiumSDKInstance(CustomTabActivity.this, getString(R.string.secret_key));
		
	    if(sdk != null){
	    	sdk.setCrashReportingMode(CrashReportingMode.SILENT);
	    }
	    
		intent = new Intent(mTabHost.getContext(), HomeActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		setupTab(res.getDrawable(R.drawable.ic_tab_home), "Home", intent);
		
		intent = new Intent(mTabHost.getContext(), SubmitPaymentActivity.class);
		setupTab(res.getDrawable(R.drawable.ic_tab_send), "Send $", intent);
		
//		if(prefs.getString("paymentAccountId", "").length() == 0) {
//			intent = new Intent(mTabHost.getContext(), GetMoneyActivity.class);
//			setupTab(res.getDrawable(R.drawable.ic_tab_home), "Get $", intent);
//		}
		
		intent = new Intent(mTabHost.getContext(), RequestMoneyActivity.class);
		setupTab(res.getDrawable(R.drawable.ic_tab_recv), "Req $", intent);
		
		intent = new Intent(mTabHost.getContext(), PaystreamActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		setupTab(res.getDrawable(R.drawable.ic_tab_stream), "Stream", intent);
		
		mTabHost.setCurrentTab(0);
		
		//intent = new Intent(CustomTabActivity.this, VerifyMobileNumberActivity.class);
		//startActivityForResult(intent, 0);
        
	}
	public void switchTab(int tab){
        mTabHost.setCurrentTab(tab);
	}
	private void setupTab(Drawable icon, final String tag, final Intent intent) {
		View tabview = createTabView(mTabHost.getContext(), icon, tag);

		//tabview.setBackgroundColor(Color.rgb(93, 182, 204));
		//tabview.getLayoutParams().width= 50;
		
		TabSpec setContent = mTabHost.newTabSpec(tag)
				.setIndicator(tabview)
				.setContent(intent);

		mTabHost.addTab(setContent);

	}

	private static View createTabView(final Context context, final Drawable icon, final String text) {
		View view = LayoutInflater.from(context).inflate(R.layout.tabs_bg, null);
		ImageView iv = (ImageView)view.findViewById(R.id.tabsIcon);
		iv.setImageDrawable(icon);
		
		TextView tv = (TextView) view.findViewById(R.id.tabsText);
		tv.setText(text);
		return view;
	}
	

	private OnTabChangeListener myOnTabChangeListener = new OnTabChangeListener() {

		@Override
		public void onTabChanged(String tabId) {	
			for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {
				TextView tv = (TextView) mTabHost.getTabWidget().getChildAt(i)
						.findViewById(android.R.id.title); // Unselected Tabs
				tv.setTextColor(Color.parseColor("#424242"));
				mTabHost.getTabWidget().getChildAt(i)
						.setBackgroundResource(R.drawable.tab_bg_unselected);
			}
			mTabHost.getTabWidget().getChildAt(mTabHost.getCurrentTab())
					.setBackgroundResource(R.drawable.tab_bg_selected);
			TextView tv = (TextView) mTabHost.getTabWidget()
					.getChildAt(mTabHost.getCurrentTab())
					.findViewById(android.R.id.title);
			tv.setTextColor(Color.parseColor("#FFFFFF"));
		}
	};
}
