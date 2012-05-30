package me.pdthx.Services;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import me.pdthx.RestClient;
import me.pdthx.Requests.TransactionRequest;
import me.pdthx.Responses.TransactionResponse;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class TransactionService {
	public TransactionService() {
	}

	private static final String ROOTURL = "http://23.21.203.171/api/internal/api";
	private static final String SERVICE_URL = "/Services/TransactionService/Transactions";
	private static final String APIKEY = "bda11d91-7ade-4da1-855d-24adfe39d174";

	public ArrayList<TransactionResponse> GetTransactions(TransactionRequest transactionRequest) {
		ArrayList<TransactionResponse> transactionResponses = new ArrayList<TransactionResponse>();

		HttpResponse response = null;

		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet request = new HttpGet(ROOTURL + SERVICE_URL + "/" + transactionRequest.UserId + "?" + APIKEY);

			request.setHeader("content-type", "application/json");

			response = httpClient.execute(request);

		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} 
		//catch (JSONException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		//}

		HttpEntity entity = response.getEntity();

		if (entity != null) {

			InputStream instream = null;
			String result = "";
			try {
				instream = entity.getContent();
				result = RestClient.convertStreamToString(instream);
				// Log.i(TAG, "Result of converstion: [" + result +
				// "]");
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				instream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Log.i("Get Transactions Response", result);

			
			JSONArray transactions = null;
			try
			{ 
				transactions = new JSONArray(result);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}

			try{
				//Loop the Array
				for(int i=0;i < transactions.length();i++){ 
					JSONObject transaction = transactions.getJSONObject(i);
					TransactionResponse transactionResponse = new TransactionResponse();
					
					transactionResponse.Amount = transaction.getDouble("amount");
 
					SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd kk:mm:ss Z yyyy");
					sdf.setTimeZone(TimeZone.getDefault());
					
					Date formattedDate = null;
					try {
						formattedDate = sdf.parse(transaction.getString("createDate"));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					transactionResponse.CreateDate = formattedDate;
					transactionResponse.SenderUri = transaction.getString("senderUri");
					transactionResponse.RecipientUri = transaction.getString("recipientUri");
					transactionResponse.TransactionStatus = transaction.getString("transactionStatus");
					transactionResponse.TransactionType = transaction.getString("transactionType");
					transactionResponse.TransactionCategory = transaction.getString("transactionCategory");
					
					transactionResponses.add(transactionResponse);
				}
			}
			catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return transactionResponses;
	}
}
