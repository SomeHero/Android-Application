package me.pdthx;

import me.pdthx.Adapters.ContactAdapter;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import android.app.ProgressDialog;
import android.provider.ContactsContract;
import android.net.Uri;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import me.pdthx.Adapters.FriendAdapter;
import me.pdthx.Helpers.PhoneNumberFormatter;
import me.pdthx.Models.Friend;
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

public final class FriendsListActivity extends BaseActivity implements OnScrollListener  {

    public static final String TAG = "FriendListActivity";

    private FriendAdapter m_adapter;
    private ContactAdapter selectContactAdapter;

    private ListView mListView = null;
    private TextView mEmptyTextView = null;
    private Spinner selectContactType;
    private EditText searchBar = null;

    private static HashMap<String, Bitmap> pictureMap;
    private ArrayList<String> contactTypes = new ArrayList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tracker.trackPageView("Recipient");
        setContentView(R.layout.friend_controller);
        progressDialog.setMessage("Loading contacts... please wait.");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        if (contactThread != null && contactThread.isAlive())
        {
            try
            {
                contactThread.join();
            }
            catch (InterruptedException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }


        pictureMap = new HashMap<String, Bitmap>();

        showFriendsController();

    }

    private void showFriendsController() {

        mListView = (ListView) findViewById(R.id.lvFriends);
        searchBar = (EditText) findViewById(R.id.searchBar);
        selectContactType = (Spinner) findViewById(R.id.contacttype_select);

        contactTypes.add("All Contacts");
        contactTypes.add("Phone Contacts");
        contactTypes.add("Facebook Contacts");

        selectContactAdapter = new ContactAdapter(this, R.layout.contacttype_closed, contactTypes);
        selectContactAdapter.setDropDownViewResource(R.layout.contacttype_item);
        selectContactAdapter.setSelected("All Contacts");

        selectContactType.setAdapter(selectContactAdapter);

        mListView.setOnScrollListener(this);

        mEmptyTextView = (TextView)findViewById(R.id.txtEmptyFriendList);

        if(allContacts.size() > 0) {
            Collections.sort(allContacts);
            mEmptyTextView.setVisibility(View.GONE);
            m_adapter = new FriendAdapter(this, R.layout.friend_item, allContacts);
            mListView.setAdapter(m_adapter);
            mListView.setFastScrollEnabled(true);
            m_adapter.notifyDataSetChanged();
        }
        else {
            Log.e("Friend list problem", "Friend List not populated");
            mEmptyTextView.setVisibility(View.VISIBLE);
        }

        progressDialog.dismiss();
        loadViewableImages(0, mListView.getChildCount());


        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                long arg3) {

                Intent data = new Intent();
                Friend chosenFriend = m_adapter.getItem(arg2);

                if (!chosenFriend.getId().equals(""))
                {
                    data.putExtra("id", chosenFriend.getId());
                }
                else
                {
                    data.putExtra("paypoint", chosenFriend.getPaypoints().get(0));
                }

                setResult(RESULT_OK, data);
                finish();
            }
        });

        selectContactType.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3)
            {
                if (arg2 == 0)
                {
                    m_adapter = new FriendAdapter(FriendsListActivity.this, R.layout.friend_item, allContacts);
                    selectContactAdapter.setSelected("All Contacts");
                }

                if (arg2 == 1)
                {
                    m_adapter = new FriendAdapter(FriendsListActivity.this, R.layout.friend_item, phoneContacts);
                    selectContactAdapter.setSelected("Phone Contacts");
                }

                if (arg2 == 2)
                {
                    m_adapter = new FriendAdapter(FriendsListActivity.this, R.layout.friend_item, facebookContacts);
                    selectContactAdapter.setSelected("Facebook Contacts");
                }

                ImageView img = (ImageView) selectContactType.findViewById(R.id.imgTypeClosed);
                img.setImageBitmap(selectContactAdapter.getImages().get(selectContactAdapter.getSelected()));
                mListView.setAdapter(m_adapter);

                if (m_adapter.isEmpty())
                {
                    mListView.setVisibility(View.GONE);
                    mEmptyTextView.setVisibility(View.VISIBLE);
                }
                else {
                    mListView.setVisibility(View.VISIBLE);
                    mEmptyTextView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0)
            {
                m_adapter = new FriendAdapter(FriendsListActivity.this, R.layout.friend_item, allContacts);
                mListView.setAdapter(m_adapter);
            }

        });

        searchBar.addTextChangedListener(new TextWatcher() {

            String current = "";

            @Override
            public void afterTextChanged(Editable s) {

                ArrayList<Friend> searched = new ArrayList<Friend>();
                ArrayList<Friend> list;
                String selected = selectContactAdapter.getSelected();

                if (selected.equals("All Contacts"))
                {
                    list = allContacts;
                }
                else if (selected.equals("Phone Contacts"))
                {
                    list = phoneContacts;
                }
                else if (selected.equals("Facebook Contacts"))
                {
                    list = facebookContacts;
                }
                else
                {
                    list = allContacts;
                }
            current = s.toString();

            for (int x = 0; x < list.size(); x++) {

                Friend friend = list.get(x);
                if (friend.masterSearch(current.toLowerCase())) {
                    searched.add(friend);
                }
            }

            if(searchBar.getText().toString().length() == 0)
            {
                m_adapter = new FriendAdapter(FriendsListActivity.this, R.layout.friend_item,
                    list);
            }
            else
            {
                if (searched.size() > 0)
                {
                    m_adapter = new FriendAdapter(FriendsListActivity.this, R.layout.friend_item,
                        searched);
                }
                else
                {
                    Log.d("No match found", "Maybe new person?");
                    Friend newContact = new Friend();

                    newContact.setName("'" + current + "' not found");
                    newContact.getPaypoints().add("Continue typing or check entry");

                    if (current.matches("[0-9()-]+"))
                    {
                        String phone = current.replaceAll("[^0-9]", "");
                        if (phone.length() == 10)
                        {
                            newContact.setName("New Phone Contact");
                            newContact.getPaypoints().set(0, PhoneNumberFormatter.formatNumber(phone));
                        }
                    }

                    if (current.contains("@") && current.contains("."))
                    {
                        newContact.setName("New Email Address");
                        newContact.getPaypoints().set(0, current);
                    }

                    if (current.charAt(0) == '$')
                    {
                        newContact.setName("New MeCode");
                        newContact.getPaypoints().set(0, current);
                    }

                    ArrayList<Friend> searchedContacts = new ArrayList<Friend>();
                    searchedContacts.add(newContact);
                    m_adapter = new FriendAdapter(FriendsListActivity.this, R.layout.friend_item, searchedContacts);
                }
            }
            mListView.setVisibility(View.VISIBLE);
            mEmptyTextView.setVisibility(View.GONE);
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

private void loadViewableImages(int first, int count) {
    for (int i = 0; i < count; i++) {

        Friend friend = (Friend) mListView.getAdapter().getItem(i + first);
        ImageView imageView = (ImageView) mListView.getChildAt(i).findViewById(R.id.imgFriend);
        if (friend.getPicture() == null) {
            Bitmap picture = pictureMap.get(friend.getId());
            if (picture == null) {
                if (friend.isFBContact()) {
                    fetchDrawableOnThread(friend, imageView);
                }
                else {
                    picture = loadContactPhoto(friend.getPictureUri());

                    if (picture != null)
                    {
                        friend.setPicture(picture);
                        pictureMap.put(friend.getId(), picture);
                        imageView.setImageBitmap(picture);
                    }
                    else {
                        imageView.setImageResource(R.drawable.avatar_unknown);
                    }
                }
            }
            else {
                imageView.setImageBitmap(picture);
            }
        }
        else {
            imageView.setImageBitmap(friend.getPicture());
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
                Message message = handler.obtainMessage(1, mIcon);
                handler.sendMessage(message);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    };
    thread.start();
}

public Bitmap loadContactPhoto(Uri uri) {
    InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(
        getContentResolver(), uri);
    if (input == null) {
        return null;
    }
    return BitmapFactory.decodeStream(input);
}

}