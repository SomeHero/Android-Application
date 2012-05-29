package me.pdthx;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import com.zubhium.ZubhiumSDK;
import com.zubhium.ZubhiumSDK.CrashReportingMode;

import me.pdthx.Adapters.PaystreamAdapter;
import me.pdthx.Models.PaystreamTransaction;
import me.pdthx.Requests.TransactionRequest;
import me.pdthx.Responses.TransactionResponse;
import me.pdthx.Services.TransactionService;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public final class PaystreamActivity extends BaseActivity {

	private ProgressDialog m_ProgressDialog = null;
	private ArrayList<PaystreamTransaction> m_transactions = null;
	private PaystreamAdapter m_adapter;
	private Runnable viewOrders;

	ZubhiumSDK sdk;
	private static final String TAG = "PaystreamActivity";
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

		sdk = ZubhiumSDK.getZubhiumSDKInstance(PaystreamActivity.this,
				getString(R.string.secret_key));

		if (sdk != null) {
			sdk.setCrashReportingMode(CrashReportingMode.SILENT);
		}

		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		if (prefs.getString("userId", "").length() == 0
				|| prefs.getString("mobileNumber", "").length() == 0) {
			showSignInActivity();
		} else {
			showPaystreamController();
		}
	}

	protected void showSignInActivity() {
		SignInActivity signInActivity = new SignInActivity(this, mHandler,
				prefs);
		signInActivity.showSignInActivity();
	}

	private void showPaystreamController() {
		setContentView(R.layout.paystream_controller);
		m_transactions = new ArrayList<PaystreamTransaction>();
		mListView = (ListView) findViewById(R.id.lvPaystream);
		mEmptyTextView = (TextView) findViewById(R.id.txtEmptyPaystream);
		m_adapter = new PaystreamAdapter(this, R.layout.transaction_item,
				m_transactions);
		mListView.setAdapter(m_adapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				PaystreamTransaction ref = m_transactions.get(arg2);
				Intent temp = new Intent(getApplicationContext(),
						DialogPaystream.class);

				// send information into intent
				DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG);
				String date = dateFormat.format(ref.getCreateDate());
				DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.MEDIUM);
				String theTime = timeFormat.format(ref.getCreateDate());
				String recipient = ref.getRecipientUri();
				String sender = ref.getSenderUri();
				Double amount = ref.getAmount();
				String transactionType = ref.getTransactionType();
				String transactionStat = ref.getTransactionStatus();

				temp.putExtra("date", date);
				temp.putExtra("time", theTime);
				temp.putExtra("recipient", recipient);
				temp.putExtra("sender", sender);
				temp.putExtra("amount", amount);
				temp.putExtra("transactionType", transactionType);
				temp.putExtra("transactionStat", transactionStat);

				startActivity(temp);
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
			TransactionService transactionService = new TransactionService();
			TransactionRequest transactionRequest = new TransactionRequest();

			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(this);
			String userId = prefs.getString("userId", "");

			transactionRequest.UserId = userId;
			ArrayList<TransactionResponse> transactions = transactionService
					.GetTransactions(transactionRequest);

			m_transactions = new ArrayList<PaystreamTransaction>();

			DateFormat df = DateFormat.getDateInstance();
			String previousHeader = "";
			String currentHeader = "";
			for (Iterator<TransactionResponse> i = transactions.iterator(); i
					.hasNext();) {
				TransactionResponse currentTransaction = (TransactionResponse) i
						.next();

				PaystreamTransaction o1 = new PaystreamTransaction();
				o1.setTransactionId(currentTransaction.TransactionId);
				o1.setPaymentId(currentTransaction.PaymentId);
				o1.setSenderUri(currentTransaction.SenderUri);
				o1.setRecipientUri(currentTransaction.RecipientUri);
				o1.setAmount(currentTransaction.Amount);
				o1.setACHTransactionId(currentTransaction.ACHTransactionId);
				o1.setTransactionStatus(currentTransaction.TransactionStatus);
				o1.setTransactionCategory(currentTransaction.TransactionCategory);
				o1.setTransactionType(currentTransaction.TransactionType);
				o1.setStandardEntryClass(currentTransaction.StandardEntryClass);
				o1.setPaymentChannel(currentTransaction.PaymentChannel);
				o1.setTransactionBatchId(currentTransaction.TransactionBatchId);
				o1.setTransactionSentDate(currentTransaction.TransactionSentDate);
				o1.setCreateDate(currentTransaction.CreateDate);
				o1.setLastUpdateDate(currentTransaction.LastUpdatedDate);

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

	@Override
	public void OnSignOutComplete() {
		showSignInActivity();
	}

}
