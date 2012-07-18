package me.pdthx.Dialogs;

import java.text.NumberFormat;

import me.pdthx.R;
import me.pdthx.Models.PaystreamTransaction;
import me.pdthx.Services.PaystreamService;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
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

public class OutgoingPaymentDialog extends Activity implements OnTouchListener {

	private String header = "";
	private String recipientUri = "";
	private String senderUri = "";
	private String amount = "";
	private String transactionStatus = "";
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
	// private EditText sendMessage;

	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private GestureDetector gestureDetector;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dialog);
		// retrieve data
		PaystreamTransaction ref = (PaystreamTransaction) getIntent().getParcelableExtra("obj");
		Bundle extras = getIntent().getExtras();
		recipientUri = ref.getRecipientUri();
		senderUri = ref.getSenderUri();
		createDate = ref.getTimeString();
		header = ref.getDateString();
		amount = currencyFormatter.format(ref.getAmount());
		transactionStatus = ref.getTransactionStatus();
		transactionId = ref.getTransactionId();
		user = extras.getString("username");
		comments = ref.getComments();

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
			titleText.setText("$ Sent");
		}

		usernameText = (TextView) findViewById(R.id.pDialog_Username);
		if (usernameText != null) {
			usernameText.setText(user);
		}

		username2Text = (TextView) findViewById(R.id.pDialog_Username2);
		// since outgoing, put recipient's name
		if (username2Text != null) {
			username2Text.setText(recipientUri);
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

		/**
		 * Case 1: cancel, process is still pending and not completed, can be
		 * canceled.
		 * 
		 * Case 2: refund, process is completed but user wants to get money
		 * back. (Ends up being a request back to the recipient for money back)
		 */
		rejectButton = (Button) findViewById(R.id.pDialog_Reject);
		if (rejectButton != null) {
			if (transactionStatus.toUpperCase().equals("COMPLETE")) {
				rejectButton.setText("Refund Payment");
				rejectButton.setOnClickListener(new OnClickListener() {
					public void onClick(View argO) {
						try {
							PaystreamService messageService = new PaystreamService();
							int isSuccess = messageService
									.refundMessage(transactionId);
							// if the message request is a success, then return
							// to
							// user
							// that message was sent
							if (isSuccess == 200) {
								Intent intent = new Intent(
										getApplicationContext(),
										PaymentAlertDialog.class);
								intent.putExtra("msg",
										"Your request for a refund with "
												+ recipientUri + " was sent.");
								intent.putExtra("title", "Refund...");
								startActivity(intent);
								finish();
							} else {
								Intent intent = new Intent(
										getApplicationContext(),
										PaymentAlertDialog.class);
								intent.putExtra("msg",
										"The refund service has not yet been implemented. \n \n Service code "
												+ isSuccess + " occurred.");
								intent.putExtra("title", "Refund...");
								startActivity(intent);
								finish();
							}
						} catch (Exception e) {
							setContentView(R.layout.alert_dialog);
						}
					}
				});
			} else if (transactionStatus.toUpperCase().equals("PROCESSING")
					|| transactionStatus.toUpperCase().equals("SUBMITTED")) {
				rejectButton.setText("Cancel Payment");
				rejectButton.setOnClickListener(new OnClickListener() {
					public void onClick(View argO) {
						try {
							PaystreamService messageService = new PaystreamService();
							int isSuccess = messageService
									.cancelMessage(transactionId);
							// if the message request is a success, then return
							// to
							// user
							// that message was sent
							if (isSuccess == 200) {
								Intent intent = new Intent(
										getApplicationContext(),
										PaymentAlertDialog.class);
								intent.putExtra("msg", "A cancel request with "
										+ recipientUri + " was sent.");
								intent.putExtra("title", "Cancel...");
								startActivity(intent);
								finish();
							} else {
								Intent intent = new Intent(
										getApplicationContext(),
										PaymentAlertDialog.class);
								intent.putExtra("msg",
										"Cancel service has not yet been implemented. \n \n Service code "
												+ isSuccess + " occurred.");
								intent.putExtra("title", "Cancel...");
								startActivity(intent);
								finish();
							}
						} catch (Exception e) {
							setContentView(R.layout.alert_dialog);
							getWindow().setGravity(Gravity.CENTER);
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
					|| transactionStatus.toUpperCase().equals("PROCESSING")) {
				// pending
			} else {
				reminderButton.setVisibility(View.GONE);
			}
		}
		acceptButton = (Button) findViewById(R.id.pDialog_Accept);
		acceptButton.setVisibility(View.GONE);
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
