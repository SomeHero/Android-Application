package me.pdthx.Adapters;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import me.pdthx.R;
import me.pdthx.Models.PaystreamTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public final class PaystreamAdapter extends ArrayAdapter<PaystreamTransaction> {

    private ArrayList<PaystreamTransaction> items;
    private NumberFormat currencyFormatter =  NumberFormat.getCurrencyInstance();
    private SharedPreferences prefs;
    
    public PaystreamAdapter(Context context, int textViewResourceId, ArrayList<PaystreamTransaction> items) {
            super(context, textViewResourceId, items);
            this.items = items;
            prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.transaction_item, null);
		}
		PaystreamTransaction o = items.get(position);
		if (o != null) {
			TextView txtHeader = (TextView) v
					.findViewById(R.id.list_header_title);
			ImageView imgTransactionType = (ImageView) v
					.findViewById(R.id.imgTransactionType);
			TextView txtRecipientUri = (TextView) v
					.findViewById(R.id.txtRecipientUri);
			TextView txtPaymentDate = (TextView) v
					.findViewById(R.id.txtPaymentDate);
			ImageView imgStatus = (ImageView) v.findViewById(R.id.imgStatus);
			TextView txtAmount = (TextView) v.findViewById(R.id.txtAmount);
			DateFormat timeFormat = DateFormat.getTimeInstance();

			if (o.getHeader().length() > 0) {
				txtHeader.setVisibility(View.VISIBLE);
				txtHeader.setText(o.getHeader());
				txtHeader
						.setBackgroundResource(R.drawable.paystream_header_background);
			} else {
				txtHeader.setVisibility(View.GONE);
			}

			 if(o.getDirection().equalsIgnoreCase("Out"))
             {
             	if(o.getTransactionType().equalsIgnoreCase("Payment"))
             		imgTransactionType.setImageResource(R.drawable.paystream_sent_icon);
             	else
             		imgTransactionType.setImageResource(R.drawable.paystream_request_sent_icon);
             }
             else
             {
             	if(o.getTransactionType().equalsIgnoreCase("Payment"))
             		imgTransactionType.setImageResource(R.drawable.paystream_received_icon);
             	else
             		imgTransactionType.setImageResource(R.drawable.paystream_request_received_icon);
             }
             if (txtRecipientUri != null) {
             	 if(o.getDirection().equalsIgnoreCase("Out"))
             		 txtRecipientUri.setText(o.getRecipientUri());  
	                else
	                	txtRecipientUri.setText(o.getSenderUri());
             }
             if(txtPaymentDate != null) {
             	txtPaymentDate.setText(timeFormat.format(o.getCreateDate()));
             }
                 if(imgStatus != null) {
                 	if(o.getTransactionStatus().toUpperCase() == "SUBMITTED") {
                 		imgStatus.setImageResource(R.drawable.transaction_pending_icon);
						} else if(o.getTransactionStatus().toUpperCase() == "PENDING") {
							imgStatus.setImageResource(R.drawable.transaction_pending_icon);
						} else if(o.getTransactionStatus().toUpperCase() == "COMPLETE") {
							imgStatus.setImageResource(R.drawable.transaction_complete_icon);
						}else {
							imgStatus.setImageResource(R.drawable.transaction_pending_icon);
						}
                 	//else if(o.getTransactionStatus().toUpperCase() == "FAILED") {
							//imgStatus.setImageResource(R.drawable.transaction_failed_icon);
						//}else if(o.getTransactionStatus().toUpperCase() == "RETURNED") {
						//	imgStatus.setImageResource(R.drawable.transaction_returned_icon);
						//} else if(o.getTransactionStatus().toUpperCase() == "CANCELLED") {
						//	imgStatus.setImageResource(R.drawable.transaction_cancelled_icon);
						//}
                 }
                 if(txtAmount != null){
                       txtAmount.setText(currencyFormatter.format(o.getAmount()));
                 }
         }
         Drawable drawableRow=v.getResources().getDrawable(R.drawable.transaction_row_background);
         Drawable drawableRowAlt = v.getResources().getDrawable(R.drawable.transaction_rowalt_background);
         if(position % 2 == 0)
         	v.setBackgroundDrawable(drawableRow);
         else
         	v.setBackgroundDrawable(drawableRowAlt);
         
         return v;
	}
}

