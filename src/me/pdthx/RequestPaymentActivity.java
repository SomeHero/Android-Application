package me.pdthx;

import me.pdthx.Requests.MultipleURIRequest;
import me.pdthx.Responses.MultipleURIResponse;
import me.pdthx.Responses.ResponseArrayList;
import java.io.FileInputStream;
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageButton;
import android.widget.ImageView;

public class RequestPaymentActivity extends BaseActivity {

    public static final String TAG = "RequestMoneyActivity";

    private String recipientUri;
    private double amount = 0;
    private String comments = "";
    private String errorMessage = "";

    private Location location;
    private LocationManager locationManager;
    private LocationListener locationListener;

    private Friend friend;

    private ImageButton btnCamera;
    private Button btnAddContacts;
    private Button txtAmount;
    private EditText txtComments;
    private Button btnRequestMoney;
    private String passcode = "";

    final private int ADDING_FRIEND = 6;
    final private int SUBMITREQUEST_DIALOG = 0;
    final private int NORECIPIENTSPECIFIED_DIALOG = 1;
    final private int NOAMOUNTSPECIFIED_DIALOG = 2;
    final private int SUBMITREQUESTFAILED_DIALOG = 3;
    final private int SUBMITREQUESTSUCCESS_DIALOG = 4;
    final private int INVALIDPASSCODELENGTH_DIALOG = 5;
    final private int PAYMENTEXCEEDSLIMIT_DIALOG = 6;
    final private int SUBMITPAYMENT_MULTIPLEURIS = 15;
    final private int SELECT_RECIPIENT = 16;

    final private int ADDACCOUNT_DIALOG = 7;
    final private int ADD_MONEY = 8;
    final private int SUBMITREQUEST_ACTION = 1;
    final private int CAMERA = 20;

    private Response paymentResponse;

    private Dialog dialog = null;
    private Handler dialogHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SUBMITREQUEST_ACTION:
                    removeDialog(SUBMITREQUEST_DIALOG);

                    if (paymentResponse != null
                        && paymentResponse.Success) {
                        showDialog(SUBMITREQUESTSUCCESS_DIALOG);

                    } else if (paymentResponse != null) {
                        errorMessage = paymentResponse.ReasonPhrase;
                        showDialog(SUBMITREQUESTFAILED_DIALOG);
                    } else {
                        showDialog(SUBMITREQUESTFAILED_DIALOG);
                    }
                    break;

