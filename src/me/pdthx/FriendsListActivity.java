package me.pdthx;

import java.util.Collections;

import me.pdthx.Adapters.FriendAdapter;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public final class FriendsListActivity extends BaseActivity  {
	
	private ProgressDialog m_ProgressDialog = null; 
    private FriendAdapter m_adapter;
    //private ArrayList<Friends> friendList = new ArrayList<Friends>();
	public static final String TAG = "FriendListActivity";
	private ListView mListView = null;
	private TextView mEmptyTextView = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	showFriendsController();
    }
	
	private void showFriendsController() {
		setContentView(R.layout.friend_controller);
		m_ProgressDialog = ProgressDialog.show(FriendsListActivity.this,    
	              "Please wait...", "Retrieving your friends...", true);
		
        mListView = (ListView) findViewById(R.id.lvFriends);
       
        mEmptyTextView = (TextView)findViewById(R.id.txtEmptyFriendList);
        
    	if(friendsList != null && friendsList.size() > 0) {
    		Collections.sort(friendsList);
        	mEmptyTextView.setVisibility(View.GONE);
        	m_adapter = new FriendAdapter(this, R.layout.friend_item, friendsList);
        	mListView.setAdapter(m_adapter);
            m_adapter.notifyDataSetChanged();
            
        }
    	else {
    		Log.e("Friend list problem", "Friend List not populated");
    		mEmptyTextView.setVisibility(View.VISIBLE);
    	}
        
        m_ProgressDialog.dismiss();
        
        
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				Intent data = new Intent();
				data.putExtra("index", arg2);
				
				setResult(RESULT_OK, data);
				finish();
			}
		});
		
       
    }
    
//    private Runnable returnRes = new Runnable() {
//
//        @Override
//        public void run() {
//            if(friendList != null && friendList.size() > 0) {	
//            	mEmptyTextView.setVisibility(View.GONE);
//                m_adapter.notifyDataSetChanged();
////                for(int i=0;i<friendList.size();i++)
////                	m_adapter.add(friendList.get(i));
//            }
//            m_ProgressDialog.dismiss();
//            m_adapter.notifyDataSetChanged();
//            
//            if(friendList.isEmpty())
//            	mEmptyTextView.setVisibility(View.VISIBLE);
//        }
//    };
//    private void getOrders()
//    {
////      try{     
////        	for(int x = 0; x < friendList.size(); x++)
////        	{    	
////              Friends o1 = new Friends();
////              o1.setName(friendList.get(x).getName());
////
////              //friendList.add(o1);
////          } 
////        	
////        } catch (Exception e) { 
////          Log.e("BACKGROUND_PROC", e.getMessage());
////        }
//    	runOnUiThread(returnRes);
//    }
}
    
