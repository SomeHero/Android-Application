package me.pdthx.Requests;

import android.os.Parcel;
import android.os.Parcelable;

public class ACHAccountSetupRequest extends UserRequest implements Parcelable {
	public String NameOnAccount = "";
	public String RoutingNumber = "";
	public String AccountNumber = "";
	public String AccountType = "";
	public String SecurityPin = "";
	public String SecurityAnswer = "";
	public int SecurityQuestionId;
	public String Nickname = "";
	public int tab;
	
	public ACHAccountSetupRequest() {
		
	}
	
	public ACHAccountSetupRequest(Parcel in) {
		NameOnAccount = in.readString();
		RoutingNumber = in.readString();
		AccountNumber = in.readString();
		AccountType = in.readString();
		SecurityPin = in.readString();
		SecurityAnswer = in.readString();
		SecurityQuestionId = in.readInt();
		Nickname = in.readString();
		tab = in.readInt();
		UserId = in.readString();
	}
	
	public static final Parcelable.Creator<ACHAccountSetupRequest> CREATOR = new Parcelable.Creator<ACHAccountSetupRequest>() {

		@Override
		public ACHAccountSetupRequest createFromParcel(Parcel source) {
			return new ACHAccountSetupRequest(source);
		}

		@Override
		public ACHAccountSetupRequest[] newArray(int size) {
			return new ACHAccountSetupRequest[size];
		}
	};
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(NameOnAccount);
		dest.writeString(RoutingNumber);
		dest.writeString(AccountNumber);
		dest.writeString(AccountType);
		dest.writeString(SecurityPin);
		dest.writeString(SecurityAnswer);
		dest.writeInt(SecurityQuestionId);
		dest.writeString(Nickname);
		dest.writeInt(tab);
		dest.writeString(UserId);
		
	}
}
