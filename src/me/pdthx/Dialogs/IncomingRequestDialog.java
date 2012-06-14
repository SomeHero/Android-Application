package me.pdthx.Dialogs;

import java.text.NumberFormat;

import me.pdthx.R;
import me.pdthx.Services.PaystreamService;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class IncomingRequestDialog extends Activity implements OnTouchListener {

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
	 * In incoming requests, accept, reject, and ignore buttons are made.
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
			titleText.setText("Request Received");
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
			typeText.setText("requested ");
		}

		acceptButton = (Button) findViewById(R.id.pDialog_Accept);
		if (acceptButton != null) {
			if (transactionStatus.toUpperCase().equals("SUBMITTED")
					|| transactionStatus.toUpperCase().equals("PENDING")) {
				acceptButton.setText("Accept");
				acceptButton.setOnClickListener(new OnClickListener() {
					public void onClick(View argO) {
						try {
							PaystreamService messageService = new PaystreamService();
							int isSuccess = messageService
									.acceptRequestMessage(transactionId);
							// if the message request is a success, then return
							// to
							// user
							// that message was sent
							if (isSuccess == 200) {
								Intent intent = new Intent(
										getApplicationContext(),
										PaymentAlertDialog.class);
								intent.putExtra("msg", "Your request with "
										+ recipientUri + " was sent.");
								intent.putExtra("title", "Accept...");
								startActivity(intent);
								finish();
							} else {
								Intent intent = new Intent(
										getApplicationContext(),
										PaymentAlertDialog.class);
								intent.putExtra("msg",
										"The accept service has not yet been implemented. \n \n Service code "
												+ isSuccess + " occurred.");
								intent.putExtra("title", "Accept...");
								startActivity(intent);
								finish();
							}
						} catch (Exception e) {
							//
						}
					}
				});
			} else {
				acceptButton.setVisibility(View.GONE);
			}
		}

		rejectButton = (Button) findViewById(R.id.pDialog_Reject);
		if (rejectButton != null) {

			if (transactionStatus.toUpperCase().equals("SUBMITTED")
					|| transactionStatus.toUpperCase().equals("PENDING")) {
				rejectButton.setText("Reject Request");
				rejectButton.setOnClickListener(new OnClickListener() {
					public void onClick(View argO) {
						try {
							PaystreamService messageService = new PaystreamService();
							int isSuccess = messageService
									.rejectRequestMessage(transactionId);
							// if message succeeded, reject was done.

							if (isSuccess == 200) {
								Intent intent = new Intent(
										getApplicationContext(),
										PaymentAlertDialog.class);
								intent.putExtra("msg", "Your request with "
										+ recipientUri + " was rejected.");
								intent.putExtra("title", "Reject...");
								startActivity(intent);
								finish();
							} else {
								Intent intent = new Intent(
										getApplicationContext(),
										PaymentAlertDialog.class);
								intent.putExtra("msg",
										"The reject service has not yet been implemented. \n \n Service code "
												+ isSuccess + " occurred.");
								intent.putExtra("title", "Reject...");
								startActivity(intent);
								finish();
							}
						} catch (Exception e) {
							//
						}
					}
				});
			} else {
				rejectButton.setVisibility(View.GONE);
			}
		}
		reminderButton = (Button) findViewById(R.id.pDialog_Reminder);
		if (reminderButton != null) {

			if (transactionStatus.toUpperCase().equals("SUBMITTED")
					|| transactionStatus.toUpperCase().equals("PENDING")) {
				// pending
			} else {
				reminderButton.setVisibility(View.GONE);
			}
		}

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
