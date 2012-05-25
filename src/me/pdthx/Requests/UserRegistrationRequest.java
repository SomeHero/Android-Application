package me.pdthx.Requests;

public class UserRegistrationRequest {
	
	private String userName = "";
	private String password = "";
	private String deviceToken = "";
	
	public UserRegistrationRequest(String userName, String password, String deviceToken) { 
		setUserName(userName);
		setPassword(password);
		setDeviceToken(deviceToken);
	}


	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDeviceToken() {
		return deviceToken;
	}

	public void setDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
	}
	
}
