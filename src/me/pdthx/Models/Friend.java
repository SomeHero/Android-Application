package me.pdthx.Models;

import android.graphics.Bitmap;

public class Friend implements Comparable<Friend> {
	private String name = "";
	private String id = "";
	private String type = "";
	private String phoneNumber = "";
	private String emailAddress = "";
	private Bitmap picture = null;

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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		String fixedNumber = "";
		String number = "";
		String extras = "";

		fixedNumber = phoneNumber.replaceAll("[^0-9]", "");
		int fixedNumberLength = fixedNumber.length();

		if (fixedNumberLength >= 10) {

			if (fixedNumberLength == 10) {
				number = fixedNumber;
			}
			else if (fixedNumberLength > 10) {
				extras = fixedNumber.substring(0, fixedNumberLength - 10) + " ";
				number = fixedNumber.substring(fixedNumberLength - 11);
			}

			number = "(" + number.substring(0, 3) + ") " + 
					number.substring(3, 6) + "-" + number.substring(6);

			fixedNumber = extras + number;

			this.phoneNumber = fixedNumber;
		}
		else {
			this.phoneNumber = "";
		}
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public Bitmap getPicture() {
		return picture;
	}
	public void setPicture(Bitmap picture) {
		this.picture = picture;
	}
	@Override
	public int compareTo(Friend friend) {
		return this.name.compareTo(friend.name);
	}
	
	public boolean equals(Object other) {
		return other instanceof Friend && id.equals(((Friend) other).getId());
	}


}
