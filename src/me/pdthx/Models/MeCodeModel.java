package me.pdthx.Models;

public class MeCodeModel {
	private String approvedDate;
	private String createDate;
	private boolean isActive;
	private boolean isApproved;
	private String meCode;
	
	public MeCodeModel() {
		
	}
	
	public String getApprovedDate() {
		return approvedDate;
	}
	public void setApprovedDate(String approvedDate) {
		this.approvedDate = approvedDate;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public boolean isActive() {
		return isActive;
	}
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	public boolean isApproved() {
		return isApproved;
	}
	public void setApproved(boolean isApproved) {
		this.isApproved = isApproved;
	}
	public String getMeCode() {
		return meCode;
	}
	public void setMeCode(String meCode) {
		this.meCode = meCode;
	}
}
