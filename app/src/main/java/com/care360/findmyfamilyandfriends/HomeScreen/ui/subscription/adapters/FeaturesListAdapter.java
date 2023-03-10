package com.care360.findmyfamilyandfriends.HomeScreen.ui.subscription.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.care360.findmyfamilyandfriends.HomeScreen.ui.subscription.model.Features;
import com.care360.findmyfamilyandfriends.R;

import java.util.ArrayList;

public class FeaturesListAdapter extends RecyclerView.Adapter<FeaturesListAdapter.FeaturesListViewHolder> {

    private ArrayList<Features> featuresArrayList;
    private final itemSelected selected;
    private final Context context;

    public FeaturesListAdapter(Context context, itemSelected selected) {
        this.selected = selected;
        this.context = context;
    }

    public void setFeaturesArrayList(ArrayList<Features> featuresArrayList) {
        this.featuresArrayList = featuresArrayList;
    }

    @NonNull
    @Override
    public FeaturesListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.features_itemview,parent,false);

        return new FeaturesListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeaturesListViewHolder holder, int position) {

        //loading icon
        Glide.with(context).load(featuresArrayList.get(position).getImage()).placeholder(R.drawable.bell_icon).into(holder.icon);

        //setting texts
        holder.heading.setText(featuresArrayList.get(position).getHeading());
        holder.text.setText(featuresArrayList.get(position).getText());

        holder.itemView.setOnClickListener(v -> {
            selected.itemClicked(featuresArrayList.get(position));
        });
    }

    @Override
    public int getItemCount() {

        if (featuresArrayList != null)
            return featuresArrayList.size();
        else
            return 0;
    }

    static class FeaturesListViewHolder extends RecyclerView.ViewHolder{

        private final AppCompatImageView icon;
        private final AppCompatTextView heading;
        private final AppCompatTextView text;

        public FeaturesListViewHolder(@NonNull View itemView) {
            super(itemView);

            icon = itemView.findViewById(R.id.image_);
            heading = itemView.findViewById(R.id.heading_text);
            text = itemView.findViewById(R.id.text_);
        }
    }

    public interface itemSelected{

        void itemClicked(Features feature);

    }
}
