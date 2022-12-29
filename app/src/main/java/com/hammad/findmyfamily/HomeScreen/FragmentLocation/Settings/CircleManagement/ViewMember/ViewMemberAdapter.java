package com.hammad.findmyfamily.HomeScreen.FragmentLocation.Settings.CircleManagement.ViewMember;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hammad.findmyfamily.HomeScreen.FragmentLocation.Settings.CircleManagement.RemoveMember.MemberModel;
import com.hammad.findmyfamily.SharedPreference.SharedPreference;
import com.hammad.findmyfamily.databinding.LayoutViewCircleMemberBinding;

import java.util.List;

public class ViewMemberAdapter extends RecyclerView.Adapter<ViewMemberAdapter.MembersViewHolder> {

    Context context;
    List<MemberModel> membersList;

    public ViewMemberAdapter(Context context, List<MemberModel> membersList) {
        this.context = context;
        this.membersList = membersList;
    }

    @NonNull
    @Override
    public ViewMemberAdapter.MembersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MembersViewHolder(LayoutViewCircleMemberBinding.inflate(LayoutInflater.from(context),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewMemberAdapter.MembersViewHolder holder, int position) {

        MemberModel item = membersList.get(holder.getAdapterPosition());

        holder.binding.textAddMembers.setText(item.getMemberName());

        // if member is admin of circle, displays an admin textview in front of it
        if(item.getMemberEmail().equals(SharedPreference.getCircleAdminId())) {
            holder.binding.textUserIsAdmin.setVisibility(View.VISIBLE);
        }
        else if(!item.getMemberEmail().equals(SharedPreference.getCircleAdminId())) {
            holder.binding.textUserIsAdmin.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return membersList.size();
    }

    public static class MembersViewHolder extends RecyclerView.ViewHolder {

        LayoutViewCircleMemberBinding binding;

        public MembersViewHolder(@NonNull LayoutViewCircleMemberBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }
}
