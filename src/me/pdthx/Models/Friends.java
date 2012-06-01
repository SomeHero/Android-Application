package me.pdthx.Models;

public class Friends implements Comparable<Friends> {
	public String name = "";
	public String id = "";
	public String type = "";
	public int phoneNumber = 0;
	
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
	@Override
	public int compareTo(Friends friend) {
		return this.name.compareTo(friend.name);
	}

}
