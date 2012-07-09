package me.pdthx.Settings;

import android.os.Handler;
import android.content.DialogInterface;
import android.os.Message;
import android.app.ProgressDialog;
import me.pdthx.Services.UserService;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import me.pdthx.R;
import android.os.Bundle;
import android.view.WindowManager;
import me.pdthx.Requests.UserChangePasswordRequest;
import me.pdthx.Responses.Response;
import me.pdthx.BaseActivity;

public class ChangePasswordActivity
    extends BaseActivity
{
    private UserChangePasswordRequest request;
    private Response response;

    private static final int PASSWORDCHANGE_FAILED = 1;
    private static final int PASSWORDCHANGE_SUCCESS = 2;

    private TextView txtOldPassword;
    private TextView txtNewPassword;

    private Handler changePasswordHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

            case (PASSWORDCHANGE_FAILED):
                alertDialog.setTitle("Password Change Failure");
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

            case (PASSWORDCHANGE_SUCCESS):
                alertDialog.setTitle("Password Change Success");
                alertDialog.setMessage("Your password was successfully changed.");
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


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tracker.trackPageView("ChangePasswordActivity");
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.change_password);

        txtOldPassword = (TextView) findViewById(R.id.txtOldPassword);
        txtNewPassword = (TextView) findViewById(R.id.txtNewPassword);

        Button btnChangePassword = (Button) findViewById(R.id.btnChangePassword);

        btnChangePassword.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0)
            {
                request.CurrentPassword = txtOldPassword.getText().toString();
                request.NewPassword = txtNewPassword.getText().toString();
                progressDialog.setMessage("Changing Password..");
                progressDialog
                        .setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();
                response = UserService.changePassword(request);

                if (response.Success) {
                    txtOldPassword.setText("");
                    txtNewPassword.setText("");
                    progressDialog.dismiss();
                    changePasswordHandler.sendEmptyMessage(PASSWORDCHANGE_SUCCESS);
                } else {
                    progressDialog.dismiss();
                    changePasswordHandler.sendEmptyMessage(PASSWORDCHANGE_FAILED);
                }
            }

        });

    }
}
