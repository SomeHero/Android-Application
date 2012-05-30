package me.pdthx;

import java.util.ArrayList;

import com.zubhium.ZubhiumSDK;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Contacts;
import android.provider.Contacts.People;
import android.provider.ContactsContract;

public class ContactList {
	
	private ArrayList<String> contactsList = new ArrayList<String>();
	ZubhiumSDK sdk ;
	
	public ContactList(Context context) {
		Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
		while (phones.moveToNext())
		{
		  //String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
		  String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
		  contactsList.add(phoneNumber);

		}
		phones.close();		
	
		/*ContentResolver cr = context.getContentResolver();
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
		cur.close();*/
	}
	public ArrayList<String> getContacts() {
		return contactsList;
	}
}