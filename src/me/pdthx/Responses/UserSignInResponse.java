package me.pdthx.Responses;

public class UserSignInResponse extends Response{
	public UserSignInResponse() {
		
	}
	public String UserId = "";
	public String MobileNumber = "";
	public boolean SetupSecurityPin = false;
	public String PaymentAccountId = "";
	public int UpperLimit = 0;
}
