package me.pdthx;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class AddMoneyActivity extends BaseActivity {

	private Button goButton;
	private Button oneDollar;
	private Button fiveDollar;
	private Button tenDollar;
	private Button twentyDollar;
	private EditText amount;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		launchAddMoneyView();
	}

	@Override
	public void onResume() {
		super.onResume();

	}

	@Override
	public void onPause() {
		super.onPause();
	}


	protected void launchAddMoneyView() {
		setContentView(R.layout.money_amount_controller);

		goButton = (Button) findViewById(R.id.goBtn);
		oneDollar = (Button) findViewById(R.id.oneDollar);
		fiveDollar = (Button) findViewById(R.id.fiveDollar);
		tenDollar = (Button) findViewById(R.id.tenDollar);
		twentyDollar = (Button) findViewById(R.id.twentyDollar);
		amount = (EditText) findViewById(R.id.amount);

		goButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent data = new Intent();
				data.putExtra("index", amount.getText().toString());

				setResult(RESULT_OK, data);
				finish();	
			}

		});
		oneDollar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent data = new Intent();
				data.putExtra("index", "1");

				setResult(RESULT_OK, data);
				finish();	
			}

		});
		fiveDollar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent data = new Intent();
				data.putExtra("index", "5");

				setResult(RESULT_OK, data);
				finish();	
			}

		});
		tenDollar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent data = new Intent();
				data.putExtra("index", "10");

				setResult(RESULT_OK, data);
				finish();	
			}

		});
		twentyDollar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent data = new Intent();
				data.putExtra("index", "20");

				setResult(RESULT_OK, data);
				finish();	
			}

		});
	}

}
