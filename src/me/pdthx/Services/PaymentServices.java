package me.pdthx.Services;

import java.io.IOException;
import java.io.InputStream;

import me.pdthx.Requests.PaymentRequest;
import me.pdthx.Responses.Response;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class PaymentServices {
	public PaymentServices() {
	}

	private static final String ROOTURL = "http://23.21.203.171/api/internal/api";
	private static final String SUBMITMONEY_URL = "/PayStreamMessages?apiKey=bda11d91-7ade-4da1-855d-24adfe39d174";
	private static final String REQUESTMONEY_URL = "/PayStreamMessages?apiKey=bda11d91-7ade-4da1-855d-24adfe39d174";
	private static final String APIKEY = "bda11d91-7ade-4da1-855d-24adfe39d174";

	public static Response sendMoney(
			PaymentRequest paymentRequest) {
		Response paymentResponse = new Response();

		HttpResponse response = null;

		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost request = new HttpPost(ROOTURL + SUBMITMONEY_URL);

			JSONObject json = new JSONObject();
			json.put("apiKey", APIKEY);
			json.put("senderId", paymentRequest.UserId);
			json.put("securityPin", paymentRequest.SecurityPin);
			json.put("senderUri", paymentRequest.SenderUri);
			json.put("recipientUri", paymentRequest.RecipientUri);
			json.put("amount", paymentRequest.Amount);
			json.put("comments", paymentRequest.Comments);
			json.put("senderAccountId", paymentRequest.SenderAccountId);
			json.put("messageType", "Payment");
			json.put("latitude", paymentRequest.Latitude);
			json.put("longitude", paymentRequest.Longitude);

			StringEntity entity = new StringEntity(json.toString());
			request.setEntity(entity);
			request.setHeader("content-type", "application/json");

			response = httpClient.execute(request);

		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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

			Log.i("Payment Sumitted Response", result);


			if (response.getStatusLine().getStatusCode() == 201) {
				//Do something
				paymentResponse.Success = true;
			}
			else {
				paymentResponse.ReasonPhrase = response.getStatusLine().getReasonPhrase();
				paymentResponse.Success = false;
			}
		}

		return paymentResponse;
	}

	public static Response requestMoney(PaymentRequest paymentRequest) {
		Response paymentResponse = new Response();

		HttpResponse response = null;

		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost request = new HttpPost(ROOTURL + REQUESTMONEY_URL);

			JSONObject json = new JSONObject();
			json.put("apiKey", APIKEY);
			json.put("senderId", paymentRequest.UserId);
			json.put("senderId", paymentRequest.UserId);
			json.put("securityPin", paymentRequest.SecurityPin);
			json.put("senderUri", paymentRequest.SenderUri);
			json.put("recipientUri", paymentRequest.RecipientUri);
			json.put("amount", paymentRequest.Amount);
			json.put("comments", paymentRequest.Comments);
			json.put("senderAccountId", paymentRequest.SenderAccountId);
			json.put("messageType", "PaymentRequest");
			json.put("latitude", paymentRequest.Latitude);
			json.put("longitude", paymentRequest.Longitude);

			StringEntity entity = new StringEntity(json.toString());
			request.setEntity(entity);
			request.setHeader("content-type", "application/json");

			response = httpClient.execute(request);

		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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

			Log.i("Payment Request Submitted", result);

			if (response.getStatusLine().getStatusCode() == 201) {
				//Do something
				paymentResponse.Success = true;
			}
			else {
				paymentResponse.ReasonPhrase = response.getStatusLine().getReasonPhrase();
				paymentResponse.Success = false;
			}
		}

		return paymentResponse;
	}
}
