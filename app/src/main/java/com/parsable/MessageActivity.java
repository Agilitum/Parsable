package com.parsable;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.birbit.android.jobqueue.JobManager;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.parsable.Adapters.MessageAdapter;
import com.parsable.Jobs.SendNumberJob;
import com.parsable.Jobs.SendTextJob;

import java.util.ArrayList;
import java.util.List;

public class MessageActivity extends AppCompatActivity {

	private RecyclerView messageRecyclerView;
	private MessageAdapter messageAdapter;

	private List<String> messageList = new ArrayList<>();

		private String authToken = "null";

	JobManager jobManager;

	Integer listPosition = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		jobManager = ParsableApplication.getInstance().getJobManager();

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		//retrieve stored authToken from login() call
		SharedPreferences prefs = this.getSharedPreferences("com.parsable", Context.MODE_PRIVATE);
		authToken = prefs.getString("AuthToken", "null");

		//RecyclerListView & Adapters to show send messages
		messageRecyclerView = (RecyclerView) findViewById(R.id.message_recyclerview);
		messageAdapter = new MessageAdapter(messageList);
		RecyclerView.LayoutManager messageLayoutManager = new LinearLayoutManager(getApplicationContext());
		messageRecyclerView.setLayoutManager(messageLayoutManager);
		messageRecyclerView.setItemAnimator(new DefaultItemAnimator());
		messageRecyclerView.setAdapter(messageAdapter);

		//Broadcast Receiver listening for send message job job completed to update UI
		getApplicationContext().registerReceiver(mMessageSentReceiver, new IntentFilter
			("com.parsable.jobs.SendJob.messageSent"));

		// FloatingActionMenu to facilitate UX collapsing
		final FloatingActionsMenu fam = (FloatingActionsMenu) findViewById(R.id
			.floating_action_button_menu);

		//Button for sendMessage()
		final FloatingActionButton fab1 = (FloatingActionButton) findViewById(R.id.fab1);
		fab1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final EditText input = new EditText(MessageActivity.this);

				//restrict input to text only (quite rudimentary)
				input.setFilters(new InputFilter[]{new InputFilter() {
					@Override
					public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
						if(source.equals("")){
							return "";
						} if(source.toString().matches("[a-zA-Z ]+")) {
							return source;
						}
						return "";
					}
				}});

				AlertDialog.Builder alert = new AlertDialog.Builder(MessageActivity.this);
				alert
					.setTitle("Send Message")
					.setMessage("Insert message to be send to the server")
					.setView(input)
					.setPositiveButton("Send", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if(input.getText().length() > 0){
								jobManager.addJobInBackground(new SendTextJob(input.getText().toString(),
									authToken, listPosition));
								messageList.add(input.getText().toString());
								listPosition++;
							}
							fam.collapseImmediately();
						}
					})
					.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							fam.collapseImmediately();
						}
					});
				AlertDialog dialog = alert.create();
				dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
				dialog.show();
			}
		});

		//Button for sendNumber()
		FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
		fab2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final EditText input = new EditText(MessageActivity.this);
				input.setInputType(InputType.TYPE_CLASS_NUMBER);

				AlertDialog.Builder alert = new AlertDialog.Builder(MessageActivity.this);
				alert
					.setTitle("Send Number")
					.setMessage("Insert number to be send to the server")
					.setView(input)
					.setPositiveButton("Send", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if(input.getText().length() > 0){
								jobManager.addJobInBackground(new SendNumberJob(Integer.parseInt(input.getText()
									.toString()),
									authToken, listPosition));
								messageList.add(input.getText().toString());
								listPosition++;
							}
							fam.collapseImmediately();
						}
					})
					.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							fam.collapseImmediately();
						}
					});
				AlertDialog dialog = alert.create();
				dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
				dialog.show();
			}
		});

	}

	@Override
	public void onResume(){
		super.onResume();
		getApplicationContext().registerReceiver(mMessageSentReceiver, new IntentFilter("com.parsable" +
			".jobs.SendJob.messageSent"));
	}

	@Override
	public void onPause(){
		super.onPause();
		getApplicationContext().unregisterReceiver(mMessageSentReceiver);
	}

	private BroadcastReceiver mMessageSentReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Integer id = intent.getIntExtra("ID", 0);
			messageAdapter.setTimeStamp(id);
		}
	};
}
