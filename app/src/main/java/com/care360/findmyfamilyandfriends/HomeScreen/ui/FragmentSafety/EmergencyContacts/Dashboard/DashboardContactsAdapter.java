package com.care360.findmyfamilyandfriends.HomeScreen.ui.FragmentSafety.EmergencyContacts.Dashboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.care360.findmyfamilyandfriends.HomeScreen.ui.FragmentSafety.EmergencyRoomDB.EmergencyContactEntity;
import com.care360.findmyfamilyandfriends.Util.Commons;
import com.care360.findmyfamilyandfriends.databinding.LayoutAddedContactItemBinding;

import java.util.List;

public class DashboardContactsAdapter extends RecyclerView.Adapter<DashboardContactsAdapter.ViewHolder> {

    Context context;
    List<EmergencyContactEntity> addedEmergencyContactList;
    OnAddedContactListener onAddedContactListener;

    public DashboardContactsAdapter(Context context, List<EmergencyContactEntity> contactsList,OnAddedContactListener onAddedContactListener) {
        this.context = context;
        this.addedEmergencyContactList = contactsList;
        this.onAddedContactListener = onAddedContactListener;
    }

    @NonNull
    @Override
    public DashboardContactsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutAddedContactItemBinding.inflate(LayoutInflater.from(context),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull DashboardContactsAdapter.ViewHolder holder, int position) {

        EmergencyContactEntity contactItem = addedEmergencyContactList.get(position);

        //contact name first letters
        holder.binding.txtContactLetters.setText(Commons.getContactLetters(contactItem.getContactName()));

        // contact name
        holder.binding.txtContactName.setText(contactItem.getContactName());

        //item click listener
        holder.binding.consContactItem.setOnClickListener(v -> onAddedContactListener.onAddedContact(position));
    }

    @Override
    public int getItemCount() {
        return addedEmergencyContactList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        LayoutAddedContactItemBinding binding;
        public ViewHolder(@NonNull LayoutAddedContactItemBinding layoutAddedContactItemBinding) {
            super(layoutAddedContactItemBinding.getRoot());
            binding = layoutAddedContactItemBinding;
        }
    }

    public interface OnAddedContactListener {
        void onAddedContact(int position);
    }
}
