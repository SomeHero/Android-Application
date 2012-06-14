package me.pdthx.Models;

import java.util.ArrayList;
import java.util.Date;

public class PaystreamTransaction {

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
	private Date _transactionSentDate = null;
	private Date _createDate = null;
	private Date _lastUpdateDate = null;
	private String _direction = "";
	private String _comments = "";
	private ArrayList<PaystreamTransaction> _transactions = new ArrayList<PaystreamTransaction>();
	
	public void addTransactions(PaystreamTransaction transactions)
	{
		_transactions.add(transactions);
	}
	public ArrayList<PaystreamTransaction> getTransactions()
	{
		return _transactions;
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
	public String getStandardEntryClass()
	{
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
				&& _transactionId.equals(((PaystreamTransaction) other).getTransactionId());
	}
}
