package me.pdthx.Responses;

public class UserSignInResponse {
	public UserSignInResponse() {
		
	}
	public boolean IsValid = false;
	public String UserId = "";
	public String MobileNumber = "";
	public boolean SetupSecurityPin = false;
	public String PaymentAccountId = "";
	public int UpperLimit = 0;
	public String ReasonPhrase = "";
}
