package me.pdthx.DoGood;

import me.pdthx.Requests.DoGoodRequest;
import me.pdthx.Responses.Response;
import android.widget.Button;
import android.widget.EditText;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import java.text.NumberFormat;
import me.pdthx.ACHAccountSetupActivity;
import me.pdthx.Services.PaymentServices;
import android.widget.TextView;
import me.pdthx.AddMoneyActivity;
import me.pdthx.BaseActivity;
import me.pdthx.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

public class DonateActivity extends BaseActivity {
    final private int ADD_MONEY = 8;
    final private int ADD_ORG = 4;

    private LinearLayout addMoneyBtn;
    private TextView addMoney;
    private LinearLayout addOrgBtn;
    private EditText txtComments;
    private Button donateButton;
    private TextView txtOrgName;
    private TextView txtOrgDetails;

    final private int ACCEPTPLEDGE_ACTION = 17;
    final private int ACCEPTPLEDGE_DIALOG = 0;
    final private int NORECIPIENTSPECIFIED_DIALOG = 1;
    final private int NOAMOUNTSPECIFIED_DIALOG = 2;
    final private int PAYMENTEXCEEDSLIMIT_DIALOG = 5;
    final private int ACCEPTPLEDGEFAILED_DIALOG = 3;
    final private int ACCEPTPLEDGESUCCESS_DIALOG = 4;
    final private int ADDACCOUNT_DIALOG = 10;
    final private int INVALIDPASSCODELENGTH_DIALOG = 12;

    private String orgId;
    private double amount;
    private String errorMessage;
    private String comments;
    private String passcode;

    private Response donateResponse;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dogood_controller);
        showDonateActivity();
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
                            Intent intent = new Intent(DonateActivity.this, ACHAccountSetupActivity.class);
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
                                submitDonation();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            dialogHandler.sendEmptyMessage(ACCEPTPLEDGE_ACTION);
                        }

                    }).start();
                    break;

                case ACCEPTPLEDGE_ACTION:
                    removeDialog(ACCEPTPLEDGE_DIALOG);

                    if (donateResponse != null
                        && donateResponse.Success)
                    {
                        dialogHandler.sendEmptyMessage(ACCEPTPLEDGESUCCESS_DIALOG);
                    } else if (donateResponse != null) {
                        errorMessage = donateResponse.ReasonPhrase;
                        dialogHandler.sendEmptyMessage(ACCEPTPLEDGEFAILED_DIALOG);
                    } else {
                        dialogHandler.sendEmptyMessage(ACCEPTPLEDGEFAILED_DIALOG);
                    }
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
                        txtOrgName.getText()));

                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            removeDialog(4);
                            dialog.dismiss();

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

    public void showDonateActivity() {

        addMoney = (TextView) findViewById(R.id.donate_amountResult);
        txtOrgName = (TextView) findViewById(R.id.donate_OrgResult);
        txtOrgDetails = (TextView) findViewById(R.id.donate_orgbottomtext);

        txtComments = (EditText) findViewById(R.id.donate_enterComment);

        addOrgBtn = (LinearLayout) findViewById(R.id.donate_enterOrg);
        addOrgBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(DonateActivity.this,
                    OrgListActivity.class), ADD_ORG);
            }

        });

        addMoneyBtn = (LinearLayout) findViewById(R.id.donate_chooseAmount);
        addMoneyBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(DonateActivity.this,
                    AddMoneyActivity.class), ADD_MONEY);
            }

        });

        donateButton = (Button) findViewById(R.id.donate_clickSubmit);
        donateButton.setOnClickListener(new OnClickListener() {
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
                            startSecurityPinActivity("Confirm", String.format("To confirm your payment of %s to %s, swipe you pin below.",
                                addMoney.getText(), txtOrgName.getText()));
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

                    case ADD_MONEY:
                        String amount = data.getStringExtra("index");
                        TextView addMoney = (TextView) findViewById(R.id.donate_amountResult);
                        addMoney.setText("$" + amount);
                        break;
                }
                break;
            }
        }
    }

    private void submitDonation()
    {
        DoGoodRequest request = new DoGoodRequest();
        request.Amount = amount;
        request.Comments = comments;
        request.OrganizationId = orgId;
        request.SecurityPin = passcode;
        request.SenderAccountId = prefs.getString("paymentAccountId", "");
        request.UserId = prefs.getString("userId", "");
        PaymentServices.donateMoney(request);
    }

}
