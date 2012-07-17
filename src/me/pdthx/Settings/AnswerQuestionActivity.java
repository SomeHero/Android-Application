package me.pdthx.Settings;

import me.pdthx.Responses.Response;
import me.pdthx.Requests.UserAnswerQuestionRequest;
import me.pdthx.BaseActivity;
import me.pdthx.R;
import me.pdthx.Services.UserService;
import me.pdthx.Widget.WheelView;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AnswerQuestionActivity extends BaseActivity{

    final private int ANSWERQUESTION_FAILED = 5;

    private UserAnswerQuestionRequest request;
    private Response response;

    private EditText txtAnswer;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_security_question);

        TextView header = (TextView)findViewById(R.id.setupSecurityQuestionHeader);
        header.setText("Confirm Security Question");

        TextView body = (TextView)findViewById(R.id.securityQuestionBody);
        body.setText("Enter your account's security question answer below.");

        WheelView list = (WheelView) findViewById(R.id.security_question_list);
        list.setVisibility(View.GONE);

        Button confirmBtn = (Button) findViewById(R.id.securityquestion_confirmbtn);
        confirmBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                txtAnswer = (EditText)findViewById(R.id.setupSecurityTxtAnswer);
                String answer = txtAnswer.getText().toString().trim();

                request = new UserAnswerQuestionRequest();
                request.UserId = prefs.getString("userId", "");
                request.Answer = answer;

                progressDialog.setMessage("Sending Info...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();

                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        try {
                            response = UserService
                                .validateSecurityQuestion(request);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();

                        if (response.Success) {
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            signUpHandler.sendEmptyMessage(ANSWERQUESTION_FAILED);
                        }
                    }

                });
                thread.start();
            }

        });
    }

    private Handler signUpHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case (ANSWERQUESTION_FAILED):
                    alertDialog.setTitle("Question Answer Failed");
                alertDialog.setMessage(response.ReasonPhrase);
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