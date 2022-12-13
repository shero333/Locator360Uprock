package com.hammad.findmyfamily.HomeScreen.FragmentLocation.Chat;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hammad.findmyfamily.Util.Commons;
import com.hammad.findmyfamily.databinding.LayoutChatItemBinding;

import java.util.List;

public class ChatDashboardAdapter extends RecyclerView.Adapter<ChatDashboardAdapter.ChatViewHolder> {

    Context context;
    OnChatMemberListener onChatMemberListener;
    List<String> membersList;

    public ChatDashboardAdapter(Context context,List<String> list,OnChatMemberListener onChatMemberListener) {
        this.context = context;
        this.onChatMemberListener = onChatMemberListener;
        membersList = list;
    }

    @NonNull
    @Override
    public ChatDashboardAdapter.ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatViewHolder(LayoutChatItemBinding.inflate(LayoutInflater.from(context),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatDashboardAdapter.ChatViewHolder holder, int position) {

        //random color on profile image background
        holder.binding.profileImgBackground.setBackgroundTintList(ColorStateList.valueOf(Commons.randomColor()));

        //interface click listener
        holder.binding.consContact.setOnClickListener(v -> onChatMemberListener.onChatMemberClick(position));
    }

    @Override
    public int getItemCount() {
        return 6;
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {

        LayoutChatItemBinding binding;

        public ChatViewHolder(@NonNull LayoutChatItemBinding layoutChatItemBinding) {
            super(layoutChatItemBinding.getRoot());

            binding = layoutChatItemBinding;
        }
    }

    public interface OnChatMemberListener {
        void onChatMemberClick(int position);
    }
}
