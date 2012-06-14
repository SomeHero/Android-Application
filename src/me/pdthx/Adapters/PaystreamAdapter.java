package me.pdthx.Adapters;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import me.pdthx.R;
import me.pdthx.Models.Friend;
import me.pdthx.Models.PaystreamTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public final class PaystreamAdapter extends ArrayAdapter<PaystreamTransaction> {

	private ArrayList<PaystreamTransaction> items;
	private NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
	private SharedPreferences prefs;

	public PaystreamAdapter(Context context, int textViewResourceId,
			ArrayList<PaystreamTransaction> items) {
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
			// v = vi.inflate(R.layout.transaction_item, null);
			v = vi.inflate(R.layout.paystream_nitem, null);
		}
		PaystreamTransaction o = items.get(position);
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
			DateFormat timeFormat = DateFormat.getTimeInstance();
			TextView txtHeader = (TextView) v
					.findViewById(R.id.paystreamHeaderTitle);
			TextView recipientUri = (TextView) v
					.findViewById(R.id.paystreamName);
			TextView transactionType = (TextView) v
					.findViewById(R.id.paystreamType);
			TextView transactionComment = (TextView) v
					.findViewById(R.id.paystreamComment);
			TextView transactionTime = (TextView) v
					.findViewById(R.id.paystreamTime);
			TextView transactionAmount = (TextView) v
					.findViewById(R.id.paystreamAmount);
			TextView transactionStatus = (TextView) v
					.findViewById(R.id.paystreamStatus);

			ImageView recipientUserImg = (ImageView) v
					.findViewById(R.id.paystreamUserImg);

			if (recipientUserImg != null) {
				URL url;
				try {
					if (o.getImageUri().length() != 0
							&& !o.getImageUri().equals("")) {
						url = new URL(o.getImageUri());
						Bitmap mIcon = BitmapFactory.decodeStream(url
								.openConnection().getInputStream());
						recipientUserImg.setImageBitmap(mIcon);
					} else {
						recipientUserImg
								.setImageResource(R.drawable.avatar_unknown);
					}
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (o.getHeader().length() > 0) {
				txtHeader.setVisibility(View.VISIBLE);
				txtHeader.setText(o.getHeader());
				txtHeader
						.setBackgroundResource(R.drawable.paystream_header_background);
			} else {
				txtHeader.setVisibility(View.GONE);
			}

			if (o.getDirection().equalsIgnoreCase("Out")) {
				if (o.getTransactionType().equalsIgnoreCase("Payment")) {
					transactionType.setTextColor(Color.parseColor("#269bb8"));
					transactionType.setText("You sent money to them");
				} else {
					transactionType.setTextColor(Color.parseColor("#81b28e"));
					transactionType.setText("You requested money from them");
				}
			} else {
				if (o.getTransactionType().equalsIgnoreCase("Payment")) {
					transactionType.setTextColor(Color.parseColor("#1c8839"));
					transactionType.setText("Sent money to you");
				} else {
					transactionType.setTextColor(Color.parseColor("#86acb5"));
					transactionType.setText("Requested money from you");
				}
			}

			// OLD:
			// if(o.getDirection().equalsIgnoreCase("Out"))
			// {
			// if(o.getTransactionType().equalsIgnoreCase("Payment"))
			// imgTransactionType.setImageResource(R.drawable.paystream_sent_icon);
			// else
			// imgTransactionType.setImageResource(R.drawable.paystream_request_sent_icon);
			// }
			// else
			// {
			// if(o.getTransactionType().equalsIgnoreCase("Payment"))
			// imgTransactionType.setImageResource(R.drawable.paystream_received_icon);
			// else
			// imgTransactionType.setImageResource(R.drawable.paystream_request_received_icon);
			// }
			if (recipientUri != null) {
				if (o.getDirection().equalsIgnoreCase("Out")) {
					String name = o.getRecipientUri();
					if (name.length() > 15) {
						String trailoff = "";
						for (int i = 0; i < 14; i++) {
							trailoff = trailoff + name.charAt(i);
						}
						trailoff = trailoff + "...";
						recipientUri.setText(trailoff);
					} else {
						recipientUri.setText(name);
					}
				} else {
					String name = o.getSenderUri();
					if (name.length() > 15) {
						String trailoff = "";
						for (int i = 0; i < 14; i++) {
							trailoff = trailoff + name.charAt(i);
						}
						trailoff = trailoff + "...";
						recipientUri.setText(trailoff);
					} else {
						recipientUri.setText(name);
					}
				}
			}

			// OLD:
			// if (txtRecipientUri != null) {
			// if(o.getDirection().equalsIgnoreCase("Out"))
			// txtRecipientUri.setText(o.getRecipientUri());
			// else
			// txtRecipientUri.setText(o.getSenderUri());
			// }

			// FIX createDate = time
			// header contains date
			if (transactionTime != null) {
				int minutesAgo;
				int hoursAgo;
				int secondsAgo;
				String currentDay = "";
				// today
				Calendar c = Calendar.getInstance();
				if (o.getCreateDate().getDate() == c.get(Calendar.DATE)) {

					hoursAgo = c.get(Calendar.HOUR_OF_DAY)
							- o.getCreateDate().getHours();
					if (hoursAgo > 0) {
						if (hoursAgo == 1) {
							currentDay = currentDay + hoursAgo + " hr. ";
						} else {
							currentDay = currentDay + hoursAgo + " hrs. ";
						}
					} else {
						minutesAgo = c.get(Calendar.MINUTE)
								- o.getCreateDate().getMinutes();
						if (minutesAgo > 0) {
							currentDay = currentDay + minutesAgo + " min. ";
						} else {
							// only use seconds if minutes and hours do not
							// apply
							secondsAgo = c.get(Calendar.SECOND)
									- o.getCreateDate().getSeconds();
							currentDay = currentDay + secondsAgo + "seconds";

						}
					}
					currentDay = currentDay + "ago";
					transactionTime.setText(currentDay);

				} else // else should just be date and time
				{
					SimpleDateFormat format = new SimpleDateFormat(
							"MM/dd/yy @ \n hh:mm");
					if (o.getCreateDate().getHours() < 12) {
						transactionTime
								.setText(format.format(o.getCreateDate())
										+ "AM");
					} else {
						transactionTime
								.setText(format.format(o.getCreateDate())
										+ "PM");
					}
				}
			}

			// OLD:
			// if(txtPaymentDate != null) {
			// txtPaymentDate.setText(timeFormat.format(o.getCreateDate()));
			// }

			if (transactionStatus != null) {
				transactionStatus.setText(o.getTransactionStatus());

			}

			// if(imgStatus != null) {
			// if(o.getTransactionStatus().toUpperCase() == "SUBMITTED") {
			// imgStatus.setImageResource(R.drawable.transaction_pending_icon);
			// } else if(o.getTransactionStatus().toUpperCase() == "PENDING") {
			// imgStatus.setImageResource(R.drawable.transaction_pending_icon);
			// } else if(o.getTransactionStatus().toUpperCase() == "COMPLETE") {
			// imgStatus.setImageResource(R.drawable.transaction_complete_icon);
			// }else {
			// imgStatus.setImageResource(R.drawable.transaction_pending_icon);
			// }

			// future use?
			// else if(o.getTransactionStatus().toUpperCase() == "FAILED") {
			// imgStatus.setImageResource(R.drawable.transaction_failed_icon);
			// }else if(o.getTransactionStatus().toUpperCase() == "RETURNED") {
			// imgStatus.setImageResource(R.drawable.transaction_returned_icon);
			// } else if(o.getTransactionStatus().toUpperCase() == "CANCELLED")
			// {
			// imgStatus.setImageResource(R.drawable.transaction_cancelled_icon);
			// }
			// }

			if (transactionAmount != null) {
				if (o.getDirection().equalsIgnoreCase("Out")) {
					if (o.getTransactionType().equalsIgnoreCase("Payment")) {
						transactionAmount.setTextColor(Color
								.parseColor("#269bb8"));
						transactionAmount.setText("-"
								+ currencyFormatter.format(o.getAmount()));
					} else {
						transactionAmount.setTextColor(Color
								.parseColor("#81b28e"));
						transactionAmount.setText("+"
								+ currencyFormatter.format(o.getAmount()));
					}
				} else {
					if (o.getTransactionType().equalsIgnoreCase("Payment")) {
						transactionAmount.setTextColor(Color
								.parseColor("#1c8839"));
						transactionAmount.setText("+"
								+ currencyFormatter.format(o.getAmount()));
					} else {
						transactionAmount.setTextColor(Color
								.parseColor("#86acb5"));
						transactionAmount.setText("-"
								+ currencyFormatter.format(o.getAmount()));
					}
				}
			}
			if (transactionComment != null) {
				if (o.getComments().equals("No comments")) {
					transactionComment.setText(" ");
				} else {
					String comment;
					if(o.getComments().length() > 18)
					{
						String shortened = o.getComments().substring(0, 18);
						shortened = shortened + "...";
						comment = "\"" + shortened + "\" ";
					}
					else
					{
						comment = "\"" + o.getComments() + "\" ";
					}
					transactionComment.setText(comment);
				}
			}
		}

		// OLD:
		// Drawable drawableRow = v.getResources().getDrawable(
		// R.drawable.transaction_row_background);
		// Drawable drawableRowAlt = v.getResources().getDrawable(
		// R.drawable.transaction_rowalt_background);
		// if (position % 2 == 0)
		// v.setBackgroundDrawable(drawableRow);
		// else
		// v.setBackgroundDrawable(drawableRowAlt);
		v.setBackgroundColor(Color.parseColor("#f6f6f6"));
		return v;
	}
}
