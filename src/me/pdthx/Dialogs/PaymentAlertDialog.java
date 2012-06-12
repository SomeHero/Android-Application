package me.pdthx.Dialogs;

import me.pdthx.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class PaymentAlertDialog extends Activity {

	private TextView message;
	private TextView title;
	private Button confirm;

	private String theMessage;
	private String theTitle;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.alert_dialog);
		
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

		Bundle extras = getIntent().getExtras();
		theMessage = extras.getString("msg");
		theTitle = extras.getString("title");
		
		title = (TextView) findViewById(R.id.dis_title);
		title.setText(theTitle);
		message = (TextView) findViewById(R.id.dis_msg);
		message.setText(theMessage);
		confirm = (Button) findViewById(R.id.dis_button);
		confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}

		});
	}
}
