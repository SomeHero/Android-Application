package me.pdthx;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import me.pdthx.Adapters.PaystreamAdapter;
import me.pdthx.CustomViews.PullAndRefreshListView;
import me.pdthx.CustomViews.PullAndRefreshListView.OnRefreshListener;
import me.pdthx.Dialogs.IncomingPaymentDialog;
import me.pdthx.Dialogs.IncomingRequestDialog;
import me.pdthx.Dialogs.OutgoingPaymentDialog;
import me.pdthx.Dialogs.OutgoingRequestDialog;
import me.pdthx.Models.PaystreamTransaction;
import me.pdthx.Requests.UserRequest;
import me.pdthx.Responses.PaystreamResponse;
import me.pdthx.Responses.UserResponse;
import me.pdthx.Services.PaystreamService;
import me.pdthx.Services.UserService;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RadioGroup;
import android.widget.TextView;

public final class PaystreamActivity extends BaseActivity implements
OnCheckedChangeListener{

    private ProgressDialog m_ProgressDialog = null;
    private ArrayList<PaystreamTransaction> m_transactions = null;
    private ArrayList<PaystreamTransaction> send_transactions = null;
    private ArrayList<PaystreamTransaction> recieve_transactions = null;
    private ArrayList<PaystreamTransaction> other_transactions = null;

    private PaystreamAdapter m_adapter;
    private Runnable viewOrders;
    private RadioGroup paystreamCategory;
    private EditText searchBar = null;
    private int numTransactions;
    private ArrayList<PaystreamTransaction> transactionsList;
    private int refreshCount = 0;
    public static final String TAG = "PaystreamActivity";
    private PullAndRefreshListView mListView = null;
    private TextView mEmptyTextView = null;
    private RadioGroup group;

    private String username;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tracker.trackPageView("Paystream");

        if (prefs.getString("userId", "").length() == 0) {
            logout();
        } else {
            showPaystreamController();
        }

    }

    private void showPaystreamController() {

        setContentView(R.layout.paystream_all);
        paystreamCategory = (RadioGroup) findViewById(R.id.paystreamSubCategories);
        paystreamCategory.setOnCheckedChangeListener(this);

        m_transactions = new ArrayList<PaystreamTransaction>();
        mListView = (PullAndRefreshListView) findViewById(R.id.lvPaystream);
        m_adapter = new PaystreamAdapter(this, R.layout.transaction_item,
            m_transactions);
        mListView.setAdapter(m_adapter);
        mEmptyTextView = (TextView) findViewById(R.id.txtEmptyPaystream);
        searchBar = (EditText) findViewById(R.id.searchBar);
        transactionsList = new ArrayList<PaystreamTransaction>();
        group = (RadioGroup) findViewById(R.id.paystreamSubCategories);

        if (refreshCount > 1) {
            searchBar.setText("");
        }
        searchBar.addTextChangedListener(new TextWatcher() {

            String current = "";
            @Override
            public void afterTextChanged(Editable s) {
                ArrayList<PaystreamTransaction> searched = new ArrayList<PaystreamTransaction>();
                current = s.toString().toLowerCase();
                int checked = group.getCheckedRadioButtonId();

                if (checked == R.id.paystreamAll) {
                    searched.clear();
                    for (int x = 0; x < m_transactions.size(); x++)
                    {

                        if(m_transactions.get(x).search(current.toLowerCase())){
                            searched.add(m_transactions.get(x));
                        }
                    }
                    if (searchBar.getText().toString().length() == 0) {
                        m_adapter = new PaystreamAdapter(PaystreamActivity.this,R.layout.transaction_item, m_transactions);
                    }
                    else {
                        m_adapter = new PaystreamAdapter(PaystreamActivity.this, R.layout.transaction_item, searched);
                    }
                }
                else if (checked == R.id.paystreamSent) {
                    searched.clear();
                    for (int x = 0; x < send_transactions.size(); x++)
                    {

                        if(m_transactions.get(x).search(current))
                        {
                            searched.add(send_transactions.get(x));
                        }
                    }


                    if (searchBar.getText().toString().length() == 0) {
                        m_adapter = new PaystreamAdapter(PaystreamActivity.this,R.layout.transaction_item, send_transactions);
                    }
                    else {
                        m_adapter = new PaystreamAdapter(PaystreamActivity.this, R.layout.transaction_item, searched);
                    }
                }
                else if (checked == R.id.paystreamReceived) {
                    searched.clear();
                    for (int x = 0; x < recieve_transactions.size(); x++)
                    {

                        if(m_transactions.get(x).search(current))
                        {
                            searched.add(recieve_transactions.get(x));
                        }
                    }


                    if (searchBar.getText().toString().length() == 0) {
                        m_adapter = new PaystreamAdapter(PaystreamActivity.this,R.layout.transaction_item, recieve_transactions);
                    }
                    else {
                        m_adapter = new PaystreamAdapter(PaystreamActivity.this, R.layout.transaction_item, searched);
                    }
                } else if (checked == R.id.paystreamOther) {
                    searched.clear();
                    for (int x = 0; x < other_transactions.size(); x++)
                    {

                        if(other_transactions.get(x).search(current))
                        {
                            searched.add(other_transactions.get(x));
                        }
                    }


                    if (searchBar.getText().toString().length() == 0) {
                        m_adapter = new PaystreamAdapter(PaystreamActivity.this,R.layout.transaction_item, other_transactions);
                    }
                    else {
                        m_adapter = new PaystreamAdapter(PaystreamActivity.this, R.layout.transaction_item, searched);
                    }
                }


                mListView.setAdapter(m_adapter);

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                int count) {

            }

        });

        mListView.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                getAllOrders();
                mListView.onRefreshComplete();
            }
        });

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
                runOnUiThread(returnRes);
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

