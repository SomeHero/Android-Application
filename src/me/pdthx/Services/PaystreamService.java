package me.pdthx.Services;

import me.pdthx.Responses.ResponseArrayList;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import me.pdthx.Requests.UserRequest;
import me.pdthx.Responses.PaystreamResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class PaystreamService {

    private static final String ROOTURL = "http://23.21.203.171/api/internal/api";
    private static final String APIKEY = "bda11d91-7ade-4da1-855d-24adfe39d174";
    private static final String PAYSTREAMMESSAGES_URL = "/Users/%s/PayStreamMessages";
    private static final String CANCELMESSAGE_URL = "/PayStreamMessages/%s/cancel_payment";
    private static final String REFUNDMESSAGE_URL = "/PayStreamMessages/%s/refund_payment";
    private static final String ACCEPTREQUEST_URL = "/PayStreamMessages/%s/accept_request";
    private static final String REJECTREQUEST_URL = "/PayStreamMessages/%s/reject_request";
    private static final String IGNOREREQUEST_URL = "/PayStreamMessages/%s/ignore_request";

    public PaystreamService() {

    }

    public static ResponseArrayList<PaystreamResponse> getMessages(UserRequest paystreamRequest) {

        ResponseArrayList<PaystreamResponse> messageResponses = new ResponseArrayList<PaystreamResponse>();

        SimpleDateFormat sdf = new SimpleDateFormat(
            "EEE MMM dd kk:mm:ss Z yyyy");
        sdf.setTimeZone(TimeZone.getDefault());

        HttpResponse response = null;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet request = new HttpGet(ROOTURL
                + String.format(PAYSTREAMMESSAGES_URL,
                    paystreamRequest.UserId) + "?apiKey=" + APIKEY);

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
            if (response.getStatusLine().getStatusCode() == 200)
            {

                try {
                    // Loop the Array
                    for (int i = 0; i < messages.length(); i++) {
                        JSONObject jsonResult = messages.getJSONObject(i);

                        PaystreamResponse messageResponse = new PaystreamResponse();

                        Date formattedDate = null;
                        try {
                            formattedDate = sdf.parse(jsonResult
                                .getString("createDate"));
                        } catch (ParseException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        messageResponse.MessageId = jsonResult.getString("Id");
                        messageResponse.Amount = jsonResult.getDouble("amount");
                        messageResponse.Comments = jsonResult.getString("comments");
                        messageResponse.CreateDate = formattedDate;
                        messageResponse.MessageStatus = jsonResult
                            .getString("messageStatus");
                        messageResponse.MessageType = jsonResult
                            .getString("messageType");
                        messageResponse.RecipientUri = jsonResult
                            .getString("recipientUri");
                        messageResponse.SenderUri = jsonResult
                            .getString("senderUri");
                        messageResponse.Direction = jsonResult
                            .getString("direction");

                        messageResponses.add(messageResponse);
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                messageResponses.Success = true;
            }
            else
            {
                messageResponses.Success = false;
                messageResponses.ReasonPhrase = response.getStatusLine().getReasonPhrase();
            }
        }

        return messageResponses;
    }

    public int cancelMessage(String id) {

        HttpResponse response = null;

        try {

            HttpClient httpClient = new DefaultHttpClient();

            HttpPost request = new HttpPost(ROOTURL
                + String.format(CANCELMESSAGE_URL, id) + "?apiKey="
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

        return response.getStatusLine().getStatusCode();

    }

    public int refundMessage(String id) {

        HttpResponse response = null;

        try {

            HttpClient httpClient = new DefaultHttpClient();

            HttpPost request = new HttpPost(ROOTURL
                + String.format(REFUNDMESSAGE_URL, id) + "?apiKey="
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

        return response.getStatusLine().getStatusCode();

    }

    public int acceptRequestMessage(String id) {

        HttpResponse response = null;

        try {

            HttpClient httpClient = new DefaultHttpClient();

            HttpPost request = new HttpPost(ROOTURL
                + String.format(ACCEPTREQUEST_URL, id) + "?apiKey="
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

        return response.getStatusLine().getStatusCode();

    }

    public int rejectRequestMessage(String id) {

        HttpResponse response = null;

        try {

            HttpClient httpClient = new DefaultHttpClient();

            HttpPost request = new HttpPost(ROOTURL
                + String.format(REJECTREQUEST_URL, id) + "?apiKey="
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

        return response.getStatusLine().getStatusCode();

    }

    public int ignoreRequestMessage(String id) {

        HttpResponse response = null;

        try {

            HttpClient httpClient = new DefaultHttpClient();

            HttpPost request = new HttpPost(ROOTURL
                + String.format(IGNOREREQUEST_URL, id) + "?apiKey="
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

        return response.getStatusLine().getStatusCode();

    }
}
