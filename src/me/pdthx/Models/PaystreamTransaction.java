package me.pdthx.Models;

import me.pdthx.Helpers.NameSeparator;
import java.text.DateFormat;
import java.util.Date;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class PaystreamTransaction implements Parcelable{
	private String _header = "";
	private String _paymentId = "";
	private String _transactionId;
	private String _senderUri = "";
	private String _recipientUri = "";
	private Double _amount = 0.0;
	private Number _achTransactionId = 0;
	private String _transactionStatus = "";
	private String _transactionCategory = "";
	private String _transactionType = "";
	private String _standardEntryClass = "";
	private String _paymentChannel = "";
	private String _transactionBatchId = "";
	private String _transactionImageUri = "";
	private String _date = "";
	private String _time="";
	private Date _transactionSentDate = null;
	private Date _createDate = null;
	private Date _lastUpdateDate = null;
	private String _direction = "";
	private String _comments = "";
	private Bitmap _cameraPic = null;

	public boolean search(String queary) {

		String search = queary.trim().toLowerCase();
		String[] names = NameSeparator.separateName(_recipientUri);
		String firstName = names[0].toLowerCase();
		String lastName = names[1].toLowerCase();
		String[] senderNames = NameSeparator.separateName(_senderUri);
		String senderFirstName = senderNames[0].toLowerCase();
		String senderLastName = senderNames[1].toLowerCase();


        if (search.contains(" "))
        {
            String[] searchNames = NameSeparator.separateName(search);
            String searchFirst = searchNames[0].toLowerCase();
            String searchLast = searchNames[1].toLowerCase();

            return firstName.startsWith(searchFirst) && lastName.startsWith(searchLast)
                || firstName.startsWith(searchLast) && lastName.startsWith(searchFirst);
        }

        if(_direction.equalsIgnoreCase("Out"))
        {
	        if (_recipientUri.startsWith(search))
	        {
	            return true;
	        }
	        if(firstName.startsWith(search)) {
	            return true;
	        }
	        if (lastName.startsWith(search)) {
	            return true;
	        }
        }
        else if(_direction.equalsIgnoreCase("In"))
        {
        	 if (_senderUri.startsWith(search))
 	        {
 	            return true;
 	        }
 	        if (senderFirstName.startsWith(search)) {
 	            return true;
 	        }
 	        if (senderLastName.startsWith(search)) {
 	            return true;
 	        }
        }
        return false;
	}

	public void setCameraPic(Bitmap cameraPic) {
		_cameraPic = cameraPic;
	}

	public Bitmap getCameraPic() {
		return _cameraPic;
	}

	public String getImageUri() {
		return _transactionImageUri;
	}

	public void setImageUri(String imageuri) {
		_transactionImageUri = imageuri;
	}

	public String getHeader() {
		return _header;
	}

	public void setHeader(String header) {
		_header = header;
	}

	public String getPaymentId() {
		return _paymentId;
	}

	public void setPaymentId(String paymentId) {
		_paymentId = paymentId;
	}

	public String getTransactionId() {
		return _transactionId;
	}

	public void setTransactionId(String transactionId) {
		_transactionId = transactionId;
	}

	public String getSenderUri() {
		return _senderUri;
	}

	public void setSenderUri(String senderUri) {
		_senderUri = senderUri;
	}

	public String getRecipientUri() {
		return _recipientUri;
	}

	public void setRecipientUri(String recipientUri) {
		_recipientUri = recipientUri;
	}

	public Double getAmount() {
		return _amount;
	}

	public void setAmount(Double amount) {
		_amount = amount;
	}

	public Number getACHTransactionId() {
		return _achTransactionId;
	}

	public void setACHTransactionId(Number achTransactionID) {
		_achTransactionId = achTransactionID;
	}

	public String getTransactionStatus() {
		return _transactionStatus;
	}

	public void setTransactionStatus(String transactionStatus) {
		_transactionStatus = transactionStatus;
	}

	public String getTransactionCategory() {
		return _transactionCategory;
	}

	public void setTransactionCategory(String transactionCategory) {
		_transactionCategory = transactionCategory;
	}

	public String getTransactionType() {
		return _transactionType;
	}

	public void setTransactionType(String transactionType) {
		_transactionType = transactionType;
	}

	public String getStandardEntryClass() {
		return _standardEntryClass;
	}

	public void setStandardEntryClass(String standardEntryClass) {
		_standardEntryClass = standardEntryClass;
	}

	public String getPaymentChannel() {
		return _paymentChannel;
	}

	public void setPaymentChannel(String paymentChannel) {
		_paymentChannel = paymentChannel;
	}

	public String getTransactionBatchId() {
		return _transactionBatchId;
	}

	public void setTransactionBatchId(String transactionBatchId) {
		_transactionBatchId = transactionBatchId;
	}

	public Date getTransactionSentDate() {
		return _transactionSentDate;
	}

	public void setTransactionSentDate(Date transactionSentDate) {
		_transactionSentDate = transactionSentDate;
	}

	public Date getCreateDate() {
		return _createDate;
	}

	public void setCreateDate(Date createDate) {
		_createDate = createDate;
	}

	public Date getLastUpdateDate() {
		return _lastUpdateDate;
	}

	public void setLastUpdateDate(Date lastUpdateDate) {
		_lastUpdateDate = lastUpdateDate;
	}

	public void setDirection(String direction) {
		_direction = direction;
	}

	public String getDirection() {
		return _direction;
	}

	public String getComments() {
		return _comments;
	}

	public void setComments(String comments) {
		_comments = comments;
	}

	public boolean equals(Object other) {
		return other instanceof PaystreamTransaction
				&& _transactionId.equals(((PaystreamTransaction) other)
						.getTransactionId());
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG);
		String date = dateFormat.format(_createDate);
		DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.MEDIUM);
		String theTime = timeFormat.format(_createDate);
		out.writeString(date);
		out.writeString(theTime);
		out.writeString(_senderUri);
		out.writeString(_recipientUri);
		out.writeDouble(_amount);
		out.writeString(_transactionType);
		out.writeString(_transactionStatus);
		out.writeString(_transactionId);
		out.writeString(_comments);
	}

	public static final Parcelable.Creator<PaystreamTransaction> CREATOR = new Parcelable.Creator<PaystreamTransaction>(){
		public PaystreamTransaction createFromParcel(Parcel in){
			return new PaystreamTransaction(in);
		}

		public PaystreamTransaction[] newArray(int size){
			return new PaystreamTransaction[size];
		}
	};

	private PaystreamTransaction(Parcel in){
		_date = in.readString();
		_time = in.readString();
		_senderUri = in.readString();
		_recipientUri = in.readString();
		_amount = in.readDouble();
		_transactionType = in.readString();
		_transactionStatus = in.readString();
		_transactionId = in.readString();
		_comments = in.readString();
	}

	public PaystreamTransaction() {
		// TODO Auto-generated constructor stub
	}

	public String getDateString()
	{
		return _date;
	}

	public String getTimeString()
	{
		return _time;
	}
}
