package me.pdthx.Requests;

import java.util.ArrayList;

public class UserMeCodeRequest {

	private ArrayList<String> meCodes;
	private String userId;
	
	public UserMeCodeRequest(String userId, String... meCodes) {
		this.setUserId(userId);
		this.meCodes = new ArrayList<String>();
		
		for (int i = 0; i < meCodes.length; i++) {
			this.meCodes.add(meCodes[i]);
		}
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public ArrayList<String> getMeCodes() {
		return meCodes;
	}
}
