/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.pdthx;

import java.text.NumberFormat;

import me.pdthx.Models.Friend;
import me.pdthx.Requests.PaymentRequest;
import me.pdthx.Responses.Response;
import me.pdthx.Services.PaymentServices;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public final class SendPaymentActivity extends BaseActivity {

    public static final String TAG = "SendPaymentActivity";

    private String recipientUri = "";
    private double amount = 0;
    private String comments = "";
    private String errorMessage = "";

    private Location location;
    private LocationManager locationManager;
    private LocationListener locationListener;


    private Friend friend;

    private Button btnAddContacts;
    private Button txtAmount;
    private EditText txtComments;
    private Button btnSendMoney;
    private String passcode = "";

    final private int ADDING_FRIEND = 6;
    final private int SUBMITPAYMENT_DIALOG = 0;
    final private int NORECIPIENTSPECIFIED_DIALOG = 1;
    final private int NOAMOUNTSPECIFIED_DIALOG = 2;
    final private int PAYMENTEXCEEDSLIMIT_DIALOG = 5;
    final private int SUBMITPAYMENTFAILED_DIALOG = 3;
    final private int SUBMITPAYMENTSUCCESS_DIALOG = 4;
    final private int ADDACCOUNT_DIALOG = 10;
    final private int ADD_MONEY = 8;
    final private int INVALIDPASSCODELENGTH_DIALOG = 12;
    final private int SECURITYPIN = 13;

    final private int SUBMITPAYMENT_ACTION = 0;

    private Response paymentResponse;

    private Dialog dialog = null;

    private Handler dialogHandler = new Handler() {
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case SUBMITPAYMENT_ACTION:
                    removeDialog(SUBMITPAYMENT_DIALOG);

                    if (paymentResponse != null
                        && paymentResponse.Success)
                    {
                        showDialog(SUBMITPAYMENTSUCCESS_DIALOG);
                    } else if (paymentResponse != null) {
                        errorMessage = paymentResponse.ReasonPhrase;
                        showDialog(SUBMITPAYMENTFAILED_DIALOG);
                    } else {
                        showDialog(SUBMITPAYMENTFAILED_DIALOG);
                    }
                    break;

                case ADDACCOUNT_DIALOG:
                    alertDialog.setTitle("No ACH Account Setup");
                    alertDialog.setMessage("This user account has no bank account attached, " +
                        "in order to send a payment, you must add a bank account. " +
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
                            Intent intent = new Intent(SendPaymentActivity.this, ACHAccountSetupActivity.class);
                            intent.putExtra("tab", 1);
                            dialog.dismiss();
                            startActivity(intent);
                        }

                    });

                    alertDialog.show();
                    break;
            }

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Send Money");

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location arg0) {
                // TODO Auto-generated method stub
                if (isBetterLocation(arg0, location)) {
                    location = arg0;
                }
            }

            @Override
            public void onProviderDisabled(String arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProviderEnabled(String provider) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onStatusChanged(String provider, int status,
                Bundle extras) {
                // TODO Auto-generated method stub

            }

        };

        launchSendMoneyView();


    }

    @Override
    public void onResume() {
        super.onResume();

        tracker.trackPageView("Send Money");
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER, 0, 0, locationListener);

    }

    @Override
    public void onPause() {
        super.onPause();

        locationManager.removeUpdates(locationListener);

    }

    protected android.app.Dialog onCreateDialog(int id) {
        Thread thread = null;
        switch (id) {
            case SUBMITPAYMENT_DIALOG:
                tracker.trackPageView("Send Money: Confirm");
                progressDialog = new ProgressDialog(this);

                progressDialog.setMessage("Submitting Request...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

                thread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        try {
                            submitPaymentRequest();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        dialogHandler.sendEmptyMessage(SUBMITPAYMENT_ACTION);
                    }

                });
                dialog = progressDialog;
                thread.start();

                return dialog;
            case SUBMITPAYMENTFAILED_DIALOG:
                alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("Failed");

                alertDialog.setMessage(errorMessage);
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                });

                return alertDialog;
            case SUBMITPAYMENTSUCCESS_DIALOG:
                tracker.trackPageView("Send Money: Completed");
                alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("Payment Sumitted");
                NumberFormat nf = NumberFormat.getCurrencyInstance();

                alertDialog.setMessage(String.format(
                    "Your payment for %s was sent to %s.", nf.format(amount),
                    recipientUri));

                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        removeDialog(4);
                        dialog.dismiss();

                        friend = null;
                        btnAddContacts.setText("Add recipient");
                        txtAmount.setText("$0.00");
                        txtComments.setText("");
                    }
                });

                return alertDialog;
            case NORECIPIENTSPECIFIED_DIALOG:
                alertDialog = new AlertDialog.Builder(SendPaymentActivity.this)
                .create();
                alertDialog.setTitle("Please Specify a Recipient");
                alertDialog
                .setMessage("You must specify the recipient's mobile number.");

                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                return alertDialog;
            case NOAMOUNTSPECIFIED_DIALOG:
                alertDialog = new AlertDialog.Builder(SendPaymentActivity.this)
                .create();
                alertDialog.setTitle("Please Specify an Amount");
                alertDialog.setMessage("You must specify the amount to send.");

                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                return alertDialog;

            case INVALIDPASSCODELENGTH_DIALOG:
                alertDialog = new AlertDialog.Builder(SendPaymentActivity.this)
                .create();
                alertDialog.setTitle("Invalid Passcode");
                alertDialog
                .setMessage("Your passcode is atleast 4 buttons. Please try again.");

                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                return alertDialog;


            case PAYMENTEXCEEDSLIMIT_DIALOG:
                alertDialog = new AlertDialog.Builder(SendPaymentActivity.this)
                .create();
                alertDialog.setTitle("Exceeds Limit");
                alertDialog
                .setMessage("The payment exceeds your upper limit. Please try again.");

                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                return alertDialog;

        }

        return null;
    }

    protected void launchSendMoneyView() {
        setContentView(R.layout.sendmoney_controller);

        btnAddContacts = (Button) findViewById(R.id.addRecipient);

        txtAmount = (Button) findViewById(R.id.btnAmount);
        txtComments = (EditText) findViewById(R.id.txtComments);
        btnSendMoney = (Button) findViewById(R.id.btnSubmitPaymentRequest);
        Typeface type = Typeface.createFromAsset(getAssets(),"HelveticaWorld-Bold.ttf");
        btnSendMoney.setTypeface(type);
        btnSendMoney.setTextColor(Color.WHITE);

        txtAmount.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(SendPaymentActivity.this, AddMoneyActivity.class), ADD_MONEY);
            }
        });

        btnAddContacts.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(SendPaymentActivity.this, FriendsListActivity.class), ADDING_FRIEND);
            }

        });


        btnSendMoney.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                boolean isValid = true;

                try {
                    amount = Double.parseDouble(txtAmount.getText().toString()
                        .replaceAll("[$,]*", ""));
                }
                catch (NumberFormatException e)
                {
                    amount = 0;
                }
                comments = txtComments.getText().toString();

                if (isValid && recipientUri.length() == 0) {
                    showDialog(NORECIPIENTSPECIFIED_DIALOG);
                    isValid = false;
                }
                if (isValid && amount == 0) {
                    showDialog(NOAMOUNTSPECIFIED_DIALOG);
                    isValid = false;
                }
                if (isValid && amount > prefs.getInt("upperLimit", 0)) {
                    showDialog(PAYMENTEXCEEDSLIMIT_DIALOG);
                    isValid = false;
                }
                if (isValid) {

                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                    if (isBetterLocation(lastKnownLocation, location)) {
                        location = lastKnownLocation;
                    }


                    if(prefs.getString("userId", "").length() == 0)	{
                        logout();
                    } else {
                        if (prefs.getBoolean("hasACHAccount", false) || !prefs.getString("paymentAccountId", "").equals(""))
                        {
                            Intent intent = new Intent(SendPaymentActivity.this, SecurityPinActivity.class);
                            intent.putExtra("headerText", "Confirm");
                            intent.putExtra("bodyText",
                                String.format("To confirm your payment of %s to %s, swipe you pin below.",
                                txtAmount.getText(), friend.getName()));

                            startActivityForResult(intent, SECURITYPIN);
                        }
                        else
                        {
                            dialogHandler.sendEmptyMessage(ADDACCOUNT_DIALOG);
                        }
                    }

                }
            }
        });

        btnSendMoney.setVisibility(View.VISIBLE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bundle bundle = data.getExtras();

        switch (resultCode)
        {
            case RESULT_OK:
                switch (requestCode)
                {
                    case ADDING_FRIEND:
                        addingContact(bundle.getString("id"), bundle.getString("paypoint"));
                        break;
                    case ADD_MONEY:
                        String amount = bundle.getString("index");
                        txtAmount.setText("$"+ amount);
                        break;
                    case SECURITYPIN:
                        passcode = bundle.getString("passcode");
                        showDialog(SUBMITPAYMENT_DIALOG);
                        break;
                }
                break;
            case RESULT_CANCELED:
                finish();
                break;
        }
    }

