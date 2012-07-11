package me.pdthx.Settings;

import me.pdthx.BaseActivity;
import me.pdthx.R;
import me.pdthx.SecurityPinSetupActivity;
import me.pdthx.CustomViews.CustomLockView;
import me.pdthx.Requests.UserChangeSecurityPinRequest;
import me.pdthx.Responses.Response;
import me.pdthx.Services.UserService;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SecuritySettingsActivity extends BaseActivity {

    private LinearLayout setupPin;
    private LinearLayout setupQuestion;
    private LinearLayout changePassword;
    private Button backBtn;

    final private int SETUPSECURITYPIN = 50;

    final private int USERSECURITYPIN_COMPLETE = 1;
    final private int USERSECURITYPIN_FAILED = 2;
    final private int USERSECURITYPIN_INVALIDLENGTH = 5;
    final private int USERSECURITYPIN_CONFIRMMISMATCH = 6;
    final private int INOPERABLE_ACTION = 11;

    final private int INVALID_DOLLAR = 0;
    final private int INVALID_MECODE = 3;
    final private int SUCCESS_MECODE = 4;

    private UserChangeSecurityPinRequest request;
    private Response response;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_continued);

        backBtn = (Button) findViewById(R.id.settings_BackButton);
        backBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }

        });

        setupPin = (LinearLayout) findViewById(R.id.settings_ExtraBtn1);
        TextView title = (TextView) findViewById(R.id.settingsExtraTitle1);
        title.setText("Change Security Pin");

        if (prefs.getBoolean("setupSecurityPin", false)) {

            setupPin.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                    request = new UserChangeSecurityPinRequest();
                    response = new Response();
                    changeSecurityPinCurrent();
                }

            });
        } else {
            title.setText("Setup Security Pin");
            setupPin.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                    startActivityForResult(new Intent(v.getContext(),
                        SecurityPinSetupActivity.class), SETUPSECURITYPIN);
                }

            });
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

    private void changeSecurityPinCurrent() {
        final Dialog d = new Dialog(this, R.style.CustomDialogTheme);
        d.setContentView(R.layout.security_dialog);
        d.show();

        Button hidden = (Button)d.findViewById(R.id.btnCancelSendMoney);
        hidden.setVisibility(View.GONE);

        TextView txtConfirmHeader = (TextView) d
            .findViewById(R.id.setupSecurityHeader);
        TextView txtConfirmBody = (TextView) d
            .findViewById(R.id.setupSecurityBody);

        txtConfirmHeader.setText("Current Pin");
        txtConfirmBody
        .setText("To change your security pin, input your current security pin below.");

        final CustomLockView ctrlSecurityPin = (CustomLockView) d
            .findViewById(R.id.ctrlSecurityPin);
        ctrlSecurityPin.invalidate();
        ctrlSecurityPin.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                String passcode = ctrlSecurityPin.getPasscode();

                if (passcode.length() > 3) {
                    request.CurrentSecurityPin = passcode;
                    d.dismiss();

                    changeSecurityPinNew();

                } else
                    profileSetupHandler
                    .sendEmptyMessage(USERSECURITYPIN_INVALIDLENGTH);

                return false;

            }
        });

    }

    private void changeSecurityPinNew() {
        final Dialog d = new Dialog(this, R.style.CustomDialogTheme);
        d.setContentView(R.layout.security_dialog);
        d.show();

        Button hidden = (Button)d.findViewById(R.id.btnCancelSendMoney);
        hidden.setVisibility(View.GONE);

        TextView txtConfirmHeader = (TextView) d
            .findViewById(R.id.setupSecurityHeader);
        TextView txtConfirmBody = (TextView) d
            .findViewById(R.id.setupSecurityBody);

        txtConfirmHeader.setText("New Security Pin");
        txtConfirmBody
        .setText("To change your security pin, input your new security pin below.");

        final CustomLockView ctrlSecurityPin = (CustomLockView) d
            .findViewById(R.id.ctrlSecurityPin);
        ctrlSecurityPin.invalidate();
        ctrlSecurityPin.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                String passcode = ctrlSecurityPin.getPasscode();

                if (passcode.length() > 3) {
                    request.NewSecurityPin = passcode;
                    d.dismiss();

                    changeSecurityPinConfirmNew();

                } else
                    profileSetupHandler
                    .sendEmptyMessage(USERSECURITYPIN_INVALIDLENGTH);

                return false;

            }
        });
    }

    private void changeSecurityPinConfirmNew() {
        final Dialog d = new Dialog(this, R.style.CustomDialogTheme);
        d.setContentView(R.layout.security_dialog);
        d.show();

        Button hidden = (Button)d.findViewById(R.id.btnCancelSendMoney);
        hidden.setVisibility(View.GONE);

        TextView txtConfirmHeader = (TextView) d
            .findViewById(R.id.setupSecurityHeader);
        TextView txtConfirmBody = (TextView) d
            .findViewById(R.id.setupSecurityBody);

        txtConfirmHeader.setText("Confirm Security Pin");
        txtConfirmBody.setText("Put in your new security pin to confirm.");

        final CustomLockView ctrlSecurityPin = (CustomLockView) d
            .findViewById(R.id.ctrlSecurityPin);
        ctrlSecurityPin.invalidate();
        ctrlSecurityPin.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                String passcode = ctrlSecurityPin.getPasscode();

                if (passcode.length() > 3
                    && passcode.equals(request.NewSecurityPin)) {
                    d.dismiss();

                    progressDialog = new ProgressDialog(v.getContext());
                    // ProgressDialog.Builder progressDialog = new
                    // ProgressDialog.Builder(parent);
                    progressDialog
                    .setMessage("Setting up your security pin...");
                    progressDialog
                    .setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.show();

                    Thread thread = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            try {
                                request.UserId = prefs.getString("userId", "");
                                response = UserService
                                    .changeSecurityPin(request);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            progressDialog.dismiss();

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

                } else {
                    profileSetupHandler
                    .sendEmptyMessage(USERSECURITYPIN_CONFIRMMISMATCH);
                }

                return false;
            }
        });
    }

    private Handler profileSetupHandler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {

                case (INVALID_MECODE):
                    alertDialog.setTitle("Invalid MeCode");
                alertDialog
                .setMessage("There was a problem setting up your MeCode: "
                    + response.ReasonPhrase + ". Please try again.");
                alertDialog.setButton("OK",
                    new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                        int which) {
                        dialog.dismiss();
                    }
                });

                alertDialog.show();
                break;

                case (SUCCESS_MECODE):
                    alertDialog.setTitle("MeCode Success");
                alertDialog.setMessage("MeCode setup successful");
                alertDialog.setButton("OK",
                    new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                        int which) {
                        dialog.dismiss();
                    }
                });

                alertDialog.show();
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

                case (INVALID_DOLLAR):
                    alertDialog.setTitle("Invalid MeCode");
                alertDialog
                .setMessage("The meCode must begin with a '$'. Please try again.");
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
                    alertDialog.setTitle("Password changed");
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
