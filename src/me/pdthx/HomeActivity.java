package me.pdthx;


import java.text.NumberFormat;

import me.pdthx.Requests.UserRequest;
import me.pdthx.Responses.UserResponse;
import me.pdthx.Services.UserService;

import com.zubhium.ZubhiumSDK;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public final class HomeActivity extends BaseActivity {
	
	public static final String TAG = "HomeActivity";
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
		
		if(prefs.getString("userId", "").length() == 0) {
//			SignInActivity signInActivity = new SignInActivity(this, mHandler, prefs);
//			signInActivity.showSignInActivity();
			startActivityForResult(new Intent(this, SignInActivity.class), 1);
		}
		else {
			showHomeController();
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			showHomeController();
		}
		else {
			//startActivityForResult(new Intent(this, SignInActivity.class), 1);
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
//			SignInActivity signInActivity = new SignInActivity(this, mHandler, prefs);
//			signInActivity.showSignInActivity();
			startActivityForResult(new Intent(this, SignInActivity.class), 1);
		} else {
			setContentView(R.layout.home_controller);
			
			NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
			
			TextView txtUserName = (TextView)findViewById(R.id.txtUserName);
			
			if (!userResponse.FirstName.equals("null") && !userResponse.LastName.equals("null") ) {
				txtUserName.setText(userResponse.FirstName + " " + userResponse.LastName);
			}
			else if (!userResponse.EmailAddress.equals("null")) {
				txtUserName.setText(userResponse.EmailAddress);
			}
			else if (!userResponse.MobileNumber.equals("null")) {
				txtUserName.setText(userResponse.MobileNumber);
			}
			else {
				txtUserName.setText("PaidThx User");
			}
			
			
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
//		SignInActivity signInActivity = new SignInActivity(this, mHandler, prefs);
//		signInActivity.showSignInActivity();
		startActivityForResult(new Intent(this, SignInActivity.class), 1);
	}
}
