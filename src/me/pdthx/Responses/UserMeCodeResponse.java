package me.pdthx.Responses;

import me.pdthx.Models.MeCodeModel;

public class UserMeCodeResponse {
	
	private MeCodeModel[] meCodes;
	private boolean success;
	private String reasonPhrase;
	
	public UserMeCodeResponse() {
		
	}
	
	public void populateMeCodes(MeCodeModel[] meCodes) {
		this.meCodes = new MeCodeModel[meCodes.length];
		this.meCodes = meCodes;
	}
	
	public MeCodeModel[] getMeCodes() {
		return meCodes;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getReasonPhrase() {
		return reasonPhrase;
	}

	public void setReasonPhrase(String reasonPhrase) {
		this.reasonPhrase = reasonPhrase;
	}

}
