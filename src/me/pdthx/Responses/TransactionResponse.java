package me.pdthx.Responses;

import java.util.Date;

public class TransactionResponse {
	public String TransactionId = "";
	public String PaymentId = "";
	public String SenderUri = "";
	public String RecipientUri = "";
	public double Amount = 0.0;
	public Number ACHTransactionId;
	public String TransactionStatus;
	public String TransactionCategory;
	public String TransactionType = "";
	public String StandardEntryClass = "";
	public String PaymentChannel = "";
	public String TransactionBatchId = "";
	public Date TransactionSentDate = null;
	public Date CreateDate = null;
	public Date LastUpdatedDate = null;
}
