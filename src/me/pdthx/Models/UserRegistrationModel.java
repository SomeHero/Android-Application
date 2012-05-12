package me.pdthx.Models;

public class UserRegistrationModel
{
	private String _emailAddress = "";
	private String _mobileNumber = "";
	private String _password = "";
	private String _securityPin = "";
	
	public void setEmailAddress(String emailAddress) {
		_emailAddress = emailAddress;
	}
	public String getEmailAddress() {
		return _emailAddress;
	}
	public void setMobileNumber(String mobileNumber) {
		_mobileNumber = mobileNumber;
	}
	public String getMobileNumber() {
		return _mobileNumber;
	}
	public void setPassword(String password) {
		_password = password;
	}
	public String getPassword() {
		return _password;
	}
	public void setSecurityPin(String securityPin)
	{
		_securityPin = securityPin;
	}
	public String getSecurityPin()
	{
		return _securityPin;
	}
}