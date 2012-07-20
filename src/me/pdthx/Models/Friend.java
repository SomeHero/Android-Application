package me.pdthx.Models;

import java.util.ArrayList;
import me.pdthx.Helpers.NameSeparator;
import android.net.Uri;
import android.graphics.Bitmap;

public class Friend implements Comparable<Friend> {
    private String name = "";
    private String firstName = "";
    private String lastName = "";
    private String id = "";
    private boolean fbContact = false;
    private String paypoint = "";
    private Bitmap picture;
    private Uri pictureUri;
    private ArrayList<String> paypoints = new ArrayList<String>();

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
        String[] names = NameSeparator.separateName(name);
        firstName = names[0];
        lastName = names[1];
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

    public ArrayList<String> getPaypoints()
    {
        return paypoints;
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

        if (search.contains(" "))
        {
            String[] searchNames = NameSeparator.separateName(search);
            String searchFirst = searchNames[0].toLowerCase();
            String searchLast = searchNames[1].toLowerCase();

            return firstName.toLowerCase().startsWith(searchFirst) && lastName.toLowerCase().startsWith(searchLast)
                || firstName.toLowerCase().startsWith(searchLast) && lastName.toLowerCase().startsWith(searchFirst);
        }

        if (name.toLowerCase().startsWith(search))
        {
            return true;
        }

        if (firstName.toLowerCase().startsWith(search)) {
            return true;
        }

        if (lastName.toLowerCase().startsWith(search)) {
            return true;
        }

        if (paypoint.toLowerCase().startsWith(search))
        {
            return true;
        }

        for (String paypoint : paypoints)
        {
            if (paypoint.toLowerCase().startsWith(search))
            {
                return true;
            }
        }

        return false;
    }

    public String toString() {

        if (paypoints.size() != 0)
        {
            return name + ": " + paypoints.size() + " paypoints.";
        }
        else
        {
            return name;
        }
    }

}
