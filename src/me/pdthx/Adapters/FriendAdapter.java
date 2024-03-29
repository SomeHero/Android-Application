package me.pdthx.Adapters;


import java.util.ArrayList;
import me.pdthx.R;
import me.pdthx.Models.Friends;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
			//ImageView imgFriend = (ImageView) v.findViewById(R.id.imgFriend);
			TextView txtRecipientUri = (TextView) v
					.findViewById(R.id.txtRecipientUri);
			// TextView txtPhoneNumber =
			// (TextView)v.findViewById(R.id.txtPhoneNumber);
			// TextView txtEmail = (TextView) v.findViewById(R.id.txtEmail);

			if (o.getType().equals("Facebook")) {
				//TODO: Do things here.
				txtHeader.setVisibility(View.VISIBLE);
			} else {
				txtHeader.setVisibility(View.GONE);
			}

//			 if(imgFriend != null) {
//				 imgFriend.setImageResource(resId)							//PULL FRIEND IMAGE FROM FB!!!!
//			 }
			 
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

