package me.pdthx.Adapters;

import android.graphics.BitmapFactory;
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
    private HashMap<String, Bitmap> images = new HashMap<String, Bitmap>(5);
    private String selected = "";

    public ContactAdapter(Context context, int textViewResourceId, ArrayList<String> objects)
    {
        super(context, textViewResourceId, objects);
        // TODO Auto-generated constructor stub

        images.put("All Contacts", BitmapFactory.decodeResource(context.getResources(),
            R.drawable.icon_allcontacts));
        images.put("Phone Contacts", BitmapFactory.decodeResource(context.getResources(),
            R.drawable.icon_contacts));
        images.put("Facebook Contacts", BitmapFactory.decodeResource(context.getResources(),
            R.drawable.icon_facebook));
        images.put("Non-Profits", BitmapFactory.decodeResource(context.getResources(),
            R.drawable.icon_nonprofit));
        images.put("Public Directory", BitmapFactory.decodeResource(context.getResources(),
            R.drawable.icon_publicdirectory));
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.contacttype_closed, null);
        }

        ImageView img = (ImageView) v.findViewById(R.id.imgTypeClosed);
        img.setImageBitmap(images.get(selected));

        return v;
    }

    public View getDropDownView(int position, View convertView, ViewGroup parent)
    {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.contacttype_item, null);
        }

        TextView txtContactType = (TextView) v.findViewById(R.id.txtContactType);
        ImageView imgContactType = (ImageView) v.findViewById(R.id.imgContactType);
        String contactType = getItem(position);

        txtContactType.setText(contactType);
        imgContactType.setImageBitmap(images.get(contactType));

        return v;
    }

    public String getSelected()
    {
        return selected;
    }

    public void setSelected(String newSelected)
    {
        selected = newSelected;
    }

}
