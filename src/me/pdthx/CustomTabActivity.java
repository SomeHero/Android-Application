package me.pdthx;

import com.zubhium.ZubhiumSDK;
import com.zubhium.ZubhiumSDK.CrashReportingMode;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class CustomTabActivity extends TabActivity {

	private TabHost mTabHost;
	ZubhiumSDK sdk ;
	
	private void setupTabHost() {
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		Resources res = getResources(); 
		
		mTabHost = getTabHost();  // The activity TabHost
		
		Intent intent;  // Reusable Intent for each tab

		sdk = ZubhiumSDK.getZubhiumSDKInstance(CustomTabActivity.this, getString(R.string.secret_key));
		
	    if(sdk != null){
	    	sdk.setCrashReportingMode(CrashReportingMode.SILENT);
	    }
	    
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);

		intent = new Intent(mTabHost.getContext(), HomeActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		setupTab(res.getDrawable(R.drawable.ic_tab_home), "Home", intent);
		
		intent = new Intent(mTabHost.getContext(), MakePaymentActivity.class);
		setupTab(res.getDrawable(R.drawable.ic_tab_send), "Send $", intent);
		
		if(prefs.getString("paymentAccountId", "").length() == 0) {
			intent = new Intent(mTabHost.getContext(), GetMoneyActivity.class);
			setupTab(res.getDrawable(R.drawable.ic_tab_home), "Get $", intent);
		}
		
		intent = new Intent(mTabHost.getContext(), RequestMoneyActivity.class);
		setupTab(res.getDrawable(R.drawable.ic_tab_recv), "Req $", intent);
		
		intent = new Intent(mTabHost.getContext(), PaystreamActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		setupTab(res.getDrawable(R.drawable.ic_tab_stream), "Stream", intent);
		
		mTabHost.setCurrentTab(1);
		
		//intent = new Intent(CustomTabActivity.this, VerifyMobileNumberActivity.class);
		//startActivityForResult(intent, 0);
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";
        
		String phoneNumber = "12892100266";
		String message = "123456789";
		
		PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
	            new Intent(SENT), 0);
	 
	        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
	            new Intent(DELIVERED), 0);
	 
	        //---when the SMS has been sent---
	        registerReceiver(new BroadcastReceiver(){
	            @Override
	            public void onReceive(Context arg0, Intent arg1) {
	                switch (getResultCode())
	                {
	                    case Activity.RESULT_OK:
	                        Toast.makeText(getBaseContext(), "SMS sent", 
	                                Toast.LENGTH_SHORT).show();
	                        break;
	                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
	                        Toast.makeText(getBaseContext(), "Generic failure", 
	                                Toast.LENGTH_SHORT).show();
	                        break;
	                    case SmsManager.RESULT_ERROR_NO_SERVICE:
	                        Toast.makeText(getBaseContext(), "No service", 
	                                Toast.LENGTH_SHORT).show();
	                        break;
	                    case SmsManager.RESULT_ERROR_NULL_PDU:
	                        Toast.makeText(getBaseContext(), "Null PDU", 
	                                Toast.LENGTH_SHORT).show();
	                        break;
	                    case SmsManager.RESULT_ERROR_RADIO_OFF:
	                        Toast.makeText(getBaseContext(), "Radio off", 
	                                Toast.LENGTH_SHORT).show();
	                        break;
	                }
	            }
	        }, new IntentFilter(SENT));
	 
	        //---when the SMS has been delivered---
	        registerReceiver(new BroadcastReceiver(){
	            @Override
	            public void onReceive(Context arg0, Intent arg1) {
	                switch (getResultCode())
	                {
	                    case Activity.RESULT_OK:
	                        Toast.makeText(getBaseContext(), "SMS delivered", 
	                                Toast.LENGTH_SHORT).show();
	                        break;
	                    case Activity.RESULT_CANCELED:
	                        Toast.makeText(getBaseContext(), "SMS not delivered", 
	                                Toast.LENGTH_SHORT).show();
	                        break;                        
	                }
	            }
	        }, new IntentFilter(DELIVERED));        
	 
	        SmsManager sms = SmsManager.getDefault();
	        
	        try {
	        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);  
	        }
	        catch(Exception e)
	        {
	        	e.printStackTrace();
	        }
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
}