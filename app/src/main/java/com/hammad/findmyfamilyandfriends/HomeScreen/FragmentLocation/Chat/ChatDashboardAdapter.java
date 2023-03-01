package com.hammad.findmyfamily.HomeScreen.FragmentLocation.Chat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.hammad.findmyfamily.HomeScreen.FragmentLocation.Chat.DB.MessageEntity;
import com.hammad.findmyfamily.HomeScreen.FragmentLocation.Chat.Model.UserInfo;
import com.hammad.findmyfamily.HomeScreen.FragmentSafety.EmergencyRoomDB.RoomDBHelper;
import com.hammad.findmyfamily.R;
import com.hammad.findmyfamily.Util.Commons;
import com.hammad.findmyfamily.Util.Constants;
import com.hammad.findmyfamily.databinding.LayoutChatItemBinding;

import java.util.ArrayList;
import java.util.List;

public class ChatDashboardAdapter extends RecyclerView.Adapter<ChatDashboardAdapter.ChatViewHolder> {

    Context context;

    OnChatMemberListener onChatMemberListener;

    List<UserInfo> membersList;

    //for searching/filtering users
    List<UserInfo> membersFilteringList;

    // for storing last send/receive message timestamp
    MessageEntity messageEntity;

    String currentUserEmail;

    public ChatDashboardAdapter(Context context, List<UserInfo> list, OnChatMemberListener onChatMemberListener) {
        this.context = context;
        this.onChatMemberListener = onChatMemberListener;
        this.membersList = list;
        membersFilteringList = new ArrayList<>(membersList);
        currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        messageEntity = new MessageEntity();
    }

    @NonNull
    @Override
    public ChatDashboardAdapter.ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatViewHolder(LayoutChatItemBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatDashboardAdapter.ChatViewHolder holder, int position) {

        UserInfo item = membersList.get(position);

        // for displaying the last send/received message timestamp
        messageEntity = RoomDBHelper
                .getInstance(context)
                .messageDao()
                .lastMessageTimestamp(currentUserEmail, item.getUserId());


        // if message entity is null, hides the last message timestamp textview
        if(messageEntity == null) {
            holder.binding.txtLastMessageDate.setVisibility(View.GONE);
        }
        else if (messageEntity != null) {
            holder.binding.txtLastMessageDate.setVisibility(View.VISIBLE);
            holder.binding.txtLastMessageDate.setText(Commons.timeInMilliToDateFormat(messageEntity.getTimestamp()));
        }

        // this random color is also set to the background in ChatDetailActivity if profile image url is null
        int randomColor = Commons.randomColor();

        //random color on profile image background
        holder.binding.profileImgBackground.setBackgroundTintList(ColorStateList.valueOf(randomColor));

        //profile image (if any)
        if (item.getUserImageURL().equals(Constants.NULL)) {
            holder.binding.profileImg.setVisibility(View.GONE);
            holder.binding.profileImgBackground.setText(String.valueOf(item.getUserFullName().charAt(0)));
        }
        else if (!item.getUserImageURL().equals(Constants.NULL)) {
            holder.binding.profileImg.setVisibility(View.VISIBLE);

            Glide
                    .with(context)
                    .load(item.getUserImageURL())
                    .into(holder.binding.profileImg);
        }

        // contact name
        holder.binding.txtUserName.setText(item.getUserFullName());

        // if user token is null, it means user is signed out
        if (item.getUserToken().equals(Constants.NULL)) {
            holder.binding.imgViewStatus.setImageResource(R.color.offline_golden);
            holder.binding.imgViewUserSignedOut.setVisibility(View.VISIBLE);
        }
        else if (!item.getUserToken().equals(Constants.NULL)) {
            holder.binding.imgViewStatus.setImageResource(R.color.holo_green_dark);
            holder.binding.imgViewUserSignedOut.setVisibility(View.GONE);
        }

        // image view sign out click listener
        holder.binding.imgViewUserSignedOut.setOnClickListener(v -> Toast.makeText(context, "User is signed out.", Toast.LENGTH_SHORT).show());

        //interface click listener
        holder.binding.consContact.setOnClickListener(v -> onChatMemberListener.onChatMemberClick(position, randomColor));
    }

    @Override
    public int getItemCount() {
        return membersList.size();
    }

    public interface OnChatMemberListener {
        void onChatMemberClick(int position, int randomColor);
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {

        LayoutChatItemBinding binding;

        public ChatViewHolder(@NonNull LayoutChatItemBinding layoutChatItemBinding) {
            super(layoutChatItemBinding.getRoot());

            binding = layoutChatItemBinding;
        }
    }

    public List<UserInfo> getMembersList() {
        return membersList;
    }
}
