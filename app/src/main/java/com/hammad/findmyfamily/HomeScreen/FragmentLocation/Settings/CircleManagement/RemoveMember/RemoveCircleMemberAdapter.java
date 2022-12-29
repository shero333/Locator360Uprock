package com.hammad.findmyfamily.HomeScreen.FragmentLocation.Settings.CircleManagement.RemoveMember;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hammad.findmyfamily.SharedPreference.SharedPreference;
import com.hammad.findmyfamily.Util.Constants;
import com.hammad.findmyfamily.databinding.LayoutDeleteCircleMemberBinding;

import java.util.List;

public class RemoveCircleMemberAdapter extends RecyclerView.Adapter<RemoveCircleMemberAdapter.DeleteMemberViewHolder> {

    private static final String TAG = "DEL_MEMBER_ADAPTER";

    Context context;
    List<MemberModel> membersList;

    public RemoveCircleMemberAdapter(Context context, List<MemberModel> membersList) {
        this.context = context;
        this.membersList = membersList;
    }

    @NonNull
    @Override
    public RemoveCircleMemberAdapter.DeleteMemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DeleteMemberViewHolder(LayoutDeleteCircleMemberBinding.inflate(LayoutInflater.from(context),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RemoveCircleMemberAdapter.DeleteMemberViewHolder holder, int position) {

        MemberModel item = membersList.get(holder.getAdapterPosition());

        holder.binding.textAddMembers.setText(item.getMemberName());

        // if user is owner, delete member image view visibility is set to 'GONE'
        if(item.getMemberEmail().equals(SharedPreference.getCircleAdminId())) {
            holder.binding.imageDeleteMember.setVisibility(View.GONE);
            holder.binding.textUserIsAdmin.setVisibility(View.VISIBLE);
        }
        else if(!item.getMemberEmail().equals(SharedPreference.getCircleAdminId())) {
            holder.binding.imageDeleteMember.setVisibility(View.VISIBLE);
            holder.binding.textUserIsAdmin.setVisibility(View.GONE);
        }

        holder.binding.imageDeleteMember.setOnClickListener(v -> removeMember(item.getMemberEmail(),holder.getAdapterPosition()));
    }

    private void removeMember(String memberEmail,int adapterPosition) {

        FirebaseFirestore.getInstance().collection(Constants.USERS_COLLECTION)
                .document(SharedPreference.getCircleAdminId())
                .collection(Constants.CIRCLE_COLLECTION)
                .document(SharedPreference.getCircleId())
                .update(Constants.CIRCLE_MEMBERS, FieldValue.arrayRemove(memberEmail))
                .addOnSuccessListener(unused -> {
                    Log.i(TAG, "circle member removed successfully");

                    Toast.makeText(context, "Member removed successfully", Toast.LENGTH_SHORT).show();

                    membersList.remove(adapterPosition);

                    // update the recycler view list
                    /*notifyItemChanged(adapterPosition);*/notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "error removing circle member: " + e.getMessage());
                    Toast.makeText(context, "Error! Failed to remove member.", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public int getItemCount() {
        return membersList.size();
    }

    public static class DeleteMemberViewHolder extends RecyclerView.ViewHolder {

        LayoutDeleteCircleMemberBinding binding;

        public DeleteMemberViewHolder(@NonNull LayoutDeleteCircleMemberBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }
}
