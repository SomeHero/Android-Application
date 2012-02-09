package me.pdthx.Responses;

public class UserAcknowledgementResponse {
	public UserAcknowledgementResponse() {	
	}
	public String UserId = "";
	public boolean DoesDeviceIdMatch = false;
	public boolean IsMobileNumberRegistered = false;
	public boolean SetupSecurityPin = false;
	public boolean SetupPassword = false;
	public String PaymentAccountId = "";
}
