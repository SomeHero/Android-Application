package me.pdthx.Services;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import me.pdthx.Requests.ACHAccountDeleteRequest;
import me.pdthx.Requests.ACHAccountDetailRequest;
import me.pdthx.Requests.ACHAccountUpdateRequest;
import me.pdthx.Responses.ACHAccountResponse;
import me.pdthx.Responses.Response;
import me.pdthx.Responses.UserSignInResponse;

public class PaymentAcctService {
	private static final String ROOTURL = "http://23.21.203.171/api/internal/api";
	private static final String APIKEY = "bda11d91-7ade-4da1-855d-24adfe39d174";
	private static final String RETRIEVEORADD_ACCTS = "/paymentaccounts";
	//private static final String VERIFYACCT = "/paymentaccounts/%s/verify_account";
	private static final String UPDATEORDELETEACCT = "/paymentaccounts/%s";
	private static final String USER_URL = "/Users/";
	private static final String PREFERREDSEND_URL = "/Users/%s/paymentaccounts/set_preferred_send_account";
	private static final String PREFERREDRECEIVE_URL = "/Users/%s/paymentaccounts/set_preferred_receive_account";

	public static ArrayList<ACHAccountResponse> getAccounts(
			UserSignInResponse userInfo) {
		ArrayList<ACHAccountResponse> messageResponses = new ArrayList<ACHAccountResponse>();
		HttpResponse response = null;
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet request = new HttpGet(ROOTURL + USER_URL + userInfo.UserId
					+ RETRIEVEORADD_ACCTS + "?apiKey=" + APIKEY);

			request.setHeader("content-type", "application/json");

			response = httpClient.execute(request);
		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		HttpEntity entity = response.getEntity();

		if (entity != null) {

			InputStream instream = null;
			String result = "";
			try {
				instream = entity.getContent();
				result = RestClient.convertStreamToString(instream);
				// Log.i(TAG, "Result of conversation: [" + result +
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

			Log.i("User", result);

			JSONArray messages = null;
			try {
				messages = new JSONArray(result);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}

			try {
				// Loop the Array
				for (int i = 0; i < messages.length(); i++) {
					JSONObject jsonResult = messages.getJSONObject(i);

					ACHAccountResponse messageResponse = new ACHAccountResponse();

					messageResponse.UserId = jsonResult.getString("UserId");
					messageResponse.BankId = jsonResult.getString("Id");
					messageResponse.AccountNumber = jsonResult
							.getString("AccountNumber");
					messageResponse.AccountType = jsonResult
							.getString("AccountType");
					messageResponse.NameOnAccount = jsonResult
							.getString("NameOnAccount");
					messageResponse.RoutingNumber = jsonResult
							.getString("RoutingNumber");
					messageResponse.Nickname = jsonResult.getString("Nickname");

					messageResponses.add(messageResponse);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return messageResponses;
	}

	public static ACHAccountResponse getDetailsForAccount(
			ACHAccountDetailRequest achAccountDetailRequest) {
		ACHAccountResponse achAccountResponse = new ACHAccountResponse();

		HttpResponse response = null;
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet request = new HttpGet(ROOTURL
					+ USER_URL
					+ achAccountDetailRequest.UserId
					+ String.format(UPDATEORDELETEACCT,
							achAccountDetailRequest.AccountId) + "?apiKey="
					+ APIKEY);

			request.setHeader("content-type", "application/json");

			response = httpClient.execute(request);
		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		HttpEntity entity = response.getEntity();

		if (entity != null) {

			InputStream instream = null;
			String result = "";
			try {
				instream = entity.getContent();
				result = RestClient.convertStreamToString(instream);
				// Log.i(TAG, "Result of conversation: [" + result +
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

			Log.i("User", result);

			JSONObject jsonResult = null;
			try {
				jsonResult = new JSONObject(result);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (jsonResult == null)
				return null;

			try {
				achAccountResponse.NameOnAccount = jsonResult
						.getString("nameOnAccount");
				achAccountResponse.AccountNumber = jsonResult
						.getString("accountNumber");
				achAccountResponse.AccountType = jsonResult
						.getString("accountType");
				achAccountResponse.RoutingNumber = jsonResult
						.getString("routingNumber");
				achAccountResponse.SecurityPin = jsonResult
						.getString("securityPin");
				achAccountResponse.Nickname = jsonResult.getString("nickname");

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (response.getStatusLine().getStatusCode() == 201) {
				try {
					achAccountResponse.Success = true;
					achAccountResponse.BankId = jsonResult
							.getString("paymentAccountId");

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				achAccountResponse.Success = false;
				achAccountResponse.ReasonPhrase = response.getStatusLine()
						.getReasonPhrase();
			}
		}

		return achAccountResponse;
	}

	public static ACHAccountResponse updateAccount(
			ACHAccountUpdateRequest achAccountUpdateRequest) {

		ACHAccountResponse achAccountResponse = new ACHAccountResponse();

		HttpResponse response = null;

		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPut request = new HttpPut(ROOTURL
					+ USER_URL
					+ achAccountUpdateRequest.UserId
					+ String.format(UPDATEORDELETEACCT,
							achAccountUpdateRequest.BankId) + "?apiKey="
					+ APIKEY);

			JSONObject json = new JSONObject();
			json.put("apiKey", APIKEY);
			json.put("nameOnAccount", achAccountUpdateRequest.NameOnAccount);
			json.put("routingNumber", achAccountUpdateRequest.RoutingNumber);
			json.put("accountType", achAccountUpdateRequest.AccountType);
			json.put("nickname", achAccountUpdateRequest.Nickname);

			StringEntity entity = new StringEntity(json.toString());
			request.setEntity(entity);
			request.setHeader("content-type", "application/json");

			response = httpClient.execute(request);
		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpEntity entity = response.getEntity();

		if (entity != null) {

			InputStream instream = null;
			try {
				instream = entity.getContent();
				// Log.i(TAG, "Result of converstion: [" + result + "]");
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

			if (response.getStatusLine().getStatusCode() == 200) {
				achAccountResponse.Success = true;
			} else {
				achAccountResponse.Success = false;
				achAccountResponse.ReasonPhrase = response.getStatusLine()
						.getReasonPhrase();
			}

		}

		return achAccountResponse;

	}

	public static Response updatePreferredSendAcct(
			ACHAccountDetailRequest achAccount) {
		Response updateResponse = new Response();
		HttpResponse response = null;

		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost request = new HttpPost(ROOTURL
					+ String.format(PREFERREDSEND_URL, achAccount.UserId)
					+ "?apiKey=" + APIKEY);

			JSONObject json = new JSONObject();
			json.put("apiKey", APIKEY);
			json.put("SecurityPin", achAccount.SecurityPin);
			json.put("PaymentAccountId", achAccount.AccountId);
			// json.put("nameOnAccount", achAccountSetupRequest.NameOnAccount);

			StringEntity entity = new StringEntity(json.toString());
			request.setEntity(entity);
			request.setHeader("content-type", "application/json");

			response = httpClient.execute(request);
		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (response.getStatusLine().getStatusCode() == 200
				|| response.getStatusLine().getStatusCode() == 201) {
			updateResponse.Success = true;
		} else {
			updateResponse.Success = false;
			updateResponse.ReasonPhrase = response.getStatusLine()
					.getReasonPhrase();
		}

		return updateResponse;
	}

	public static Response updatePreferredReceiveAcct(
			ACHAccountDetailRequest achAccount) {
		Response updateResponse = new Response();
		HttpResponse response = null;

		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost request = new HttpPost(ROOTURL
					+ String.format(PREFERREDRECEIVE_URL, achAccount.UserId)
					+ "?apiKey=" + APIKEY);

			JSONObject json = new JSONObject();
			json.put("apiKey", APIKEY);
			json.put("SecurityPin", achAccount.SecurityPin);
			json.put("PaymentAccountId", achAccount.AccountId);
			// json.put("nameOnAccount", achAccountSetupRequest.NameOnAccount);

			StringEntity entity = new StringEntity(json.toString());
			request.setEntity(entity);
			request.setHeader("content-type", "application/json");

			response = httpClient.execute(request);
		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (response.getStatusLine().getStatusCode() == 200
				|| response.getStatusLine().getStatusCode() == 201) {
			updateResponse.Success = true;
		} else {
			updateResponse.Success = false;
			updateResponse.ReasonPhrase = response.getStatusLine()
					.getReasonPhrase();
		}

		return updateResponse;
	}

	public static Response deleteAccount(
			ACHAccountDeleteRequest achAccountDeleteRequest) {
		Response responseResult = new Response();
		HttpResponse response = null;

		try {

			HttpClient httpClient = new DefaultHttpClient();

			HttpDelete request = new HttpDelete(ROOTURL
					+ USER_URL
					+ achAccountDeleteRequest.UserId
					+ String.format(UPDATEORDELETEACCT,
							achAccountDeleteRequest.BankId) + "?apiKey="
					+ APIKEY);

			request.setHeader("content-type", "application/json");

			response = httpClient.execute(request);

		} catch (ClientProtocolException e1) {

			// TODO Auto-generated catch block

			e1.printStackTrace();

		} catch (IOException e1) {

			// TODO Auto-generated catch block

			e1.printStackTrace();

		}

		if (response.getStatusLine().getStatusCode() == 200) {
			responseResult.Success = true;
		} else {
			responseResult.Success = false;
			responseResult.ReasonPhrase = response.getStatusLine()
					.getReasonPhrase();
		}

		return responseResult;
	}
}
