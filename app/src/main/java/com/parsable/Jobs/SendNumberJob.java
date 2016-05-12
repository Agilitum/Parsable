package com.parsable.Jobs;

import android.content.Intent;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.parsable.Network.VolleyJsonRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ludwig on 09/05/16.
 */
public class SendNumberJob extends Job {

	public static final int PRIORITY = 1;
	private long localId;
	private Integer number;
	private Integer listPosition;
	private RequestQueue requestQueue;
	private String authToken = "null";

	public SendNumberJob(Integer number, String authToken, Integer listPosition){
		super(new Params(PRIORITY).requireNetwork().persist().groupBy("send_number"));
		localId = -System.currentTimeMillis();
		this.number = number;
		this.authToken = authToken;
		this.listPosition = listPosition;
	}

	@Override
	public void onAdded() {

	}

	@Override
	public void onRun() throws Throwable {
		sendNumber(number, listPosition);
	}

	@Override
	protected void onCancel(int cancelReason) {

	}

	@Override
	protected RetryConstraint shouldReRunOnThrowable(Throwable throwable, int runCount, int maxRunCount) {
		return null;
	}

	public void sendNumber(final Integer number, final Integer listPosition) throws Exception {

//		if(number != null && !number.isEmpty()){
//			Toast.makeText(getApplicationContext(), "Message is null", Toast.LENGTH_LONG).show();
//			throw new Exception("Message is null");
//		}

		if (authToken.equals("null")) {
			Toast.makeText(getApplicationContext(), "No AuthToken", Toast.LENGTH_LONG).show();
		} else {
			String url = "http://52.26.166.121/api/candidatestore";
			JSONObject jsonObject = new JSONObject()
				.put("api", "sendNumber")
				.put("number", number);

			requestQueue = Volley.newRequestQueue(getApplicationContext());

			VolleyJsonRequest sendNumberRequest = new VolleyJsonRequest(url, jsonObject, new Response
				.Listener<JSONObject>() {
				@Override
				public void onResponse(JSONObject response) {
					//TODO: check response -> server doesn't provide one yet?

					// notify Broadcast receiver on data been sent
					Intent broadcastIntent = new Intent("com.parsable.jobs.SendJob" +
						".messageSent");
					broadcastIntent.putExtra("ID", listPosition);
					getApplicationContext().sendBroadcast(broadcastIntent);

					Toast.makeText(getApplicationContext(), "Number successfully send!", Toast.LENGTH_LONG)
						.show();
				}
			}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					VolleyLog.e("Error: ", error.getMessage());
				}
			}) {
				@Override
				public Map<String, String> getHeaders() throws AuthFailureError {
					Map<String, String> params = new HashMap<String, String>();
					params.put("Content-Type", "application/json");
					params.put("Authorization", "Token " + authToken);
					return params;
				}
			};
			requestQueue.add(sendNumberRequest);
		}
	}

}

