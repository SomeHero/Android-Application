package me.pdthx.Models;

import android.net.Uri;
import me.pdthx.helpers.NameSeparator;
import android.graphics.Bitmap;
import me.pdthx.helpers.PhoneNumberFormatter;

public class Friend implements Comparable<Friend> {
    private String name = "";
    private String id = "";
    private boolean fbContact = false;
    private String paypoint = "";
    private Bitmap picture;
    private Uri pictureUri;

    public String getId()
    {
        return id;
    }
    public void setId(String id)
    {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isFBContact() {
        return fbContact;
    }

    public void setFBContact(boolean fbContact) {
        this.fbContact = fbContact;
    }

    public String getPaypoint()
    {
        return paypoint;
    }
    public void setPaypoint(String paypoint)
    {
        this.paypoint = paypoint;
    }
    public Bitmap getPicture() {
        return picture;
    }
    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }

    public Uri getPictureUri()
    {
        return pictureUri;
    }
    public void setPictureUri(Uri pictureUri)
    {
        this.pictureUri = pictureUri;
    }
    public boolean equals(Object other) {
        return other instanceof Friend && ((Friend)other).getId().equals(id);
    }

    @Override
    public int compareTo(Friend friend) {
        return this.name.compareToIgnoreCase(friend.name);
    }

    public boolean masterSearch(String s) {
        String search = s.trim().toLowerCase();
        String[] names = NameSeparator.separateName(name);
        String firstName = names[0].toLowerCase();
        String lastName = names[1].toLowerCase();

        if (search.contains(" "))
        {
            String[] searchNames = NameSeparator.separateName(search);
            String searchFirst = searchNames[0].toLowerCase();
            String searchLast = searchNames[1].toLowerCase();

            return firstName.startsWith(searchFirst) && lastName.startsWith(searchLast)
                || firstName.startsWith(searchLast) && lastName.startsWith(searchFirst);
        }

        if (name.startsWith(search))
        {
            return true;
        }

        if (firstName.startsWith(search)) {
            return true;
        }

        if (lastName.startsWith(search)) {
            return true;
        }

        if (paypoint.toLowerCase().startsWith(search))
        {
            return true;
        }

        return false;
    }

    public String toString() {
        if (!paypoint.equals("")) {
            return name + ": " + paypoint;
        }

        return name;
    }

}
