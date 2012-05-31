package me.pdthx;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.android.FacebookError;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.zubhium.ZubhiumSDK;
import me.pdthx.Adapters.FriendAdapter;
import me.pdthx.Dialogs.OutgoingRequestDialog;
import me.pdthx.Models.Friends;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public final class FriendListActivity extends BaseActivity  {
	
	private ProgressDialog m_ProgressDialog = null; 
    private ArrayList<Friends> m_friends = null;
    private FriendAdapter m_adapter;
    private Runnable viewOrders;
    //private ArrayList<Friends> friendList = new ArrayList<Friends>();
    private Intent fbIntent;
	ZubhiumSDK sdk ;
	private static final String TAG = "FriendListActivity";
	private ListView mListView = null;
	private TextView mEmptyTextView = null;
    

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);

//    	getFriends();
    	
    	showFriendsController();
    }
	
//    private void getFriends() {
//    	mAsyncRunner.request("me/friends", new RequestListener(){
//
//			@Override
//			public void onComplete(String response, Object state) {
//																				            
//				try {
//					JSONObject json = new JSONObject(response);
//					JSONArray d = json.getJSONArray("data");
//					 int l = (d != null ? d.length() : 0);
//					  Log.d("Facebook-Example-Friends Request", "d.length(): " + l);
//					  for (int i=0; i<l; i++) 
//					  {
//						  JSONObject o = d.getJSONObject(i);
//						  String n = o.getString("name");
//						  String id = o.getString("id");
//						  Friends f = new Friends();
//						  f.id = id;										
//						  f.name = n;
//						  friendList.add(f);
//						  Log.d(f.name, f.id);	
//					    	
//						}
//					  
//					  //populateFriendList();
//					  
//				} catch (JSONException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();      
//	            }
//			}
//
//			@Override
//			public void onIOException(IOException e, Object state) {
//				e.printStackTrace();
//				// TODO Auto-generated method stub
//				
//			}
//
//			@Override
//			public void onFileNotFoundException(FileNotFoundException e,
//					Object state) {
//				e.printStackTrace();
//				// TODO Auto-generated method stub
//				
//			}
//
//			@Override
//			public void onMalformedURLException(MalformedURLException e,
//					Object state) {
//				e.printStackTrace();
//				// TODO Auto-generated method stub
//				
//			}
//
//			@Override
//			public void onFacebookError(FacebookError e, Object state) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//		});
//		
//	}
	private void showFriendsController() {
		setContentView(R.layout.friend_controller);
		m_ProgressDialog = ProgressDialog.show(FriendListActivity.this,    
	              "Please wait...", "Retrieving your friends...", true);
		
		m_friends = new ArrayList<Friends>();
        mListView = (ListView) findViewById(R.id.lvFriends);
       
        mEmptyTextView = (TextView)findViewById(R.id.txtEmptyFriendList);
        
    	if(friendList != null && friendList.size() > 0) {	
        	mEmptyTextView.setVisibility(View.GONE);
        	m_adapter = new FriendAdapter(this, R.layout.friend_item, friendList);
            mListView.setAdapter(m_adapter);
            m_adapter.notifyDataSetChanged();
        }
    	else {
    		Log.e("hi", "way to go dude");
    		//mEmptyTextView.setVisibility(View.VISIBLE);
    	}        	
        
        m_ProgressDialog.dismiss();
        
        
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				Friends ref = m_friends.get(arg2);
				// first determine type of transaction
				// 1) incoming or outgoing
				// 2) payment or request
				// then create intent on appropriate dialog class
				Intent temp = new Intent(getApplicationContext(), OutgoingRequestDialog.class);
				startActivity(temp);
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
    
