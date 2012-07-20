package me.pdthx.Adapters;

import java.io.IOException;
import java.util.ArrayList;
import me.pdthx.R;
import me.pdthx.DoGood.DoGoodInfoActivity;
import me.pdthx.Models.Organization;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class OrganizationAdapter extends ArrayAdapter<Organization> {
	protected static final int CHOSEN_ORG = 5;
	private ArrayList<Organization> items;
private Context ctx;
	public OrganizationAdapter(Context context, int textViewResourceId,
			ArrayList<Organization> items) {
		super(context, textViewResourceId, items);
		this.items = items;
		ctx = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			// v = vi.inflate(R.layout.transaction_item, null);
			v = vi.inflate(R.layout.dogood_org_item, null);
		}
		final Organization o = items.get(position);
		if (o != null) {
			// OLD:
			// TextView txtHeader = (TextView) v
			// .findViewById(R.id.list_header_title);
			// ImageView imgTransactionType = (ImageView) v
			// .findViewById(R.id.imgTransactionType);
			// TextView txtRecipientUri = (TextView) v
			// .findViewById(R.id.txtRecipientUri);
			// TextView txtPaymentDate = (TextView) v
			// .findViewById(R.id.txtPaymentDate);
			// ImageView imgStatus = (ImageView) v.findViewById(R.id.imgStatus);
			// TextView txtAmount = (TextView) v.findViewById(R.id.txtAmount);
			// DateFormat timeFormat = DateFormat.getTimeInstance();
			TextView txtHeader = (TextView) v
					.findViewById(R.id.org_item_header);
			TextView txtName = (TextView) v.findViewById(R.id.org_item_name);
			TextView txtSlogan = (TextView) v
					.findViewById(R.id.org_item_slogan);
			ImageView picture = (ImageView)v.findViewById(R.id.org_item_pic);
			ImageView txtInfo = (ImageView) v.findViewById(R.id.org_item_info);

			if (txtHeader != null) {
				if (o.getHeader().length() > 0) {
					txtHeader.setVisibility(View.VISIBLE);
					txtHeader.setText(o.getHeader());
					txtHeader.setBackgroundColor(Color.parseColor("#7A7A7A"));
				} else {
					txtHeader.setVisibility(View.GONE);
				}
			}

			if(picture != null)
			{
				if(o.getImageUri().length() > 0)
				{
					Uri url;
					try {
						url = Uri.parse(o.getImageUri());
						Bitmap bmp=BitmapFactory.decodeStream(ctx.getContentResolver().openInputStream(url));
						picture.setImageBitmap(bmp);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else
				{
					picture.setImageResource(R.drawable.avatar_unknown);
				}
			}

			if (txtName != null) {
				txtName.setText(o.getName());
			}

			if (txtSlogan != null) {
				txtSlogan.setText(o.getSlogan());
			}

			if (txtInfo != null) {
				if(o.getInfo().length() > 0)
				{
					txtInfo.setVisibility(View.VISIBLE);
					txtInfo.setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View v) {
							String name = o.getName();
							String slogan = o.getSlogan();
							String picUri = o.getImageUri();
							Intent ref = new Intent(ctx, DoGoodInfoActivity.class);
							ref.putExtra("name", name);
							ref.putExtra("slogan", slogan);
							ref.putExtra("pic", picUri);
							((Activity) ctx).startActivityForResult(ref, CHOSEN_ORG);
						}

					});
				}
				else
				{
					txtInfo.setVisibility(View.GONE);
				}
			}

		}
		v.setBackgroundColor(Color.parseColor("#ffffff"));
		return v;
	}
}
