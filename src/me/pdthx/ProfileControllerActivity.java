package me.pdthx;

import me.pdthx.Requests.UserRequest;
import me.pdthx.Responses.UserResponse;
import me.pdthx.Services.UserService;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class ProfileControllerActivity extends BaseActivity {

	private LinearLayout firstNameBtn;
	private LinearLayout lastNameBtn;
	private LinearLayout phoneBtn;
	private LinearLayout emailBtn;
	private LinearLayout facebookBtn;
	private LinearLayout twitterBtn;
	private LinearLayout aboutMeBtn;
	private ToggleButton makePublicBtn;

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

	private boolean hasFirstName;
	private boolean hasLastName;
	private boolean hasPhoneNumber;
	private boolean hasEmail;
	private boolean isFacebookLinked;
	private boolean isTwitterLinked;
	private boolean hasAboutMe;
	private boolean isPublic;

	private boolean hasAddress;
	private boolean hasCity;
	private boolean hasZipCode;
	private boolean hasState;
	private boolean hasPhotoId;
	private boolean hasSSN;
	private boolean hasBirthday;
	private boolean hasGender;
	private boolean hasIncome;
	private boolean hasCredit;

	private Button backBtn;
	private String selectedState;

	private UserResponse userResponse;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		String userId = prefs.getString("userId", "");

		UserRequest userRequest = new UserRequest();
		userRequest.UserId = userId;

		userResponse = new UserResponse();
		userResponse = UserService.getUser(userRequest);

		showProfileController();
	}

	public void showProfileController() {
		setContentView(R.layout.profile_controller);

		firstNameBtn = (LinearLayout) findViewById(R.id.profile_firstNameBtn);
		lastNameBtn = (LinearLayout) findViewById(R.id.profile_lastNameBtn);
		phoneBtn = (LinearLayout) findViewById(R.id.profile_phoneNumberBtn);
		emailBtn = (LinearLayout) findViewById(R.id.profile_emailBtn);
		facebookBtn = (LinearLayout) findViewById(R.id.profile_facebookBtn);
		twitterBtn = (LinearLayout) findViewById(R.id.profile_twitterBtn);
		aboutMeBtn = (LinearLayout) findViewById(R.id.profile_aboutMeBtn);

		makePublicBtn = (ToggleButton) findViewById(R.id.makePublicProfileBtn);

		addressBtn = (LinearLayout) findViewById(R.id.profile_addressBtn);
		cityBtn = (LinearLayout) findViewById(R.id.profile_cityBtn);
		stateBtn = (LinearLayout) findViewById(R.id.profile_stateBtn);
		zipCodeBtn = (LinearLayout) findViewById(R.id.profile_zipBtn);
		photoIdBtn = (LinearLayout) findViewById(R.id.profile_photoIdBtn);
		ssnIdBtn = (LinearLayout) findViewById(R.id.profile_ssnIDBtn);
		birthdayBtn = (LinearLayout) findViewById(R.id.profile_birthdayBtn);
		genderBtn = (LinearLayout) findViewById(R.id.profile_genderBtn);
		incomeBtn = (LinearLayout) findViewById(R.id.profile_incomeBtn);
		creditBtn = (LinearLayout) findViewById(R.id.profile_creditBtn);

		enterUserData();
		addListeners();
	}

	public void addListeners() {
		if (firstNameBtn.isClickable()) {
			firstNameBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					showFirstNameDialog();
				}
			});
		}
		if (lastNameBtn.isClickable()) {
			lastNameBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					showLastNameDialog();
				}

			});
		}
		if (phoneBtn.isClickable()) {
			phoneBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					showPhoneDialog();
				}

			});
		}
		if (emailBtn.isClickable()) {
			emailBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					showEmailDialog();
				}

			});
		}
		if (facebookBtn.isClickable()) {
			facebookBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Toast.makeText(getApplicationContext(),
							"Facebook feature currently unavailible",
							Toast.LENGTH_SHORT);
				}

			});
		}
		if (twitterBtn.isClickable()) {
			twitterBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Toast.makeText(getApplicationContext(),
							"Twitter feature currently unavailible",
							Toast.LENGTH_SHORT);
				}

			});
		}
		if (aboutMeBtn.isClickable()) {
			aboutMeBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					showAboutMeDialog();
				}
			});
		}

		if (addressBtn.isClickable()) {
			addressBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					showAddressDialog();
				}

			});
		}

		if (cityBtn.isClickable()) {
			cityBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					showCityDialog();
				}

			});
		}

		if (stateBtn.isClickable()) {
			stateBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					showStateDialog();
				}

			});
		}

		if (zipCodeBtn.isClickable()) {
			zipCodeBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					showZipcodeDialog();
				}

			});
		}

		if (birthdayBtn.isClickable()) {
			birthdayBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					showBirthdayDialog();
				}

			});
		}

		if (genderBtn.isClickable()) {
			genderBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					showGenderDialog();
				}

			});
		}

	}

	public void showDialog() {
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.profile_dialog_input);
		dialog.setTitle("Title...");

		// set the custom dialog components - text, image and button
		TextView text = (TextView) dialog.findViewById(R.id.profileDialogBody);
		text.setText("This dialog body will change depending on the button clicked.");

		EditText input = (EditText) dialog
				.findViewById(R.id.profileDialogInput);
		input.setHint("A hint");

		Button dialogButton = (Button) dialog
				.findViewById(R.id.profileDialogBtn);
		// if button is clicked, close the custom dialog
		dialogButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.show();
	}

	public void showFirstNameDialog() {
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.profile_dialog_input);
		dialog.setTitle("First Name");

		// set the custom dialog components - text, image and button
		TextView text = (TextView) dialog.findViewById(R.id.profileDialogBody);
		text.setText("Enter your first name in the textbox below.");

		AutoCompleteTextView hide = (AutoCompleteTextView) dialog
				.findViewById(R.id.profileEnterState);
		hide.setVisibility(View.GONE);

		final EditText input = (EditText) dialog
				.findViewById(R.id.profileDialogInput);
		input.setHint("First Name");

		Button dialogButton = (Button) dialog
				.findViewById(R.id.profileDialogBtn);

		// if button is clicked, close the custom dialog
		dialogButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String userInput = input.getText().toString().trim();
				if (userInput != null || userInput.length() > 0 || userInput.length() > 0) {
					TextView firstName = (TextView) firstNameBtn
							.findViewById(R.id.firstNameProfile);
					firstName.setText(userInput);
					firstName.setTextColor(Color.parseColor("#9C9C9C"));
					firstName.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
					TextView pts = (TextView) firstNameBtn
							.findViewById(R.id.firstNameProfile_Pts);
					pts.setVisibility(View.GONE);
					firstNameBtn.setClickable(false);
					runOnUiThread(new Runnable() {
						public void run() {
							firstNameBtn.invalidate();
						}
					});
				}
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	public void showLastNameDialog() {
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.profile_dialog_input);
		dialog.setTitle("Last Name");

		// set the custom dialog components - text, image and button
		TextView text = (TextView) dialog.findViewById(R.id.profileDialogBody);
		text.setText("Enter your last name in the textbox below.");

		final EditText input = (EditText) dialog
				.findViewById(R.id.profileDialogInput);
		input.setHint("Last Name");

		Button dialogButton = (Button) dialog
				.findViewById(R.id.profileDialogBtn);
		// if button is clicked, close the custom dialog
		dialogButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String userInput = input.getText().toString().trim();
				if (userInput != null || userInput.length() > 0) {
					TextView lastName = (TextView) lastNameBtn
							.findViewById(R.id.lastNameProfile);
					lastName.setText(userInput);
					lastName.setTextColor(Color.parseColor("#9C9C9C"));
					lastName.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
					TextView pts = (TextView) lastNameBtn
							.findViewById(R.id.lastNameProfile_Pts);
					pts.setVisibility(View.GONE);
					lastNameBtn.setClickable(false);
					runOnUiThread(new Runnable() {
						public void run() {
							lastNameBtn.invalidate();
						}
					});
				}
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	public void showEmailDialog() {
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.profile_dialog_input);
		dialog.setTitle("Email Address");

		// set the custom dialog components - text, image and button
		TextView text = (TextView) dialog.findViewById(R.id.profileDialogBody);
		text.setText("Enter your email address in the textbox below.");

		AutoCompleteTextView hide = (AutoCompleteTextView) dialog
				.findViewById(R.id.profileEnterState);
		hide.setVisibility(View.GONE);
		LinearLayout genderHide = (LinearLayout) dialog
				.findViewById(R.id.profileEnterGender);
		genderHide.setVisibility(View.GONE);

		final EditText input = (EditText) dialog
				.findViewById(R.id.profileDialogInput);
		input.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS);
		input.setHint("Email Address");

		Button dialogButton = (Button) dialog
				.findViewById(R.id.profileDialogBtn);
		// if button is clicked, close the custom dialog
		dialogButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String userInput = input.getText().toString().trim();
				if (userInput != null || userInput.length() > 0) {
					TextView email = (TextView) emailBtn
							.findViewById(R.id.emailProfile);
					email.setText(userInput);
					email.setTextColor(Color.parseColor("#9C9C9C"));
					email.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
					TextView pts = (TextView) emailBtn
							.findViewById(R.id.emailProfile_Pts);
					pts.setVisibility(View.GONE);
					emailBtn.setClickable(false);
					runOnUiThread(new Runnable() {
						public void run() {
							emailBtn.invalidate();
						}
					});
				}
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	public void showPhoneDialog() {
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.profile_dialog_input);
		dialog.setTitle("Phone Number");

		// set the custom dialog components - text, image and button
		TextView text = (TextView) dialog.findViewById(R.id.profileDialogBody);
		text.setText("Enter your phone number in the textbox below.");

		AutoCompleteTextView hide = (AutoCompleteTextView) dialog
				.findViewById(R.id.profileEnterState);
		hide.setVisibility(View.GONE);

		LinearLayout genderHide = (LinearLayout) dialog
				.findViewById(R.id.profileEnterGender);
		genderHide.setVisibility(View.GONE);

		final EditText input = (EditText) dialog
				.findViewById(R.id.profileDialogInput);
		input.setHint("Phone #");
		input.setInputType(InputType.TYPE_CLASS_PHONE);

		Button dialogButton = (Button) dialog
				.findViewById(R.id.profileDialogBtn);
		// if button is clicked, close the custom dialog
		dialogButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String userInput = input.getText().toString().trim();
				if (userInput != null || userInput.length() > 0) {
					TextView phone = (TextView) phoneBtn
							.findViewById(R.id.phoneNumberProfile);
					phone.setText(userInput);
					phone.setTextColor(Color.parseColor("#9C9C9C"));
					phone.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
					TextView pts = (TextView) phoneBtn
							.findViewById(R.id.phoneNumberProfile_Pts);
					pts.setVisibility(View.GONE);
					phoneBtn.setClickable(false);
					runOnUiThread(new Runnable() {
						public void run() {
							phoneBtn.invalidate();
						}
					});
				}
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	public void showAboutMeDialog() {
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.profile_dialog_input);
		dialog.setTitle("About Me");

		// set the custom dialog components - text, image and button
		TextView text = (TextView) dialog.findViewById(R.id.profileDialogBody);
		text.setText("Enter a small paragraph about yourself.");

		AutoCompleteTextView hide = (AutoCompleteTextView) dialog
				.findViewById(R.id.profileEnterState);
		hide.setVisibility(View.GONE);

		LinearLayout genderHide = (LinearLayout) dialog
				.findViewById(R.id.profileEnterGender);
		genderHide.setVisibility(View.GONE);

		final EditText input = (EditText) dialog
				.findViewById(R.id.profileDialogInput);

		Button dialogButton = (Button) dialog
				.findViewById(R.id.profileDialogBtn);
		// if button is clicked, close the custom dialog
		dialogButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String userInput = input.getText().toString().trim();
				if (userInput != null || userInput.length() > 0) {
					TextView about = (TextView) aboutMeBtn
							.findViewById(R.id.aboutMeProfile);
					about.setText(userInput);
					about.setTextColor(Color.parseColor("#9C9C9C"));
					about.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
					TextView pts = (TextView) aboutMeBtn
							.findViewById(R.id.aboutMeProfile_Pts);
					pts.setVisibility(View.GONE);
					aboutMeBtn.setClickable(false);
					runOnUiThread(new Runnable() {
						public void run() {
							aboutMeBtn.invalidate();
						}
					});
				}
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	public void showAddressDialog() {
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.profile_dialog_input);
		dialog.setTitle("Address");

		// set the custom dialog components - text, image and button
		TextView text = (TextView) dialog.findViewById(R.id.profileDialogBody);
		text.setText("Enter your address below.");

		AutoCompleteTextView hide = (AutoCompleteTextView) dialog
				.findViewById(R.id.profileEnterState);
		hide.setVisibility(View.GONE);
		LinearLayout genderHide = (LinearLayout) dialog
				.findViewById(R.id.profileEnterGender);
		genderHide.setVisibility(View.GONE);

		final EditText input = (EditText) dialog
				.findViewById(R.id.profileDialogInput);

		Button dialogButton = (Button) dialog
				.findViewById(R.id.profileDialogBtn);
		// if button is clicked, close the custom dialog
		dialogButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String userInput = input.getText().toString().trim();
				if (userInput != null || userInput.length() > 0) {
					TextView address = (TextView) addressBtn
							.findViewById(R.id.addressProfile);
					address.setText(userInput);
					address.setTextColor(Color.parseColor("#9C9C9C"));
					address.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
					TextView pts = (TextView) addressBtn
							.findViewById(R.id.addressProfile_Pts);
					pts.setVisibility(View.GONE);
					addressBtn.setClickable(false);
					runOnUiThread(new Runnable() {
						public void run() {
							addressBtn.invalidate();
						}
					});
				}
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	public void showCityDialog() {
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.profile_dialog_input);
		dialog.setTitle("City");

		AutoCompleteTextView hide = (AutoCompleteTextView) dialog
				.findViewById(R.id.profileEnterState);
		hide.setVisibility(View.GONE);

		LinearLayout genderHide = (LinearLayout) dialog
				.findViewById(R.id.profileEnterGender);
		genderHide.setVisibility(View.GONE);
		// set the custom dialog components - text, image and button
		TextView text = (TextView) dialog.findViewById(R.id.profileDialogBody);
		text.setText("Enter the city corresponding to your address below.");

		final EditText input = (EditText) dialog
				.findViewById(R.id.profileDialogInput);

		Button dialogButton = (Button) dialog
				.findViewById(R.id.profileDialogBtn);
		// if button is clicked, close the custom dialog
		dialogButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String userInput = input.getText().toString().trim();
				if (userInput != null || userInput.length() > 0) {
					TextView city = (TextView) cityBtn
							.findViewById(R.id.cityProfile);
					city.setText(userInput);
					city.setTextColor(Color.parseColor("#9C9C9C"));
					city.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
					TextView pts = (TextView) cityBtn
							.findViewById(R.id.cityProfile_Pts);
					pts.setVisibility(View.GONE);
					cityBtn.setClickable(false);
					runOnUiThread(new Runnable() {
						public void run() {
							cityBtn.invalidate();
						}
					});
				}
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	public void showStateDialog() {
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.profile_dialog_input);
		dialog.setTitle("State");

		// set the custom dialog components - text, image and button
		TextView text = (TextView) dialog.findViewById(R.id.profileDialogBody);
		text.setText("Enter the state address below.");

		final EditText input = (EditText) dialog
				.findViewById(R.id.profileDialogInput);
		input.setVisibility(View.GONE);

		LinearLayout genderHide = (LinearLayout) dialog
				.findViewById(R.id.profileEnterGender);
		genderHide.setVisibility(View.GONE);

		final AutoCompleteTextView listofStates = (AutoCompleteTextView) dialog
				.findViewById(R.id.profileEnterState);
		final String[] STATES = new String[] { "Alabama", "Alaska", "Arizona",
				"Arkansas", "California", "Colorado", "Connecticut",
				"Delaware", "Florida", "Georgia", "Hawaii", "Idaho",
				"Illinois", "Indiana", "Iowa", "Kansas", "Kentucky",
				"Louisana", "Maine", "Maryland", "Massachusetts", "Michigan",
				"Minnesota", "Mississippi", "Missouri", "Montana", "Nebraska",
				"Nevada", "New Hampshire", "New Jersey", "New Mexico",
				"New York", "North Carolina", "North Dakota", "Ohio",
				"Oklahoma", "Oregon", "Pennyslvania", "Rhode Island",
				"South Carolina", "South Dakota", "Tennessee", "Texas", "Utah",
				"Vermont", "Virginia", "Washington", "West Virginia",
				"Wisconsin", "Wyoming" };

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, STATES);
		listofStates.setAdapter(adapter);

		Button dialogButton = (Button) dialog
				.findViewById(R.id.profileDialogBtn);
		// if button is clicked, close the custom dialog
		dialogButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (listofStates.getText().toString().trim() != null
						|| listofStates.getText().toString().trim().length() > 0) {
					TextView email = (TextView) stateBtn
							.findViewById(R.id.stateProfile);
					email.setText(listofStates.getText().toString().trim());
					email.setTextColor(Color.parseColor("#9C9C9C"));
					email.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
					TextView pts = (TextView) stateBtn
							.findViewById(R.id.stateProfile_Pts);
					pts.setVisibility(View.GONE);
					stateBtn.setClickable(false);
					runOnUiThread(new Runnable() {
						public void run() {
							stateBtn.invalidate();
						}
					});
				}
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	public void showZipcodeDialog() {
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.profile_dialog_input);
		dialog.setTitle("Zipcode");
		AutoCompleteTextView hide = (AutoCompleteTextView) dialog
				.findViewById(R.id.profileEnterState);
		hide.setVisibility(View.GONE);

		LinearLayout genderHide = (LinearLayout) dialog
				.findViewById(R.id.profileEnterGender);
		genderHide.setVisibility(View.GONE);

		// set the custom dialog components - text, image and button
		TextView text = (TextView) dialog.findViewById(R.id.profileDialogBody);

		text.setInputType(InputType.TYPE_CLASS_NUMBER);
		text.setText("Enter your zipcode below.");

		final EditText input = (EditText) dialog
				.findViewById(R.id.profileDialogInput);
		Button dialogButton = (Button) dialog
				.findViewById(R.id.profileDialogBtn);
		// if button is clicked, close the custom dialog
		dialogButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String userInput = input.getText().toString().trim();
				if (userInput != null || userInput.length() > 0) {
					TextView zipcode = (TextView) zipCodeBtn
							.findViewById(R.id.zipProfile);
					zipcode.setText(userInput);
					zipcode.setTextColor(Color.parseColor("#9C9C9C"));
					zipcode.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
					TextView pts = (TextView) zipCodeBtn
							.findViewById(R.id.zipProfile_Pts);
					pts.setVisibility(View.GONE);
					zipCodeBtn.setClickable(false);
					runOnUiThread(new Runnable() {
						public void run() {
							zipCodeBtn.invalidate();
						}
					});
				}
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	// social security number

	public void showBirthdayDialog() {
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.profile_dialog_input);
		dialog.setTitle("Birthday");
		AutoCompleteTextView hide = (AutoCompleteTextView) dialog
				.findViewById(R.id.profileEnterState);
		hide.setVisibility(View.GONE);

		LinearLayout genderHide = (LinearLayout) dialog
				.findViewById(R.id.profileEnterGender);
		genderHide.setVisibility(View.GONE);
		// set the custom dialog components - text, image and button
		TextView text = (TextView) dialog.findViewById(R.id.profileDialogBody);
		text.setText("Enter your birthday below.");

		final EditText input = (EditText) dialog
				.findViewById(R.id.profileDialogInput);
		input.setInputType(InputType.TYPE_DATETIME_VARIATION_DATE);

		Button dialogButton = (Button) dialog
				.findViewById(R.id.profileDialogBtn);
		// if button is clicked, close the custom dialog
		dialogButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String userInput = input.getText().toString().trim();
				if (userInput != null || userInput.length() > 0) {
					TextView birthday = (TextView) birthdayBtn
							.findViewById(R.id.birthdayProfile);
					birthday.setText(userInput);
					birthday.setTextColor(Color.parseColor("#9C9C9C"));
					birthday.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
					TextView pts = (TextView) birthdayBtn
							.findViewById(R.id.birthdayProfile_Pts);
					pts.setVisibility(View.GONE);
					birthdayBtn.setClickable(false);
					runOnUiThread(new Runnable() {
						public void run() {
							birthdayBtn.invalidate();
						}
					});
				}
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	public void showGenderDialog() {
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.profile_dialog_input);
		dialog.setTitle("Birthday");
		AutoCompleteTextView hide = (AutoCompleteTextView) dialog
				.findViewById(R.id.profileEnterState);
		hide.setVisibility(View.GONE);

		EditText hide2 = (EditText) dialog
				.findViewById(R.id.profileDialogInput);
		hide2.setVisibility(View.GONE);
		// set the custom dialog components - text, image and button
		TextView text = (TextView) dialog.findViewById(R.id.profileDialogBody);
		text.setText("Enter your birthday below.");

		Button dialogButton = (Button) dialog
				.findViewById(R.id.profileDialogBtn);
		dialogButton.setVisibility(View.GONE);

		LinearLayout maleBtn = (LinearLayout) dialog.findViewById(R.id.profileDialogMale);
		maleBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				TextView gender = (TextView) genderBtn
						.findViewById(R.id.genderProfile);
				gender.setText("Male");
				gender.setTextColor(Color.parseColor("#9C9C9C"));
				gender.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
				TextView pts = (TextView)genderBtn
						.findViewById(R.id.genderProfile_Pts);
				pts.setVisibility(View.GONE);
				genderBtn.setClickable(false);
				runOnUiThread(new Runnable() {
					public void run() {
						genderBtn.invalidate();
					}
				});
				dialog.dismiss();
			}
		});

		LinearLayout femaleBtn = (LinearLayout) dialog.findViewById(R.id.profileDialogFemale);
		femaleBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				TextView gender = (TextView) genderBtn
						.findViewById(R.id.genderProfile);
				gender.setText("Female");
				gender.setTextColor(Color.parseColor("#9C9C9C"));
				gender.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
				TextView pts = (TextView)genderBtn
						.findViewById(R.id.genderProfile_Pts);
				pts.setVisibility(View.GONE);
				genderBtn.setClickable(false);
				runOnUiThread(new Runnable() {
					public void run() {
						genderBtn.invalidate();
					}
				});
				dialog.dismiss();
			}
		});

		dialog.show();
	}

	// income <-- diff. layout, have list of options

	// credit ?

	// pre-enter all user data that user has already inserted into the
	// profile fields.
	public void enterUserData() {
		if (!userResponse.FirstName.equals("null")) {
			TextView firstName = (TextView) firstNameBtn
					.findViewById(R.id.firstNameProfile);
			firstName.setText(userResponse.FirstName);
			firstName.setTextColor(Color.parseColor("#9C9C9C"));
			firstName.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
			TextView pts = (TextView) firstNameBtn
					.findViewById(R.id.firstNameProfile_Pts);
			pts.setVisibility(View.GONE);
			firstNameBtn.setClickable(false);
		}
		if (!userResponse.LastName.equals("null")) {
			TextView lastName = (TextView) lastNameBtn
					.findViewById(R.id.lastNameProfile);
			lastName.setText(userResponse.LastName);
			lastName.setTextColor(Color.parseColor("#9C9C9C"));
			lastName.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
			TextView pts = (TextView) lastNameBtn
					.findViewById(R.id.lastNameProfile_Pts);
			pts.setVisibility(View.GONE);
			lastNameBtn.setClickable(false);
		}

		if (!userResponse.MobileNumber.equals("null")) {
			TextView phoneNumber = (TextView) phoneBtn
					.findViewById(R.id.phoneNumberProfile);
			phoneNumber.setText(userResponse.MobileNumber);
			phoneNumber.setTextColor(Color.parseColor("#9C9C9C"));
			phoneNumber.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
			TextView pts = (TextView) phoneBtn
					.findViewById(R.id.phoneNumberProfile_Pts);
			pts.setVisibility(View.GONE);
			phoneBtn.setClickable(false);
		}

		if (!userResponse.EmailAddress.equals("null")) {
			TextView emailAddress = (TextView) emailBtn
					.findViewById(R.id.emailProfile);
			emailAddress.setText(userResponse.MobileNumber);
			emailAddress.setTextColor(Color.parseColor("#9C9C9C"));
			emailAddress.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
			TextView pts = (TextView) emailBtn
					.findViewById(R.id.emailProfile_Pts);
			pts.setVisibility(View.GONE);
			emailBtn.setClickable(false);
		}
		// facebook
		// twitter
		// about me
		// make public
	}
}
