package me.pdthx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import me.pdthx.Models.Friends;

import com.zubhium.ZubhiumSDK;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

public class ContactList {

	private ArrayList<Friends> contactsList = new ArrayList<Friends>();
	ZubhiumSDK sdk;
	public ContactList(Context context) {

	
		   ContentResolver cr = context.getContentResolver();

		    Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

		   // ArrayList<String> contactsList = new ArrayList<String>();

		    if (cur.getCount() > 0) {
		        if(cur.moveToFirst()) {
		            do {
		             //   Log.i("Test", "---------------------count-------------------" +  cur.getCount());
		                
		            	int phoneNumber = Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
		            	
		                Friends friend = new Friends();
		                friend.id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
		                friend.name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
		                friend.type = "Contact";
		                
		                //contactsList.add(friend);
		                if (phoneNumber > 0) {
		                    //Query phone here.  Covered next
		                	//Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,  ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ", null, null);
		                    //String num = phones.getString(phones.getColumnIndex(ContactsContract.PhoneLookup.NUMBER));
		                	friend.phoneNumber = phoneNumber;
		                    contactsList.add(friend);
		                    //phones.close();
		                }
		            }while (cur.moveToNext()) ;
		        }
		    }
		    
//		    Cursor emailCur = cr.query( 																		///THIS E-MAIL SECTION DOESN'T DO ANYTHING : (  FIX THIS
//		            ContactsContract.CommonDataKinds.Email.CONTENT_URI, 
//		            null,
//		            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", 
//		            new String[]{}, null);//new String[]{id}, null); 
//		        while (emailCur.moveToNext()) { 
//		            // This would allow you get several email addresses
//		                // if the email addresses were stored in an array
//		            String email = emailCur.getString(
//		                          emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
//		            String emailType = emailCur.getString(
//		                          emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE)); 
//		            contactsList.add(email);
//		        } 
//		        emailCur.close();
//		        
//		        for (int x = 0; x < contactsList.size(); x++)
//		        {
//		        	for (int y = 0; y < contactsList.size(); y++)
//		        	{
//		        		if(contactsList.get(x).equals(contactsList.get(y)))
//		        		{
//		        			contactsList.remove(y);
//		        		}
//		        	}
//		        }
		        
		      
		/*
		 * ContentResolver cr = context.getContentResolver();
		 * Cursor cur = cr.query(ContactsContact.CONTENT_URI, null, null, null, null);
		if (cur.getCount() > 0) {
			while (cur.moveToNext()) {
				String contactId = cur.getString(cur.getColumnIndex(People._ID));

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

				Cursor emails = cr.query(Email.CONTENT_URI, null, Email.CONTACT_ID + " = " + contactId, null, null);
				while (emails.moveToNext()) {
					String email = emails.getString(emails.getColumnIndex(Email.DATA));
					int type = emails.getInt(emails.getColumnIndex(Phone.TYPE));
					switch (type) {
					case Email.TYPE_HOME:
						contactsList.add(email);
						break;
					case Email.TYPE_WORK:
						contactsList.add(email);
						// do something with the Work email here...
						break;
					}
				}
				emails.close();
			}
		}
		cur.close();*/
	        
	}

	public ArrayList<Friends> getContacts() {
		return contactsList;
	}


}
