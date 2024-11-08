package com.angelbroking.smartapi.http;

import com.angelbroking.smartapi.http.exceptions.SmartAPIException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Proxy;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Request handler for all Http requests
 */
public class SmartAPIRequestHandler {

	private OkHttpClient client;
	private String USER_AGENT = "javasmartapiconnect/3.0.0";
	JSONObject apiheader = apiHeaders();

	/**
	 * Initialize request handler.
	 * 
	 * @param proxy to be set for making requests.
	 */
	public SmartAPIRequestHandler(Proxy proxy) {
		OkHttpClient.Builder builder = new OkHttpClient.Builder();
		builder.connectTimeout(10000, TimeUnit.MILLISECONDS);
		if (proxy != null) {
			builder.proxy(proxy);
		}

		client = builder.build();
	}

	public JSONObject apiHeaders() {
		try {
			JSONObject headers = new JSONObject();

			// Local IP Address
			InetAddress localHost = InetAddress.getLocalHost();
			String clientLocalIP = localHost.getHostAddress();
			headers.put("clientLocalIP", clientLocalIP);

			// Public IP Address
			URL urlName = new URL("http://checkip.amazonaws.com");
			BufferedReader sc = new BufferedReader(new InputStreamReader(urlName.openStream()));
			String clientPublicIP = sc.readLine().trim();
			headers.put("clientPublicIP", clientPublicIP);
			String macAddress = "02:00:00:00:00:00";
			headers.put("macAddress", macAddress);
			String accept = "application/json";
			headers.put("accept", accept);
			String userType = "USER";
			headers.put("userType", userType);
			String sourceID = "WEB";
			headers.put("sourceID", sourceID);
			return headers;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * Makes a POST request.
	 * 
	 * @return JSONObject which is received by Smart API.
	 * @param url         is the endpoint to which request has to be sent.
	 * @param accessToken is the access token obtained after successful login
	 *                    process.
	 * @param params      is the map of params which has to be sent in the body.
	 * @throws IOException       is thrown when there is a connection related error.
	 * @throws SmartAPIException is thrown for all Smart API Trade related errors.
	 * @throws JSONException     is thrown for parsing errors.
	 */
	public JSONObject postRequest(String apiKey, String url, JSONObject params)
			throws IOException, JSONException, SmartAPIException {

		Request request = createPostRequest(apiKey, url, params);
		try{
			Response response = client.newCall(request).execute();
			String body = response.body().string();
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(body);
			if(jsonNode.get("status") == null){
				System.out.printf("Error in POST request. Request URL: %s, Request Headers: %s, Request Body: %s,Response : %s\n",
						url, request.headers(), params,body);
			}
			return new SmartAPIResponseHandler().handle(response, body);
		}
		catch (Exception e){
			System.out.printf("Error in POST request. Request URL: %s, Request Headers: %s, Request Body: %s,Response : %s\n",
					url, request.headers(), params,e.getMessage());
			throw e;
		}

	}

	/**
	 * Makes a POST request.
	 * 
	 * @return JSONObject which is received by Smart API Trade.
	 * @param url         is the endpoint to which request has to be sent.
	 * @param apiKey      is the api key of the Smart API Connect app.
	 * @param accessToken is the access token obtained after successful login
	 *                    process.
	 * @param params      is the map of params which has to be sent in the body.
	 * @throws IOException       is thrown when there is a connection related error.
	 * @throws SmartAPIException is thrown for all Smart API Trade related errors.
	 * @throws JSONException     is thrown for parsing errors.
	 */
	public JSONObject postRequest(String apiKey, String url, JSONObject params, String accessToken)
			throws IOException, SmartAPIException, JSONException {
		Request request = createPostRequest(apiKey, url, params, accessToken);
		try{
			Response response = client.newCall(request).execute();
			String body = response.body().string();
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(body);
			if(jsonNode.get("status") == null){
				System.out.printf("Error in POST request. Request URL: %s, Request Headers: %s, Request Body: %s,Response : %s\n",
						url, request.headers(), params,body);
			}
			return new SmartAPIResponseHandler().handle(response, body);
		}
		catch (Exception e){
			System.out.printf("Error in POST request. Request URL: %s, Request Headers: %s, Request Body: %s,Response : %s\n",
					url, request.headers(), params,e.getMessage());
			throw e;
		}
	}

	/**
	 * Make a JSON POST request.
	 * 
	 * @param url         is the endpoint to which request has to be sent.
	 * @param apiKey      is the api key of the Smart API Connect app.
	 * @param accessToken is the access token obtained after successful login
	 *                    process.
	 * @param jsonArray   is the JSON array of params which has to be sent in the
	 *                    body.
	 * @throws IOException       is thrown when there is a connection related error.
	 * @throws SmartAPIException is thrown for all Smart API Trade related errors.
	 * @throws JSONException     is thrown for parsing errors.
	 * @throws NullPointerException is thrown for body is null
	 */
	public JSONObject postRequestJSON(String url, JSONArray jsonArray, String apiKey, String accessToken)
			throws IOException, SmartAPIException, JSONException, NullPointerException {
		Request request = createJsonPostRequest(url, jsonArray, apiKey, accessToken);
		Response response = client.newCall(request).execute();
        String body = response.body().string();
		return new SmartAPIResponseHandler().handle(response, body);
	}

	/**
	 * Makes a PUT request.
	 * 
	 * @return JSONObject which is received by Smart API Trade.
	 * @param url         is the endpoint to which request has to be sent.
	 * @param apiKey      is the api key of the Smart API Connect app.
	 * @param accessToken is the access token obtained after successful login
	 *                    process.
	 * @param params      is the map of params which has to be sent in the body.
	 * @throws IOException       is thrown when there is a connection related error.
	 * @throws SmartAPIException is thrown for all Smart API Trade related errors.
	 * @throws JSONException     is thrown for parsing errors.
	 * @throws NullPointerException is thrown for body is null
	 */
	public JSONObject putRequest(String url, Map<String, Object> params, String apiKey, String accessToken)
			throws IOException, SmartAPIException, JSONException, NullPointerException {
		Request request = createPutRequest(url, params, apiKey, accessToken);
		Response response = client.newCall(request).execute();
		String body = response.body().string();
		return new SmartAPIResponseHandler().handle(response, body);
	}

	/**
	 * Makes a DELETE request.
	 * 
	 * @return JSONObject which is received by Smart API Trade.
	 * @param url         is the endpoint to which request has to be sent.
	 * @param apiKey      is the api key of the Smart API Connect app.
	 * @param accessToken is the access token obtained after successful login
	 *                    process.
	 * @param params      is the map of params which has to be sent in the query
	 *                    params.
	 * @throws IOException       is thrown when there is a connection related error.
	 * @throws SmartAPIException is thrown for all Smart API Trade related errors.
	 * @throws JSONException     is thrown for parsing errors.
	 * @throws NullPointerException is thrown for body is null
	 */
	public JSONObject deleteRequest(String url, Map<String, Object> params, String apiKey, String accessToken)
			throws IOException, SmartAPIException, JSONException, NullPointerException {
		Request request = createDeleteRequest(url, params, apiKey, accessToken);
		Response response = client.newCall(request).execute();
		String body = response.body().string();
		return new SmartAPIResponseHandler().handle(response, body);
	}

	/**
	 * Makes a GET request.
	 * 
	 * @return JSONObject which is received by Smart API Trade.
	 * @param url         is the endpoint to which request has to be sent.
	 * @param apiKey      is the api key of the Smart API Connect app.
	 * @param accessToken is the access token obtained after successful login
	 *                    process.
	 * @param commonKey   is the key that has to be sent in query param for quote
	 *                    calls.
	 * @param values      is the values that has to be sent in query param like 265,
	 *                    256265, NSE:INFY.
	 * @throws IOException       is thrown when there is a connection related error.
	 * @throws SmartAPIException is thrown for all Smart API Trade related errors.
	 * @throws JSONException     is thrown for parsing errors.
	 * @throws NullPointerException is thrown for body is null
	 */
	public JSONObject getRequest(String apiKey, String url, String accessToken)
			throws IOException, SmartAPIException, JSONException, NullPointerException {
		Request request = createGetRequest(apiKey, url, accessToken);
		try {
			Response response = client.newCall(request).execute();
			String body = response.body().string();
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(body);
			if(jsonNode.get("status") == null){
				System.out.printf("Error in GET request. Request URL: %s, Request Headers: %s,Response : %s\n",
						url, request.headers(),body);
			}
			return new SmartAPIResponseHandler().handle(response, body);
		}
		catch (Exception e){
			System.out.printf("Error in POST request. Request URL: %s, Request Headers: %s,Response : %s\n",
					url, request.headers(),e.getMessage());
			throw e;
		}
	}

	/**
	 * Creates a GET request.
	 * 
	 * @param url         is the endpoint to which request has to be done.
	 * @param apiKey      is the api key of the Smart API Connect app.
	 * @param accessToken is the access token obtained after successful login
	 *                    process.
	 * @throws IOException
	 */
	public Request createGetRequest(String apiKey, String url, String accessToken) throws IOException {

		HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();

		String privateKey = apiKey;

		return new Request.Builder().url(httpBuilder.build()).header("User-Agent", USER_AGENT)
				.header("Authorization", "Bearer " + accessToken).header("Content-Type", "application/json")
				.header("X-ClientLocalIP", apiheader.getString("clientLocalIP"))
				.header("X-ClientPublicIP", apiheader.getString("clientPublicIP"))
				.header("X-MACAddress", apiheader.getString("macAddress"))
				.header("Accept", apiheader.getString("accept")).header("X-PrivateKey", privateKey)
				.header("X-UserType", apiheader.getString("userType"))
				.header("X-SourceID", apiheader.getString("sourceID")).build();
	}

	/**
	 * Creates a GET request.
	 * 
	 * @param url         is the endpoint to which request has to be done.
	 * @param apiKey      is the api key of the Smart API Connect app.
	 * @param accessToken is the access token obtained after successful login
	 *                    process.
	 * @param commonKey   is the key that has to be sent in query param for quote
	 *                    calls.
	 * @param values      is the values that has to be sent in query param like 265,
	 *                    256265, NSE:INFY.
	 */
	public Request createGetRequest(String url, String commonKey, String[] values, String apiKey, String accessToken) {
		HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();
		for (int i = 0; i < values.length; i++) {
			httpBuilder.addQueryParameter(commonKey, values[i]);
		}
		return new Request.Builder().url(httpBuilder.build()).header("User-Agent", USER_AGENT)
				.header("X-Smart API-Version", "3").header("Authorization", "token " + apiKey + ":" + accessToken)
				.build();
	}

	/**
	 * Creates a POST request.
	 * 
	 * @param url         is the endpoint to which request has to be done.
	 * @param apiKey      is the api key of the Smart API Connect app.
	 * @param accessToken is the access token obtained after successful login
	 *                    process.
	 * @param params      is the map of data that has to be sent in the body.
	 */
	public Request createPostRequest(String apiKey, String url, JSONObject params) {
		try {

			MediaType JSON = MediaType.parse("application/json; charset=utf-8");
			RequestBody body = RequestBody.create(params.toString(), JSON);

			String privateKey = apiKey;
			Request request = new Request.Builder().url(url).post(body).header("Content-Type", "application/json")
					.header("X-ClientLocalIP", apiheader.getString("clientLocalIP"))
					.header("X-ClientPublicIP", apiheader.getString("clientPublicIP"))
					.header("X-MACAddress", apiheader.getString("macAddress"))
					.header("Accept", apiheader.getString("accept")).header("X-PrivateKey", privateKey)
					.header("X-UserType", apiheader.getString("userType"))
					.header("X-SourceID", apiheader.getString("sourceID")).build();
			return request;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Creates a POST request.
	 * 
	 * @param url         is the endpoint to which request has to be done.
	 * @param apiKey      is the api key of the Smart API Connect app.
	 * @param accessToken is the access token obtained after successful login
	 *                    process.
	 * @param params      is the map of data that has to be sent in the body.
	 */
	public Request createPostRequest(String apiKey, String url, JSONObject params, String accessToken) {
		try {

			MediaType JSON = MediaType.parse("application/json; charset=utf-8");
			RequestBody body = RequestBody.create(params.toString(), JSON);

			String privateKey = apiKey;

			Request request = new Request.Builder().url(url).post(body).header("Content-Type", "application/json")
					.header("Authorization", "Bearer " + accessToken)
					.header("X-ClientLocalIP", apiheader.getString("clientLocalIP"))
					.header("X-ClientPublicIP", apiheader.getString("clientPublicIP"))
					.header("X-MACAddress", apiheader.getString("macAddress"))
					.header("Accept", apiheader.getString("accept")).header("X-PrivateKey", privateKey)
					.header("X-UserType", apiheader.getString("userType"))
					.header("X-SourceID", apiheader.getString("sourceID")).build();
			return request;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Create a POST request with body type JSON.
	 * 
	 * @param url         is the endpoint to which request has to be done.
	 * @param apiKey      is the api key of the Smart API Connect app.
	 * @param accessToken is the access token obtained after successful login
	 *                    process.
	 * @param jsonArray   is the JSONArray of data that has to be sent in the body.
	 */
	public Request createJsonPostRequest(String url, JSONArray jsonArray, String apiKey, String accessToken) {
		MediaType JSON = MediaType.parse("application/json; charset=utf-8");

		RequestBody body = RequestBody.create(jsonArray.toString(), JSON);
		Request request = new Request.Builder().url(url).header("User-Agent", USER_AGENT)
				.header("X-Smart API-Version", "3").header("Authorization", "token " + apiKey + ":" + accessToken)
				.post(body).build();
		return request;
	}

	/**
	 * Makes a POST request.
	 *
	 * @return JSONObject which is received by Smart API Trade.
	 * @param url         is the endpoint to which request has to be sent.
	 * @param apiKey      is the api key of the Smart API Connect app.
	 * @param accessToken is the access token obtained after successful login
	 *                    process.
	 * @param params      is the map of params which has to be sent in the body.
	 * @throws IOException       is thrown when there is a connection related error.
	 * @throws SmartAPIException is thrown for all Smart API Trade related errors.
	 * @throws JSONException     is thrown for parsing errors.
	 */
	public String postRequestJSONObject(String apiKey, String url, JSONObject params, String accessToken)
			throws IOException, SmartAPIException, JSONException {
		Request request = createPostRequest(apiKey, url, params, accessToken);
		try{
			Response response = client.newCall(request).execute();
			String body = response.body().string();
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(body);
			if(jsonNode.get("status") == null){
				System.out.printf("Error in POST request. Request URL: %s, Request Headers: %s, Request Body: %s,Response : %s\n",
						url, request.headers(), params,body);
			}
			return new SmartAPIResponseHandler().handler(response, body);
		}
		catch (Exception e){
			System.out.printf("Error in POST request. Request URL: %s, Request Headers: %s, Request Body: %s,Response : %s\n",
					url, request.headers(), params,e.getMessage());
			throw e;
		}
	}

	/**
	 * Creates a PUT request.
	 * 
	 * @param url         is the endpoint to which request has to be done.
	 * @param apiKey      is the api key of the Smart API Connect app.
	 * @param accessToken is the access token obtained after successful login
	 *                    process.
	 * @param params      is the map of data that has to be sent in the body.
	 */
	public Request createPutRequest(String url, Map<String, Object> params, String apiKey, String accessToken) {
		FormBody.Builder builder = new FormBody.Builder();
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			builder.add(entry.getKey(), entry.getValue().toString());
		}
		RequestBody requestBody = builder.build();
		Request request = new Request.Builder().url(url).put(requestBody).header("User-Agent", USER_AGENT)
				.header("X-Smart API-Version", "3").header("Authorization", "token " + apiKey + ":" + accessToken)
				.build();
		return request;
	}

	/**
	 * Creates a DELETE request.
	 * 
	 * @param url         is the endpoint to which request has to be done.
	 * @param apiKey      is the api key of the Smart API Connect app.
	 * @param accessToken is the access token obtained after successful login
	 *                    process.
	 * @param params      is the map of data that has to be sent in the query
	 *                    params.
	 */
	public Request createDeleteRequest(String url, Map<String, Object> params, String apiKey, String accessToken) {
		HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			httpBuilder.addQueryParameter(entry.getKey(), entry.getValue().toString());
		}

		Request request = new Request.Builder().url(httpBuilder.build()).delete().header("User-Agent", USER_AGENT)
				.header("X-Smart API-Version", "3").header("Authorization", "token " + apiKey + ":" + accessToken)
				.build();
		return request;
	}

}
