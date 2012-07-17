package me.pdthx.DoGood;

import me.pdthx.BaseActivity;
import me.pdthx.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class DoGoodIntroActivity extends BaseActivity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dogood_intro);
		setupIntroController();
	}
	
	public void setupIntroController()
	{
		Button clickCause = (Button)findViewById(R.id.donate_clickCauseBtn);
		clickCause.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(), DonateActivity.class));
			}
			
		});
		Button clickPledge = (Button)findViewById(R.id.donate_clickPledgeBtn);
		clickPledge.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(), PledgeActivity.class));
			}
			
		});
	}
}
