package me.pdthx;

import java.util.ArrayList;

import com.zubhium.ZubhiumSDK;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.Contacts;
import android.provider.Contacts.People;

public class ContactList {
	
	private ArrayList<String> contactsList = new ArrayList<String>();
	ZubhiumSDK sdk ;
	
	public ContactList(Context context) {
		ContentResolver cr = context.getContentResolver();
		Cursor cur = cr.query(People.CONTENT_URI, null,
				null, null, null);
		if (cur.getCount() > 0) {
			while (cur.moveToNext()) {
				String contactId = cur.getString(cur
						.getColumnIndex(People._ID));

				// Get all phone numbers.

				Cursor phones = cr.query(Contacts.Phones.CONTENT_URI, null,
						Contacts.Phones._ID + " = " + contactId, null, null);
				while (phones.moveToNext()) {
					String number = phones.getString(phones
							.getColumnIndex(Contacts.Phones.NUMBER));
					contactsList.add(number);
				}
				phones.close();

				// Get all email addresses.
				//
				// Cursor emails = cr.query(Email.CONTENT_URI, null,
				// Email.CONTACT_ID + " = " + contactId, null, null);
				// while (emails.moveToNext()) {
				// String email =
				// emails.getString(emails.getColumnIndex(Email.DATA));
				// int type = emails.getInt(emails.getColumnIndex(Phone.TYPE));
				// switch (type) {
				// case Email.TYPE_HOME:
				// do something with the Home email here...
				// break;
				// case Email.TYPE_WORK:
				// do something with the Work email here...
				// break;
				// }
				// }
				//emails.close();
			}
		}
		cur.close();
		contactsList.add("804-387-9693");	
	}
	public ArrayList<String> getContacts() {
		return contactsList;
	}
}
