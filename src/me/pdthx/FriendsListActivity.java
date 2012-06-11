package me.pdthx;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
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
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
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

				int first = view.getFirstVisiblePosition();
				int count = view.getChildCount();

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


				break;

			case SCROLL_STATE_TOUCH_SCROLL:

				break;

			case SCROLL_STATE_FLING :
				break;

			}

		}
	};

	public void fetchDrawableOnThread(final Friend friend, final ImageView imageView) {
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

}