package com.hammad.findmyfamily.HomeScreen.FragmentLocation;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hammad.findmyfamily.HomeScreen.FragmentLocation.JoinCircle.CircleModel;
import com.hammad.findmyfamily.R;
import com.hammad.findmyfamily.databinding.ToolbarListItemBinding;

import java.util.List;

public class CircleToolbarAdapter extends RecyclerView.Adapter<CircleToolbarAdapter.ViewHolder> {

    Context context;

    List<CircleModel> circleList;

    OnToolbarCircleClickListener mOnCircleClickListener;

    private ToolbarListItemBinding listItemBinding;

    int selectedItemPosition = -1;

    public CircleToolbarAdapter(Context context, List<CircleModel> list, OnToolbarCircleClickListener onToolbarCircleClickListener) {
        this.context = context;
        this.circleList = list;
        this.mOnCircleClickListener = onToolbarCircleClickListener;
    }

    @NonNull
    @Override
    public CircleToolbarAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(ToolbarListItemBinding.inflate(LayoutInflater.from(context),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull CircleToolbarAdapter.ViewHolder holder, int position) {

        // circle item
        CircleModel circleItem = circleList.get(position);

        //setting the details to view
        holder.binding.txtCircleName.setText(circleItem.getCircleName());

        holder.binding.txtCircleNameFirstChar.setText(String.valueOf(circleItem.getCircleName().charAt(0)));

        // circle name click listener
        holder.binding.consToolbarListItem.setOnClickListener(v -> {

            listItemBinding.consToolbarListItem.setBackgroundColor(Color.TRANSPARENT);
            listItemBinding.imgCheck.setVisibility(View.GONE);

            if (selectedItemPosition == holder.getAdapterPosition()) {
                selectedItemPosition = -1;
                notifyDataSetChanged();
                return;
            }

            selectedItemPosition = holder.getAdapterPosition();
            notifyDataSetChanged();

            //interface click listener
            mOnCircleClickListener.onCircleSelected(position);
        });

        if (selectedItemPosition == holder.getAdapterPosition()) {

            // saving updated value in shared pref

            holder.binding.consToolbarListItem.setBackgroundColor(context.getColor(R.color.grey_bottom));
            holder.binding.imgCheck.setVisibility(View.VISIBLE);
        }
        else {
            holder.binding.consToolbarListItem.setBackgroundColor(Color.TRANSPARENT);
            holder.binding.imgCheck.setVisibility(View.GONE);
        }

        if (circleItem.equals("Circle 2")) {
            listItemBinding.consToolbarListItem.setBackgroundColor(context.getColor(R.color.grey_bottom));
            listItemBinding.imgCheck.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return circleList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ToolbarListItemBinding binding;

        public ViewHolder(@NonNull ToolbarListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            CircleToolbarAdapter.this.listItemBinding = binding;
        }
    }

    public interface OnToolbarCircleClickListener {
        void onCircleSelected(int position);
    }

}
