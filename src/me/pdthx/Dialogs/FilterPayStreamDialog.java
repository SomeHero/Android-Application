package me.pdthx.Dialogs;

import me.pdthx.FilterPayStreamActivity;
import me.pdthx.R;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;

/**
 * @author Justin!
 *
 */
public class FilterPayStreamDialog extends Activity{
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.searchable);
		
		Bundle appData = getIntent().getBundleExtra(SearchManager.APP_DATA);
		handleIntent(getIntent());	
	}
	@Override
	protected void onNewIntent(Intent intent) {
	    setIntent(intent);
	    handleIntent(intent);
	}

	private void handleIntent(Intent intent) {
	    if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
	      String query = intent.getStringExtra(SearchManager.QUERY);
	      doMySearch(query);
	    }
	}
	private void doMySearch(String queary)
	{
		
	}
	
}
