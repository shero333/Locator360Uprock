package com.hammad.findmyfamily.HomeScreen.CustomToolbar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hammad.findmyfamily.databinding.ToolbarListItemBinding;

import java.util.List;

public class CircleAdapterToolbar extends RecyclerView.Adapter<CircleAdapterToolbar.ViewHolder> {

    private Context context;

    private List<String> stringList;

    private ToolbarListItemBinding binding;

    private OnToolbarCircleClickListener mOnCircleClickListener;

    public CircleAdapterToolbar(Context context,List<String> list,OnToolbarCircleClickListener onToolbarCircleClickListener) {
        this.context = context;
        this.stringList = list;
        this.mOnCircleClickListener = onToolbarCircleClickListener;
    }

    @NonNull
    @Override
    public CircleAdapterToolbar.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(ToolbarListItemBinding.inflate(LayoutInflater.from(context),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull CircleAdapterToolbar.ViewHolder holder, int position) {

        //setting the details to view
        binding.txtCircleName.setText(stringList.get(position));

        binding.txtCircleNameFirstChar.setText(String.valueOf(stringList.get(position).charAt(7)));

        // circle name click listener
        binding.txtCircleName.setOnClickListener(v -> mOnCircleClickListener.onCircleSelected(position));

    }

    @Override
    public int getItemCount() {
        return stringList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(@NonNull ToolbarListItemBinding binding) {
            super(binding.getRoot());
            CircleAdapterToolbar.this.binding = binding;
        }
    }

    public interface OnToolbarCircleClickListener{
        void onCircleSelected(int position);
    }

}