package me.pdthx.Services;

import java.io.IOException;
import java.io.InputStream;

import me.pdthx.RestClient;
import me.pdthx.Requests.SubmitPaymentRequest;
import me.pdthx.Responses.SubmitPaymentResponse;

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

	private static final String ROOTURL = "http://beta.paidthx.com";
	private static final String SUBMITPAYMENT_URL = "/Services/PaymentService/Payments?apiKey=bda11d91-7ade-4da1-855d-24adfe39d174";
	private static final String APIKEY = "bda11d91-7ade-4da1-855d-24adfe39d174";

	public SubmitPaymentResponse SubmitPayment(
			SubmitPaymentRequest submitPaymentRequest) {
		SubmitPaymentResponse submitPaymentResponse = new SubmitPaymentResponse();

		HttpResponse response = null;

		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost request = new HttpPost(ROOTURL + SUBMITPAYMENT_URL);

			JSONObject json = new JSONObject();
			json.put("apiKey", APIKEY);
			json.put("userId", submitPaymentRequest.UserId);
			json.put("securityPin", submitPaymentRequest.SecurityPin);
			json.put("fromMobileNumber", submitPaymentRequest.FromMobileNumber);
			json.put("toMobileNumber", submitPaymentRequest.Recipient);
			json.put("amount", submitPaymentRequest.Amount.toString());
			json.put("comment", submitPaymentRequest.Comments);
			json.put("paymentAccountId", submitPaymentRequest.PaymentAccountId);

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
				submitPaymentResponse.Amount = jsonResult.getDouble("amount");
				submitPaymentResponse.Comment = jsonResult.getString("comment");
				submitPaymentResponse.FromMobileNumber = jsonResult
						.getString("fromMobileNumber");
				submitPaymentResponse.ToMobileNumber = jsonResult
						.getString("toMobileNumber");
				submitPaymentResponse.Message = jsonResult.getString("message");
				submitPaymentResponse.PaymentId = jsonResult
						.getString("paymentId");
				submitPaymentResponse.ResponseStatus = jsonResult
						.getString("responseStatus");
				submitPaymentResponse.Success = jsonResult.getBoolean("success");
				submitPaymentResponse.UserId = jsonResult.getString("userId");

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return submitPaymentResponse;
	}
}
