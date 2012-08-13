package me.pdthx.DoGood;

import android.app.ActivityGroup;
import me.pdthx.BaseActivity;
import me.pdthx.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

@SuppressWarnings("deprecation")
public class DoGoodIntroActivity extends BaseActivity {

    private boolean atIntroScreen = true;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dogood_intro);
		setupIntroController();
	}

	public void setupIntroController() {
		Button clickCause = (Button) findViewById(R.id.donate_clickCauseBtn);
		clickCause.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(DoGoodIntroActivity.this, DonateActivity.class);
				replaceContentView("intro", intent);
			}

		});
		Button clickPledge = (Button) findViewById(R.id.donate_clickPledgeBtn);
		clickPledge.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
			    Intent intent = new Intent(DoGoodIntroActivity.this, PledgeActivity.class);
			    replaceContentView("intro", intent);
			}

		});
	}

	public void replaceContentView(String id, Intent newIntent)
	{
	    View view = ((ActivityGroup) getParent()).getLocalActivityManager().startActivity(id, newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)).getDecorView();
	    setContentView(view);
	    atIntroScreen = false;
	}

    @Override
    public void onBackPressed()
    {
        if (atIntroScreen)
        {
            finish();
        }
        else {
            View view = ((ActivityGroup) getParent()).getLocalActivityManager().startActivity("Do Good", new Intent(DoGoodIntroActivity.this, DoGoodIntroActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)).getDecorView();
            setContentView(view);
            atIntroScreen = true;
        }

    }
}
