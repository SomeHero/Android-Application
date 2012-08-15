package me.pdthx.Setup;

import me.pdthx.BaseActivity;
import me.pdthx.CameraActivity;
import me.pdthx.R;
import java.io.FileInputStream;

//import com.googlecode.tesseract.android.TessBaseAPI;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import me.pdthx.Requests.ACHAccountSetupRequest;

public class ACHAccountSetupActivity extends BaseActivity implements OnCheckedChangeListener {

    final private int SETUPACHACCOUNT_FAILED = 3;
    final private int USERREGISTRATION_ACHNUMBERMISMATCH = 8;
    final private int CAMERA = 20;
    private Button btnAddAccount;
    private Button btnRemoveAccount;
    private Button btnUpdateAccount;
    private LinearLayout btnCheckImage;
    private RadioGroup btnAcctType;
    private boolean isCheckingAcct;
    private int tab;

    private String nameOnAccount;
    private String nickname;
    private String routingNumber;
    private String accountNumber;
    private String passcode;

    private ACHAccountSetupRequest request = new ACHAccountSetupRequest();;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tracker.trackPageView("ACHAccountSetupActivity");

        progressDialog.dismiss();
        setContentView(R.layout.achaccountsetup_controller);
        btnCheckImage = (LinearLayout) findViewById(R.id.takePhotoBtn);
        tab = getIntent().getIntExtra("tab", 0);

        btnAddAccount = (Button) findViewById(R.id.btnSubmitACHAccount);
        btnAddAccount.setText("Add Account");
        btnRemoveAccount = (Button) findViewById(R.id.btnremoveACHAcct);
        btnRemoveAccount.setVisibility(View.GONE);
        btnUpdateAccount = (Button) findViewById(R.id.btnRemindMeLater);
        btnUpdateAccount.setVisibility(View.GONE);
        btnAcctType = (RadioGroup) findViewById(R.id.achBankCategories);
        btnAcctType.setOnCheckedChangeListener(this);
        showSetupACHController();


    }

    private Handler achSetupHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case (SETUPACHACCOUNT_FAILED):
                    alertDialog = new AlertDialog.Builder(
                        ACHAccountSetupActivity.this).create();
                alertDialog.setTitle("Setup Failed");
                alertDialog
                .setMessage("There was an error setting up your ACH account. Please try again.");
                alertDialog.setButton("OK",
                    new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                        int which) {
                        dialog.dismiss();
                    }
                });

                alertDialog.show();
                break;

                case (USERREGISTRATION_ACHNUMBERMISMATCH):
                    alertDialog = new AlertDialog.Builder(
                        ACHAccountSetupActivity.this).create();
                alertDialog.setTitle("ACH Account Number Mismatch.");
                alertDialog
                .setMessage("The ACH account numbers you entered do not match. Please try again.");
                alertDialog.setButton("OK",
                    new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                        int which) {
                        dialog.dismiss();
                    }
                });

                alertDialog.show();
                break;
            }

        }

    };

    private void showSetupACHController() {


        btnCheckImage.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(ACHAccountSetupActivity.this, CameraActivity.class), CAMERA);

            }

        });


        btnAddAccount.setOnClickListener(new OnClickListener() {
            public void onClick(View argO) {

                EditText txtNameOnAccount = (EditText) findViewById(R.id.txtNameOnAccount);
                EditText txtRoutingNumber = (EditText) findViewById(R.id.txtRoutingNumber);
                EditText txtAccountNumber = (EditText) findViewById(R.id.txtAccountNumber);
                EditText txtNickname = (EditText)findViewById(R.id.txtNicknameonAcct);

                if (txtAccountNumber
                    .getText()
                    .toString()
                    .trim()
                    .equals(((EditText) findViewById(R.id.txtConfirmAccountNumber))
                        .getText().toString().trim())) {

                    nameOnAccount = txtNameOnAccount.getText().toString().trim();
                    routingNumber = txtRoutingNumber.getText().toString().trim();
                    accountNumber = txtAccountNumber.getText().toString().trim();
                    nickname = txtNickname.getText().toString().trim();

                    startSecurityPinActivity("Create Security Pin", "To setup a security pin, connect 4 or more dots using a swiping motion with your finger.");

                } else {
                    achSetupHandler
                    .sendEmptyMessage(USERREGISTRATION_ACHNUMBERMISMATCH);
                }
            }
        });
    }

    @Override
    public void onCheckedChanged(RadioGroup arg0, int arg1) {
        if (arg1 == R.id.achCheckings) {
            isCheckingAcct = true;
        }
        if (arg1 == R.id.achSavings) {
            isCheckingAcct = false;
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode)
        {
            case RESULT_OK:
            {
                switch (requestCode)
                {
                    case SECURITYPIN:
                    {
                        if (passcode == null)
                        {
                            passcode = data.getStringExtra("passcode");
                            startSecurityPinActivity("Confirm Your Pin", "Re-enter your security pin to confirm your new pin code.");
                        }
                        else
                        {
                            if (passcode.equals(data.getStringExtra("passcode")))
                            {
                                request.AccountNumber = accountNumber;
                                request.NameOnAccount = nameOnAccount;
                                request.Nickname = nickname;
                                request.RoutingNumber = routingNumber;
                                request.UserId = prefs.getString("userId", "");
                                request.AccountType = isCheckingAcct ? "Checking" : "Savings";
                                request.SecurityPin = passcode;
                                Intent intent = new Intent(ACHAccountSetupActivity.this, CreateQuestionActivity.class);
                                intent.putExtra("tab", tab);
                                intent.putExtra("achAccountObject", request);
                                startActivity(intent);
                            }
                        }
                        break;
                    }
                    case CAMERA:
                    {
                        try{
                            String path = (String) data.getExtras().get("index");
                            FileInputStream in = new FileInputStream(path);

//		                  Bitmap thumbnail = null;

//		                  cameraImage.setImageResource(R.drawable.bg_pop3);
//		                  thumbnail = BitmapFactory.decodeStream(in);
//		                  cameraImage.setImageBitmap(thumbnail);
                            in.close();
                        }
                        catch (Exception e)
                        {
                            Log.d("Error", e.getMessage());
                        }

                        break;
                    }
                }
                break;
            }
            default:
            {
                break;
            }
        }
    }



}
