package com.hammad.findmyfamily.HomeScreen.CustomToolbar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hammad.findmyfamily.databinding.ToolbarListItemBinding;

public class CircleAdapterToolbar extends RecyclerView.Adapter<CircleAdapterToolbar.ViewHolder> {

    Context context;

    private ToolbarListItemBinding binding;

    public CircleAdapterToolbar(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public CircleAdapterToolbar.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(ToolbarListItemBinding.inflate(LayoutInflater.from(context),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull CircleAdapterToolbar.ViewHolder holder, int position) {

        binding.txtCircleName.setText("Circle ".concat(String.valueOf((position+1))));
        binding.txtCircleNameFirstChar.setText(""+(position+1));

        binding.txtCircleName.setOnClickListener(v -> Toast.makeText(context, ""+(position+1), Toast.LENGTH_SHORT).show());

    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(@NonNull ToolbarListItemBinding binding) {
            super(binding.getRoot());
            CircleAdapterToolbar.this.binding = binding;
        }
    }
}
