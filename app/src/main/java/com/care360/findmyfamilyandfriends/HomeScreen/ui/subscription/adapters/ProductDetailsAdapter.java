package com.care360.findmyfamilyandfriends.HomeScreen.ui.subscription.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.ProductDetails;
import com.care360.findmyfamilyandfriends.HomeScreen.ui.subscription.model.ProductDetailsModel;
import com.care360.findmyfamilyandfriends.R;

import java.util.List;

public class ProductDetailsAdapter extends RecyclerView.Adapter<ProductDetailsAdapter.ProductDetailsViewHolder> {

    private List<ProductDetailsModel> productDetails;
    private final ProductSelected selected;
    private final Context context;


    public ProductDetailsAdapter(Context context,ProductSelected selected) {
        this.selected = selected;
        this.context = context;
    }

    public void setProductDetails(List<ProductDetailsModel> productDetails) {
        this.productDetails = productDetails;
    }

    @NonNull
    @Override
    public ProductDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subscription_itemview,parent,false);

        return new ProductDetailsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductDetailsViewHolder holder, int position) {

//
//        if (productDetails.get(position).getProductId().contains("month")) {
//            holder.percentOff.setText("10%");
//            holder.price.setText("");
//            holder.savePercent.setText("");
//            holder.productType.setText(productDetails.get(position).getProductType());
//
//        }
//        else if (productDetails.get(position).getProductId().contains("yearly")) {
//            holder.percentOff.setText(productDetails.get(position).getSubscriptionOfferDetails().get(position).getOfferTags().get(position));
//            holder.price.setText(productDetails.get(position).getSubscriptionOfferDetails().get(position).getPricingPhases().getPricingPhaseList().get(position).getFormattedPrice());
//            holder.savePercent.setText("");
//            holder.productType.setText(productDetails.get(position).getSubscriptionOfferDetails().get(position).getPricingPhases().getPricingPhaseList().get(position).getBillingPeriod());
//
//        }

        holder.percentOff.setText(productDetails.get(position).getProduct().getSubscriptionOfferDetails().get(position).getOfferTags().get(position));
        holder.price.setText(productDetails.get(position).getProduct().getSubscriptionOfferDetails().get(position).getPricingPhases().getPricingPhaseList().get(position).getFormattedPrice());
        holder.savePercent.setText("");
        holder.productType.setText(productDetails.get(position).getProduct().getSubscriptionOfferDetails().get(position).getPricingPhases().getPricingPhaseList().get(position).getBillingPeriod());

        if (productDetails.get(position).isSelected()){

            holder.selectionview.setImageResource(R.drawable.tick_circle);
            holder.cardView.setBackgroundColor(context.getColor(R.color.orange));
        }else{

            holder.selectionview.setImageResource(0);
            holder.cardView.setBackgroundColor(context.getColor(R.color.subs_card_back_def));
        }


        holder.itemView.setOnClickListener(v -> {

            productDetails.get(position).setSelected(!productDetails.get(position).isSelected());
            notifyItemChanged(position);

            selected.clickedProduct(position);

        });

    }

    @Override
    public int getItemCount() {

        if (productDetails != null)
            return productDetails.size();
        else
            return 0;
    }

    static class ProductDetailsViewHolder extends RecyclerView.ViewHolder{

        private final AppCompatTextView percentOff;
        private final AppCompatTextView price;
        private final AppCompatTextView savePercent;
        private final AppCompatTextView productType;
        private final CardView cardView;
        private AppCompatImageView selectionview;


        public ProductDetailsViewHolder(@NonNull View itemView) {
            super(itemView);

            percentOff = itemView.findViewById(R.id.percent_off);
            price = itemView.findViewById(R.id.price);
            savePercent = itemView.findViewById(R.id.percentage_save);
            productType = itemView.findViewById(R.id.product_type);
            cardView = itemView.findViewById(R.id.cardView);
            selectionview = itemView.findViewById(R.id.selected_tick);
        }
    }


    public interface ProductSelected{
        void clickedProduct(int position);
    }
}
