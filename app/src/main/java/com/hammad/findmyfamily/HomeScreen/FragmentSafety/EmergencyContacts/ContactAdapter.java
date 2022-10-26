package com.hammad.findmyfamily.HomeScreen.FragmentSafety.EmergencyContacts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hammad.findmyfamily.databinding.AddContactListItemBinding;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    Context context;

    AddContactListItemBinding binding;

    OnAddContactListener onAddContactListener;

    public ContactAdapter(Context context,OnAddContactListener onAddContactListener) {
        this.context = context;
        this.onAddContactListener = onAddContactListener;
    }

    @NonNull
    @Override
    public ContactAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(AddContactListItemBinding.inflate(LayoutInflater.from(context),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ContactAdapter.ViewHolder holder, int position) {

        //item click listener
        binding.constraintContact.setOnClickListener(v -> onAddContactListener.onAddContactClick(position));

        //if a contact is added, then update the preference value which will navigate to a different activity
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull AddContactListItemBinding itemBinding) {
            super(itemBinding.getRoot());
            binding = itemBinding;
        }
    }

    public interface OnAddContactListener {
        void onAddContactClick(int position);
    }
}
