package me.pdthx.Login;

import com.zubhium.ZubhiumSDK;
import com.zubhium.ZubhiumSDK.CrashReportingMode;

import me.pdthx.CustomTabActivity;
import me.pdthx.R;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;

@SuppressWarnings("deprecation")
public class TabUIActivity extends TabActivity {
	public static TabUIActivity self;

	private Resources res; // Resource object to get Drawables
	private TabHost tabHost; // The activity TabHost
	private SharedPreferences prefs;
	ZubhiumSDK sdk;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		
		if (!prefs.getString("userId", "").equals("")) {
			startActivity(new Intent(this, CustomTabActivity.class));
		}
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.tabslayout);
		self = this;

		res = getResources(); // Resource object to get Drawables
		tabHost = getTabHost(); // The activity TabHost
		tabHost.getTabWidget().setDividerDrawable(R.drawable.tab_divider);

		Intent intent;
		
		sdk = ZubhiumSDK.getZubhiumSDKInstance(TabUIActivity.this,
				getString(R.string.secret_key));

		if (sdk != null) {
			sdk.setCrashReportingMode(CrashReportingMode.SILENT);
		}
		
		intent = new Intent(tabHost.getContext(), WelcomeActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		setupTab(res.getDrawable(R.drawable.pdthx_wel), "Welcome", intent);
		
		intent = new Intent(tabHost.getContext(), SignUpUIActivity.class);
		setupTab(res.getDrawable(R.drawable.pdthx_signup), "Sign Up", intent);

		intent = new Intent(tabHost.getContext(), SignInUIActivity.class);
		setupTab(res.getDrawable(R.drawable.pdthx_signin), "Sign In", intent);

		intent = new Intent(tabHost.getContext(), AboutActivity.class);
		setupTab(res.getDrawable(R.drawable.pdthx_about), "About", intent);
		// Contact tabs

		tabHost.setCurrentTab(0);
	}
	
	public void switchTab(int tab){
       tabHost.setCurrentTab(tab);
	}
	
	private void setupTab(Drawable icon, final String tag, final Intent intent) {
		View tabview = createTabView(tabHost.getContext(), icon, tag);

		//tabview.setBackgroundColor(Color.rgb(93, 182, 204));
		//tabview.getLayoutParams().width= 50;
		
		TabSpec setContent = tabHost.newTabSpec(tag)
				.setIndicator(tabview)
				.setContent(intent);

		tabHost.addTab(setContent);

	}
	
	private static View createTabView(final Context context, final Drawable icon, final String text) {
		View view = LayoutInflater.from(context).inflate(R.layout.tabs_welcome_bg, null);
		ImageView iv = (ImageView)view.findViewById(R.id.tabsWIcon);
		iv.setImageDrawable(icon);
		
		TextView tv = (TextView) view.findViewById(R.id.tabsWText);
		tv.setText(text);
		return view;
	}
	
	private OnTabChangeListener myOnTabChangeListener = new OnTabChangeListener() {

		@Override
		public void onTabChanged(String tabId) {	
			for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
				TextView tv = (TextView) tabHost.getTabWidget().getChildAt(i)
						.findViewById(android.R.id.title); // Unselected Tabs
				tv.setTextColor(Color.parseColor("#feffff"));
				tabHost.getTabWidget().getChildAt(i)
						.setBackgroundResource(R.drawable.tab_welcome_bg_unselected);
			}
			tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab())
					.setBackgroundResource(R.drawable.tab_welcome_bg_selected);
			TextView tv = (TextView) tabHost.getTabWidget()
					.getChildAt(tabHost.getCurrentTab())
					.findViewById(android.R.id.title);
			tv.setTextColor(Color.parseColor("#191919"));
		}
	};
	
}