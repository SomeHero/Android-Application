package me.pdthx.Responses;

public class SubmitPaymentResponse {
	public SubmitPaymentResponse() {
		
	}
	public Boolean Success = false;
	public String ResponseStatus = "";
	public String Message = "";
	public String PaymentId = "";
	public String UserId = "";
	public String FromMobileNumber = "";
	public String ToMobileNumber = "";
	public Double Amount = (double) 0;
	public String Comment = "";
}
