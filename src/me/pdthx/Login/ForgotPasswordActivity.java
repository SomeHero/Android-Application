package me.pdthx.Login;

import me.pdthx.Responses.Response;
import android.content.DialogInterface;
import me.pdthx.Services.UserService;
import android.view.View;
import android.view.View.OnClickListener;
import me.pdthx.R;
import android.widget.Button;
import android.widget.EditText;
import android.os.Bundle;
import me.pdthx.BaseActivity;

public class ForgotPasswordActivity extends BaseActivity
{
    private EditText txtForgotPasswordEmail;
    private Button btnForgotPasswordSubmit;
    private String emailAddress;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);

        txtForgotPasswordEmail = (EditText) findViewById(R.id.txtForgotPasswordEmail);
        btnForgotPasswordSubmit = (Button) findViewById(R.id.btnForgotPasswordSubmit);



        btnForgotPasswordSubmit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v)
            {
                emailAddress = txtForgotPasswordEmail.getText().toString().trim();

                //TODO: Send email address to server.
                Response response = UserService.resetPassword(emailAddress);

                if (response.Success)
                {
                    alertDialog.setTitle("Email Sent!");
                    alertDialog
                    .setMessage("Email sent to reset password.");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    });
                }
                else
                {
                    alertDialog.setTitle("Unable to send email.");
                    alertDialog
                    .setMessage(response.ReasonPhrase);
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                }
                alertDialog.show();
            }

        });
    }
}
