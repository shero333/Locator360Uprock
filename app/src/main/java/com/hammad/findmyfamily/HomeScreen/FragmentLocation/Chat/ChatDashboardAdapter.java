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
import com.hammad.findmyfamily.HomeScreen.FragmentLocation.Chat.Model.UserInfo;
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

    public ChatDashboardAdapter(Context context,List<UserInfo> list,OnChatMemberListener onChatMemberListener) {
        this.context = context;
        this.onChatMemberListener = onChatMemberListener;
        this.membersList = list;

        membersFilteringList = new ArrayList<>(membersList);
    }

    @NonNull
    @Override
    public ChatDashboardAdapter.ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatViewHolder(LayoutChatItemBinding.inflate(LayoutInflater.from(context),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatDashboardAdapter.ChatViewHolder holder, int position) {

        UserInfo item = membersList.get(position);

        // this random color is also set to the background in ChatDetailActivity if profile image url is null
        int randomColor = Commons.randomColor();

        //random color on profile image background
        holder.binding.profileImgBackground.setBackgroundTintList(ColorStateList.valueOf(randomColor));

        //profile image (if any)
        if(item.getUserImageURL().equals(Constants.NULL)) {
            holder.binding.profileImg.setVisibility(View.GONE);
            holder.binding.profileImgBackground.setText(String.valueOf(item.getUserFullName().charAt(0)));
        }
        else if(!item.getUserImageURL().equals(Constants.NULL)) {
            holder.binding.profileImg.setVisibility(View.VISIBLE);

            Glide
                 .with(context)
                 .load(item.getUserImageURL())
                 .into(holder.binding.profileImg);
        }

        // contact name
        holder.binding.txtUserName.setText(item.getUserFullName());

        // if user token is null, it means user is signed out
        if(item.getUserToken().equals(Constants.NULL)) {
            holder.binding.imgViewStatus.setImageResource(R.color.offline_golden);
            holder.binding.imgViewUserSignedOut.setVisibility(View.VISIBLE);
        }
        else if(!item.getUserToken().equals(Constants.NULL)) {
            holder.binding.imgViewStatus.setImageResource(R.color.holo_green_dark);
            holder.binding.imgViewUserSignedOut.setVisibility(View.GONE);
        }

        // image view sign out click listener
        holder.binding.imgViewUserSignedOut.setOnClickListener(v -> Toast.makeText(context, "User is signed out.", Toast.LENGTH_SHORT).show());

        //interface click listener
        holder.binding.consContact.setOnClickListener(v -> onChatMemberListener.onChatMemberClick(position,randomColor));
    }

    @Override
    public int getItemCount() {
        return membersList.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {

        LayoutChatItemBinding binding;

        public ChatViewHolder(@NonNull LayoutChatItemBinding layoutChatItemBinding) {
            super(layoutChatItemBinding.getRoot());

            binding = layoutChatItemBinding;
        }
    }

    public interface OnChatMemberListener {
        void onChatMemberClick(int position,int randomColor);
    }

    public Filter getMembersFilter() {
        return membersFilter;
    }

    private final Filter membersFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            List<UserInfo> filteredMemberList = new ArrayList<>();

            if(charSequence == null || charSequence.length() == 0) {
                filteredMemberList.addAll(membersFilteringList);
            }
            else {
                String filterPattern = charSequence.toString().trim().toLowerCase();

                for(UserInfo member : membersFilteringList) {
                    if(member.getUserFullName().toLowerCase().contains(filterPattern)) {
                        filteredMemberList.add(member);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredMemberList;
            return filterResults;
        }

        @SuppressLint("NotifyDataSetChanged")
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            membersList.clear();
            membersList.addAll((List<UserInfo>) filterResults.values);
            notifyDataSetChanged();
        }
    };
}
