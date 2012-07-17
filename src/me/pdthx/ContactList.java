package me.pdthx;

import me.pdthx.Helpers.PhoneNumberFormatter;
import android.util.Log;
import java.util.Random;
import java.util.UUID;
import android.provider.ContactsContract.Contacts;
import android.content.ContentUris;
import android.net.Uri;
import java.util.ArrayList;

import me.pdthx.Helpers.PhoneNumberFormatter;
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
            if (hasPhone.equals("1")) {
                // You know it has a number so now query it like this
                Friend phoneFriend = new Friend();
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                long contactId = cursor.getLong(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                phoneFriend.setName(name);
                Uri contactUri = ContentUris.withAppendedId(Contacts.CONTENT_URI, contactId);
                phoneFriend.setPictureUri(contactUri);
                //phoneFriend.setPictureUri(Uri.withAppendedPath(contactUri, Contacts.Photo.CONTENT_DIRECTORY));
                Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId, null, null);
                while (phones.moveToNext()) {
                    String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    String fixedNumber = PhoneNumberFormatter.formatNumber(phoneNumber);

                    if (fixedNumber != null && !phoneNumbers.contains(fixedNumber)) {
                        phoneFriend.setPaypoint(fixedNumber);
                        phoneFriend.setId(new UUID(new Random().nextLong(), contactId).toString());
                        phoneNumbers.add(fixedNumber);
                        Log.d("Adding Contact:", phoneFriend.toString());
                        contactsList.add(phoneFriend);
                    }
                }
                phones.close();

                Friend emailFriend = new Friend();
                emailFriend.setName(name);
                emailFriend.setPictureUri(contactUri);
                Cursor emails = context.getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId, null, null);
                while (emails.moveToNext()) {
                    // This would allow you get several email addresses
                    String emailAddress = emails.getString(
                        emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));

                    if (!emailAddresses.contains(emailAddress)) {
                        emailFriend.setPaypoint(emailAddress);
                        emailFriend.setId(new UUID(new Random().nextLong(), contactId).toString());
                        emailAddresses.add(emailAddress);
                        if (name.equals("Ben")) {
                          Log.d("Adding Contact:", emailFriend.toString());
                        }
                        contactsList.add(emailFriend);
                    }
                }
                emails.close();
            }
        }
        Log.d("Contacts", "Contacts complete!");
        cursor.close();

    }

    public ArrayList<Friend> getContacts() {
        return contactsList;
    }

}
