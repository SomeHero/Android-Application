package me.pdthx.Models;

public class Organization {

	private String _name = "";
	private String _slogan = "";
	private String _header = "";
	private String _info = "";
	private String _imageUri = "";

	public void setImageUri(String uri)
	{
		_imageUri = uri;
	}
	
	public String getImageUri()
	{
		return _imageUri;
	}
	
	public void setHeader(String header) {
		_header = header;
	}

	public String getHeader() {
		return _header;
	}

	public void setName(String name) {
		_name = name;
	}

	public void setSlogan(String slogan) {
		_slogan = slogan;
	}

	public void setInfo(String info) {
		_info = info;
	}

	public String getName() {
		return _name;
	}

	public String getSlogan() {

		return _slogan;
	}

	public String getInfo() {
		return _info;
	}
}