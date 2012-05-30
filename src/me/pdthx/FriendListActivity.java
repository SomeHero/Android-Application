package me.pdthx;

import java.util.ArrayList;
import com.zubhium.ZubhiumSDK;
import me.pdthx.Adapters.FriendAdapter;
import me.pdthx.Models.Friends;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

public final class FriendListActivity extends BaseActivity  {
	
	private ProgressDialog m_ProgressDialog = null; 
    private ArrayList<Friends> m_friends = null;
    private FriendAdapter m_adapter;
    private Runnable viewOrders;
    private ArrayList<Friends> friendList = new ArrayList<Friends>();
    
	ZubhiumSDK sdk ;
	private static final String TAG = "FriendListActivity";
	private ListView mListView = null;
	private TextView mEmptyTextView = null;
    

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    }
	

    

}
    
