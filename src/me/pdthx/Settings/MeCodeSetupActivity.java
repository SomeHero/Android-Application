package me.pdthx.Settings;

import me.pdthx.BaseActivity;
import me.pdthx.R;
import android.text.Editable;
import android.text.TextWatcher;
import me.pdthx.Requests.UserMeCodeRequest;
import me.pdthx.Responses.Response;
import me.pdthx.Services.UserService;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

public class MeCodeSetupActivity extends BaseActivity {

	final private int INVALID_DOLLAR = 0;
	final private int INVALID_MECODE = 3;
	final private int SUCCESS_MECODE = 4;

	private UserMeCodeRequest request;
	private Response response;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tracker.trackPageView("ProfileSetupActivity");
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		showProfileSetup();

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

			}
		}
	};

	public void showProfileSetup() {
		View view = View.inflate(MeCodeSetupActivity.this,
				R.layout.setup_profile, null);
		setContentView(view);

		Button btnCreateProfile = (Button) findViewById(R.id.btnSaveChanges);

		btnCreateProfile.setOnClickListener(new OnClickListener() {
			public void onClick(View argO) {

				EditText txtMeCode = (EditText) findViewById(R.id.meCode);
				txtMeCode.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void afterTextChanged(Editable s)
                    {
                        if (s.toString().charAt(0) != '$')
                        {
                            s.insert(0, "$");
                        }

                    }

                    @Override
                    public void beforeTextChanged(
                        CharSequence s,
                        int start,
                        int count,
                        int after)
                    {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onTextChanged(
                        CharSequence s,
                        int start,
                        int before,
                        int count)
                    {
                        // TODO Auto-generated method stub

                    }

				});
				if (txtMeCode.getText().toString().charAt(0) != '$') {
					profileSetupHandler.sendEmptyMessage(INVALID_DOLLAR);
				} else {
					progressDialog.setMessage("Adding Me Code..");
					progressDialog
							.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					request = new UserMeCodeRequest(
							prefs.getString("userId", ""), txtMeCode.getText()
									.toString().trim());
					progressDialog.show();
					response = UserService.createMeCode(request);

					if (response.Success) {
						txtMeCode.setText("");
						progressDialog.dismiss();
						profileSetupHandler.sendEmptyMessage(SUCCESS_MECODE);
					} else {
						progressDialog.dismiss();
						profileSetupHandler.sendEmptyMessage(INVALID_MECODE);
					}
				}
			}
		});
	}
}
