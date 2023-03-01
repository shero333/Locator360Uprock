package com.care360.findmyfamilyandfriends.HomeScreen.FragmentSafety.EmergencyContacts.ContactsFromPhone;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.care360.findmyfamilyandfriends.HomeScreen.FragmentSafety.EmergencyRoomDB.EmergencyContactEntity;
import com.care360.findmyfamilyandfriends.HomeScreen.FragmentSafety.EmergencyRoomDB.RoomDBHelper;
import com.care360.findmyfamilyandfriends.R;
import com.care360.findmyfamilyandfriends.Util.Commons;
import com.care360.findmyfamilyandfriends.databinding.AddContactListItemBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> implements Filterable {

    Context context;

    OnAddContactListener onAddContactListener;
    List<ContactModel> contactList;

    // Room
    RoomDBHelper database;

    // added emergency contacts list
    List<EmergencyContactEntity> addedEmergencyContactList;

    //for filtering/searching contacts
    List<ContactModel> contactFilteringList;

    HashMap<String,String> contactsHashmap = new HashMap<>();

    public ContactAdapter(Context context, List<ContactModel> contactList, OnAddContactListener onAddContactListener) {
        this.context = context;
        this.contactList = contactList;
        this.onAddContactListener = onAddContactListener;

        database = RoomDBHelper.getInstance(context);

        addedEmergencyContactList = database.emergencyContactDao().getEmergencyContactsList(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        contactFilteringList = new ArrayList<>(contactList);
    }

    @NonNull
    @Override
    public ContactAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(AddContactListItemBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ContactAdapter.ViewHolder holder, int position) {

        // contact item
        ContactModel contactItem = contactList.get(position);

        // list to hashmap
        for(EmergencyContactEntity item: addedEmergencyContactList) {
            contactsHashmap.put(item.getContactId(),item.getContactName());
        }

        if(contactsHashmap.containsKey(contactItem.getContactId()))
        {
            holder.binding.txtContactStatus.setText(context.getString(R.string.added));

            //removing drawable from text view
            holder.binding.txtContactStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

            //item click listener
            holder.binding.constraintContact.setOnClickListener(v -> Toast.makeText(context, "Contact Already Added.", Toast.LENGTH_SHORT).show());
        }
        else if(!contactsHashmap.containsKey(contactItem.getContactId()))
        {
            holder.binding.txtContactStatus.setText(context.getString(R.string.add));

            //removing drawable from text view
            holder.binding.txtContactStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add, 0, 0, 0);

            // setting tint
            holder.binding.txtContactStatus.getCompoundDrawables()[0].setTint(context.getColor(R.color.orange));

            //item click listener
            holder.binding.constraintContact.setOnClickListener(v -> onAddContactListener.onAddContactClick(position));
        }

        // contact name characters
        holder.binding.txtContactLetters.setText(Commons.getContactLetters(contactItem.getContactName()));

        //random generated background color
        holder.binding.txtContactLetters.setBackgroundTintList(ColorStateList.valueOf(Commons.randomColor()));

        holder.binding.txtContactName.setText(contactItem.getContactName());

        holder.binding.txtContactNo.setText(contactItem.getContactNumber());

    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public interface OnAddContactListener {
        void onAddContactClick(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        AddContactListItemBinding binding;

        public ViewHolder(@NonNull AddContactListItemBinding itemBinding) {
            super(itemBinding.getRoot());
            binding = itemBinding;
        }
    }

    @Override
    public Filter getFilter() {
        return contactFilter;
    }

    private final Filter contactFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            List<ContactModel> filteredContactList = new ArrayList<>();

            if(charSequence == null || charSequence.length() == 0) {
                filteredContactList.addAll(contactFilteringList);
            }
            else {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for(ContactModel item : contactFilteringList) {
                    if(item.getContactName().toLowerCase().contains(filterPattern)) {
                        filteredContactList.add(item);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredContactList;
            return filterResults;
        }

        @SuppressLint("NotifyDataSetChanged")
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            contactList.clear();
            contactList.addAll((List<ContactModel>) filterResults.values);
            notifyDataSetChanged();
        }
    };
}
