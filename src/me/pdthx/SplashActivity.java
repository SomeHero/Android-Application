package me.pdthx;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.ImageView;

/**
 * This is the Splash activity which is loaded when the application is invoked
 */
public class SplashActivity extends Activity {
	// Set the display time, in milliseconds (or extract it out as a
	// configurable parameter)
	private boolean isActive = true;
	private final int SPLASH_DISPLAY_LENGTH = 4000;
	private AnimationDrawable frameAnimation;
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash);
		
		ImageView img = (ImageView) findViewById(R.id.SplashImageView);
		img.setVisibility(ImageView.VISIBLE);
		img.setBackgroundResource(R.anim.strtan);
		frameAnimation = (AnimationDrawable) img.getBackground();
		img.post(new Runnable() {
			@Override
			public void run(){
				frameAnimation.start();
			}
		});
		
		Thread splashThread = new Thread() {
			@Override
			public void run(){
				try{
					int waited = 0;
					while(isActive && waited < SPLASH_DISPLAY_LENGTH){
						sleep(100);
						if(isActive){
							waited+=100;
						}
					}
				} catch(InterruptedException e){
					
				} finally{
					finish();
					//overridePendingTransition(R.anim.mainfadein, R.anim.splashfadeout);
					startActivity(new Intent(getApplicationContext(), CustomTabActivity.class));
				}
			}
		};
		splashThread.start();
	}
		
	@Override
	public boolean onTouchEvent(MotionEvent event){
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			isActive = false;
		}
		return true;
	}

	/**@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(this);
		// Obtain the sharedPreference, default to true if not available
		boolean isSplashEnabled = sp.getBoolean("isSplashEnabled", true);
		
		if (isSplashEnabled) {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					// Finish the splash activity so it can't be returned to.
					//frameAnimation.start();  
					SplashActivity.this.finish();
					// Create an Intent that will start the main activity.
					Intent mainIntent = new Intent(SplashActivity.this,
							CustomTabActivity.class);
					SplashActivity.this.startActivity(mainIntent);
				}
			}, SPLASH_DISPLAY_LENGTH);
		} else {
			// if the splash is not enabled, then finish the activity
			// immediately and go to main.
			finish();
			Intent mainIntent = new Intent(SplashActivity.this,
					CustomTabActivity.class);
			SplashActivity.this.startActivity(mainIntent);
			
			SplashActivity.this.finish();
			overridePendingTransition(R.anim.mainfadein, R.anim.splashfadeout);
		}
	}**/
}
