package me.pdthx;


import java.text.NumberFormat;

import me.pdthx.Requests.UserRequest;
import me.pdthx.Requests.UserSignInRequest;
import me.pdthx.Responses.UserRegistrationResponse;
import me.pdthx.Responses.UserResponse;
import me.pdthx.Responses.UserSignInResponse;
import me.pdthx.Services.UserService;

import com.zubhium.ZubhiumSDK;
import com.zubhium.ZubhiumSDK.CrashReportingMode;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;

public final class HomeActivity extends BaseActivity {

	public static final String TAG = "HomeActivity";
	private String userName = "";
	private String userId = "";


	ZubhiumSDK sdk ;
	

	Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
        	switch(msg.what) {
        		case(R.id.USERSIGNIN_COMPLETE):
        			showHomeController();
        			break;
        		case(R.id.USERREGISTRATION_COMPLETE):
        			showHomeController();
        			break;
        	}

        }

	};


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(prefs.getString("userId", "").length() == 0 || prefs.getString("mobileNumber", "").length() == 0)	{
		    SignInActivity signInActivity = new SignInActivity(this, mHandler, prefs);
		    signInActivity.showSignInActivity();
		}
		else {
			showHomeController();
		}
		
	}

	public void switchTabInActivity(int indexTabToSwitchTo){
        CustomTabActivity ParentActivity;
        ParentActivity = (CustomTabActivity) this.getParent();
        ParentActivity.switchTab(indexTabToSwitchTo);
	}
	private void showHomeController() {
		userId = prefs.getString("userId", "");

		UserService userService = new UserService();
		UserRequest userRequest = new UserRequest();
		userRequest.UserId = userId;

		UserResponse userResponse = userService.GetUser(userRequest);

		if(userResponse == null)
		{
			 SignInActivity signInActivity = new SignInActivity(this, mHandler, prefs);
			 signInActivity.showSignInActivity();
		} else {
			setContentView(R.layout.home_controller);

			NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();

			TextView txtUserName = (TextView)findViewById(R.id.txtUserName);
			txtUserName.setText(userResponse.MobileNumber);

			TextView txtTotalMoneySent = (TextView)findViewById(R.id.txtTotalMoneySent);
			txtTotalMoneySent.setText(currencyFormatter.format(userResponse.TotalMoneySent));

			TextView txtTotalMoneyReceived = (TextView)findViewById(R.id.txtTotalMoneyReceived);
			txtTotalMoneyReceived.setText(currencyFormatter.format(userResponse.TotalMoneyReceived));

			Button btnSendMoney = (Button)findViewById(R.id.btnQuickLinkSent);
			btnSendMoney.setOnClickListener(new OnClickListener() {
				public void onClick(View argO) {

					switchTabInActivity(1);
				}
			});
			Button btnRequestMoney = (Button)findViewById(R.id.btnQuickLinkRequest);

			 btnRequestMoney.setOnClickListener(new OnClickListener() {
				public void onClick(View argO) {

					switchTabInActivity(2);
				}
			});
		}
	}
	@Override
	public void OnSignOutComplete()
	{
	    SignInActivity signInActivity = new SignInActivity(this, mHandler, prefs);
	    signInActivity.showSignInActivity();
	}
}



