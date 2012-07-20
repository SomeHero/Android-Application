package me.pdthx.Services;

import me.pdthx.Requests.UserAnswerQuestionRequest;
import me.pdthx.Requests.UserSetupSecurityPinRequest;
import me.pdthx.Requests.UserChangePasswordRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import me.pdthx.Models.MeCodeModel;
import me.pdthx.Requests.ACHAccountSetupRequest;
import me.pdthx.Requests.UserChangeSecurityPinRequest;
import me.pdthx.Requests.UserCheckImageRequest;
import me.pdthx.Requests.UserFBSignInRequest;
import me.pdthx.Requests.UserMeCodeRequest;
import me.pdthx.Requests.UserPersonalRequest;
import me.pdthx.Requests.UserPushNotificationRequest;
import me.pdthx.Requests.UserRegistrationRequest;
import me.pdthx.Requests.UserRequest;
import me.pdthx.Requests.UserSignInRequest;
import me.pdthx.Responses.ACHAccountSetupResponse;
import me.pdthx.Responses.Response;
import me.pdthx.Responses.SecurityQuestionResponse;
import me.pdthx.Responses.UserCheckImageResponse;
import me.pdthx.Responses.UserMeCodeResponse;
import me.pdthx.Responses.UserRegistrationResponse;
import me.pdthx.Responses.UserResponse;
import me.pdthx.Responses.UserSignInResponse;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class UserService {
	private static final String ROOTURL = "http://23.21.203.171/api/internal/api";
	private static final String APIKEY = "bda11d91-7ade-4da1-855d-24adfe39d174";
	private static final String USERSIGNINSERVICE_URL = "/Users/Validate_User?apiKey=bda11d91-7ade-4da1-855d-24adfe39d174";
	private static final String USERFBSIGNINSERVICE_URL = "/Users/signin_withfacebook?apiKey=bda11d91-7ade-4da1-855d-24adfe39d174";
	private static final String REGISTER_URL = "/Users?apiKey=bda11d91-7ade-4da1-855d-24adfe39d174";
	private static final String SETUPSECURITYPIN_URL = "/Users/%s/Setup_SecurityPin?apiKey=bda11d91-7ade-4da1-855d-24adfe39d174";
	private static final String CHANGESECURITYPIN_URL = "/Users/%s/change_securitypin";
	private static final String CHANGEPASSWORD_URL = "/Users/%s/change_password";
	private static final String RESETPASSWORD_URL = "/Users/reset_password";
	private static final String MECODE_URL = "/Users/%s/mecodes";
	private static final String PUSHNOTIFICATION_URL = "/Users/%s/RegisterPushNotifications";
	private static final String USER_URL = "/Users/";
	private static final String SETUPACHACCOUNT_URL = "/Users/%s/PaymentAccounts?apiKey=bda11d91-7ade-4da1-855d-24adfe39d174";
	private static final String PERSONALIZE_URL = "/Users/%s/personalize_user?apiKey=bda11d91-7ade-4da1-855d-24adfe39d174";
	private static final String SECURITYQUESTIONS_URL = "/securityquestions?apiKey=bda11d91-7ade-4da1-855d-24adfe39d174";
	private static final String VALIDATESECURITYQUESTION_URL = "/Users/%s/validate_security_question";
	private static final String CHECKIMAGE_URL = "/Users/%s/paymentaccounts/upload_check_image";

	public UserService() {

	}

	public static UserResponse getUser(UserRequest userRequest) {

		UserResponse userResponse = new UserResponse();

		HttpResponse response = null;
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet request = new HttpGet(ROOTURL + USER_URL
					+ userRequest.UserId + "?apiKey=" + APIKEY);

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
				userResponse.UserId = jsonResult.getString("userId");
				userResponse.RegistrationId = jsonResult.getString("registrationId");
				userResponse.DeviceToken = jsonResult.getString("deviceToken");
				userResponse.MobileNumber = jsonResult.getString("mobileNumber");
				userResponse.EmailAddress = jsonResult.getString("emailAddress");

				userResponse.UserName = jsonResult.getString("userName");
				userResponse.UserStatus = jsonResult.getString("userStatus");
				userResponse.FirstName = jsonResult.getString("firstName");
				userResponse.LastName = jsonResult.getString("lastName");
				userResponse.UpperLimit = jsonResult.getInt("upperLimit");
				userResponse.ImageUrl = jsonResult.getString("imageUrl");
				userResponse.TotalMoneySent = jsonResult
						.getDouble("totalMoneySent");
				userResponse.TotalMoneyReceived = jsonResult
						.getDouble("totalMoneyReceived");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return userResponse;
	}

	public static UserSignInResponse signInUser(
			UserSignInRequest userSignInRequest) {
		UserSignInResponse userSignInResponse = new UserSignInResponse();
		HttpResponse response = null;
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost request = new HttpPost(ROOTURL + USERSIGNINSERVICE_URL);

			JSONObject json = new JSONObject();
			json.put("userName", userSignInRequest.Login);
			json.put("password", userSignInRequest.Password);
			//json.put("deviceToken", userFBSignInRequest.DeviceToken);

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

			Log.i("User Sign In", result);

			JSONObject jsonResult = null;
			try {
				jsonResult = new JSONObject(result);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (response.getStatusLine().getStatusCode() == 200) {

				try {
					userSignInResponse.Success = true;
					userSignInResponse.UserId = jsonResult.getString("userId");
					userSignInResponse.MobileNumber = jsonResult
							.getString("mobileNumber");
					userSignInResponse.SetupSecurityPin = jsonResult
							.getBoolean("setupSecurityPin");
					userSignInResponse.PaymentAccountId = jsonResult
							.getString("paymentAccountId");
					userSignInResponse.UpperLimit = jsonResult
							.getInt("upperLimit");
					userSignInResponse.hasACHAccount = jsonResult
					    .getBoolean("hasACHAccount");

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				userSignInResponse.Success = false;
				userSignInResponse.ReasonPhrase = response.getStatusLine()
						.getReasonPhrase();
			}
		}

		return userSignInResponse;
	}

	public static UserSignInResponse signInUser(
			UserFBSignInRequest userFBSignInRequest) {
		UserSignInResponse userSignInResponse = new UserSignInResponse();
		HttpResponse response = null;
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost request = new HttpPost(ROOTURL + USERFBSIGNINSERVICE_URL);

			JSONObject json = new JSONObject();
			json.put("apiKey", APIKEY);
			json.put("accountId", userFBSignInRequest.IDNumber);
			json.put("firstName", userFBSignInRequest.FirstName);
			json.put("lastName", userFBSignInRequest.LastName);
			json.put("emailAddress", userFBSignInRequest.Email);
			json.put("deviceToken", userFBSignInRequest.DeviceToken);

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

			Log.i("User Sign In", result);

			JSONObject jsonResult = null;
			try {
				jsonResult = new JSONObject(result);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() == 201) {

				try {
					userSignInResponse.Success = true;
					userSignInResponse.UserId = jsonResult.getString("userId");
					userSignInResponse.MobileNumber = jsonResult
							.getString("mobileNumber");
					userSignInResponse.SetupSecurityPin = jsonResult
							.getBoolean("hasSecurityPin");
					userSignInResponse.PaymentAccountId = jsonResult
							.getString("paymentAccountId");
					userSignInResponse.hasACHAccount = jsonResult
                        .getBoolean("hasACHAccount");

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				userSignInResponse.Success = false;
				userSignInResponse.ReasonPhrase = response.getStatusLine()
						.getReasonPhrase();
			}
		}

		return userSignInResponse;
	}

	public static UserRegistrationResponse registerUser(
			UserRegistrationRequest userRegistrationRequest) {
		UserRegistrationResponse userRegistrationResponse = new UserRegistrationResponse();
		HttpResponse response = null;

		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost request = new HttpPost(ROOTURL + REGISTER_URL);

			JSONObject json = new JSONObject();

			json.put("apiKey", APIKEY);
			json.put("userName", userRegistrationRequest.UserName);
			json.put("emailAddress", userRegistrationRequest.UserName);
			json.put("password", userRegistrationRequest.Password);
			json.put("deviceToken", userRegistrationRequest.DeviceToken);

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

			if (response.getStatusLine().getStatusCode() == 201) {
				try {
					userRegistrationResponse.Success = true;
					userRegistrationResponse.UserId = jsonResult
							.getString("userId");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				userRegistrationResponse.Success = false;
				userRegistrationResponse.ReasonPhrase = response
						.getStatusLine().getReasonPhrase();
			}

		}

		return userRegistrationResponse;

	}

	public static ACHAccountSetupResponse setupACHAccount(
			ACHAccountSetupRequest achAccountSetupRequest) {
		ACHAccountSetupResponse achAccountSetupResponse = new ACHAccountSetupResponse();
		HttpResponse response = null;
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost request = new HttpPost(ROOTURL
					+ String.format(SETUPACHACCOUNT_URL,
							achAccountSetupRequest.UserId));

			JSONObject json = new JSONObject();
			json.put("apiKey", APIKEY);
			json.put("nameOnAccount", achAccountSetupRequest.NameOnAccount);
			json.put("routingNumber", achAccountSetupRequest.RoutingNumber);
			json.put("accountNumber", achAccountSetupRequest.AccountNumber);
			json.put("accountType", achAccountSetupRequest.AccountType);
			json.put("securityPin", achAccountSetupRequest.SecurityPin);
			json.put("securityQuestionAnswer",
					achAccountSetupRequest.SecurityAnswer);
			json.put("securityQuestionId",
					achAccountSetupRequest.SecurityQuestionId);
			json.put("nickname", achAccountSetupRequest.Nickname);

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

			if (response.getStatusLine().getStatusCode() == 201) {

				try {
					achAccountSetupResponse.PaymentAccountId = jsonResult
							.getString("paymentAccountId");
					achAccountSetupResponse.Success = true;
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				achAccountSetupResponse.Success = false;
				achAccountSetupResponse.ReasonPhrase = response.getStatusLine()
						.getReasonPhrase();
			}

		}

		return achAccountSetupResponse;
	}

	public static Response setupSecurityPin(
			UserSetupSecurityPinRequest userSecurityPinRequest) {
		Response setupPinResponse = new Response();
		HttpResponse response = null;
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost request = new HttpPost(ROOTURL
					+ String.format(SETUPSECURITYPIN_URL,
							userSecurityPinRequest.UserId));

			JSONObject json = new JSONObject();
			json.put("securityPin", userSecurityPinRequest.SecurityPin);

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

		if (response.getStatusLine().getStatusCode() == 200) {

			setupPinResponse.Success = true;
		} else {
			setupPinResponse.Success = false;
			setupPinResponse.ReasonPhrase = response.getStatusLine()
					.getReasonPhrase();
		}

		return setupPinResponse;
	}

	public static Response changeSecurityPin(UserChangeSecurityPinRequest userSecurityPinRequest) {
		Response changePinResponse = new Response();
		HttpResponse response = null;
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost request = new HttpPost(ROOTURL
					+ String.format(CHANGESECURITYPIN_URL,
							userSecurityPinRequest.UserId));

			JSONObject json = new JSONObject();
			json.put("currentSecurityPin",
					userSecurityPinRequest.CurrentSecurityPin);
			json.put("newSecurityPin", userSecurityPinRequest.NewSecurityPin);

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

		if (response.getStatusLine().getStatusCode() == 200) {

			changePinResponse.Success = true;
		} else {
			changePinResponse.Success = false;
			changePinResponse.ReasonPhrase = response.getStatusLine()
					.getReasonPhrase();
		}

		return changePinResponse;
	}

	public static Response changePassword(UserChangePasswordRequest userChangePasswordRequest) {
	    HttpResponse response = null;
	    Response changePasswordResponse = new Response();
	    try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost request = new HttpPost(ROOTURL
                    + String.format(CHANGEPASSWORD_URL,
                            userChangePasswordRequest.UserId));

            JSONObject json = new JSONObject();
            json.put("currentPassword",
                    userChangePasswordRequest.CurrentPassword);
            json.put("newPassword", userChangePasswordRequest.NewPassword);

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

        if (response.getStatusLine().getStatusCode() == 200) {

            changePasswordResponse.Success = true;
        } else {
            changePasswordResponse.Success = false;
            changePasswordResponse.ReasonPhrase = response.getStatusLine()
                    .getReasonPhrase();
        }

        return changePasswordResponse;
	}

	public static Response resetPassword(String emailAddress)
	{
	    HttpResponse response = null;
        Response resetPasswordResponse = new Response();
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost request = new HttpPost(ROOTURL
                    + RESETPASSWORD_URL);

            JSONObject json = new JSONObject();
            json.put("emailAddress", emailAddress);

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

        if (response.getStatusLine().getStatusCode() == 200) {

            resetPasswordResponse.Success = true;
        } else {
            resetPasswordResponse.Success = false;
            resetPasswordResponse.ReasonPhrase = response.getStatusLine()
                    .getReasonPhrase();
        }

        return resetPasswordResponse;
	}

	public static Response createMeCode(UserMeCodeRequest userMeCodeRequest) {
		HttpResponse response = null;
		Response meCodeResponse = new Response();
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost request = new HttpPost(ROOTURL
					+ String.format(MECODE_URL, userMeCodeRequest.UserId));

			JSONObject json = new JSONObject();
			json.put("MeCode", userMeCodeRequest.getMeCodes().get(0));

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

		if (response.getStatusLine().getStatusCode() == 201) {
			meCodeResponse.Success = true;
		} else {
			meCodeResponse.Success = false;
			meCodeResponse.ReasonPhrase = response.getStatusLine()
					.getReasonPhrase();
		}

		return meCodeResponse;
	}

	public static UserMeCodeResponse getMeCodes(UserRequest userRequest) {
		UserMeCodeResponse userMeCodeResponse = new UserMeCodeResponse();
		HttpResponse response = null;
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost request = new HttpPost(ROOTURL
					+ String.format(MECODE_URL, userRequest.UserId));

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

			JSONArray jsonArray = null;

			try {
				jsonArray = new JSONArray(result);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
			int jsonArrayLength = jsonArray.length();
			if (response.getStatusLine().getStatusCode() == 201) {
				MeCodeModel[] meCodeArray = new MeCodeModel[jsonArrayLength];
				try {
					for (int i = 0; i < jsonArrayLength; i++) {
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						MeCodeModel meCodeModel = new MeCodeModel();

						meCodeModel.setId(jsonObject.getString("Id"));
						meCodeModel.setApprovedDate(jsonObject
								.getString("ApprovedDate"));
						meCodeModel.setCreateDate(jsonObject
								.getString("CreateDate"));
						meCodeModel
						.setActive(jsonObject.getBoolean("IsActive"));
						meCodeModel.setApproved(jsonObject
								.getBoolean("IsApproved"));
						meCodeModel.setMeCode(jsonObject.getString("MeCode"));

						meCodeArray[i] = meCodeModel;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

				userMeCodeResponse.populateMeCodes(meCodeArray);
				userMeCodeResponse.Success = true;
			} else {
				userMeCodeResponse.Success = false;
				userMeCodeResponse.ReasonPhrase = response.getStatusLine()
						.getReasonPhrase();
				return userMeCodeResponse;
			}
		}

		return userMeCodeResponse;
	}

	public static ArrayList<SecurityQuestionResponse> getSecurityQuestions() {
		ArrayList<SecurityQuestionResponse> messageResponses = new ArrayList<SecurityQuestionResponse>();
		HttpResponse response = null;
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet request = new HttpGet(ROOTURL + SECURITYQUESTIONS_URL);

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

					SecurityQuestionResponse messageResponse = new SecurityQuestionResponse();
					messageResponse.Question = jsonResult.getString("Question");
					messageResponse.Id = jsonResult.getInt("Id");
					messageResponse.IsActive = jsonResult.getString("IsActive") != null;

					// messageResponse.MessageId = jsonResult.getString("Id");

					messageResponses.add(messageResponse);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return messageResponses;
	}

	public static Response validateSecurityQuestion(UserAnswerQuestionRequest answerQuestionRequest)
	{
	    HttpResponse response = null;
	    Response serverResponse = new Response();
	    try {
	        HttpClient httpClient = new DefaultHttpClient();
	        HttpPost request = new HttpPost(ROOTURL +
	            String.format(VALIDATESECURITYQUESTION_URL, answerQuestionRequest.UserId));

	        JSONObject json = new JSONObject();
	        json.put("questionAnswer", answerQuestionRequest.Answer);
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

	    if (response.getStatusLine().getStatusCode() == 200) {
            serverResponse.Success = true;
        } else {
            serverResponse.Success = false;
            serverResponse.ReasonPhrase = response.getStatusLine()
                    .getReasonPhrase();
        }

        return serverResponse;
	}

	public static Response registerForPush(
			UserPushNotificationRequest userRequest) {
		HttpResponse response = null;
		Response serverResponse = new Response();
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost request = new HttpPost(ROOTURL +
					String.format(PUSHNOTIFICATION_URL, userRequest.UserId));

			JSONObject json = new JSONObject();
			json.put("registrationId", userRequest.RegistrationId);
			json.put("deviceToken", userRequest.DeviceToken);

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

		if (response.getStatusLine().getStatusCode() == 201) {
			serverResponse.Success = true;
		} else {
			serverResponse.Success = false;
			serverResponse.ReasonPhrase = response.getStatusLine()
					.getReasonPhrase();
		}

		return serverResponse;
	}

	public static Response setUserPersonalization(
			UserPersonalRequest userPersonalRequest) {
		Response userResponse = new Response();
		HttpResponse response = null;

		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost request = new HttpPost(
					ROOTURL
					+ String.format(PERSONALIZE_URL,
							userPersonalRequest.UserId));

			JSONObject json = new JSONObject();

			json.put("apiKey", APIKEY);
			json.put("firstName", userPersonalRequest.FirstName);
			json.put("lastName", userPersonalRequest.LastName);
			json.put("imageUri", userPersonalRequest.ImageUri);

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
		if (response.getStatusLine().getStatusCode() == 200) {
			userResponse.Success = true;
		} else {
			userResponse.Success = false;
			userResponse.ReasonPhrase = response.getStatusLine()
					.getReasonPhrase();
		}

		return userResponse;
	}
	
	public static UserCheckImageResponse sendCheckImage(UserCheckImageRequest userRequest){
		UserCheckImageResponse checkResponse =  new UserCheckImageResponse();
		HttpResponse response = null;
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost request = new HttpPost(ROOTURL + String.format(CHECKIMAGE_URL, userRequest.UserId));
			MultipartEntity requestEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			requestEntity.addPart("returnformat", new StringBody("json"));

			requestEntity.addPart("uploaded", new ByteArrayBody(userRequest.image,"myImage.jpg"));

			//request.setHeader("content-type", "image/jpeg");
			request.setEntity(requestEntity);


			response = httpClient.execute(request);
		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		HttpEntity entity = response.getEntity();

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

		JSONObject jsonResult = null;
		try {
			jsonResult = new JSONObject(result);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (jsonResult == null)
			{
			checkResponse.Success = false;
			checkResponse.ReasonPhrase = "Unable to Parse Check Image";
			return checkResponse;
			}

		if (response.getStatusLine().getStatusCode() == 201)
		{

			checkResponse.Success = true;

			try {
				checkResponse.name  = jsonResult.getString("name");
				checkResponse.accountNumber = jsonResult.getInt("accountNumber");
				checkResponse.routingNumber = jsonResult.getInt("routingNumber");

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		else
		{
			checkResponse.Success = false;
			checkResponse.ReasonPhrase = response.getStatusLine().getReasonPhrase();
		}

		return checkResponse;
	}

}

