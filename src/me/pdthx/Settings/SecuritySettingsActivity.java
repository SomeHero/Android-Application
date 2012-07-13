package me.pdthx.Settings;

import me.pdthx.Requests.UserSetupSecurityPinRequest;
import me.pdthx.SecurityPinActivity;
import me.pdthx.BaseActivity;
import me.pdthx.R;
import me.pdthx.Requests.UserChangeSecurityPinRequest;
import me.pdthx.Responses.Response;
import me.pdthx.Services.UserService;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SecuritySettingsActivity extends BaseActivity {

    private LinearLayout setupPin;
    private LinearLayout forgotPin;
    private LinearLayout setupQuestion;
    private LinearLayout changePassword;

    final private int USERSECURITYPIN_COMPLETE = 1;
    final private int USERSECURITYPIN_FAILED = 2;
    final private int USERSECURITYPIN_CONFIRMMISMATCH = 6;
    final private int INOPERABLE_ACTION = 11;
    final private int DISMISS_DIALOG = 12;


    final private int CURRENTPIN = 15;
    final private int NEWPIN = 16;
    final private int CONFIRMPIN = 17;
    final private int ANSWER_QUESTION = 18;
    final private int FORGOTNEWPIN = 19;
    final private int FORGOTCONFIRMPIN = 20;

    private UserChangeSecurityPinRequest changePinRequest;
    private UserSetupSecurityPinRequest forgotPinRequest;
    private Response response;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_continued);

        setupPin = (LinearLayout) findViewById(R.id.settings_ExtraBtn1);
        TextView title = (TextView) findViewById(R.id.settingsExtraTitle1);
        title.setText("Change Security Pin");

        if (prefs.getBoolean("setupSecurityPin", false)) {

            setupPin.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    changePinRequest = new UserChangeSecurityPinRequest();
                    changePinRequest.UserId = prefs.getString("userId", "");
                    response = new Response();
                    changeSecurityPinCurrent();
                }

            });
        }
        else {
            setupPin.setVisibility(View.GONE);
        }

        forgotPin = (LinearLayout) findViewById(R.id.settings_ExtraBtn4);
        TextView title4 = (TextView) findViewById(R.id.settingsExtraTitle4);
        title4.setText("Forgot Security Pin");

        if (prefs.getBoolean("setupSecurityPin", false)) {

            forgotPin.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    forgotPinRequest = new UserSetupSecurityPinRequest();
                    response = new Response();
                    startActivityForResult(new Intent(SecuritySettingsActivity.this,
                        AnswerQuestionActivity.class), ANSWER_QUESTION);
                }

            });
        }
        else {
            forgotPin.setVisibility(View.GONE);
        }

        setupQuestion = (LinearLayout) findViewById(R.id.settings_ExtraBtn2);
        TextView title2 = (TextView)findViewById(R.id.settingsExtraTitle2);
        title2.setText("Change Security Question");
        setupQuestion.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                profileSetupHandler.sendEmptyMessage(INOPERABLE_ACTION);
            }

        });

        changePassword = (LinearLayout) findViewById(R.id.settings_ExtraBtn3);
        if (!signedInViaFacebook)
        {
            TextView title3 = (TextView) findViewById(R.id.settingsExtraTitle3);
            title3.setText("Change Password");
            changePassword.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(SecuritySettingsActivity.this, ChangePasswordActivity.class));
                }
            });
        }
        else
        {
            changePassword.setVisibility(View.GONE);
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (resultCode)
        {
            case RESULT_OK:
            {
                switch (requestCode)
                {
                    case ANSWER_QUESTION:
                    {
                        Intent intent = new Intent(SecuritySettingsActivity.this, SecurityPinActivity.class);
                        intent.putExtra("headerText", "New Security Pin");
                        intent.putExtra("bodyText", "To change your security pin, input your new security pin below.");
                        startActivityForResult(intent, FORGOTNEWPIN);
                        break;
                    }
                    case FORGOTNEWPIN:
                    {
                        forgotPinRequest.SecurityPin = data.getExtras().getString("passcode");
                        Intent intent = new Intent(SecuritySettingsActivity.this, SecurityPinActivity.class);
                        intent.putExtra("headerText", "Confirm Security Pin");
                        intent.putExtra("bodyText", "Put in your new security pin to confirm.");
                        startActivityForResult(intent, FORGOTCONFIRMPIN);
                        break;
                    }
                    case FORGOTCONFIRMPIN:
                    {
                        if (forgotPinRequest.SecurityPin.equals(data.getExtras().getString("passcode")))
                        {
                            progressDialog = new ProgressDialog(this);
                            // ProgressDialog.Builder progressDialog = new
                            // ProgressDialog.Builder(parent);
                            progressDialog
                            .setMessage("Changing your security pin...");
                            progressDialog
                            .setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            progressDialog.show();

                            Thread thread = new Thread(new Runnable() {

                                @Override
                                public void run() {
                                    // TODO Auto-generated method stub
                                    try {
                                        forgotPinRequest.UserId = prefs.getString("userId", "");
                                        response = UserService
                                            .setupSecurityPin(forgotPinRequest);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    profileSetupHandler.sendEmptyMessage(DISMISS_DIALOG);

                                    if (response.Success) {
                                        profileSetupHandler
                                        .sendEmptyMessage(USERSECURITYPIN_COMPLETE);
                                    } else {
                                        profileSetupHandler
                                        .sendEmptyMessage(USERSECURITYPIN_FAILED);
                                    }
                                }

                            });
                            thread.start();
                        }
                        else
                        {
                            profileSetupHandler.sendEmptyMessage(USERSECURITYPIN_CONFIRMMISMATCH);
                        }
                        break;
                    }
                    case CURRENTPIN:
                    {
                        changePinRequest.CurrentSecurityPin = data.getExtras().getString("passcode");
                        changeSecurityPinNew();
                        break;
                    }
                    case NEWPIN:
                    {
                        changePinRequest.NewSecurityPin = data.getExtras().getString("passcode");
                        changeSecurityPinConfirm();
                        break;
                    }
                    case CONFIRMPIN:
                    {
                        if (changePinRequest.NewSecurityPin.equals(data.getExtras().getString("passcode")))
                        {
                            progressDialog = new ProgressDialog(this);
                            // ProgressDialog.Builder progressDialog = new
                            // ProgressDialog.Builder(parent);
                            progressDialog
                            .setMessage("Changing your security pin...");
                            progressDialog
                            .setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            progressDialog.show();

                            Thread thread = new Thread(new Runnable() {

                                @Override
                                public void run() {
                                    // TODO Auto-generated method stub
                                    try {
                                        changePinRequest.UserId = prefs.getString("userId", "");
                                        response = UserService
                                            .changeSecurityPin(changePinRequest);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    profileSetupHandler.sendEmptyMessage(DISMISS_DIALOG);

                                    if (response.Success) {
                                        profileSetupHandler
                                        .sendEmptyMessage(USERSECURITYPIN_COMPLETE);
                                    } else {
                                        profileSetupHandler
                                        .sendEmptyMessage(USERSECURITYPIN_FAILED);
                                    }
                                }

                            });
                            thread.start();
                        }
                        else
                        {
                            profileSetupHandler.sendEmptyMessage(USERSECURITYPIN_CONFIRMMISMATCH);
                        }
                        break;
                    }
                }
                break;
            }
            case RESULT_CANCELED:
            {
                changePinRequest = null;
                break;
            }

        }
    }

    private void changeSecurityPinCurrent() {
        Intent intent = new Intent(SecuritySettingsActivity.this, SecurityPinActivity.class);
        intent.putExtra("headerText", "Current Pin");
        intent.putExtra("bodyText", "To change your security pin, input your current security pin below.");
        startActivityForResult(intent, CURRENTPIN);
    }

    private void changeSecurityPinNew() {
        Intent intent = new Intent(SecuritySettingsActivity.this, SecurityPinActivity.class);
        intent.putExtra("headerText", "New Security Pin");
        intent.putExtra("bodyText", "To change your security pin, input your new security pin below.");
        startActivityForResult(intent, NEWPIN);
    }

    private void changeSecurityPinConfirm() {
        Intent intent = new Intent(SecuritySettingsActivity.this, SecurityPinActivity.class);
        intent.putExtra("headerText", "Confirm Security Pin");
        intent.putExtra("bodyText", "Put in your new security pin to confirm.");
        startActivityForResult(intent, CONFIRMPIN);
    }

    private Handler profileSetupHandler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {


                case DISMISS_DIALOG:
                    progressDialog.dismiss();
                    break;

                case (INOPERABLE_ACTION):
                    alertDialog.setTitle("Unavailible Content");
                alertDialog
                .setMessage("The added feature is currently unavailible. Please try again later.");
                alertDialog.setButton("OK",
                    new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                        int which) {
                        dialog.dismiss();
                    }
                });

                alertDialog.show();
                break;

                case (USERSECURITYPIN_COMPLETE):
                    alertDialog.setTitle("Passcode changed");
                alertDialog
                .setMessage("Your passcode was successfully changed.");
                alertDialog.setButton("OK",
                    new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                        int which) {
                        dialog.dismiss();
                    }
                });

                alertDialog.show();
                break;

                case (USERSECURITYPIN_FAILED):
                    alertDialog.setTitle("Setup Failed");
                alertDialog
                .setMessage("There was an error setting up your security pin: "
                    + response.ReasonPhrase + " Please try again.");
                alertDialog.setButton("OK",
                    new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                        int which) {
                        dialog.dismiss();
                    }
                });

                alertDialog.show();
                break;

                case (USERSECURITYPIN_CONFIRMMISMATCH):
                    alertDialog.setTitle("Security Pins Mismatch.");
                alertDialog
                .setMessage("The two security pins you just swiped don't match. Please try again.");
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
}
