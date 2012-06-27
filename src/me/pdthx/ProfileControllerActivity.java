package me.pdthx;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ProfileControllerActivity extends BaseActivity {
	
	private LinearLayout firstNameBtn;
	private LinearLayout lastNameBtn;
	private LinearLayout phoneBtn;
	private LinearLayout emailBtn;
	private LinearLayout facebookBtn;
	private LinearLayout twitterBtn;
	private LinearLayout aboutMeBtn;
	private Button makePublicBtn;
	
	private LinearLayout addressBtn;
	private LinearLayout cityBtn;
	private LinearLayout zipCodeBtn;
	private LinearLayout stateBtn;
	private LinearLayout photoIdBtn;
	private LinearLayout ssnIdBtn;
	private LinearLayout birthdayBtn;
	private LinearLayout genderBtn;
	private LinearLayout incomeBtn;
	private LinearLayout creditBtn;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		showProfileController();
	}

	public void showProfileController() {
		setContentView(R.layout.profile_controller);
		
		firstNameBtn = (LinearLayout)findViewById(R.id.profile_firstNameBtn);
		lastNameBtn = (LinearLayout)findViewById(R.id.profile_lastNameBtn);
		phoneBtn = (LinearLayout)findViewById(R.id.profile_phoneNumberBtn);
		emailBtn = (LinearLayout)findViewById(R.id.profile_emailBtn);
		facebookBtn = (LinearLayout)findViewById(R.id.profile_facebookBtn);
		twitterBtn = (LinearLayout)findViewById(R.id.profile_twitterBtn);
		aboutMeBtn = (LinearLayout)findViewById(R.id.profile_aboutMeBtn);
		
		makePublicBtn = (Button)findViewById(R.id.makePublicProfileBtn);
		
		addressBtn = (LinearLayout)findViewById(R.id.profile_addressBtn);
		cityBtn = (LinearLayout)findViewById(R.id.profile_cityBtn);
		stateBtn = (LinearLayout)findViewById(R.id.profile_stateBtn);
		zipCodeBtn = (LinearLayout)findViewById(R.id.profile_zipBtn);
		photoIdBtn = (LinearLayout)findViewById(R.id.profile_photoIdBtn);
		ssnIdBtn = (LinearLayout)findViewById(R.id.profile_ssnIDBtn);
		birthdayBtn = (LinearLayout)findViewById(R.id.profile_birthdayBtn);
		genderBtn = (LinearLayout)findViewById(R.id.profile_genderBtn);
		incomeBtn = (LinearLayout)findViewById(R.id.profile_incomeBtn);
		creditBtn = (LinearLayout)findViewById(R.id.profile_creditBtn);
		}
	
	public void showDialog()
	{
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.profile_dialog_input);
		dialog.setTitle("Title...");

		// set the custom dialog components - text, image and button
		TextView text = (TextView) dialog.findViewById(R.id.profileDialogBody);
		text.setText("This dialog body will change depending on the button clicked.");
		
		EditText input = (EditText) dialog.findViewById(R.id.profileDialogInput);
		input.setHint("A hint");

		Button dialogButton = (Button) dialog.findViewById(R.id.profileDialogBtn);
		// if button is clicked, close the custom dialog
		dialogButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.show();
	}
}
