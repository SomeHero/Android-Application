package me.pdthx.Services;

import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;

import me.pdthx.RestClient;
import me.pdthx.Requests.ACHAccountSetupRequest;
import me.pdthx.Requests.UserAcknowledgementRequest;
import me.pdthx.Requests.UserRegistrationRequest;
import me.pdthx.Requests.UserRequest;
import me.pdthx.Requests.UserSetupPasswordRequest;
import me.pdthx.Requests.UserSetupSecurityPinRequest;
import me.pdthx.Requests.UserSignInRequest;
import me.pdthx.Requests.UserVerifyMobileDeviceRequest;
import me.pdthx.Responses.ACHAccountSetupResponse;
import me.pdthx.Responses.UserAcknowledgementResponse;
import me.pdthx.Responses.UserRegistrationResponse;
import me.pdthx.Responses.UserResponse;
import me.pdthx.Responses.UserSetupPasswordResponse;
import me.pdthx.Responses.UserSetupSecurityPinResponse;
import me.pdthx.Responses.UserSignInResponse;
import me.pdthx.Responses.UserVerifyMobileDeviceResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class UserService {
	private static final String ROOTURL = "http://beta.paidthx.com";
	private static final String APIKEY = "bda11d91-7ade-4da1-855d-24adfe39d174";
    private static final String ACKNOWLEDGEUSER_URL = "/Services/UserService/UserAcknowledgement?apiKey=bda11d91-7ade-4da1-855d-24adfe39d174";
	private static final String SETUPACHACCOUNT_URL = "/Services/PaymentAccountService/PaymentAccounts?apiKey=bda11d91-7ade-4da1-855d-24adfe39d174";
	private static final String USERSIGNINSERVICE_URL = "/Services/UserService/SignIn?apiKey=bda11d91-7ade-4da1-855d-24adfe39d174";
	private static final String USERVERIFYMOBILEDEVICESERVICE_URL = "/Services/UserService/VerifyMobileDevice?apiKey=bda11d91-7ade-4da1-855d-24adfe39d174";
	private static final String USERSETUPPASSWORDSERVICE_URL = "/Services/UserService/SetupPassword?apiKey=bda11d91-7ade-4da1-855d-24adfe39d174";
	private static final String REGISTER_URL = "/Services/UserService/Register?apiKey=bda11d91-7ade-4da1-855d-24adfe39d174";
	private static final String USERSETUPSECURITYPIN_URL = "/Services/UserService/SetupSecurityPin?apiKey=bda11d91-7ade-4da1-855d-24adfe39d174";
	private static final String USER_URL = "/Services/UserService/Users/";
	public UserService() {

	}

	public UserAcknowledgementResponse AcknowledgeUser(
			UserAcknowledgementRequest userAcknowledgementRequest) {

		UserAcknowledgementResponse userAcknowledgementResponse = new UserAcknowledgementResponse();

		HttpResponse response = null;
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost request = new HttpPost(ROOTURL + ACKNOWLEDGEUSER_URL);

			JSONObject json = new JSONObject();
			json.put("mobileNumber", userAcknowledgementRequest.MobileNumber);
			json.put("deviceId", userAcknowledgementRequest.DeviceId);

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

			Log.i("User Acknowledgement", result);

			JSONObject jsonResult = null;
			try {
				jsonResult = new JSONObject(result);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				userAcknowledgementResponse.UserId = jsonResult.getString("userId");
				userAcknowledgementResponse.PaymentAccountId = jsonResult.getString("paymentAccountId");
				userAcknowledgementResponse.DoesDeviceIdMatch = jsonResult
						.getBoolean("doesDeviceIdMatch");
				userAcknowledgementResponse.IsMobileNumberRegistered = jsonResult
						.getBoolean("isMobileNumberRegistered");
				userAcknowledgementResponse.SetupSecurityPin = jsonResult.getBoolean("setupSecurityPin");
				userAcknowledgementResponse.SetupPassword = jsonResult.getBoolean("setupPassword");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return userAcknowledgementResponse;
	}
	public ACHAccountSetupResponse SetupACHAccount(ACHAccountSetupRequest achAccountSetupRequest) {
		ACHAccountSetupResponse achAccountSetupResponse = new ACHAccountSetupResponse();
		HttpResponse response = null;
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost request = new HttpPost(ROOTURL + SETUPACHACCOUNT_URL);

			JSONObject json = new JSONObject();
			json.put("userId", achAccountSetupRequest.UserId);
			json.put("nameOnAccount", achAccountSetupRequest.NameOnAccount);
			json.put("routingNumber", achAccountSetupRequest.RoutingNumber);
			json.put("accountNumber", achAccountSetupRequest.AccountNumber);
			json.put("accountType", achAccountSetupRequest.AccountType);

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
			String result = "";
			try {
				instream = entity.getContent();
				result = RestClient.convertStreamToString(instream);
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

			JSONObject jsonResult = null;
			try {
				jsonResult = new JSONObject(result);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				achAccountSetupResponse.PaymentAccountId = jsonResult.getString("paymentAccountId");
				achAccountSetupResponse.Success = true;
				achAccountSetupResponse.AccountInformation = jsonResult.getString("accountInformation");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return achAccountSetupResponse;
	}
	public UserResponse GetUser(
			UserRequest userRequest) {

		UserResponse userResponse = new UserResponse();

		HttpResponse response = null;
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet request = new HttpGet(ROOTURL + USER_URL  + userRequest.UserId + "?apiKey=" + APIKEY);

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

			Log.i("User", result);

			JSONObject jsonResult = null;
			try {
				jsonResult = new JSONObject(result);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if(jsonResult == null)
				return null;

			try {
				userResponse.UserId = jsonResult.getString("userId");
		        userResponse.MobileNumber = jsonResult.getString("mobileNumber");
		        userResponse.EmailAddress = jsonResult.getString("emailAddress");
		        userResponse.UserName = jsonResult.getString("userName");
		        userResponse.UserStatus = jsonResult.getString("userStatus");
		        userResponse.FirstName = jsonResult.getString("firstName");
		        userResponse.LastName = jsonResult.getString("lastName");
		        userResponse.TotalMoneySent = jsonResult.getDouble("totalMoneySent");
		        userResponse.TotalMoneyReceived = jsonResult.getDouble("totalMoneyReceived");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return userResponse;
	}
	public UserSignInResponse SignInUser(UserSignInRequest userSignInRequest) {
		UserSignInResponse userSignInResponse = new UserSignInResponse();
		HttpResponse response = null;
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost request = new HttpPost(ROOTURL + USERSIGNINSERVICE_URL);

			JSONObject json = new JSONObject();
			json.put("userName", userSignInRequest.UserName);
			json.put("password", userSignInRequest.Password);

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
			String result = "";
			try {
				instream = entity.getContent();
				result = RestClient.convertStreamToString(instream);
				// Log.i(TAG, "Result of conversion: [" + result +
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

			Log.i("User Sign In", result);

			JSONObject jsonResult = null;
			try {
				jsonResult = new JSONObject(result);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				userSignInResponse.IsValid = jsonResult.getBoolean("isValid");
				userSignInResponse.UserId = jsonResult.getString("userId");
				userSignInResponse.MobileNumber = jsonResult
						.getString("mobileNumber");
				userSignInResponse.SetupPassword = jsonResult.getBoolean("setupPassword");
				userSignInResponse.SetupSecurityPin = jsonResult.getBoolean("setupSecurityPin");
				userSignInResponse.PaymentAccountId = jsonResult
						.getString("paymentAccountId");

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return userSignInResponse;
	}
	public UserVerifyMobileDeviceResponse VerifyMobileDevice(UserVerifyMobileDeviceRequest userVerifyMobileDeviceRequest) {
		UserVerifyMobileDeviceResponse userVerifyMobileDeviceResponse = new UserVerifyMobileDeviceResponse();

		HttpResponse response = null;
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost request = new HttpPost(ROOTURL + USERVERIFYMOBILEDEVICESERVICE_URL);

			JSONObject json = new JSONObject();
			json.put("userId",  userVerifyMobileDeviceRequest.UserId);
			json.put("mobileNumber", userVerifyMobileDeviceRequest.MobileNumber);
			json.put("deviceId", userVerifyMobileDeviceRequest.DeviceId);
			json.put("verificationCode1", userVerifyMobileDeviceRequest.VerificationCode1);
			json.put("verificationCode2", userVerifyMobileDeviceRequest.VerificationCode2);

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

			Log.i("Mobile Device Verification", result);

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
				userVerifyMobileDeviceResponse.Success = jsonResult.getBoolean("success");
				userVerifyMobileDeviceResponse.Message = jsonResult.getString("message");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return userVerifyMobileDeviceResponse;
	}
	public UserSetupPasswordResponse SetupPassword(UserSetupPasswordRequest userSetupPasswordRequest) {
		UserSetupPasswordResponse userSetupPasswordResponse = new UserSetupPasswordResponse();

		HttpResponse response = null;
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost request = new HttpPost(ROOTURL + USERSETUPPASSWORDSERVICE_URL);

			JSONObject json = new JSONObject();
			json.put("userId",  userSetupPasswordRequest.UserId);
			json.put("deviceId", userSetupPasswordRequest.DeviceId);
			json.put("userName", userSetupPasswordRequest.UserName);
			json.put("password", userSetupPasswordRequest.Password);

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

			Log.i("Mobile Device Verification", result);

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
				userSetupPasswordResponse.Success = jsonResult.getBoolean("success");
				userSetupPasswordResponse.Message = jsonResult.getString("message");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return userSetupPasswordResponse;
	}
	public UserRegistrationResponse RegisterUser(UserRegistrationRequest userRegistrationRequest)
	{
		UserRegistrationResponse userRegistrationResponse = new UserRegistrationResponse();
		HttpResponse response = null;

		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost request = new HttpPost(ROOTURL + REGISTER_URL);

			JSONObject json = new JSONObject();

			json.put("apiKey", APIKEY);
			json.put("userName", userRegistrationRequest.UserName);
			json.put("emailAddress", userRegistrationRequest.EmailAddress);
			json.put("password", userRegistrationRequest.Password);
			json.put("mobileNumber", userRegistrationRequest.MobileNumber);
			json.put("deviceId", userRegistrationRequest.DeviceId);
			json.put("securityPin", userRegistrationRequest.SecurityPin);

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
			String result = "";
			try {
				instream = entity.getContent();
				result = RestClient.convertStreamToString(instream);
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

			JSONObject jsonResult = null;
			try {
				jsonResult = new JSONObject(result);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				userRegistrationResponse.Success = jsonResult.getBoolean("success");
				userRegistrationResponse.Message = jsonResult.getString("message");
				userRegistrationResponse.UserId = jsonResult.getString("userId");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return userRegistrationResponse;

	}
	public UserSetupSecurityPinResponse SetupSecurityPin(UserSetupSecurityPinRequest userSetupSecurityPinRequest) {
		UserSetupSecurityPinResponse userSetupSecurityPinResponse = new UserSetupSecurityPinResponse();

		HttpResponse response = null;
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost request = new HttpPost(ROOTURL + USERSETUPSECURITYPIN_URL);

			JSONObject json = new JSONObject();
			json.put("userId",  userSetupSecurityPinRequest.UserId);
			json.put("deviceId", userSetupSecurityPinRequest.DeviceId);
			json.put("securityPin", userSetupSecurityPinRequest.SecurityPin);

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

			Log.i("User Setup Security Pin", result);

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
				userSetupSecurityPinResponse.Success = jsonResult.getBoolean("success");
				userSetupSecurityPinResponse.Message = jsonResult.getString("message");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return userSetupSecurityPinResponse;
	}
}
