package com.parsable.Network;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by ludwig on 30/04/16.
 * Custom JsonObjectRequest class which allows for empty responses from the server
 */
public class VolleyJsonRequest extends JsonObjectRequest {

	public VolleyJsonRequest(int method, String url, String requestBody,
	                         Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
		super(method, url, requestBody, listener,
			errorListener);
	}

	public VolleyJsonRequest(int method, String url, JSONObject jsonRequest,
	                         Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
		super(method, url, (jsonRequest == null) ? null : jsonRequest.toString(), listener,
			errorListener);
	}


	public VolleyJsonRequest(String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener,
	                         Response.ErrorListener errorListener) {
		this(jsonRequest == null ? Method.GET : Method.POST, url, jsonRequest,
			listener, errorListener);
	}

	// response accepts empty response instead of throwing an error like default implementation of
	// volley
	@Override
	protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
		try {
			if (response.data.length == 0) {
				byte[] responseData = "{}".getBytes("UTF8");
				response = new NetworkResponse(response.statusCode, responseData, response.headers, response.notModified);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return super.parseNetworkResponse(response);
	}
}