//    protected void showSecurityPinDialog() {
//        setContentView(R.layout.security_dialog);
//        TextView txtConfirmHeader = (TextView) findViewById(R.id.setupSecurityHeader);
//        TextView txtConfirmBody = (TextView) findViewById(R.id.setupSecurityBody);
//
//        txtConfirmHeader.setText("Confirm Your Payment");
//        txtConfirmBody.setText(String.format("To confirm your payment of %s to %s, swipe you pin below.",
//            txtAmount.getText(), friend.getName()));
//
//        Button btnCancel = (Button) findViewById(R.id.btnCancelSendMoney);
//        btnCancel.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                setContentView(R.layout.sendmoney_controller);
//            }
//        });
//
//        final CustomLockView ctrlSecurityPin = (CustomLockView) findViewById(R.id.ctrlSecurityPin);
//        ctrlSecurityPin.invalidate();
//        ctrlSecurityPin.setOnTouchListener(new OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                passcode = ctrlSecurityPin.getPasscode();
//
//                if (passcode.length() > 3) {
//                    amount = Double.parseDouble(txtAmount.getText().toString()
//                        .replace("$", ""));
//                    comments = txtComments.getText().toString();
//                    passcode = ctrlSecurityPin.getPasscode();
//
//                    showDialog(SUBMITPAYMENT_DIALOG);
//                } else
//                    showDialog(INVALIDPASSCODELENGTH_DIALOG);
//
//                return false;
//            }
//        });
//    }

    private void addingContact(String id, String paypoint) {
        Friend chosenContact = new Friend();
        if (!id.equals(""))
        {
            chosenContact.setId(id);
            friend = friendsList.get(friendsList.indexOf(chosenContact));

            if (friend.isFBContact()) {
                recipientUri = "fb_" + friend.getId();
                btnAddContacts.setText(friend.getName() + ": " + friend.getId());
            }
            else {
                recipientUri = "" + friend.getPaypoint();
                btnAddContacts.setText(friend.toString());
            }
        }
        else
        {
            chosenContact.setName("New Contact");
            chosenContact.setPaypoint(paypoint);
            friend = chosenContact;
            recipientUri = "" + paypoint;
            btnAddContacts.setText("New contact: " + paypoint);
        }
    }

    protected void submitPaymentRequest() {

        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.UserId = prefs.getString("userId", "");
        paymentRequest.SecurityPin = passcode;
        paymentRequest.SenderUri = prefs.getString("login", "");
        paymentRequest.RecipientUri = recipientUri;
        paymentRequest.Amount = amount;
        paymentRequest.Comments = comments;
        paymentRequest.SenderAccountId = prefs.getString("paymentAccountId", "0");

        if (location != null) {
            paymentRequest.Latitude = location.getLatitude();
            paymentRequest.Longitude = location.getLongitude();
        }

        paymentResponse = PaymentServices.sendMoney(paymentRequest);

    }

    /** Determines whether one Location reading is better than the current Location fix
     * @param location The new Location that you want to evaluate
     * @param currentBestLocation The current Location fix, to which you want to compare the new one
     */
    private boolean isBetterLocation(Location location, Location currentBestLocation) {
        final int TWO_MINUTES = 1000 * 60 * 2;

        if (location != null) {

            if (currentBestLocation == null) {
                // A new location is always better than no location
                return true;
            }

            // Check whether the new location fix is newer or older
            long timeDelta = location.getTime() - currentBestLocation.getTime();
            boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
            boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
            boolean isNewer = timeDelta > 0;

            // If it's been more than two minutes since the current location, use the new location
            // because the user has likely moved
            if (isSignificantlyNewer) {
                return true;
                // If the new location is more than two minutes older, it must be worse
            } else if (isSignificantlyOlder) {
                return false;
            }

            // Check whether the new location fix is more or less accurate
            int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
            boolean isLessAccurate = accuracyDelta > 0;
            boolean isMoreAccurate = accuracyDelta < 0;
            boolean isSignificantlyLessAccurate = accuracyDelta > 200;

            // Check if the old and new location are from the same provider
            boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

            // Determine location quality using a combination of timeliness and accuracy
            if (isMoreAccurate) {
                return true;
            } else if (isNewer && !isLessAccurate) {
                return true;
            } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
                return true;
            }
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

}