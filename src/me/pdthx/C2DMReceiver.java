package me.pdthx;

import me.pdthx.Requests.UserPushNotificationRequest;
import me.pdthx.Services.UserService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class C2DMReceiver extends BroadcastReceiver {

	private String userId = "";
	private String registrationId = "";
	private String deviceToken = "";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals("com.google.android.c2dm.intent.REGISTRATION")) {
	        handleRegistration(context, intent);
	    } else if (intent.getAction().equals("com.google.android.c2dm.intent.RECEIVE")) {
	        handleMessage(context, intent);
	    }
	}

	public void createNotification(Context context, String notificationTitleText, String mUserId, String transactionId) {
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.paidthx_icon,
				notificationTitleText, System.currentTimeMillis());
		// Hide the notification after its selected
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		Intent intent = new Intent(context, PaystreamActivity.class);
	    intent.putExtra("userId", mUserId);
	    intent.putExtra("transactionId", transactionId);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				intent, 0);
		notification.setLatestEventInfo(context, "Payment Details",
				notificationTitleText, pendingIntent);
		notificationManager.notify(0, notification);
	}


	private void handleMessage(Context context, Intent intent) {
		String notificationString = intent.getExtras().getString("notificationString");
	    String transactionId = intent.getExtras().getString("transactionId");
	    String mUserId = intent.getExtras().getString("userId");
	    createNotification(context, notificationString, mUserId, transactionId);

	}

	private void handleRegistration(Context context, Intent intent) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		registrationId = intent.getExtras().getString("registration_id");
		Log.d("Registration Got back from Google", registrationId);
		deviceToken = prefs.getString("deviceToken", "");
		userId = prefs.getString("userId", "");

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					UserPushNotificationRequest request = new UserPushNotificationRequest();
					request.RegistrationId = registrationId;
					request.DeviceToken = deviceToken;
					request.UserId = userId;
					UserService.registerForPush(request);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		});
		thread.start();

		//TODO Send this information to the server.
	}

}
