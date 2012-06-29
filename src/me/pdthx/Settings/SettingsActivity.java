package me.pdthx.Settings;

import me.pdthx.BaseActivity;
import me.pdthx.ProfileControllerActivity;
import me.pdthx.R;
import me.pdthx.Accounts.AccountManagerActivity;
import me.pdthx.R.drawable;
import me.pdthx.R.id;
import me.pdthx.R.layout;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class SettingsActivity extends BaseActivity {
	private LinearLayout profileBtn;
	private ImageView profileImg;

	private LinearLayout bankBtn;
	private ImageView bankImg;

	private LinearLayout emailBtn;
	private ImageView emailImg;
	private LinearLayout socialBtn;
	private ImageView socialImg;
	private LinearLayout phoneBtn;
	private LinearLayout meCodeBtn;
	private ImageView meCodeImg;

	private LinearLayout notificationBtn;
	private ImageView notificationImg;
	private LinearLayout sharingBtn;
	private ImageView sharingImg;
	private LinearLayout securityBtn;
	private ImageView securityImg;

	private LinearLayout feedbackBtn;
	private ImageView feedbackImg;
	private LinearLayout helpBtn;
	private ImageView helpImg;
	private LinearLayout rateBtn;
	private ImageView rateImg;

	private LinearLayout signOutBtn;
	private LinearLayout userAgreementBtn;
	private LinearLayout aboutBtn;

	private Button backBtn;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		showSettingsController();
	}

	public void showSettingsController() {
		setContentView(R.layout.settings_controller);

		setupViews();
		setupSelectorListeners();

		backBtn = (Button) findViewById(R.id.settings_BackButton);

		backBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}

		});

		profileBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(),
						ProfileControllerActivity.class));
			}
		});

		bankBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(),
						AccountManagerActivity.class));
			}
		});

		securityBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(),
						SecuritySettingsActivity.class));
			}
		});

		meCodeBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(),
						MeCodeSettingsActivity.class));
			}

		});
	}

	public void setupViews() {
		// profile
		profileBtn = (LinearLayout) findViewById(R.id.settings_ProfileBtn);
		profileImg = (ImageView) findViewById(R.id.settingsProfileImg);

		// bank accounts
		bankBtn = (LinearLayout) findViewById(R.id.settings_BankAccountsBtn);
		bankImg = (ImageView) findViewById(R.id.settingsBankImg);

		// email
		emailBtn = (LinearLayout) findViewById(R.id.settings_EmailAccountsBtn);
		emailImg = (ImageView) findViewById(R.id.settingsEmailImg);
		// social
		socialBtn = (LinearLayout) findViewById(R.id.settings_SocialNetworksBtn);
		socialImg = (ImageView) findViewById(R.id.settingsSocialImg);
		// phone
		phoneBtn = (LinearLayout) findViewById(R.id.settings_PhonesBtn);
		// mecode
		meCodeBtn = (LinearLayout) findViewById(R.id.settings_MeCodesBtn);
		meCodeImg = (ImageView) findViewById(R.id.settingsMeCodeImg);

		// notifications
		notificationBtn = (LinearLayout) findViewById(R.id.settings_NotificationsBtn);
		notificationImg = (ImageView) findViewById(R.id.settingsNotiImg);
		// sharing
		sharingBtn = (LinearLayout) findViewById(R.id.settings_SharingBtn);
		sharingImg = (ImageView) findViewById(R.id.settingsSharingImg);
		// security & privacy
		securityBtn = (LinearLayout) findViewById(R.id.settings_SecurityBtn);
		securityImg = (ImageView) findViewById(R.id.settingsSecurityImg);
		// feedback
		feedbackBtn = (LinearLayout) findViewById(R.id.settings_FeedbackBtn);
		feedbackImg = (ImageView) findViewById(R.id.settingsFeedbackImg);
		// help
		helpBtn = (LinearLayout) findViewById(R.id.settings_HelpBtn);
		helpImg = (ImageView) findViewById(R.id.settingsHelpImg);
		// rate
		rateBtn = (LinearLayout) findViewById(R.id.settings_RatePdthxBtn);
		rateImg = (ImageView) findViewById(R.id.settingsRateImg);

		// user agreement
		userAgreementBtn = (LinearLayout) findViewById(R.id.settings_UserAgreementBtn);
		// sign out
		signOutBtn = (LinearLayout) findViewById(R.id.settings_SignOutBtn);
		// about
		aboutBtn = (LinearLayout) findViewById(R.id.settings_AboutBtn);
	}

	// for changing imgs in active and inactive states
	public void setupSelectorListeners() {
		profileBtn.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP
						|| event.getAction() == MotionEvent.ACTION_OUTSIDE
						|| event.getAction() == MotionEvent.ACTION_CANCEL) {
					profileImg
							.setImageResource(R.drawable.settings_profile_inactive);
				} else {
					profileImg
							.setImageResource(R.drawable.settings_profile_active);
				}
				return false;
			}
		});

		bankBtn.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP
						|| event.getAction() == MotionEvent.ACTION_OUTSIDE
						|| event.getAction() == MotionEvent.ACTION_CANCEL) {
					bankImg.setImageResource(R.drawable.settings_bank_inactive);
				} else {
					bankImg.setImageResource(R.drawable.settings_bank_active);
				}
				return false;
			}
		});

		emailBtn.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP
						|| event.getAction() == MotionEvent.ACTION_OUTSIDE
						|| event.getAction() == MotionEvent.ACTION_CANCEL) {
					emailImg.setImageResource(R.drawable.settings_email_inactive);
				} else {
					emailImg.setImageResource(R.drawable.settings_email_active);
				}
				return false;
			}
		});

		socialBtn.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP
						|| event.getAction() == MotionEvent.ACTION_OUTSIDE
						|| event.getAction() == MotionEvent.ACTION_CANCEL) {
					socialImg
							.setImageResource(R.drawable.settings_social_inactive);
				} else {
					socialImg
							.setImageResource(R.drawable.settings_social_active);
				}
				return false;
			}
		});

		meCodeBtn.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP
						|| event.getAction() == MotionEvent.ACTION_OUTSIDE
						|| event.getAction() == MotionEvent.ACTION_CANCEL) {
					meCodeImg
							.setImageResource(R.drawable.settings_mecode_inactive);
				} else {
					meCodeImg
							.setImageResource(R.drawable.settings_mecode_active);
				}
				return false;
			}
		});

		notificationBtn.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP
						|| event.getAction() == MotionEvent.ACTION_OUTSIDE
						|| event.getAction() == MotionEvent.ACTION_CANCEL) {
					notificationImg
							.setImageResource(R.drawable.settings_preferences_inactive);
				} else {
					notificationImg
							.setImageResource(R.drawable.settings_preferences_active);
				}
				return false;
			}
		});

		sharingBtn.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP
						|| event.getAction() == MotionEvent.ACTION_OUTSIDE
						|| event.getAction() == MotionEvent.ACTION_CANCEL) {
					sharingImg
							.setImageResource(R.drawable.settings_sharing_inactive);
				} else {
					sharingImg
							.setImageResource(R.drawable.settings_sharing_active);
				}
				return false;
			}
		});

		securityBtn.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP
						|| event.getAction() == MotionEvent.ACTION_OUTSIDE
						|| event.getAction() == MotionEvent.ACTION_CANCEL) {
					securityImg
							.setImageResource(R.drawable.settings_security_inactive);
				} else {
					securityImg
							.setImageResource(R.drawable.settings_security_active);
				}
				return false;
			}
		});

		feedbackBtn.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP
						|| event.getAction() == MotionEvent.ACTION_OUTSIDE
						|| event.getAction() == MotionEvent.ACTION_CANCEL) {
					feedbackImg
							.setImageResource(R.drawable.settings_feedback_inactive);
				} else {
					feedbackImg
							.setImageResource(R.drawable.settings_feedback_active);
				}
				return false;
			}
		});

		helpBtn.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP
						|| event.getAction() == MotionEvent.ACTION_OUTSIDE
						|| event.getAction() == MotionEvent.ACTION_CANCEL) {
					helpImg.setImageResource(R.drawable.settings_help_inactive);
				} else {
					helpImg.setImageResource(R.drawable.settings_help_active);
				}
				return false;
			}
		});

		rateBtn.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP
						|| event.getAction() == MotionEvent.ACTION_OUTSIDE
						|| event.getAction() == MotionEvent.ACTION_CANCEL) {
					rateImg.setImageResource(R.drawable.settings_rate_inactive);
				} else {
					rateImg.setImageResource(R.drawable.settings_rate_active);
				}
				return false;
			}
		});

	}
}
