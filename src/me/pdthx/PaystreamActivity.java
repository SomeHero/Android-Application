package me.pdthx;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Iterator;

import com.zubhium.ZubhiumSDK;
import com.zubhium.ZubhiumSDK.CrashReportingMode;

import me.pdthx.Adapters.PaystreamAdapter;
import me.pdthx.Dialogs.DialogPaystream;
import me.pdthx.Dialogs.IncomingPaymentDialog;
import me.pdthx.Dialogs.IncomingRequestDialog;
import me.pdthx.Dialogs.OutgoingPaymentDialog;
import me.pdthx.Dialogs.OutgoingRequestDialog;
import me.pdthx.Models.PaystreamTransaction;
import me.pdthx.Requests.MessageRequest;
import me.pdthx.Responses.MessageResponse;
import me.pdthx.Services.MessageService;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;


public final class PaystreamActivity extends BaseActivity  {
	
	private ProgressDialog m_ProgressDialog = null; 
    private ArrayList<PaystreamTransaction> m_transactions = null;
    private PaystreamAdapter m_adapter;
    private Runnable viewOrders;
    	
	ZubhiumSDK sdk ; 
	
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

		sdk = ZubhiumSDK.getZubhiumSDKInstance(PaystreamActivity.this,
				getString(R.string.secret_key));

		if (sdk != null) {
			sdk.setCrashReportingMode(CrashReportingMode.SILENT);
		}

		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		System.out.println(prefs.getString("userId", ""));
		System.out.println(prefs.getString("mobileNumber", ""));

		if (prefs.getString("userId", "").length() == 0) {
			// showSignInActivity();
		} else {
			showPaystreamController();
		}
	}

	protected void showSignInActivity() {
		// SignInActivity signInActivity = new SignInActivity(this, mHandler,
		// prefs);
		// signInActivity.showSignInActivity();
		startActivityForResult(new Intent(this, SignInActivity.class), 1);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1 && resultCode == RESULT_OK) {
			showPaystreamController();
		}
	}

    private void showPaystreamController() {
    	setContentView(R.layout.paystream_controller);
        m_transactions = new ArrayList<PaystreamTransaction>();
        mListView = (ListView) findViewById(R.id.lvPaystream);
        m_adapter = new PaystreamAdapter(this, R.layout.transaction_item, m_transactions);
        mListView.setAdapter(m_adapter);
        mEmptyTextView = (TextView)findViewById(R.id.txtEmptyPaystream);
        
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				PaystreamTransaction ref = m_transactions.get(arg2);
				// first determine type of transaction
				// 1) incoming or outgoing
				// 2) payment or request
				// then create intent on appropriate dialog class
				Intent temp = new Intent(getApplicationContext(), OutgoingRequestDialog.class);
				startActivity(temp);
			}
		});
        viewOrders = new Runnable(){
            @Override
            public void run() {
                getOrders();
            }
        };
        Thread thread =  new Thread(null, viewOrders, "MagentoBackground");
        thread.start();
        m_ProgressDialog = ProgressDialog.show(PaystreamActivity.this,    
              "Please wait...", "Retrieving your paystream...", true);
    }
    private Runnable returnRes = new Runnable() {

        @Override
        public void run() {
            if(m_transactions != null && m_transactions.size() > 0){
            	mEmptyTextView.setVisibility(View.GONE);
                m_adapter.notifyDataSetChanged();
                
                for(int i=0;i<m_transactions.size();i++)
                	m_adapter.add(m_transactions.get(i));
            }
            m_ProgressDialog.dismiss();
            m_adapter.notifyDataSetChanged();
            
            if(m_transactions.isEmpty())
            	mEmptyTextView.setVisibility(View.VISIBLE);
            
        }
    };
    private void getOrders()
    {
      try{
    	  MessageService messageService = new MessageService();
    	  MessageRequest messageRequest = new MessageRequest();
    	  
  		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String userId = prefs.getString("userId", "");
		
    	  messageRequest.UserId = userId;
    	  ArrayList<MessageResponse> messages = messageService.GetMessages(messageRequest);
    	  
          m_transactions = new ArrayList<PaystreamTransaction>();
          
          DateFormat df = DateFormat.getDateInstance();
          String previousHeader = "";
          String currentHeader = "";
          for (Iterator<MessageResponse> i = messages.iterator(); i.hasNext();) {
        	  MessageResponse currentTransaction = (MessageResponse)i.next();
        	  
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
              if(!previousHeader.equals(currentHeader)) {
            	  o1.setHeader(currentHeader);
            	  previousHeader = currentHeader;
              } else {
            	  o1.setHeader("");
              }
              
              m_transactions.add(o1);
          }
         
          //Log.i("ARRAY", ""+ m_transactions.size());
        } catch (Exception e) { 
          Log.e("BACKGROUND_PROC", e.getMessage());
        }
        runOnUiThread(returnRes);
    }
   
	@Override
	public void OnSignOutComplete() {
		// showSignInActivity();
	}

	private Intent putExtras(Intent intent, PaystreamTransaction ref) {
		Intent temp = intent;
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
		return temp;
	}
}
