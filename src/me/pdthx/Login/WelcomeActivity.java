package me.pdthx.Login;
import me.pdthx.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TabHost;

public class WelcomeActivity extends Activity {

	 Button signIn;
	 Button signUp;

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
				TabHost tabHost = TabUIActivity.self.getTabHost();
				tabHost.setCurrentTab(2);
			}

		});

		signUp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				//Intent intent = new Intent(getApplicationContext(),
				//		signUpActivity.class);
				//startActivity(intent);
				TabHost tabHost = TabUIActivity.self.getTabHost();
				tabHost.setCurrentTab(1);
			}

		});
	}
}