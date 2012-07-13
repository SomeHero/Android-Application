package me.pdthx.Login;

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
            }

        });
    }
}
