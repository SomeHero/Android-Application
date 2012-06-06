package me.pdthx.Models;

public class Friend implements Comparable<Friend> {
	private String name = "";
	private String id = "";
	private String type = "";
	private String phoneNumber = "";
	private String emailAddress = "";
	
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
		this.phoneNumber = phoneNumber;
	}
	
	public String getEmailAddress() {
		return emailAddress;
	}
	
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	
	@Override
	public int compareTo(Friend friend) {
		return this.name.compareTo(friend.name);
	}
	

}
