package me.pdthx.DoGood;

import java.io.IOException;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import me.pdthx.AddMoneyActivity;
import me.pdthx.BaseActivity;
import me.pdthx.R;
import me.pdthx.SendPaymentActivity;
import me.pdthx.Models.Friend;
import me.pdthx.Models.Organization;

public class DoGoodInfoActivity extends BaseActivity {
	protected static final int ADDING_MONEY = 10;
	String name;
	String slogan;
	String imageUri;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dogood_info_controller);
		Bundle extras = getIntent().getExtras();
		imageUri = extras.getString("pic");
		name = extras.getString("name");
		slogan = extras.getString("slogan");
		showInfoDialog();
	}

	/**
	 * Buttons only work for pledge.
	 */
	private void showInfoDialog() {
		TextView title = (TextView)findViewById(R.id.info_orgName);
		title.setText(name);
		TextView sloganInfo = (TextView)findViewById(R.id.info_orgSlogan);
		sloganInfo.setText(slogan);
		
		ImageView picture = (ImageView)findViewById(R.id.info_orgPic);
		Uri url;
		try {
			url = Uri.parse(imageUri);
			Bitmap bmp=BitmapFactory.decodeStream(getContentResolver().openInputStream(url));
			picture.setImageBitmap(bmp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Button backBtn = (Button) findViewById(R.id.btnBackBtn);
		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}

		});

		Button donateRecommended = (Button) findViewById(R.id.info_donateRecAmt);
		donateRecommended.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent data = new Intent();
				data.putExtra("amount", "50.00");
				data.putExtra("name", name);

				setResult(RESULT_OK, data);
				finish();
			}
		});

		Button donateAmount = (Button) findViewById(R.id.info_donateDifAmtBtn);
		donateAmount.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(DoGoodInfoActivity.this,
						AddMoneyActivity.class), ADDING_MONEY);
			}

		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Bundle bundle = data.getExtras();
		switch (resultCode) {
		case RESULT_OK:
			switch (requestCode) {
			case ADDING_MONEY:
				Intent returnInfo = new Intent();
				returnInfo.putExtra("amount", bundle.getString("index"));
				returnInfo.putExtra("name", name);

				setResult(RESULT_OK, returnInfo);
				finish();
				break;
			}
			break;
		}
	}
}
