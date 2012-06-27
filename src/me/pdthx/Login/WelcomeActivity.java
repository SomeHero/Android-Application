package me.pdthx.Login;
import me.pdthx.BaseActivity;
import me.pdthx.R;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class WelcomeActivity extends BaseActivity {

	 private Button signIn;
	 private Button signUp;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);

		signIn = (Button) findViewById(R.id.signin_button);
		signUp = (Button) findViewById(R.id.signup_button);

		signIn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				//Intent intent = new Intent(getApplicationContext(),
				//		signInActivity.class);
				//startActivity(intent);
				switchTabInActivity(2);
			}

		});

		signUp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				//Intent intent = new Intent(getApplicationContext(),
				//		signUpActivity.class);
				//startActivity(intent);
				switchTabInActivity(1);
			}

		});
	}

	public void switchTabInActivity(int indexTabToSwitchTo){
		TabUIActivity ParentActivity;
		ParentActivity = (TabUIActivity) this.getParent();
		ParentActivity.switchTab(indexTabToSwitchTo);
	}
}