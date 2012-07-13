package me.pdthx;

import me.pdthx.CustomViews.CustomLockView;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

public class SecurityPinActivity extends BaseActivity {

	private TextView securityPinHeader;
	private TextView securityPinBody;
	private CustomLockView securityPinView;
	private final static int USERSECURITYPIN_INVALIDLENGTH = 5;

	private Handler securityPinHandler = new Handler() {

		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
				case USERSECURITYPIN_INVALIDLENGTH:
					alertDialog.setTitle("Invalid Length");
					alertDialog
							.setMessage("Your pincode must contain" +
									"at least 4 inputs.");
					alertDialog.setButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							});

					alertDialog.show();
					break;
			}
		}
	};


	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.setup_security_dialog);

		Bundle extras = getIntent().getExtras();

		securityPinHeader = (TextView) findViewById(R.id.setupSecurityHeader);
		securityPinBody = (TextView) findViewById(R.id.setupSecurityBody);
		securityPinView = (CustomLockView) findViewById(R.id.ctrlSecurityPin);

		if (extras != null)
		{
			securityPinHeader.setText(extras.getString("headerText"));
			securityPinBody.setText(extras.getString("bodyText"));
		}

		securityPinView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				String passcode = securityPinView.getPasscode();

				if (passcode.length() > 3) {
					Intent intent = new Intent();
					intent.putExtra("passcode", passcode);

					setResult(RESULT_OK, intent);
					finish();
					return true;
				}
				else
				{
					securityPinHandler.sendEmptyMessage(USERSECURITYPIN_INVALIDLENGTH);
				}
				return false;

			}
		});
	}
}
