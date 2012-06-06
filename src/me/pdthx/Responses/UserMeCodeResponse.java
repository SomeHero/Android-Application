package me.pdthx.Responses;

import me.pdthx.Models.MeCodeModel;

public class UserMeCodeResponse extends Response{
	
	private MeCodeModel[] meCodes;
	
	public UserMeCodeResponse() {
		
	}
	
	public void populateMeCodes(MeCodeModel[] meCodes) {
		this.meCodes = new MeCodeModel[meCodes.length];
		this.meCodes = meCodes;
	}
	
	public MeCodeModel[] getMeCodes() {
		return meCodes;
	}

}
