package me.pdthx.Adapters;

import android.graphics.Bitmap;
import java.util.HashMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.LayoutInflater;
import me.pdthx.R;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.widget.ArrayAdapter;
import java.util.ArrayList;

public class ContactAdapter
    extends ArrayAdapter<String>
{
    private HashMap<String, Bitmap> images;

    public ContactAdapter(Context context, int textViewResourceId, ArrayList<String> objects)
    {
        super(context, textViewResourceId, objects);
        // TODO Auto-generated constructor stub
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.friend_item, null);
        }

        ImageView img = (ImageView) v.findViewById(R.id.imgTypeClosed);
        img.setImageBitmap(images.get("All Contacts"));

        return v;
    }

    public View getDropDownView(int position, View convertView, ViewGroup parent)
    {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.friend_item, null);
        }

        TextView txtContactType = (TextView) v.findViewById(R.id.txtContactType);
        ImageView imgContactType = (ImageView) v.findViewById(R.id.imgContactType);
        String contactType = getItem(position);

        txtContactType.setText(contactType);
        imgContactType.setImageBitmap(images.get(contactType));

        return v;
    }

}
