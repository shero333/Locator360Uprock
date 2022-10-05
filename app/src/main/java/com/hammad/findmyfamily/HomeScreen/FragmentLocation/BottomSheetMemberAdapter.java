package com.hammad.findmyfamily.HomeScreen.FragmentLocation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hammad.findmyfamily.Util.Constants;
import com.hammad.findmyfamily.databinding.LayoutAddNewMemberBinding;
import com.hammad.findmyfamily.databinding.LayoutRecyclerViewBottomSheetBinding;

public class BottomSheetMemberAdapter extends RecyclerView.Adapter<BottomSheetMemberAdapter.MyViewHolder> {

    Context context;
    int listSize;

    //binding
    LayoutRecyclerViewBottomSheetBinding recyclerViewItemBinding;
    LayoutAddNewMemberBinding addNewMemberBinding;

    //click listener interfaces
    OnAddedMemberClickInterface addedMemberInterface;
    OnAddNewMemberInterface addNewMemberInterface;

    public BottomSheetMemberAdapter(Context context, int listSize, OnAddedMemberClickInterface onAddedMemberClickInterface, OnAddNewMemberInterface onAddNewMemberInterface) {
        this.context = context;
        this.listSize = listSize;
        this.addedMemberInterface = onAddedMemberClickInterface;
        this.addNewMemberInterface = onAddNewMemberInterface;
    }


    @NonNull
    @Override
    public BottomSheetMemberAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // if view type is VIEW_TYPE_ITEM, then recyclerview item is loaded, else add new member layout is loaded
        if (viewType == Constants.VIEW_TYPE_ITEM) {
            return new MemberViewHolder(LayoutRecyclerViewBottomSheetBinding.inflate(LayoutInflater.from(context), parent, false));
        } else {
            return new ButtonViewHolder(LayoutAddNewMemberBinding.inflate(LayoutInflater.from(context), parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BottomSheetMemberAdapter.MyViewHolder holder, int position) {

        if (getItemViewType(position) == Constants.VIEW_TYPE_ITEM) {
            recyclerViewItemBinding.imgViewMemberProfile.setOnClickListener(v -> addedMemberInterface.onAddedMemberClicked(position));

        } else if (getItemViewType(position) == Constants.VIEW_TYPE_BUTTON) {

            addNewMemberBinding.consAddNewMember.setOnClickListener(v -> addNewMemberInterface.onAddNewMemberClicked());
        }

    }

    @Override
    public int getItemCount() {
        return listSize + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position < listSize ? Constants.VIEW_TYPE_ITEM : Constants.VIEW_TYPE_BUTTON;
    }

    public interface OnAddNewMemberInterface {
        void onAddNewMemberClicked();
    }

    public interface OnAddedMemberClickInterface {
        void onAddedMemberClicked(int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    //view holder for inflating recyclerview item extended from base view holder class
    public class MemberViewHolder extends MyViewHolder {

        public MemberViewHolder(@NonNull LayoutRecyclerViewBottomSheetBinding binding) {
            super(binding.getRoot());
            BottomSheetMemberAdapter.this.recyclerViewItemBinding = binding;
        }
    }

    //view holder for inflating add new member layout extended from base view holder class
    public class ButtonViewHolder extends MyViewHolder {

        public ButtonViewHolder(@NonNull LayoutAddNewMemberBinding newMemberBinding) {
            super(newMemberBinding.getRoot());
            BottomSheetMemberAdapter.this.addNewMemberBinding = newMemberBinding;
        }
    }
}