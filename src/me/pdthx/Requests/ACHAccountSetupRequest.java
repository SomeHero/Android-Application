package me.pdthx.Requests;

public class ACHAccountSetupRequest extends UserRequest {
	public ACHAccountSetupRequest() {

	}

	public String NameOnAccount = "";
	public String RoutingNumber = "";
	public String AccountNumber = "";
	public String AccountType = "";
	public String SecurityPin = "";
	public String SecurityAnswer = "";
	public int SecurityQuestionId;
	public String Nickname = "";
}
