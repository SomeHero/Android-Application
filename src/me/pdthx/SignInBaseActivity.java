package me.pdthx;

import me.pdthx.Responses.UserRegistrationResponse;
import me.pdthx.Responses.UserSignInResponse;
import android.app.Activity;

public abstract class SignInBaseActivity extends Activity {
	public abstract void UserSignInComplete(UserSignInResponse userSignInResponse);
	public abstract void UserRegistrationComplete(UserRegistrationResponse userRegistrationResponse);

}
