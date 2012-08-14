package me.pdthx;

import me.pdthx.Helpers.PhoneNumberFormatter;
import android.util.Log;
import java.util.Random;
import java.util.UUID;
import android.provider.ContactsContract.Contacts;
import android.content.ContentUris;
import android.net.Uri;
import java.util.ArrayList;

import me.pdthx.Models.Friend;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

public class ContactList {

    private ArrayList<Friend> contactsList = new ArrayList<Friend>();

    public ContactList(Context context) {


        Cursor cursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,null, null, null, null);
        ArrayList<String> phoneNumbers = new ArrayList<String>();
        ArrayList<String> emailAddresses = new ArrayList<String>();
        while (cursor.moveToNext()) {
            String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
            Friend contact = new Friend();
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            contact.setName(name);
            long contactId = cursor.getLong(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            contact.setId(new UUID(new Random().nextLong(), contactId).toString());
            Uri contactUri = ContentUris.withAppendedId(Contacts.CONTENT_URI, contactId);
            contact.setPictureUri(contactUri);
            if (contact.getName() != null)
            {
                if (hasPhone.equals("1")) {
                    // You know it has a number so now query it like this
                    Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId, null, null);
                    while (phones.moveToNext()) {
                        String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        String fixedNumber = PhoneNumberFormatter.stripNumber(phoneNumber);

                        if (fixedNumber != null && !phoneNumbers.contains(fixedNumber)) {
                            //phoneFriend.setPaypoint(fixedNumber);

                            phoneNumbers.add(fixedNumber);
                            contact.getPaypoints().add(fixedNumber);
                            Log.d("Adding Contact:", name + ": " + fixedNumber);
                            //contactsList.add(phoneFriend);
                        }
                    }
                    phones.close();
                }

                Cursor emails = context.getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId, null, null);
                while (emails.moveToNext()) {
                    // This would allow you get several email addresses
                    String emailAddress = emails.getString(
                        emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));

                    if (!emailAddresses.contains(emailAddress)) {
                        //emailFriend.setPaypoint(emailAddress);

                        emailAddresses.add(emailAddress);
                        contact.getPaypoints().add(emailAddress);
                        Log.d("Adding Contact:", name + ": " + emailAddress);
                        //contactsList.add(emailFriend);
                    }
                }
                emails.close();

                contactsList.add(contact);
            }
        }
        Log.d("Contacts", "Contacts complete!");
        cursor.close();

    }

    public ArrayList<Friend> getContacts() {
        return contactsList;
    }

}
