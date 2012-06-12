package me.pdthx;

import java.util.ArrayList;
import me.pdthx.Models.Friend;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract;

public class ContactList {

	private ArrayList<Friend> contactsList = new ArrayList<Friend>();
	public ContactList(Context context) {


		Cursor cursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,null, null, null, null); 
		while (cursor.moveToNext()) { 
			String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)); 
			if (hasPhone.equals("1")) { 
				// You know it has a number so now query it like this
				String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
				Cursor phones = context.getContentResolver().query( ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId, null, null); 
				while (phones.moveToNext()) {
					Friend friend = new Friend();
					String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
					String phoneNumber = phones.getString(phones.getColumnIndex( ContactsContract.CommonDataKinds.Phone.NUMBER));
					friend.setId(contactId);
					friend.setName(name);
					friend.setPhoneNumber(phoneNumber);
					friend.setPicture(BitmapFactory.decodeResource(context.getResources(), R.drawable.person_icon_small));
					contactsList.add(friend);
				} 
				phones.close(); 
			}

//			Cursor emails = context.getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId, null, null); 
//			while (emails.moveToNext()) { 
//				// This would allow you get several email addresses 
//				String emailAddress = emails.getString( 
//						emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
//				
//			} 
//			emails.close(); 
		}
		
		cursor.close();

	}

	public ArrayList<Friend> getContacts() {
		return contactsList;
	}


}
