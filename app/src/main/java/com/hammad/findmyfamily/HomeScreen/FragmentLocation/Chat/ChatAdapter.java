package com.hammad.findmyfamily.HomeScreen.FragmentLocation.Chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.hammad.findmyfamily.HomeScreen.FragmentLocation.Chat.Model.Message;
import com.hammad.findmyfamily.Util.Commons;
import com.hammad.findmyfamily.databinding.LayoutMessagesItemBinding;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {

    Context context;
    List<Message> messageList;

    String date = "";

    public ChatAdapter(Context context, List<Message> messageList) {
        this.context = context;
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public ChatAdapter.MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MessageViewHolder(LayoutMessagesItemBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.MessageViewHolder holder, int position) {

        //message item
        Message messageItem = messageList.get(position);

        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        date = Commons.dateFromTimeInMilli(messageItem.getMessageTimestamp());


        /*if(date.equals(Commons.dateFromTimeInMilli(messageItem.getMessageTimestamp()))) {
            holder.binding.txtDate.setVisibility(View.GONE);
        }
        else if(!date.equals(Commons.dateFromTimeInMilli(messageItem.getMessageTimestamp()))) {
            holder.binding.txtDate.setVisibility(View.VISIBLE);*/
        holder.binding.txtDate.setText(Commons.dateFromTimeInMilli(messageItem.getMessageTimestamp()));
        //}

        // message send
        if (messageItem.getSenderId().equals(email)) {

            holder.binding.textMessageSend.setText(messageItem.getMessageText());
            holder.binding.timestampMessageSend.setText(Commons.timeFromTimeInMilli(String.valueOf(System.currentTimeMillis())));

            //message received visibility to GONE
            holder.binding.consGroupReceiveMessage.setVisibility(View.GONE);
        } else if (messageItem.getReceiverId().equals(email)) {

            holder.binding.textMessageReceived.setText(messageItem.getMessageText());
            holder.binding.timestampMessageReceived.setText(Commons.timeFromTimeInMilli(String.valueOf(System.currentTimeMillis())));

            //message send visibility to GONE
            holder.binding.consGroupSendMessage.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {

        LayoutMessagesItemBinding binding;

        public MessageViewHolder(@NonNull LayoutMessagesItemBinding messagesItemBinding) {
            super(messagesItemBinding.getRoot());
            binding = messagesItemBinding;
        }
    }
}
