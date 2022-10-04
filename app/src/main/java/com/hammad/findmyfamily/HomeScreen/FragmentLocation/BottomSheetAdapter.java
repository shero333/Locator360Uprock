package com.hammad.findmyfamily.HomeScreen.FragmentLocation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hammad.findmyfamily.databinding.LayoutRecyclerViewBottomSheetBinding;

public class BottomSheetAdapter extends RecyclerView.Adapter<BottomSheetAdapter.ViewHolder> {

    Context context;

    LayoutRecyclerViewBottomSheetBinding binding;

    public BottomSheetAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public BottomSheetAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutRecyclerViewBottomSheetBinding.inflate(LayoutInflater.from(context),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull BottomSheetAdapter.ViewHolder holder, int position) {

        binding.imgViewMemberProfile.setOnClickListener(v -> Toast.makeText(context, ""+position, Toast.LENGTH_SHORT).show());

        binding.txtViewMemberName.append(" "+position);
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull LayoutRecyclerViewBottomSheetBinding binding) {
            super(binding.getRoot());

            BottomSheetAdapter.this.binding = binding;
        }
    }
}
