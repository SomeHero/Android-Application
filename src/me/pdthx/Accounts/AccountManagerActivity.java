package me.pdthx.Accounts;

import me.pdthx.R;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AccountManagerActivity extends Activity {

	Button editAcct;
	Button addAcct;
	Button backAcctBtn;
	Button checkButton;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		showAccountScreen();
	}
	
	public void showAddAccounts()
	{
		setContentView(R.layout.achaccountsetup_controller);
		TextView title = (TextView)findViewById(R.id.txtACHTitle);
		title.setText("Add Account");
		Button hideRemove = (Button)findViewById(R.id.btnremoveACHAcct);
		hideRemove.setVisibility(View.GONE);
		
		Button back = (Button)findViewById(R.id.btnACHBack);
		back.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) {
				showAccountScreen();
			}
			
		});
		
	}
	
	public void showUpdateAccounts()
	{
		setContentView(R.layout.achaccountsetup_controller);
		TextView title = (TextView)findViewById(R.id.txtACHTitle);
		title.setText("Update Account");
		Button hideRemind = (Button)findViewById(R.id.btnRemindMeLater);
		hideRemind.setVisibility(View.GONE);
		
		Button back = (Button)findViewById(R.id.btnACHBack);
		back.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) {
				showAccountScreen();
			}
			
		});
	}

	public void showAccountScreen()
	{
		setContentView(R.layout.account_controller);

		LinearLayout list = (LinearLayout) findViewById(R.id.bankIDs);
		for (int i = 0; i < 2; i++) {
			LayoutInflater inflater = (LayoutInflater) getBaseContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View vi = inflater.inflate(R.layout.account_item, null);
			list.addView(vi);
		}
		
		addAcct = (Button)findViewById(R.id.bankAddAcct);
		editAcct = (Button)findViewById(R.id.bankUpdateAcct);
		
		addAcct.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				showAddAccounts();
			}
			
		});
		
		editAcct.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showUpdateAccounts();
			}
			
		});
		

		backAcctBtn = (Button)findViewById(R.id.acct_BackButton);
		backAcctBtn.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) {
				finish();
			}
			
		});
		
		if(checkCameraHardware(this)){
			checkButton = (Button)findViewById(R.id.checkButton);
			checkButton.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v) {
					Camera checkCamera = getCameraInstance();
					
				}
				
			});
		}
	}
	
	public static Camera getCameraInstance(){
	    Camera c = null;
	    try {
	        c = Camera.open(); // attempt to get a Camera instance
	    }
	    catch (Exception e){
	        // Camera is not available (in use or does not exist)
	    }
	    return c; // returns null if camera is unavailable
	}
	
	private boolean checkCameraHardware(Context context) {
	    if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
	        // this device has a camera
	        return true;
	    } else {
	        // no camera on this device
	        return false;
	    }
	}
}
