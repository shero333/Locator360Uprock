package com.hammad.findmyfamily.HomeScreen.FragmentSafety.EmergencyContacts.Dashboard;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hammad.findmyfamily.HomeScreen.FragmentSafety.EmergRoomDB.EmergencyContactEntity;

import java.util.List;

public class DashboardContactsAdapter extends RecyclerView.Adapter<DashboardContactsAdapter.ViewHolder> {

    Context context;
    List<EmergencyContactEntity> addedEmergencyContactList;

    public DashboardContactsAdapter(Context context, List<EmergencyContactEntity> contactsList) {
        this.context = context;
        this.addedEmergencyContactList = contactsList;
    }

    @NonNull
    @Override
    public DashboardContactsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull DashboardContactsAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return addedEmergencyContactList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
