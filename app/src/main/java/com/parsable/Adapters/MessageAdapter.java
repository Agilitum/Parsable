package com.parsable.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parsable.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by ludwig on 28/04/16.
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

	private List<String>  messageList;
	private String timeStamp = "Pending";

	public class MessageViewHolder extends RecyclerView.ViewHolder {

		public TextView message;
		public TextView timeStamp;

		public MessageViewHolder(View itemView) {
			super(itemView);
			message = (TextView) itemView.findViewById(R.id.content_message_text_view_message);
			timeStamp = (TextView) itemView.findViewById(R.id.content_message_text_view_timestamp);
		}
	}

	public MessageAdapter (List<String> messageList){
		this.messageList = messageList;
	}

	@Override
	public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
		View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_message,
			parent, false);

		return new MessageViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder (MessageViewHolder holder, int position){
		String message = messageList.get(position);
		holder.message.setText(message);
		holder.timeStamp.setText(timeStamp);
	}

	@Override
	public int getItemCount(){
		return messageList.size();
	}

	public void setTimeStamp(int position){
		timeStamp = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.ENGLISH)
			.format(new Date());
		notifyItemChanged(position);
	}
}
