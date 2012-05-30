package me.pdthx;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Contacts;
import android.provider.Contacts.People;
import android.provider.ContactsContract;

public class ContactList {

	private ArrayList<String> contactsList = new ArrayList<String>();
	private ArrayList<String> names = new ArrayList<String>();
	private ArrayList<String> emails = new ArrayList<String>();
	
	public ContactList(Context context) {

		String NAME = "NAME"; //temp variable.
		 ContentResolver cr = context.getContentResolver();
		    Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
		        "DISPLAY_NAME = '" + NAME + "'", null, null);
		    if (cursor.moveToFirst()) {
		        String contactId =
		            cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
		        //
		        //  Get all phone numbers.
		        //
		        Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
		        		ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
		        while (phones.moveToNext()) {
		            String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
		            int type = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
		            switch (type) {
		                case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
		                	((ArrayList<String>) contactsList).add(number);
		                    break;
		                case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
		                	((ArrayList<String>) contactsList).add(number);
		                    break;
		                case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
		                	((ArrayList<String>) contactsList).add(number);
		                    break;
		                }
		        }
		        phones.close();
		        //
		        //  Get all email addresses.
		        //
		        Cursor emails = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
		        		ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId, null, null);
		        while (emails.moveToNext()) {
		            String email = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
		            int type = emails.getInt(emails.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
		            switch (type) {
		                case ContactsContract.CommonDataKinds.Email.TYPE_HOME:
		                 	((ArrayList<String>) emails).add(email);
		                    break;
		                case ContactsContract.CommonDataKinds.Email.TYPE_WORK:
		                	((ArrayList<String>) emails).add(email);
		                    break;
		            }
		        }
		        emails.close();
		    }
		    cursor.close();
	;
		/*
		Cursor phones = context.getContentResolver().query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
				null, null);
		while (phones.moveToNext()) {

			String name = "";
			String phoneNumber = "";
			String email = " ";
			name = phones
					.getString(phones 
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
			phoneNumber = phones
					.getString(phones
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			
			email = phones
					.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
if(email.equals(phoneNumber))
{
	email = "";
}
			names.add(name);
			contactsList.add(phoneNumber);
			emails.add(email);
		}
		phones.close();
		
		Cursor emailCur = context.getContentResolver().query(
				ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
				ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
				new String[] {}, null);
		while (emailCur.moveToNext()) {
			String email = emailCur
					.getString(emailCur
							.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
			String emailType = emailCur
					.getString(emailCur
							.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
			emails.add(email + " " + emailType);
		}
		emailCur.close();
*/
		/*
		 * ContentResolver cr = context.getContentResolver(); Cursor cur =
		 * cr.query(People.CONTENT_URI, null, null, null, null); if
		 * (cur.getCount() > 0) { while (cur.moveToNext()) { String contactId =
		 * cur.getString(cur .getColumnIndex(People._ID));
		 * 
		 * // Get all phone numbers.
		 * 
		 * Cursor phones = cr.query(Contacts.Phones.CONTENT_URI, null,
		 * Contacts.Phones._ID + " = " + contactId, null, null); while
		 * (phones.moveToNext()) { String number = phones.getString(phones
		 * .getColumnIndex(Contacts.Phones.NUMBER)); contactsList.add(number); }
		 * phones.close();
		 * 
		 * // Get all email addresses. // // Cursor emails =
		 * cr.query(Email.CONTENT_URI, null, // Email.CONTACT_ID + " = " +
		 * contactId, null, null); // while (emails.moveToNext()) { // String
		 * email = // emails.getString(emails.getColumnIndex(Email.DATA)); //
		 * int type = emails.getInt(emails.getColumnIndex(Phone.TYPE)); //
		 * switch (type) { // case Email.TYPE_HOME: // do something with the
		 * Home email here... // break; // case Email.TYPE_WORK: // do something
		 * with the Work email here... // break; // } // } //emails.close(); } }
		 * cur.close();
		 */
	}

	public ArrayList<String> getContacts() {
		return contactsList;
	}

	public ArrayList<String> getNames() {
		return names;
	}

	public ArrayList<String> getEmails() {
		return emails;
	}
	
}