                case ADDACCOUNT_DIALOG:
                    alertDialog.setTitle("No ACH Account Setup");
                    alertDialog.setMessage("This user account has no bank account attached, " +
                        "in order to request a payment, you must add a bank account. " +
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
                            Intent intent = new Intent(RequestPaymentActivity.this, ACHAccountSetupActivity.class);
                            intent.putExtra("tab", 1);
                            dialog.dismiss();
                            startActivity(intent);
                        }

                    });
                    alertDialog.dismiss();
                    break;
            }
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Request Money");

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

        launchRequestMoneyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        tracker.trackPageView("Request Money");
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
            case SUBMITREQUEST_DIALOG:
                progressDialog = new ProgressDialog(this);
                tracker.trackPageView("Request Money: Confirm");
                progressDialog.setMessage("Submitting Request...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

                thread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            submitRequest();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        dialogHandler.sendEmptyMessage(SUBMITREQUEST_ACTION);
                    }

                });
                dialog = progressDialog;
                thread.start();

                return dialog;
            case SUBMITPAYMENT_MULTIPLEURIS:
                progressDialog.setMessage("Finding recipient...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

                thread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        ResponseArrayList<MultipleURIResponse> response = new ResponseArrayList<MultipleURIResponse>();
                        MultipleURIRequest request = new MultipleURIRequest();
                        request.recipientUris.addAll(friend.getPaypoints());

                        response = PaymentServices.determineRecipient(request);


                        removeDialog(SUBMITPAYMENT_MULTIPLEURIS);

                        if (response.Success)
                        {
                            if (response.size() != 1)
                            {
                                Intent intent = new Intent(RequestPaymentActivity.this, SelectRecipientActivity.class);

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
                                recipientUri = response.get(0).UserUri;
                                startSecurityPinActivity("Confirm", String.format("To confirm your payment of %s to %s, swipe you pin below.",
                                    txtAmount.getText(), friend.getName()));
                            }
                        }
                        else
                        {
                            errorMessage = response.ReasonPhrase;
                            showDialog(SUBMITREQUESTFAILED_DIALOG);
                        }
                    }

                });
                dialog = progressDialog;
                thread.start();

                return dialog;
            case SUBMITREQUESTFAILED_DIALOG:
                alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("Failed");

                alertDialog
                .setMessage(errorMessage);
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                });

                return alertDialog;
            case SUBMITREQUESTSUCCESS_DIALOG:
                tracker.trackPageView("Request Money: Completed");
                alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("Request Sumitted");
                NumberFormat nf = NumberFormat.getCurrencyInstance();

                alertDialog.setMessage(String.format(
                    "Your request for %s was sent to %s.", nf.format(amount),
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
                alertDialog = new AlertDialog.Builder(RequestPaymentActivity.this)
                .create();
                alertDialog.setTitle("Invalid Recipient");
                alertDialog
                .setMessage("Your recipient does not have a valid paypoint.");

                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                return alertDialog;
            case NOAMOUNTSPECIFIED_DIALOG:
                alertDialog = new AlertDialog.Builder(RequestPaymentActivity.this)
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
                alertDialog = new AlertDialog.Builder(RequestPaymentActivity.this)
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
                alertDialog = new AlertDialog.Builder(RequestPaymentActivity.this)
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

    protected void launchRequestMoneyView() {
        setContentView(R.layout.requestmoney_controller);


        txtComments = (EditText) findViewById(R.id.txtRequestMoneyComments);
        btnRequestMoney = (Button) findViewById(R.id.btnSubmit);
        Typeface type = Typeface.createFromAsset(getAssets(),"HelveticaWorld-Bold.ttf");
        btnRequestMoney.setTypeface(type);
        btnRequestMoney.setTextColor(Color.WHITE);

        txtAmount = (Button) findViewById(R.id.txtRequestMoneyAmount);
        txtAmount.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(RequestPaymentActivity.this, AddMoneyActivity.class), ADD_MONEY);
            }
        });

        btnCamera = (ImageButton) findViewById(R.id.camera);
        btnCamera.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v) {

                startActivityForResult(new Intent(RequestPaymentActivity.this, CameraActivity.class), CAMERA);
            }
        });

        btnAddContacts = (Button) findViewById(R.id.addRecipient);
        btnAddContacts.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(RequestPaymentActivity.this, FriendsListActivity.class), ADDING_FRIEND);
            }

        });

        btnRequestMoney.setOnClickListener(new OnClickListener() {

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

                int numPaypoints = friend != null ? friend.getPaypoints().size() : 0;

                if (isValid & numPaypoints == 0) {
                    showDialog(NORECIPIENTSPECIFIED_DIALOG);
                    isValid = false;
                }
                if (isValid & amount == 0) {
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

                    if(prefs.getString("userId", "").length() == 0) {
                        logout();
                    } else {

                        if (prefs.getBoolean("hasACHAccount", false) || !prefs.getString("paymentAccountId", "").equals(""))
                        {
                            if (numPaypoints == 1)
                            {
                                recipientUri = friend.getPaypoints().get(0);
                                startSecurityPinActivity("Confirm", String.format("To confirm your payment of %s to %s, swipe you pin below.",
                                    txtAmount.getText(), friend.getName()));
                            }
                            else if (numPaypoints > 1)
                            {
                                showDialog(SUBMITPAYMENT_MULTIPLEURIS);
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

        btnRequestMoney.setVisibility(View.VISIBLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        switch (resultCode)
        {
            case RESULT_OK:
                Bundle bundle = data.getExtras();
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
                        showDialog(SUBMITREQUEST_DIALOG);
                        break;
                    case SELECT_RECIPIENT:
                        recipientUri = data.getStringExtra("uri");
                        startSecurityPinActivity("Confirm", String.format("To confirm your payment of %s to %s, swipe you pin below.",
                            txtAmount.getText(), friend.getName()));
                        break;
                    case CAMERA:
                    {
                        try{

                            String path = (String) data.getExtras().get("index");
                            FileInputStream in = new FileInputStream(path);
                            Bitmap thumbnail = BitmapFactory.decodeStream(in);
                            ImageView cameraImage = (ImageView) findViewById(R.id.cameraImage);
                            cameraImage.setImageBitmap(thumbnail);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
                break;
        }
    }

    private void addingContact(String id, String paypoint) {
        Friend chosenContact = new Friend();
        if (id != null)
        {
            chosenContact.setId(id);
            friend = combinedContactList.get(combinedContactList.indexOf(chosenContact));

            if (friend.isFBContact()) {
                recipientUri = "fb_" + friend.getId();
                btnAddContacts.setText(friend.getName() + ": " + friend.getId());
            }
            else {
                //recipientUri = "" + friend.getPaypoint();
                btnAddContacts.setText(friend.toString());
            }
        }
        else
        {
            chosenContact.setName("New Contact");
            chosenContact.getPaypoints().add(paypoint);
            friend = chosenContact;
            recipientUri = "" + paypoint;
            btnAddContacts.setText("New contact: " + paypoint);
        }
    }

    protected void submitRequest() {

        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.UserId = prefs.getString("userId", "");
        paymentRequest.SenderAccountId = prefs.getString("paymentAccountId", "");
        paymentRequest.RecipientUri = recipientUri;
        paymentRequest.Amount = Double.parseDouble(txtAmount.getText().toString()
            .replaceAll("[$,]*", ""));
        paymentRequest.Comments = comments;
        paymentRequest.SecurityPin = passcode;

        if (location != null) {
            paymentRequest.Latitude = location.getLatitude();
            paymentRequest.Longitude = location.getLongitude();
        }

        paymentResponse = PaymentServices.requestMoney(paymentRequest);
    }

    /** Determines whether one Location reading is better than the current Location fix
     * @param location  The new Location that you want to evaluate
     * @param currentBestLocation  The current Location fix, to which you want to compare the new one
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
