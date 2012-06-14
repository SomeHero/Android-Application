package me.pdthx.Dialogs;

import java.text.NumberFormat;

import me.pdthx.R;
import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class IncomingPaymentDialog extends Activity implements OnTouchListener {

	private String header = "";
	private String senderUri = "";
	private String recipientUri = "";
	private Double amount = 0.0;
	private String transactionStatus = "";
	private String transactionType = "";
	private String transactionId = "";
	private String createDate = null;
	private NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
	private String user = "";
	private String comments = "";

	private Button closeButton;
	private TextView titleText;
	private TextView usernameText;
	private TextView username2Text;
	private TextView amountText;
	private TextView commentText;
	private TextView dateText;
	private TextView statusText;
	private TextView typeText;
	private Button rejectButton;
	private Button acceptButton;
	private Button reminderButton;
	/**
	 * None of the buttons are viewed in incoming payments.
	 */

	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private GestureDetector gestureDetector;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dialog);
		// retrieve data
		Bundle extras = getIntent().getExtras();
		senderUri = extras.getString("sender");
		recipientUri = extras.getString("recipient");
		createDate = extras.getString("time");
		header = extras.getString("date");
		amount = extras.getDouble("amount");
		transactionType = extras.getString("transactionType");
		transactionStatus = extras.getString("transactionStat");
		transactionId = extras.getString("transactionId");
		user = extras.getString("username");
		comments = extras.getString("comments");

		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		getWindow().setGravity(Gravity.BOTTOM | Gravity.RIGHT);

		gestureDetector = new GestureDetector(new MyGestureDetector());
		View mainview = (View) findViewById(R.id.theDialog);

		// Set the touch listener for the main view to be our custom gesture
		// listener
		mainview.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (gestureDetector.onTouchEvent(event)) {
					return true;
				}
				return false;
			}
		});

		setupButtons();
	}

	private void setupButtons() {

		closeButton = (Button) findViewById(R.id.pDialog_CloseButton);
		closeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}

		});

		titleText = (TextView) findViewById(R.id.pDialog_Title);
		if (titleText != null) {
			titleText.setText("$ Received");
		}

		usernameText = (TextView) findViewById(R.id.pDialog_Username);
		if (usernameText != null) {
			usernameText.setText(recipientUri);
		}

		username2Text = (TextView) findViewById(R.id.pDialog_Username2);
		// since outgoing, put recipient's name
		if (username2Text != null) {
			username2Text.setText(user);
		}

		// PICTURES FROM USERS
		// ????????????????

		amountText = (TextView) findViewById(R.id.pDialog_Amount);
		if (amountText != null) {
			amountText.setText("$" + amount + " ");
		}

		commentText = (TextView) findViewById(R.id.pDialog_Comments);
		if (commentText != null) {
			commentText.setText(comments);
		}

		dateText = (TextView) findViewById(R.id.pDialog_Date);
		if (dateText != null) {
			dateText.setText("on " + header + " at " + createDate);
		}

		statusText = (TextView) findViewById(R.id.pDialog_Status);
		if (statusText != null) {
			statusText.setText(transactionStatus);
		}

		typeText = (TextView) findViewById(R.id.pDialog_type);
		if (typeText != null) {
			typeText.setText("sent ");
		}

		rejectButton = (Button) findViewById(R.id.pDialog_Reject);
		rejectButton.setVisibility(View.GONE);
		acceptButton = (Button) findViewById(R.id.pDialog_Accept);
		acceptButton.setVisibility(View.GONE);
		reminderButton = (Button) findViewById(R.id.pDialog_Reminder);
		reminderButton.setVisibility(View.GONE);

		acceptButton.setVisibility(View.GONE);

		Button borderLeft = (Button) findViewById(R.id.payDialogLeft);
		Button borderTop = (Button) findViewById(R.id.payDialogTop);
		borderLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}

		});
		borderTop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}

		});
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return true;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		Rect dialogBounds = new Rect();
		getWindow().getDecorView().getHitRect(dialogBounds);

		if (!dialogBounds.contains((int) ev.getX(), (int) ev.getY())) {
			// Tapped outside so we finish the activity
			this.finish();
		}
		return super.dispatchTouchEvent(ev);
	}

	private class MyGestureDetector extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) {
				return false;
			}

			// right to left swipe
			if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
					&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				finish();
			}

			return false;
		}

		// It is necessary to return true from onDown for the onFling event to
		// register
		@Override
		public boolean onDown(MotionEvent e) {
			return true;
		}
	}
}
