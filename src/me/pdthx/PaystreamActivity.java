package me.pdthx;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import me.pdthx.Adapters.PaystreamAdapter;
import me.pdthx.Dialogs.IncomingPaymentDialog;
import me.pdthx.Dialogs.IncomingRequestDialog;
import me.pdthx.Dialogs.OutgoingPaymentDialog;
import me.pdthx.Dialogs.OutgoingRequestDialog;
import me.pdthx.Models.PaystreamTransaction;
import me.pdthx.Requests.UserRequest;
import me.pdthx.Responses.PaystreamResponse;
import me.pdthx.Services.PaystreamService;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

public final class PaystreamActivity extends BaseActivity implements
		OnCheckedChangeListener {

	private ProgressDialog m_ProgressDialog = null;
	private ArrayList<PaystreamTransaction> m_transactions = null;
	private PaystreamAdapter m_adapter;
	private Runnable viewOrders;
	private RadioGroup paystreamCategory;

//	private SearchView searchView = null;
	private int FILTER_PAYSTREAM = 1;

	public static final String TAG = "PaystreamActivity";
	private ListView mListView = null;
	private TextView mEmptyTextView = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.out.println(prefs.getString("userId", ""));
		System.out.println(prefs.getString("mobileNumber", ""));

		if (prefs.getString("userId", "").length() == 0) {
			startActivityForResult(new Intent(this, SignInActivity.class), 1);
		} else {
			showPaystreamController();
			Bundle extras = getIntent().getExtras();
			
			if (extras != null && extras.getString("userId") != null && 
					prefs.getString("userId", "").equals(
							extras.getString("userId"))) {
				PaystreamTransaction transaction = new PaystreamTransaction();
				transaction.setTransactionId(extras.getString("transactionId"));
				
				//Use a fake transaction with the proper id to
				//get the real transaction.
				getTransactionDetails(m_transactions.indexOf(transaction));
				
				
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == 1) {
				showPaystreamController();
			}
		}
//		if(resultCode == FILTER_PAYSTREAM)
//		{
//			//Show new view?
//		}
		else {
			finish();
		}
		
	}
	private void showPaystreamController() {
		// setContentView(R.layout.paystream_controller);

		setContentView(R.layout.paystream_all);
		paystreamCategory = (RadioGroup) findViewById(R.id.paystreamSubCategories);
		paystreamCategory.setOnCheckedChangeListener(this);

		m_transactions = new ArrayList<PaystreamTransaction>();
		mListView = (ListView) findViewById(R.id.lvPaystream);
		m_adapter = new PaystreamAdapter(this, R.layout.transaction_item,
				m_transactions);
		mListView.setAdapter(m_adapter);
		mEmptyTextView = (TextView) findViewById(R.id.txtEmptyPaystream);

//		searchView = (SearchView)findViewById(R.id.searchBar);
		
//		searchView.setOnSearchClickListener(new OnClickListener()
//		{
//			@Override
//			public void onClick(View v) {
//				startActivityForResult(new Intent(PaystreamActivity.this, FilterPayStreamActivity.class), FILTER_PAYSTREAM);				
//				
//			}
//			
//		});
		
		
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				getTransactionDetails(arg2);
			}
		});
		viewOrders = new Runnable() {
			@Override
			public void run() {
				getAllOrders();
			}
		};
		Thread thread = new Thread(null, viewOrders, "MagentoBackground");
		thread.start();
		m_ProgressDialog = ProgressDialog.show(PaystreamActivity.this,
				"Please wait...", "Retrieving your paystream...", true);
	}

	private Runnable returnRes = new Runnable() {

		@Override
		public void run() {
			if (m_transactions != null && m_transactions.size() > 0) {
				mEmptyTextView.setVisibility(View.GONE);
				m_adapter.clear();

				for (int i = 0; i < m_transactions.size(); i++)
					m_adapter.add(m_transactions.get(i));
			} else {
				m_adapter.clear();
				mEmptyTextView.setVisibility(View.VISIBLE);
			}
			m_ProgressDialog.dismiss();
			m_adapter.notifyDataSetChanged();
		}
	};

	private void getAllOrders() {
		try {
			UserRequest messageRequest = new UserRequest();

			String userId = prefs.getString("userId", "");

			messageRequest.UserId = userId;
			ArrayList<PaystreamResponse> messages = PaystreamService
					.getMessages(messageRequest);

			m_transactions = new ArrayList<PaystreamTransaction>();

			DateFormat df = DateFormat.getDateInstance();
			String previousHeader = "";
			String currentHeader = "";
			for (Iterator<PaystreamResponse> i = messages.iterator(); i.hasNext();) {
				PaystreamResponse currentTransaction = (PaystreamResponse) i.next();

				PaystreamTransaction o1 = new PaystreamTransaction();
				o1.setTransactionId(currentTransaction.MessageId);
				o1.setSenderUri(currentTransaction.SenderUri);
				o1.setRecipientUri(currentTransaction.RecipientUri);
				o1.setAmount(currentTransaction.Amount);
				o1.setCreateDate(currentTransaction.CreateDate);
				o1.setLastUpdateDate(currentTransaction.CreateDate);
				o1.setTransactionType(currentTransaction.MessageType);
				o1.setTransactionStatus(currentTransaction.MessageStatus);
				o1.setDirection(currentTransaction.Direction);
				o1.setComments(currentTransaction.Comments);

				currentHeader = createHeader(currentTransaction);

				if (!previousHeader.equals(currentHeader)) {
					o1.setHeader(currentHeader);
					previousHeader = currentHeader;
				} else {
					o1.setHeader("");
				}

				m_transactions.add(o1);
			}

			// Log.i("ARRAY", ""+ m_transactions.size());
		} catch (Exception e) {
			Log.e("BACKGROUND_PROC", e.getMessage());
		}
		runOnUiThread(returnRes);
	}

	private void getSentOrders() {
		try {
			UserRequest messageRequest = new UserRequest();

			String userId = prefs.getString("userId", "");

			messageRequest.UserId = userId;
			ArrayList<PaystreamResponse> messages = PaystreamService
					.getMessages(messageRequest);

			m_transactions = new ArrayList<PaystreamTransaction>();

			DateFormat df = DateFormat.getDateInstance();
			String previousHeader = "";
			String currentHeader = "";
			for (Iterator<PaystreamResponse> i = messages.iterator(); i.hasNext();) {
				PaystreamResponse currentTransaction = (PaystreamResponse) i.next();
				if (currentTransaction.MessageType.equalsIgnoreCase("Payment")
						&& currentTransaction.Direction.equalsIgnoreCase("Out")) {
					PaystreamTransaction o1 = new PaystreamTransaction();
					o1.setTransactionId(currentTransaction.MessageId);
					o1.setSenderUri(currentTransaction.SenderUri);
					o1.setRecipientUri(currentTransaction.RecipientUri);
					o1.setAmount(currentTransaction.Amount);
					o1.setCreateDate(currentTransaction.CreateDate);
					o1.setLastUpdateDate(currentTransaction.CreateDate);
					o1.setTransactionType(currentTransaction.MessageType);
					o1.setTransactionStatus(currentTransaction.MessageStatus);
					o1.setDirection(currentTransaction.Direction);
					o1.setComments(currentTransaction.Comments);

					currentHeader = createHeader(currentTransaction);
					if (!previousHeader.equals(currentHeader)) {
						o1.setHeader(currentHeader);
						previousHeader = currentHeader;
					} else {
						o1.setHeader("");
					}

					m_transactions.add(o1);
				}
			}

			// Log.i("ARRAY", ""+ m_transactions.size());
		} catch (Exception e) {
			Log.e("BACKGROUND_PROC", e.getMessage());
		}
		runOnUiThread(returnRes);
	}

	private void getReceivedOrders() {
		try {
			UserRequest messageRequest = new UserRequest();

			String userId = prefs.getString("userId", "");

			messageRequest.UserId = userId;
			ArrayList<PaystreamResponse> messages = PaystreamService
					.getMessages(messageRequest);

			m_transactions = new ArrayList<PaystreamTransaction>();

			DateFormat df = DateFormat.getDateInstance();
			String previousHeader = "";
			String currentHeader = "";
			for (Iterator<PaystreamResponse> i = messages.iterator(); i.hasNext();) {
				PaystreamResponse currentTransaction = (PaystreamResponse) i.next();

				if (currentTransaction.MessageType.equalsIgnoreCase("Payment")
						&& !currentTransaction.Direction
								.equalsIgnoreCase("Out")) {
					PaystreamTransaction o1 = new PaystreamTransaction();
					o1.setTransactionId(currentTransaction.MessageId);
					o1.setSenderUri(currentTransaction.SenderUri);
					o1.setRecipientUri(currentTransaction.RecipientUri);
					o1.setAmount(currentTransaction.Amount);
					o1.setCreateDate(currentTransaction.CreateDate);
					o1.setLastUpdateDate(currentTransaction.CreateDate);
					o1.setTransactionType(currentTransaction.MessageType);
					o1.setTransactionStatus(currentTransaction.MessageStatus);
					o1.setDirection(currentTransaction.Direction);
					o1.setComments(currentTransaction.Comments);

					currentHeader = createHeader(currentTransaction);
					if (!previousHeader.equals(currentHeader)) {
						o1.setHeader(currentHeader);
						previousHeader = currentHeader;
					} else {
						o1.setHeader("");
					}

					m_transactions.add(o1);
				}
			}

			// Log.i("ARRAY", ""+ m_transactions.size());
		} catch (Exception e) {
			Log.e("BACKGROUND_PROC", e.getMessage());
		}
		runOnUiThread(returnRes);
	}

	private void getOtherOrders() {
		try {
			UserRequest messageRequest = new UserRequest();

			String userId = prefs.getString("userId", "");

			messageRequest.UserId = userId;
			ArrayList<PaystreamResponse> messages = PaystreamService
					.getMessages(messageRequest);

			m_transactions = new ArrayList<PaystreamTransaction>();

			DateFormat df = DateFormat.getDateInstance();
			String previousHeader = "";
			String currentHeader = "";
			for (Iterator<PaystreamResponse> i = messages.iterator(); i.hasNext();) {
				PaystreamResponse currentTransaction = (PaystreamResponse) i.next();

				if (!currentTransaction.MessageType.equalsIgnoreCase("Payment")) {
					PaystreamTransaction o1 = new PaystreamTransaction();
					o1.setTransactionId(currentTransaction.MessageId);
					o1.setSenderUri(currentTransaction.SenderUri);
					o1.setRecipientUri(currentTransaction.RecipientUri);
					o1.setAmount(currentTransaction.Amount);
					o1.setCreateDate(currentTransaction.CreateDate);
					o1.setLastUpdateDate(currentTransaction.CreateDate);
					o1.setTransactionType(currentTransaction.MessageType);
					o1.setTransactionStatus(currentTransaction.MessageStatus);
					o1.setDirection(currentTransaction.Direction);
					o1.setComments(currentTransaction.Comments);

					currentHeader = createHeader(currentTransaction);
					if (!previousHeader.equals(currentHeader)) {
						o1.setHeader(currentHeader);
						previousHeader = currentHeader;
					} else {
						o1.setHeader("");
					}

					m_transactions.add(o1);
				}
			}

			// Log.i("ARRAY", ""+ m_transactions.size());
		} catch (Exception e) {
			Log.e("BACKGROUND_PROC", e.getMessage());
		}
		runOnUiThread(returnRes);
	}

	@Override
	public void onCheckedChanged(RadioGroup arg0, int arg1) {
		if (arg1 == R.id.paystreamAll) {
			viewOrders = new Runnable() {
				@Override
				public void run() {
					getAllOrders();
				}
			};
			Thread thread = new Thread(null, viewOrders, "MagentoBackground");
			thread.start();
			m_ProgressDialog = ProgressDialog.show(PaystreamActivity.this,
					"Please wait...", "Retrieving your paystream...", true);
		} else if (arg1 == R.id.paystreamSent) {
			viewOrders = new Runnable() {
				@Override
				public void run() {
					getSentOrders();
				}
			};
			Thread thread = new Thread(null, viewOrders, "MagentoBackground");
			thread.start();
			m_ProgressDialog = ProgressDialog.show(PaystreamActivity.this,
					"Please wait...", "Retrieving your paystream...", true);
		} else if (arg1 == R.id.paystreamReceived) {
			viewOrders = new Runnable() {
				@Override
				public void run() {
					getReceivedOrders();
				}
			};
			Thread thread = new Thread(null, viewOrders, "MagentoBackground");
			thread.start();
			m_ProgressDialog = ProgressDialog.show(PaystreamActivity.this,
					"Please wait...", "Retrieving your paystream...", true);
		} else if (arg1 == R.id.paystreamOther) {
			viewOrders = new Runnable() {
				@Override
				public void run() {
					getOtherOrders();
				}
			};
			Thread thread = new Thread(null, viewOrders, "MagentoBackground");
			thread.start();
			m_ProgressDialog = ProgressDialog.show(PaystreamActivity.this,
					"Please wait...", "Retrieving your paystream...", true);
		}
	}

	public String createHeader(PaystreamResponse currentTransaction) {
		String currentHeader = "";
		DateFormat df = DateFormat.getDateInstance();
		/*
		 * Change header style: today, this week, 2 weeks ago... 1 month ago, 2
		 * month ago... 1 year... etc.
		 */
		Calendar currentDate = Calendar.getInstance();
		Calendar compareTo = Calendar.getInstance();
		compareTo.setTime(currentTransaction.CreateDate);
		Date d = currentDate.getTime();
		Date comp = compareTo.getTime();
		if (d.getMonth() == comp.getMonth() && d.getYear() == comp.getYear()
				&& d.getDate() == comp.getDate()) {
			currentHeader = "Today";
		} else {
			currentDate.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
			Date firstDayofWk = currentDate.getTime();
			currentDate.add(Calendar.DATE, 7);
			Date lastDayofWk = currentDate.getTime();
			if (comp.after(firstDayofWk) && comp.before(lastDayofWk)) {
				currentHeader = "This week";
			} else {
				currentDate.roll(Calendar.DATE, -15); // two wks ago, sunday
				Date firstDayofLastWk = currentDate.getTime();
				currentDate.add(Calendar.DATE, 8);
				Date lastDayofLastWk = currentDate.getTime(); // to monday
				if (comp.after(firstDayofLastWk)
						&& comp.before(lastDayofLastWk)) {
					currentHeader = "Last week";
				} else {
					currentDate.set(Calendar.DAY_OF_MONTH, 1);
					currentDate.roll(Calendar.DATE, -1);
					Date firstDayofMonth = currentDate.getTime();
					int maxDays = currentDate
							.getActualMaximum(Calendar.DAY_OF_MONTH);
					currentDate.set(Calendar.DAY_OF_MONTH, maxDays);
					currentDate.add(Calendar.DATE, 1);
					Date lastDayofMonth = currentDate.getTime();
					if (comp.after(firstDayofMonth)
							&& comp.before(lastDayofMonth)) {
						currentHeader = "This month";
					}
					else
					{
						currentDate.roll(Calendar.MONTH, -1);
						currentDate.set(Calendar.DAY_OF_MONTH, 1);
						currentDate.roll(Calendar.DATE, -1);
						Date firstDayLastMonth = currentDate.getTime();
						maxDays = currentDate.getActualMaximum(Calendar.DAY_OF_MONTH);
						currentDate.set(Calendar.DAY_OF_MONTH, maxDays);
						currentDate.add(Calendar.DATE, 1);
						Date lastDayLastMonth = currentDate.getTime();
						if(comp.after(firstDayLastMonth) && comp.before(lastDayLastMonth))
						{
							currentHeader = "Last month";
						}
						else
						{
							currentHeader = comp.getMonth() + " " + comp.getYear();
						}
					}
				}
			}
		}
		return currentHeader;
	}
	
	private void getTransactionDetails(int index) {
		PaystreamTransaction ref = m_transactions.get(index);	
		
		DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG);
		String date = dateFormat.format(ref.getCreateDate());
		DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.MEDIUM);
		String theTime = timeFormat.format(ref.getCreateDate());
		String recipient = ref.getRecipientUri();
		String sender = ref.getSenderUri();

		Double amount = ref.getAmount();
		String transactionType = ref.getTransactionType();
		String transactionStat = ref.getTransactionStatus();
		String transactionId = ref.getTransactionId();

		// first create intent based on what the transaction type is
		// 1) determine outgoing or incoming
		// 2) determine payment or request
		if (ref.getDirection().equalsIgnoreCase("Out")) {
			if (ref.getTransactionType().equalsIgnoreCase("Payment")) {
				// imgTransactionType.setImageResource(R.drawable.paystream_sent_icon);
				Intent temp1 = new Intent(getApplicationContext(),
						OutgoingPaymentDialog.class);
				temp1.putExtra("date", date);
				temp1.putExtra("time", theTime);
				temp1.putExtra("recipient", recipient);
				temp1.putExtra("sender", sender);
				temp1.putExtra("amount", amount);
				temp1.putExtra("transactionType", transactionType);
				temp1.putExtra("transactionStat", transactionStat);
				temp1.putExtra("transactionId", transactionId);
				startActivity(temp1);
			} else {
				Intent temp2 = new Intent(getApplicationContext(),
						OutgoingRequestDialog.class);
				temp2.putExtra("date", date);
				temp2.putExtra("time", theTime);
				temp2.putExtra("recipient", recipient);
				temp2.putExtra("sender", sender);
				temp2.putExtra("amount", amount);
				temp2.putExtra("transactionType", transactionType);
				temp2.putExtra("transactionStat", transactionStat);
				temp2.putExtra("transactionId", transactionId);
				startActivity(temp2);
			}
		} else {
			if (ref.getTransactionType().equalsIgnoreCase("Payment")) {
				Intent temp3 = new Intent(getApplicationContext(),
						IncomingPaymentDialog.class);
				temp3.putExtra("date", date);
				temp3.putExtra("time", theTime);
				temp3.putExtra("recipient", recipient);
				temp3.putExtra("sender", sender);
				temp3.putExtra("amount", amount);
				temp3.putExtra("transactionType", transactionType);
				temp3.putExtra("transactionStat", transactionStat);
				temp3.putExtra("transactionId", transactionId);
				startActivity(temp3);
			} else {
				Intent temp4 = new Intent(getApplicationContext(),
						IncomingRequestDialog.class);
				temp4.putExtra("date", date);
				temp4.putExtra("time", theTime);
				temp4.putExtra("recipient", recipient);
				temp4.putExtra("sender", sender);
				temp4.putExtra("amount", amount);
				temp4.putExtra("transactionType", transactionType);
				temp4.putExtra("transactionStat", transactionStat);
				temp4.putExtra("transactionId", transactionId);
				startActivity(temp4);
			}
		}
	}
}
