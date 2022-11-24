package com.hammad.findmyfamily.HomeScreen.FragmentSafety.EmergencyContacts.ContactsFromPhone;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.hammad.findmyfamily.HomeScreen.FragmentSafety.EmergRoomDB.EmergencyContactEntity;
import com.hammad.findmyfamily.HomeScreen.FragmentSafety.EmergRoomDB.RoomDBHelper;
import com.hammad.findmyfamily.R;
import com.hammad.findmyfamily.Util.Commons;
import com.hammad.findmyfamily.databinding.AddContactListItemBinding;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    Context context;

    OnAddContactListener onAddContactListener;
    List<ContactModel> contactList;

    // Room
    RoomDBHelper database;

    // added emergency contacts list
    List<EmergencyContactEntity> addedEmergencyContactList;

    public ContactAdapter(Context context, List<ContactModel> contactList, OnAddContactListener onAddContactListener) {
        this.context = context;
        this.contactList = contactList;
        this.onAddContactListener = onAddContactListener;

        database = RoomDBHelper.getInstance(context);

        addedEmergencyContactList = database.emergencyContactDao().getEmergencyContactsList(FirebaseAuth.getInstance().getCurrentUser().getEmail());
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

        if(addedEmergencyContactList.size() > 0) {

            // saved emergency contact item
            EmergencyContactEntity emergConItem = addedEmergencyContactList.get(position);

            if (contactItem.getContactId().equals(emergConItem.getContactId())) {

                holder.binding.txtContactStatus.setText(context.getString(R.string.added));

                //removing drawable from text view
                holder.binding.txtContactStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                //item click listener
                holder.binding.constraintContact.setOnClickListener(v -> Toast.makeText(context, "Contact Already Added.", Toast.LENGTH_SHORT).show());

            }
            else if (!contactItem.getContactId().equals(emergConItem.getContactId())) {

                holder.binding.txtContactStatus.setText(context.getString(R.string.add));

                //removing drawable from text view
                holder.binding.txtContactStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add, 0, 0, 0);

                // setting tint
                holder.binding.txtContactStatus.getCompoundDrawables()[0].setTint(context.getColor(R.color.orange));

                //item click listener
                holder.binding.constraintContact.setOnClickListener(v -> onAddContactListener.onAddContactClick(position));
            }
        }
        else if(addedEmergencyContactList.size() == 0) {

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
        return 3;
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
}
