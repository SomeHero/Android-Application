package me.pdthx.DoGood;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import me.pdthx.BaseActivity;
import me.pdthx.R;

public class DoGoodInfoActivity extends BaseActivity{
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dogood_info_controller);
		showInfoDialog();
	}
	
	private void showInfoDialog()
	{
		Button backBtn = (Button)findViewById(R.id.btnBackBtn);
		backBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				finish();
			}
			
		});
	}
}
