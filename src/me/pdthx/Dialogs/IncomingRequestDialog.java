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

	private ImageView pic;
	private TextView username;
	private ImageView payStatusPic;
	private TextView payStatusText;
	private TextView theAmount;
	private TextView payDate;
	private TextView payTime;
	private TextView comments;
	// private EditText sendMessage;

	/**
	 * In incoming requests, accept, reject, and ignore buttons are made.
	 */
	private Button button1;
	private Button button2;
	private Button button3;
	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private GestureDetector gestureDetector;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Display display = getWindowManager().getDefaultDisplay();
		int width = display.getWidth();
		int height = display.getHeight();
		if (width < height) {
			setContentView(R.layout.dialog);
		} else {
			setContentView(R.layout.dialog_land);
		}

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
		pic = (ImageView) findViewById(R.id.paypic);
		if (pic != null) {
			pic.setImageResource(R.drawable.paystream_request_received_icon);
		}

		username = (TextView) findViewById(R.id.pay_username);
		// since incoming, set text to sender's name
		if (username != null) {
			username.setText(senderUri);
		}

		payStatusPic = (ImageView) findViewById(R.id.pay_status);
		if (payStatusPic != null) {
			if (transactionStatus.toUpperCase().equals("SUBMITTED")) {
				payStatusPic
						.setImageResource(R.drawable.transaction_pending_icon);
			} else if (transactionStatus.toUpperCase().equals("PENDING")) {
				payStatusPic
						.setImageResource(R.drawable.transaction_pending_icon);
			} else if (transactionStatus.toUpperCase().equals("COMPLETE")) {
				payStatusPic
						.setImageResource(R.drawable.transaction_complete_icon);
			} else {
				payStatusPic
						.setImageResource(R.drawable.transaction_pending_icon);
			}
			// else if(o.getTransactionStatus().toUpperCase() == "FAILED") {
			// imgStatus.setImageResource(R.drawable.transaction_failed_icon);
			// }else if(o.getTransactionStatus().toUpperCase() == "RETURNED") {
			// imgStatus.setImageResource(R.drawable.transaction_returned_icon);
			// } else if(o.getTransactionStatus().toUpperCase() == "CANCELLED")
			// {
			// imgStatus.setImageResource(R.drawable.transaction_cancelled_icon);
			// }
		}

		payStatusText = (TextView) findViewById(R.id.pay_status_txt);
		if (payStatusText != null) {
			if (transactionStatus.toUpperCase().equals("SUBMITTED")) {
				payStatusText.setText("Submitted");
			} else if (transactionStatus.toUpperCase().equals("PENDING")) {
				payStatusText.setText("Pending");
			} else if (transactionStatus.toUpperCase().equals("COMPLETE")) {
				payStatusText.setText("Complete");
			} else {
				payStatusText.setText("Pending");
			}
		}

		theAmount = (TextView) findViewById(R.id.pay_amountgiven);
		if (theAmount != null) {
			theAmount.setText(currencyFormatter.format(amount));
		}

		payDate = (TextView) findViewById(R.id.pay_date);
		if (payDate != null) {
			payDate.setText(header);
		}

		payTime = (TextView) findViewById(R.id.pay_time);
		if (payTime != null) {
			payTime.setText(createDate);
		}

		comments = (TextView) findViewById(R.id.pay_comments);
		comments.setMovementMethod(new ScrollingMovementMethod());

		// sendMessage = (EditText) findViewById(R.id.pay_send_msg);

		/*
		 * Accept: send request to message service that a request was accepted
		 * 
		 * Reject: send request to message service that a request was rejected
		 * 
		 * Ignore : send request to message serve that a request was ignored
		 */

		button1 = (Button) findViewById(R.id.pay_button1);
		if (button1 != null) {
			if (transactionStatus.toUpperCase().equals("SUBMITTED")
					|| transactionStatus.toUpperCase().equals("PENDING")) {
				button1.setText("Accept");
				button1.setOnClickListener(new OnClickListener() {
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
										"The accept service has not yet been implemented. \n \n Service code " + isSuccess + " occurred.");
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
				button1.setVisibility(View.GONE);
			}
		}

		button2 = (Button) findViewById(R.id.pay_button2);
		if (button2 != null) {

			if (transactionStatus.toUpperCase().equals("SUBMITTED")
					|| transactionStatus.toUpperCase().equals("PENDING")) {
				button2.setText("Reject");
				button2.setOnClickListener(new OnClickListener() {
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
										"The reject service has not yet been implemented. \n \n Service code " + isSuccess + " occurred.");
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
				button2.setVisibility(View.GONE);
			}
		}
		button3 = (Button) findViewById(R.id.pay_button3);
		if (button3 != null) {

			if (transactionStatus.toUpperCase().equals("SUBMITTED")
					|| transactionStatus.toUpperCase().equals("PENDING")) {
				button3.setText("Ignore");
				button3.setOnClickListener(new OnClickListener() {
					public void onClick(View argO) {
						try {
							PaystreamService messageService = new PaystreamService();
							int isSuccess = messageService
									.ignoreRequestMessage(transactionId);
							// if the message request is a success, then return
							// to
							// user
							// that message was sent

							if (isSuccess == 200) {
								Intent intent = new Intent(
										getApplicationContext(),
										PaymentAlertDialog.class);
								intent.putExtra("msg", "Your request with "
										+ recipientUri + " was sent to server.");
								intent.putExtra("title", "Ignore...");
								startActivity(intent);
								finish();
							} else {
								Intent intent = new Intent(
										getApplicationContext(),
										PaymentAlertDialog.class);
								intent.putExtra("msg",
										"The ignore service has not yet been implemented. \n \n Service code "
												+ isSuccess + " occurred.");
								intent.putExtra("title", "Ignore...");
								startActivity(intent);
								finish();
							}
						} catch (Exception e) {
							//
						}
					}
				});
			} else {
				button3.setVisibility(View.GONE);
			}
		}
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
