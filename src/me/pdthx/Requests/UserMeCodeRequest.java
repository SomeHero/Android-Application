package me.pdthx.Requests;

import java.util.ArrayList;

public class UserMeCodeRequest extends UserRequest {

	private ArrayList<String> meCodes;
	
	public UserMeCodeRequest(String userId, String... meCodes) {
		this.meCodes = new ArrayList<String>();
		super.UserId = userId;
		
		for (int i = 0; i < meCodes.length; i++) {
			this.meCodes.add(meCodes[i]);
		}
	}
	
	public ArrayList<String> getMeCodes() {
		return meCodes;
	}
}
