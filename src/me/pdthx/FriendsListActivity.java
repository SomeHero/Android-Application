package me.pdthx;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import me.pdthx.Adapters.FriendAdapter;
import me.pdthx.Models.Friend;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public final class FriendsListActivity extends BaseActivity  {

	private ProgressDialog m_ProgressDialog = null; 
	private FriendAdapter m_adapter;
	public static final String TAG = "FriendListActivity";
	private ListView mListView = null;
	private TextView mEmptyTextView = null;
	private final static int SETFRIENDIMAGE = 1;
	private static HashMap<String, Bitmap> pictureMap;
	private EditText searchBar = null;
	private ArrayList<Friend> contacts = new ArrayList<Friend>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		pictureMap = new HashMap<String, Bitmap>();

		showFriendsController();

	}

	private void showFriendsController() {
		setContentView(R.layout.friend_controller);
		m_ProgressDialog = ProgressDialog.show(FriendsListActivity.this,    
				"Please wait...", "Retrieving your friends...", true);

		mListView = (ListView) findViewById(R.id.lvFriends);
		mListView.setOnScrollListener(mOnScrollListener);

		mEmptyTextView = (TextView)findViewById(R.id.txtEmptyFriendList);

		if(friendsList != null && friendsList.size() > 0) {
			for(int x = 0; x < friendsList.size(); x++)
			{
				contacts.add(friendsList.get(x));
			}
			Collections.sort(friendsList, myComparator);
			mEmptyTextView.setVisibility(View.GONE);
			m_adapter = new FriendAdapter(this, R.layout.friend_item, friendsList);
			mListView.setAdapter(m_adapter);
			mListView.setFastScrollEnabled(true);
			m_adapter.notifyDataSetChanged();

		}
		else {
			Log.e("Friend list problem", "Friend List not populated");
			mEmptyTextView.setVisibility(View.VISIBLE);
		}

		m_ProgressDialog.dismiss();

		loadViewableImages(mListView.getFirstVisiblePosition(), mListView.getChildCount());


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

		searchBar = (EditText) findViewById(R.id.searchBar);
		searchBar.addTextChangedListener(new TextWatcher() {

			String current = "";

			@Override
			public void afterTextChanged(Editable s) {

				ArrayList<String>searched = new ArrayList<String>();
				current = s.toString();
				int currentLength = current.length();

				for (int x = 0; x < friendsList.size(); x++) {
					String name = friendsList.get(x).getName().toLowerCase();
//					if (s.charAt(currentLength - 1) == name.charAt(currentLength - 1)) {
//						searched.add(name);
//					}

					if (name.length() > currentLength) {
						int counter = 0;
						for (int y = 0; y < currentLength; y++) {
							char currentChar = current.charAt(y);
							char recipientChar = name.charAt(y);

							if (currentChar == recipientChar) {
								counter++;
							}
							if (counter == currentLength) {
								searched.add(name);
							}

						}
					}
				}

				ArrayList<Friend> tempList = new ArrayList<Friend>();
				tempList.addAll(friendsList);
				contacts.clear();
				for(int i = 0; i < searched.size(); i++)
				{
					for (int j = 0; j < friendsList.size(); j++)
					{
						if(tempList.get(j).getName().toLowerCase().equals(searched.get(i)))
						{															
							contacts.add(tempList.get(j));					
							break;
						}
					}
				}				
				if(searchBar.getText().toString().length() == 0)
				{
					m_adapter = new FriendAdapter(FriendsListActivity.this, R.layout.friend_item,
							friendsList);
					contacts.clear();
				}
				else
				{
					m_adapter = new FriendAdapter(FriendsListActivity.this, R.layout.friend_item,
							contacts);
				}
				mListView.setAdapter(m_adapter);

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) 
			{


			}

		});
	}

	private OnScrollListener mOnScrollListener = new OnScrollListener() {

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			// TODO Auto-generated method stub
			switch (scrollState) {

			case SCROLL_STATE_IDLE :

				loadViewableImages(view.getFirstVisiblePosition(), view.getChildCount());
				
				break;

			case SCROLL_STATE_TOUCH_SCROLL:

				break;

			case SCROLL_STATE_FLING :
				break;

			}

		}
	};

	private void loadViewableImages(int first, int count) {
		for (int i = 0; i < count; i++) {

			Friend friend = (Friend) mListView.getAdapter().getItem(i + first);
			ImageView imageView = (ImageView) mListView.getChildAt(i).findViewById(R.id.imgFriend);
			if (friend.getPicture() == null && friend.getType().equals("Facebook")) {
				if (!pictureMap.containsKey(friend.getId())) {
					Log.d("Retrieving Image for:", friend.getName());
					fetchDrawableOnThread(friend, imageView);
				}
				else {
					imageView.setImageBitmap(pictureMap.get(friend.getId()));
				}
			}

		}
	}

	private void fetchDrawableOnThread(final Friend friend, final ImageView imageView) {
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message message) {
				Bitmap image = (Bitmap) message.obj;

				friend.setPicture(image);
				pictureMap.put(friend.getId(), image);
				imageView.setImageBitmap(image);
				m_adapter.notifyDataSetChanged();
			}
		};

		Thread thread = new Thread() {
			public void run() {
				try {
					URL url = new URL("http://graph.facebook.com/" + friend.getId() + "/picture");
					Bitmap mIcon = BitmapFactory.decodeStream(url.openConnection().getInputStream());
					Message message = handler.obtainMessage(SETFRIENDIMAGE, mIcon);
					handler.sendMessage(message);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		};
		thread.start();
	}
	
	private Comparator<Friend> myComparator = new Comparator<Friend>() {

		@Override
		public int compare(Friend arg0, Friend arg1) {
			return arg0.getName().compareToIgnoreCase(arg1.getName());
		}
		
	};

}