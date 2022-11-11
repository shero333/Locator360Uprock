package com.hammad.findmyfamily.HomeScreen.FragmentLocation.BottomSheetMembers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hammad.findmyfamily.R;
import com.hammad.findmyfamily.Util.Constants;
import com.hammad.findmyfamily.databinding.LayoutAddNewMemberBinding;
import com.hammad.findmyfamily.databinding.LayoutRecyclerViewBottomSheetBinding;

import java.util.List;

public class BottomSheetMemberAdapter extends RecyclerView.Adapter<BottomSheetMemberAdapter.MyViewHolder> {

    Context context;
    List<MemberDetail> memberDetailList;

    //binding
    LayoutRecyclerViewBottomSheetBinding recyclerViewItemBinding;
    LayoutAddNewMemberBinding addNewMemberBinding;

    //click listener interfaces
    OnAddedMemberClickInterface addedMemberInterface;
    OnAddNewMemberInterface addNewMemberInterface;

    public BottomSheetMemberAdapter(Context context, List<MemberDetail> memberDetailList, OnAddedMemberClickInterface onAddedMemberClickInterface, OnAddNewMemberInterface onAddNewMemberInterface) {
        this.context = context;
        this.memberDetailList = memberDetailList;
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

            MemberDetail memberItem = memberDetailList.get(position);

            // user profile image if any
            if(memberItem.getMemberImageUrl().equals(Constants.NULL)) {

                // make visible the name's first character text view
                recyclerViewItemBinding.txtViewMemberFirstChar.setVisibility(View.VISIBLE);
                recyclerViewItemBinding.txtViewMemberFirstChar.setText(String.valueOf(memberItem.getMemberFirstName().charAt(0)));

            }
            else if(!memberItem.getMemberImageUrl().equals(Constants.NULL)) {

                // hides the name's first character text view
                recyclerViewItemBinding.txtViewMemberFirstChar.setVisibility(View.GONE);

                // displaying the image
                Glide.with(context)
                        .load(memberItem.getMemberImageUrl())
                        .into(recyclerViewItemBinding.imgViewMemberProfile);

            }

            // name
            recyclerViewItemBinding.txtViewMemberName.setText(memberItem.getMemberFirstName().concat(" ").concat(memberItem.getMemberLastName()));

            // phone charging status
            if(memberItem.isPhoneCharging()) {
                if(memberItem.getBatteryPercentage() <= 20) {
                    recyclerViewItemBinding.imgViewBatteryStatus.setImageResource(R.drawable.ic_battery_low_charging);
                }
                else if(memberItem.getBatteryPercentage() > 20) {
                    recyclerViewItemBinding.imgViewBatteryStatus.setImageResource(R.drawable.ic_battery_good_charging);
                }
            }
            else if(!memberItem.isPhoneCharging()) {
                if(memberItem.getBatteryPercentage() <= 20) {
                    recyclerViewItemBinding.imgViewBatteryStatus.setImageResource(R.drawable.ic_battery_low);
                }
                else if(memberItem.getBatteryPercentage() > 20) {
                    recyclerViewItemBinding.imgViewBatteryStatus.setImageResource(R.drawable.ic_battery_good);
                }
            }

            // battery percentage
            recyclerViewItemBinding.txtViewBatteryPercentage.setText(String.valueOf(memberItem.getBatteryPercentage()).concat(" %"));

            // last known location address
            recyclerViewItemBinding.txtViewLastKnownAddress.setText(memberItem.getLocationAddress());

            // time stamp
            recyclerViewItemBinding.txtViewTimestamp.setText(memberItem.getLocationTimestamp());

            // view click listener
            recyclerViewItemBinding.consMemberBottomSheet.setOnClickListener(v -> addedMemberInterface.onAddedMemberClicked(position));

        } else if (getItemViewType(position) == Constants.VIEW_TYPE_BUTTON) {

            addNewMemberBinding.consAddNewMember.setOnClickListener(v -> addNewMemberInterface.onAddNewMemberClicked());
        }

    }

    @Override
    public int getItemCount() {
        return memberDetailList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position < memberDetailList.size() ? Constants.VIEW_TYPE_ITEM : Constants.VIEW_TYPE_BUTTON;
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

        LayoutRecyclerViewBottomSheetBinding binding;

        public MemberViewHolder(@NonNull LayoutRecyclerViewBottomSheetBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            //BottomSheetMemberAdapter.this.recyclerViewItemBinding = binding;
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
