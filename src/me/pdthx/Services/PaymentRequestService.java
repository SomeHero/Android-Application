package me.pdthx.Services;

import java.io.IOException;
import java.io.InputStream;

import me.pdthx.RestClient;
import me.pdthx.Requests.PaymentRequestRequest;
import me.pdthx.Responses.PaymentRequestResponse;

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

public class PaymentRequestService {
	private static final String SUBMITPAYMENTREQUEST_URL = "http://www.pdthx.me/Services/PaymentRequestService/PaymentRequests?apiKey=bda11d91-7ade-4da1-855d-24adfe39d174";
	private static final String APIKEY = "bda11d91-7ade-4da1-855d-24adfe39d174";

	public PaymentRequestResponse SendPaymentRequest(PaymentRequestRequest paymentRequest) {
		PaymentRequestResponse paymentRequestResponse = new PaymentRequestResponse();

		HttpResponse response = null;

		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost request = new HttpPost(SUBMITPAYMENTREQUEST_URL);

			JSONObject json = new JSONObject();
			json.put("apiKey", paymentRequest.ApiKey);
			json.put("userId", paymentRequest.UserId);
			json.put("deviceId", paymentRequest.DeviceId);
			json.put("recipientUri", paymentRequest.RecipientUri);
			json.put("amount", paymentRequest.Amount.toString());
			json.put("comments", paymentRequest.Comments);
			
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

			JSONObject jsonResult = null;
			try {
				jsonResult = new JSONObject(result);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			boolean success = false;
			String message = "";
			try {
				paymentRequestResponse.Success = jsonResult.getBoolean("success");
				paymentRequestResponse.Message = jsonResult.getString("message");


			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return paymentRequestResponse;
	}
}
