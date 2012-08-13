package me.pdthx.Models;

public class Organization {

    private String id = "";
	private String name = "";
	private String slogan = "";
	private String info = "";
	private String imageUri = "";
	private String preferredReceiveId = "";
	private String preferredSendId = "";
	private int suggestedAmount = 0;

	public String getId()
    {
        return id;
    }

    public void setId(String _id)
    {
        this.id = _id;
    }

    public void setImageUri(String uri)
	{
		imageUri = uri;
	}

	public void setPreferredReceive(String id)
	{
		preferredReceiveId = id;
	}

	public void setPreferredSend(String id)
	{
		preferredSendId = id;
	}

	public String getPreferredReceive()
	{
		return preferredReceiveId;
	}

	public String getPreferredSend()
	{
		return preferredSendId;
	}

	public String getImageUri()
	{
		return imageUri;
	}

	public void setName(String name) {
		name = name;
	}

	public void setSlogan(String slogan) {
		slogan = slogan;
	}

	public void setInfo(String info) {
		info = info;
	}

	public String getName() {
		return name;
	}

	public String getSlogan() {

		return slogan;
	}

	public String getInfo() {
		return info;
	}

    public int getSuggestedAmount()
    {
        return suggestedAmount;
    }

    public void setSuggestedAmount(int _suggestedAmount)
    {
        this.suggestedAmount = _suggestedAmount;
    }

    public boolean search(String searchString)
    {
        return name.startsWith(searchString);
    }
}
