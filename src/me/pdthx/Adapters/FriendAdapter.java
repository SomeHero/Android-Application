package me.pdthx.Adapters;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import me.pdthx.R;
import me.pdthx.Models.Friend;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

public final class FriendAdapter extends ArrayAdapter<Friend> implements SectionIndexer {

    private HashMap<String, Integer> alphaIndexer;
    private String[] sections;

    public FriendAdapter(Context context, int textViewResourceId,
        ArrayList<Friend> items) {
        super(context, textViewResourceId, items);

        alphaIndexer = new HashMap<String, Integer>();
        int size = items.size();
        for (int i = size - 1; i >= 0; i--) {
            String element = items.get(i).getName();
            alphaIndexer.put(element.substring(0, 1).toUpperCase(), i);
        }

        Set<String> keys = alphaIndexer.keySet(); // set of letters ...sets
        // cannot be sorted...

        Iterator<String> it = keys.iterator();
        ArrayList<String> keyList = new ArrayList<String>(); // list can be
        // sorted

        while (it.hasNext()) {
            String key = it.next();
            keyList.add(key);
        }

        Collections.sort(keyList);

        sections = new String[keyList.size()]; // simple conversion to an
        // array of object
        keyList.toArray(sections);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.friend_item, null);
        }
        Friend o = getItem(position);
        if (o != null) {
            TextView txtHeader = (TextView) v
                .findViewById(R.id.list_header_title);
            ImageView imgFriend = (ImageView) v.findViewById(R.id.imgFriend);
            TextView txtRecipientUri = (TextView) v
                .findViewById(R.id.txtRecipientUri);
            TextView txtPaypoint = (TextView) v.findViewById(R.id.txtPaypoint);
            txtHeader.setVisibility(View.GONE);

            if (position == alphaIndexer.get(o.getName().substring(0, 1).toUpperCase())) {
                txtHeader.setText(o.getName().substring(0, 1));
                txtHeader.setVisibility(View.VISIBLE);
            }

            if(imgFriend != null) {

                if (o.getPicture() == null) {
                    if (o.isFBContact()) {
                        imgFriend.setImageResource(R.drawable.paidthx_icon);
                    }
                    else {
                        imgFriend.setImageResource(R.drawable.avatar_unknown);
                    }
                }
                else {
                    imgFriend.setImageBitmap(o.getPicture());
                }

            }

            txtRecipientUri.setText(o.getName());

            if (o.isFBContact())
            {
                txtPaypoint.setText("Facebook Contact");
            }
            else {
                txtPaypoint.setText(o.getPaypoint());
            }
        }
        Drawable drawableRow = v.getResources().getDrawable(
            R.drawable.transaction_row_background);
        Drawable drawableRowAlt = v.getResources().getDrawable(
            R.drawable.transaction_rowalt_background);
        if (position % 2 == 0)
            v.setBackgroundDrawable(drawableRow);
        else
            v.setBackgroundDrawable(drawableRowAlt);

        return v;
    }

    @Override
    public int getPositionForSection(int section) {
        String letter = sections[section];
        return alphaIndexer.get(letter);
    }

    @Override
    public int getSectionForPosition(int position) {
        Log.v("getSectionForPosition", "called");
        return 0;
    }

    @Override
    public Object[] getSections() {
        return sections;
    }
}

