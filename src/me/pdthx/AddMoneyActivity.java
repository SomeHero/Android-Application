package me.pdthx;

import java.text.NumberFormat;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
		tracker.trackPageView("Amount");

	}

	protected void launchAddMoneyView() {
		setContentView(R.layout.money_amount_controller);

		goButton = (Button) findViewById(R.id.goBtn);
		oneDollar = (Button) findViewById(R.id.oneDollar);
		fiveDollar = (Button) findViewById(R.id.fiveDollar);
		tenDollar = (Button) findViewById(R.id.tenDollar);
		twentyDollar = (Button) findViewById(R.id.twentyDollar);
		amount = (EditText) findViewById(R.id.amount);

		amount.addTextChangedListener(new TextWatcher() {
			String current = "";

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			if (!s.toString().equals(current)) {
				amount.removeTextChangedListener(this);

				String cleanString = s.toString().replaceAll("[$,.]", "");

				double parsed = Double.parseDouble(cleanString);
				String formatted = NumberFormat.getCurrencyInstance()
						.format((parsed / 100));

				current = formatted;
				amount.setText(formatted);
				amount.setSelection(formatted.length());
				
				amount.addTextChangedListener(this);
			}
		}

		@Override
		public void afterTextChanged(Editable arg0) {
			// TODO Auto-generated method stub
			if (arg0.length() == 14) {
				arg0.replace(13, 14, "");
			}
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1,
				int arg2, int arg3) {
			// TODO Auto-generated method stub

		}
	});
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
