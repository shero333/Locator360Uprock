package com.hammad.findmyfamily.HomeScreen.FragmentLocation.Chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hammad.findmyfamily.databinding.LayoutMessagesItemBinding;

import java.util.List;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.MessageViewHolder> {

    Context context;
    List<String> messageList;

    public ChatMessageAdapter(Context context, List<String> messageList) {
        this.context = context;
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public ChatMessageAdapter.MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MessageViewHolder(LayoutMessagesItemBinding.inflate(LayoutInflater.from(context),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatMessageAdapter.MessageViewHolder holder, int position) {

        holder.binding.textMessageReceived.setOnClickListener(v -> Toast.makeText(context, "Received: "+position, Toast.LENGTH_SHORT).show());

        holder.binding.textMessageSend.setOnClickListener(v -> Toast.makeText(context, "Send: "+position, Toast.LENGTH_SHORT).show());
    }

    @Override
    public int getItemCount() {
        return 4;
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {

        LayoutMessagesItemBinding binding;

        public MessageViewHolder(@NonNull LayoutMessagesItemBinding messagesItemBinding) {
            super(messagesItemBinding.getRoot());
            binding = messagesItemBinding;
        }
    }
}
