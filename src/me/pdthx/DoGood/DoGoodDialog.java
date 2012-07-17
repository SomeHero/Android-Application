package me.pdthx.DoGood;

import me.pdthx.R;
import me.pdthx.Models.Organization;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class DoGoodDialog extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.navigation_contacts);

		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		getWindow().setGravity(Gravity.TOP | Gravity.RIGHT);
		showDialog();
	}
	
	public void showDialog()
	{
		LinearLayout allContactsBtn = (LinearLayout)findViewById(R.id.nav_allcontacts);
		allContactsBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				Intent data = new Intent();
				data.putExtra("result", "allContacts");
				setResult(RESULT_OK, data);
				finish();
			}
			
		});
		
		LinearLayout phoneContactsBtn = (LinearLayout)findViewById(R.id.nav_phonecontacts);
		phoneContactsBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent data = new Intent();
				data.putExtra("result", "phoneContacts");
				setResult(RESULT_OK, data);
				finish();
			}
			
		});
		
		LinearLayout fbContactsBtn = (LinearLayout)findViewById(R.id.nav_facebookcontacts);
		fbContactsBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent data = new Intent();
				data.putExtra("result", "fbContacts");
				setResult(RESULT_OK, data);
				finish();
			}
			
		});
		
		LinearLayout nonProfitsBtn = (LinearLayout)findViewById(R.id.nav_nonprofits);
		nonProfitsBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent data = new Intent();
				data.putExtra("result", "nonProfits");
				setResult(RESULT_OK, data);
				finish();
			}
			
		});
		
		LinearLayout pubDirectBtn = (LinearLayout)findViewById(R.id.nav_pubdirect);
		pubDirectBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent data = new Intent();
				data.putExtra("result", "pubDirectory");
				setResult(RESULT_OK, data);
				finish();
			}
			
		});
	}
}
