package me.pdthx;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Iterator;

import com.zubhium.ZubhiumSDK;
import com.zubhium.ZubhiumSDK.CrashReportingMode;

import me.pdthx.Adapters.PaystreamAdapter;
import me.pdthx.Dialogs.FilterPayStreamDialog;
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
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

public final class PaystreamActivity extends BaseActivity {

	private ProgressDialog m_ProgressDialog = null;
	private ArrayList<PaystreamTransaction> m_transactions = null;
	private PaystreamAdapter m_adapter;
	private Runnable viewOrders;
//	private SearchView searchView = null;
	ZubhiumSDK sdk;
	private int FILTER_PAYSTREAM = 1;
	public static final String TAG = "PaystreamActivity";
	private ListView mListView = null;
	private TextView mEmptyTextView = null;

	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			showPaystreamController();

		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		sdk = ZubhiumSDK.getZubhiumSDKInstance(PaystreamActivity.this, getString(R.string.secret_key));

		if(sdk != null){
			sdk.setCrashReportingMode(CrashReportingMode.SILENT);
		}

		prefs = PreferenceManager
				.getDefaultSharedPreferences(this);

		System.out.println(prefs.getString("userId", ""));
		System.out.println(prefs.getString("mobileNumber", ""));

		if(prefs.getString("userId", "").length() == 0)        {
			startActivityForResult(new Intent(this, SignInActivity.class), 1);
		}
		else {
			showPaystreamController();
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
		setContentView(R.layout.paystream_controller);
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
				PaystreamTransaction ref = m_transactions.get(arg2);

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
		});
		viewOrders = new Runnable() {
			@Override
			public void run() {
				getOrders();
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
				m_adapter.notifyDataSetChanged();

				for (int i = 0; i < m_transactions.size(); i++)
					m_adapter.add(m_transactions.get(i));
			}
			m_ProgressDialog.dismiss();
			m_adapter.notifyDataSetChanged();

			if (m_transactions.isEmpty())
				mEmptyTextView.setVisibility(View.VISIBLE);

		}
	};

	private void getOrders() {
		try {
			UserRequest request = new UserRequest();

			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(this);
			String userId = prefs.getString("userId", "");

			request.UserId = userId;
			ArrayList<PaystreamResponse> messages = PaystreamService
					.getMessages(request);

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

				currentHeader = df.format(currentTransaction.CreateDate);
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
	
}