//				Toast.makeText(getBaseContext(),
//						"CHILD COUNT: " + mListView.getChildCount(), 3);
//				if (mListView.getChildAt(mListView.getChildCount() - 1)
//						.getVisibility() == View.GONE) {
//					mListView.findViewById(R.id.pull_to_refresh_header)
//							.setVisibility(View.GONE);
//				} else {
//					mListView.findViewById(R.id.pull_to_refresh_header)
//							.setVisibility(View.VISIBLE);
//				}

            } else {
                m_adapter.clear();
                mEmptyTextView.setVisibility(View.VISIBLE);
            }
            m_ProgressDialog.dismiss();
            m_adapter.notifyDataSetChanged();

            if (getIntent().getStringExtra("userId") != null
                && prefs.getString("userId", "").equals(
                    getIntent().getStringExtra("userId"))) {
                PaystreamTransaction transaction = new PaystreamTransaction();
                transaction.setTransactionId(getIntent().getStringExtra("transactionId"));

                // Use a fake transaction with the proper id to
                // get the real transaction.
                getTransactionDetails(m_transactions.indexOf(transaction) + 1);
            }
        }
    };

    private void getAllOrders() {
        try {
            UserRequest messageRequest = new UserRequest();

            String userId = prefs.getString("userId", "");

            messageRequest.UserId = userId;
            UserResponse userInfo = UserService.getUser(messageRequest);

            username = userInfo.FirstName + " " + userInfo.LastName;
            if (username.length() <= 0) {
                username = userInfo.EmailAddress;
                if (username.length() <= 0) {
                    username = userInfo.MobileNumber;
                    if (username.length() <= 0) {
                        username = userInfo.UserName;
                    }
                }
            }

            ArrayList<PaystreamResponse> messages = PaystreamService
                .getMessages(messageRequest);

            m_transactions = new ArrayList<PaystreamTransaction>();
            send_transactions = new ArrayList<PaystreamTransaction>();
            recieve_transactions = new ArrayList<PaystreamTransaction>();
            other_transactions = new ArrayList<PaystreamTransaction>();


            String previousHeader = "";
            String currentHeader = "";
            for (Iterator<PaystreamResponse> i = messages.iterator(); i
                .hasNext();) {
                PaystreamResponse currentTransaction = i
                    .next();

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
                o1.setImageUri(currentTransaction.ImageUri);

                currentHeader = createHeader(currentTransaction);

                if (!previousHeader.equals(currentHeader)) {
                    o1.setHeader(currentHeader);
                    previousHeader = currentHeader;
                } else {
                    o1.setHeader("");
                }

                m_transactions.add(o1);
            }
            previousHeader = "";
            currentHeader = "";
            for (Iterator<PaystreamResponse> i = messages.iterator(); i
                .hasNext();) {
                PaystreamResponse currentTransaction = i
                    .next();
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

                    send_transactions.add(o1);
                }
            }
            previousHeader = "";
            currentHeader = "";
            for (Iterator<PaystreamResponse> i = messages.iterator(); i
                .hasNext();) {
                PaystreamResponse currentTransaction = i
                    .next();

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

                    recieve_transactions.add(o1);
                }
            }
            previousHeader = "";
            currentHeader = "";
            for (Iterator<PaystreamResponse> i = messages.iterator(); i
                .hasNext();) {
                PaystreamResponse currentTransaction = i
                    .next();

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

                    other_transactions.add(o1);
                }
            }

            refreshCount++; // This won't work once Paystream is more efficient
            if (refreshCount > 1) {
                transactionsList.clear();
            }
            numTransactions = m_transactions.size();
            for (int t = 0; t < numTransactions; t++) {
                transactionsList.add(m_transactions.get(t));
            }

            // Log.i("ARRAY", ""+ m_transactions.size());
        } catch (Exception e) {
            Log.e("BACKGROUND_PROC", e.getMessage());
        }

    }

    @Override
    public void onCheckedChanged(RadioGroup arg0, int arg1) {

        if (arg1 == R.id.paystreamAll) {
            m_transactions.clear();
            for (int t = 0; t < transactionsList.size(); t++) {
                m_transactions.add(transactionsList.get(t));
            }
            runOnUiThread(returnRes);
        } else if (arg1 == R.id.paystreamSent) {
            m_transactions.clear();
            for (int t = 0; t < send_transactions.size(); t++) {
                m_transactions.add(send_transactions.get(t));
            }
            runOnUiThread(returnRes);
        } else if (arg1 == R.id.paystreamReceived) {
            m_transactions.clear();
            for (int t = 0; t < recieve_transactions.size(); t++) {
                m_transactions.add(recieve_transactions.get(t));
            }
            runOnUiThread(returnRes);
        } else if (arg1 == R.id.paystreamOther) {
            m_transactions.clear();
            for (int t = 0; t < other_transactions.size(); t++) {
                m_transactions.add(other_transactions.get(t));
            }
            runOnUiThread(returnRes);
        }
    }

    public String createHeader(PaystreamResponse currentTransaction) {
        String currentHeader = "";
        DateFormat.getDateInstance();
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
            currentDate.add(Calendar.DAY_OF_YEAR, 7);
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
                    } else {
                        currentDate.roll(Calendar.MONTH, -1);
                        currentDate.set(Calendar.DAY_OF_MONTH, 1);
                        currentDate.roll(Calendar.DATE, -1);
                        Date firstDayLastMonth = currentDate.getTime();
                        maxDays = currentDate
                            .getActualMaximum(Calendar.DAY_OF_MONTH);
                        currentDate.set(Calendar.DAY_OF_MONTH, maxDays);
                        currentDate.add(Calendar.DATE, 1);
                        Date lastDayLastMonth = currentDate.getTime();
                        if (comp.after(firstDayLastMonth)
                            && comp.before(lastDayLastMonth)) {
                            currentHeader = "Last month";
                        } else {
                            currentHeader = comp.getMonth() + " "
                                + comp.getYear();
                        }
                    }
                }
            }
        }
        return currentHeader;
    }

    private void getTransactionDetails(int index) {
        PaystreamTransaction ref = m_transactions.get(index - 1);

        // first create intent based on what the transaction type is
        // 1) determine outgoing or incoming
        // 2) determine payment or request
        if (ref.getDirection().equalsIgnoreCase("Out")) {
            if (ref.getTransactionType().equalsIgnoreCase("Payment")) {
                // imgTransactionType.setImageResource(R.drawable.paystream_sent_icon);
                Intent temp1 = new Intent(getApplicationContext(),
                    OutgoingPaymentDialog.class);
                temp1.putExtra("username", username);
                temp1.putExtra("obj", ref);
                // picture?
                startActivity(temp1);
            } else {
                Intent temp2 = new Intent(getApplicationContext(),
                    OutgoingRequestDialog.class);
                temp2.putExtra("obj", ref);
                temp2.putExtra("username", username);
                startActivity(temp2);
            }
        } else {
            if (ref.getTransactionType().equalsIgnoreCase("Payment")) {
                Intent temp3 = new Intent(getApplicationContext(),
                    IncomingPaymentDialog.class);
                temp3.putExtra("obj", ref);
                temp3.putExtra("username", username);
                startActivity(temp3);
            } else {
                Intent temp4 = new Intent(getApplicationContext(),
                    IncomingRequestDialog.class);
                temp4.putExtra("obj", ref);
                temp4.putExtra("username", username);
                startActivity(temp4);
            }
        }
    }
}
