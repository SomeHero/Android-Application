package me.pdthx.Adapters;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import com.zubhium.utils.ZubhiumBase.InputStream;

import me.pdthx.R;
import me.pdthx.Models.Friends;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public final class FriendAdapter extends ArrayAdapter<Friends> {


	public FriendAdapter(Context context, int textViewResourceId,
			ArrayList<Friends> items) {
		super(context, textViewResourceId, items);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.friend_item, null);
		}
		Friends o = getItem(position);
		if (o != null) {
			TextView txtHeader = (TextView) v
					.findViewById(R.id.list_header_title);
			ImageView imgFriend = (ImageView) v.findViewById(R.id.imgFriend);
			TextView txtRecipientUri = (TextView) v
					.findViewById(R.id.txtRecipientUri);
			// TextView txtPhoneNumber =
			// (TextView)v.findViewById(R.id.txtPhoneNumber);
			// TextView txtEmail = (TextView) v.findViewById(R.id.txtEmail);
			txtHeader.setVisibility(View.GONE);

			if(imgFriend != null && o.type == "Facebook") {
				try {
					URL img_value = new URL("http://graph.facebook.com/" + o.id + "/picture");
					Bitmap mIcon = BitmapFactory.decodeStream(img_value.openConnection().getInputStream());
					imgFriend.setImageBitmap(mIcon);						//PULL FRIEND IMAGE FROM FB!!!!
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			if (txtRecipientUri != null) {
				txtRecipientUri.setText(o.getName());
			}
			// if(txtEmail != null){
			// txtAmount.setText(currencyFormatter.format(o.getAmount()));
			// }
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
}

