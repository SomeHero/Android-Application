package me.pdthx.DoGood;

import me.pdthx.Requests.DoGoodRequest;
import android.app.ProgressDialog;
import java.text.NumberFormat;
import me.pdthx.SelectRecipientActivity;
import me.pdthx.Requests.MultipleURIRequest;
import me.pdthx.Responses.MultipleURIResponse;
import me.pdthx.Responses.ResponseArrayList;
import me.pdthx.Services.PaymentServices;
import me.pdthx.Responses.Response;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import me.pdthx.ACHAccountSetupActivity;
import android.widget.EditText;
import android.widget.Button;
import me.pdthx.Models.Friend;
import me.pdthx.AddMoneyActivity;
import me.pdthx.BaseActivity;
import me.pdthx.FriendsListActivity;
import me.pdthx.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PledgeActivity extends BaseActivity {

	private static final int ADD_MONEY = 8;
	private static final int ADDING_FRIEND = 7;
	private static final int ADD_ORG = 0;

	final private int ACCEPTPLEDGE_ACTION = 17;
    final private int ACCEPTPLEDGE_DIALOG = 0;
    final private int NORECIPIENTSPECIFIED_DIALOG = 1;
    final private int NOAMOUNTSPECIFIED_DIALOG = 2;
    final private int PAYMENTEXCEEDSLIMIT_DIALOG = 5;
    final private int ACCEPTPLEDGEFAILED_DIALOG = 3;
    final private int ACCEPTPLEDGESUCCESS_DIALOG = 4;
    final private int ADDACCOUNT_DIALOG = 10;
    final private int INVALIDPASSCODELENGTH_DIALOG = 12;
    final private int ACCEPTPLEDGE_MULTIPLEURIS = 15;
    final private int SELECT_RECIPIENT = 16;

	private LinearLayout addAmount;
	private TextView addMoney;
	private LinearLayout addContactBtn;
	private LinearLayout addOrgBtn;
	private EditText txtComments;
	private Button pledgeButton;
	private TextView txtOrgName;
	private TextView txtOrgDetails;

	private String orgId;
	private double amount;
	private Friend pledger;
	private String pledgerUri;
	private String comments;
	private String passcode;
	private Response pledgeResponse;
	private String errorMessage;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pledge_controller);
		showPledge();
	}

	private Handler dialogHandler = new Handler() {
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case ADDACCOUNT_DIALOG:
                    alertDialog.setTitle("No ACH Account Setup");
                    alertDialog.setMessage("This user account has no bank account attached, " +
                        "in order to accept a pledge, you must add a bank account. " +
                        "After adding a bank account, you will return to this screen " +
                        "with all the information filled in.");
                    alertDialog.setButton("Cancel", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            // TODO Auto-generated method stub
                            dialog.dismiss();
                        }

                    });
                    alertDialog.setButton2("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            // TODO Auto-generated method stub
                            Intent intent = new Intent(PledgeActivity.this, ACHAccountSetupActivity.class);
                            intent.putExtra("tab", 3);
                            dialog.dismiss();
                            startActivity(intent);
                        }

                    });

                    alertDialog.show();
                    break;


                case ACCEPTPLEDGE_DIALOG:
                    tracker.trackPageView("Acceping Pledge: Confirm");

                    progressDialog.setMessage("Submitting...");
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            try {
                                submitPledge();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            dialogHandler.sendEmptyMessage(ACCEPTPLEDGE_ACTION);
                        }

                    }).start();
                    break;

                case ACCEPTPLEDGE_ACTION:
                    removeDialog(ACCEPTPLEDGE_DIALOG);

                    if (pledgeResponse != null
                        && pledgeResponse.Success)
                    {
                        dialogHandler.sendEmptyMessage(ACCEPTPLEDGESUCCESS_DIALOG);
                    } else if (pledgeResponse != null) {
                        errorMessage = pledgeResponse.ReasonPhrase;
                        dialogHandler.sendEmptyMessage(ACCEPTPLEDGEFAILED_DIALOG);
                    } else {
                        dialogHandler.sendEmptyMessage(ACCEPTPLEDGEFAILED_DIALOG);
                    }
                    break;

                case ACCEPTPLEDGE_MULTIPLEURIS:
                    progressDialog.setMessage("Finding recipient...");
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            ResponseArrayList<MultipleURIResponse> response = new ResponseArrayList<MultipleURIResponse>();
                            MultipleURIRequest request = new MultipleURIRequest();
                            request.recipientUris.addAll(pledger.getPaypoints());

                            response = PaymentServices.determineRecipient(request);

                            removeDialog(ACCEPTPLEDGE_MULTIPLEURIS);

                            if (response.Success)
                            {
                                if (response.size() != 1)
                                {
                                    Intent intent = new Intent(PledgeActivity.this, SelectRecipientActivity.class);

                                    if (response.size() != 0)
                                    {

                                        String[] recipientUris = new String[response.size()];
                                        String[] recipientStrings = new String[response.size()];

                                        for (int i = 0; i < response.size(); i++)
                                        {
                                            recipientUris[i] = response.get(i).UserUri;
                                        }

                                        for (int i = 0; i < response.size(); i++)
                                        {
                                            recipientStrings[i] = response.get(i).toString();
                                        }
                                        intent.putExtra("uris", recipientUris);
                                        intent.putExtra("recipients", recipientStrings);
                                    }
                                    else {
                                        String[] recipientUris = new String[request.recipientUris.size()];
                                        request.recipientUris.toArray(recipientUris);

                                        intent.putExtra("uris", recipientUris);
                                        intent.putExtra("recipients", recipientUris);
                                    }

                                    startActivityForResult(intent, SELECT_RECIPIENT);
                                }
                                else
                                {
                                    pledgerUri = response.get(0).UserUri;
                                    startSecurityPinActivity("Confirm", String.format("To confirm your payment of %s to %s, swipe you pin below.",
                                        addMoney.getText(), pledger.getName()));
                                }
                            }
                            else
                            {
                                errorMessage = response.ReasonPhrase;
                                dialogHandler.sendEmptyMessage(ACCEPTPLEDGEFAILED_DIALOG);
                            }
                        }

                    }).start();
                    break;

                case ACCEPTPLEDGEFAILED_DIALOG:
                    alertDialog.setTitle("Failed");

                    alertDialog.setMessage(errorMessage);
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    });
                    break;

                case ACCEPTPLEDGESUCCESS_DIALOG:
                    tracker.trackPageView("Accept Pledge: Completed");
                    alertDialog.setTitle("Pledge Sumitted");
                    NumberFormat nf = NumberFormat.getCurrencyInstance();

                    alertDialog.setMessage(String.format(
                        "Your payment for %s was sent to %s.", nf.format(amount),
                        pledgerUri));

                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            removeDialog(4);
                            dialog.dismiss();

                            pledger = null;
                            txtOrgName.setText("Add recipient");
                            txtOrgDetails.setText("");
                            addMoney.setText("$0.00");
                            txtComments.setText("");
                        }
                    });
                    break;

                case NORECIPIENTSPECIFIED_DIALOG:
                    alertDialog.setTitle("Please Specify a Recipient");
                    alertDialog
                    .setMessage("You have not selected a valid reicpient. Please select a recipient with a valid email address or phone number.");

                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    break;

                case NOAMOUNTSPECIFIED_DIALOG:
                    alertDialog.setTitle("Please Specify an Amount");
                    alertDialog.setMessage("You must specify the amount to send.");

                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    break;

                case INVALIDPASSCODELENGTH_DIALOG:
                    alertDialog.setTitle("Invalid Passcode");
                    alertDialog
                    .setMessage("Your passcode is atleast 4 buttons. Please try again.");

                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    break;

                case PAYMENTEXCEEDSLIMIT_DIALOG:
                    alertDialog.setTitle("Exceeds Limit");
                    alertDialog
                    .setMessage("The payment exceeds your upper limit. Please try again.");

                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    break;
            }

        }
    };

	public void showPledge() {

	    addMoney = (TextView) findViewById(R.id.donate_amountResult);
	    txtOrgName = (TextView) findViewById(R.id.donate_OrgResult);
	    txtOrgDetails = (TextView) findViewById(R.id.donate_orgbottomtext);

	    txtComments = (EditText) findViewById(R.id.donate_enterComment);

		addOrgBtn = (LinearLayout) findViewById(R.id.donate_chooseOrg);
		addOrgBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(PledgeActivity.this,
						OrgListActivity.class), ADD_ORG);
			}
		});

		addAmount = (LinearLayout) findViewById(R.id.donate_chooseAmount);
		addAmount.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(PledgeActivity.this,
						AddMoneyActivity.class), ADD_MONEY);
			}

		});

		addContactBtn = (LinearLayout) findViewById(R.id.donate_chooseContact);
		addContactBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(PledgeActivity.this,
						FriendsListActivity.class), ADDING_FRIEND);
			}

		});

		pledgeButton = (Button) findViewById(R.id.donate_clickSubmit);
		pledgeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v)
            {
                boolean isValid = true;

                try {
                    amount = Double.parseDouble(addMoney.getText().toString()
                        .replaceAll("[$,]*", ""));
                }
                catch (NumberFormatException e)
                {
                    amount = 0;
                }
                comments = txtComments.getText().toString();

                int numPaypoints = pledger != null ? pledger.getPaypoints().size() : 0;

                if (isValid && numPaypoints == 0) {
                    dialogHandler.sendEmptyMessage(NORECIPIENTSPECIFIED_DIALOG);
                    isValid = false;
                }
                if (isValid && amount == 0) {
                    dialogHandler.sendEmptyMessage(NOAMOUNTSPECIFIED_DIALOG);
                    isValid = false;
                }
                if (isValid && amount > prefs.getInt("upperLimit", 0)) {
                    dialogHandler.sendEmptyMessage(PAYMENTEXCEEDSLIMIT_DIALOG);
                    isValid = false;
                }
                if (isValid) {

                    if(prefs.getString("userId", "").length() == 0) {
                        logout();
                    } else {
                        if (prefs.getBoolean("hasACHAccount", false) || !prefs.getString("paymentAccountId", "").equals(""))
                        {
                            if (numPaypoints == 1)
                            {
                                pledgerUri = pledger.getPaypoints().get(0);
                                startSecurityPinActivity("Confirm", String.format("To confirm your payment of %s to %s, swipe you pin below.",
                                    addMoney.getText(), pledger.getName()));
                            }
                            else if (numPaypoints > 1)
                            {
                                dialogHandler.sendEmptyMessage(ACCEPTPLEDGE_MULTIPLEURIS);
                            }
                        }
                        else
                        {
                            dialogHandler.sendEmptyMessage(ADDACCOUNT_DIALOG);
                        }
                    }

                }
            }
		});

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    switch(resultCode)
	    {
	        case RESULT_OK :
	        {
	            switch (requestCode)
	            {
	                case ADD_ORG :
	                    orgId = data.getStringExtra("id");
	                    break;
	                case ADDING_FRIEND:
	                    addingPledger(data.getStringExtra("id"), data.getStringExtra("paypoint"));
	                    break;
	                case SECURITYPIN:
                        passcode = data.getStringExtra("passcode");
                        dialogHandler.sendEmptyMessage(ACCEPTPLEDGE_DIALOG);
                        break;
	                case ADD_MONEY:
                        String amount = data.getStringExtra("index");
                        addMoney.setText("$" + amount);
	                    break;
	            }
	            break;
	        }
	    }
	}

	private void addingPledger(String id, String paypoint) {
        Friend chosenContact = new Friend();
        if (!id.equals("")) {
            chosenContact.setId(id);
            pledger = combinedContactList.get(combinedContactList.indexOf(chosenContact));

            if (pledger.isFBContact()) {
                pledgerUri = "fb_" + pledger.getId();
                TextView contactResult = (TextView) findViewById(R.id.donate_OrgResult);
                contactResult.setText(pledger.getName() + ": " + pledger.getId());
            } else {
                pledgerUri = "" + pledger.getPaypoints().get(0);
                TextView contactResult = (TextView) findViewById(R.id.donate_OrgResult);
                contactResult.setText(pledger.toString());
            }
        } else {
            chosenContact.setName("New Contact");
            chosenContact.getPaypoints().add(paypoint);
            pledger = chosenContact;
            pledgerUri = "" + paypoint;
            TextView contactResult = (TextView) findViewById(R.id.donate_OrgResult);
            contactResult.setText("New contact: " + paypoint);
        }
    }

	private void submitPledge()
	{
	    DoGoodRequest request = new DoGoodRequest();
	    request.Amount = amount;
	    request.Comments = comments;
	    request.OrganizationId = orgId;
	    request.RecipientUri = pledgerUri;
	    request.SecurityPin = passcode;
	    request.SenderAccountId = prefs.getString("paymentAccountId", "");
	    request.UserId = prefs.getString("userId", "");

	    pledgeResponse = PaymentServices.acceptPledge(request);
	}
}
