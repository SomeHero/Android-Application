package me.pdthx.Requests;

public class SecurityPinSetupRequest {
	
	public SecurityPinSetupRequest(String userId, String passcode) {
		setUserId(userId);
		setSecurityPin(passcode);
	}
	
	public String getSecurityPin() {
		return SecurityPin;
	}

	public void setSecurityPin(String securityPin) {
		this.SecurityPin = securityPin;
	}

	public String getUserId() {
		return UserId;
	}

	public void setUserId(String userId) {
		UserId = userId;
	}

	private String SecurityPin = "";
	private String UserId;

}
