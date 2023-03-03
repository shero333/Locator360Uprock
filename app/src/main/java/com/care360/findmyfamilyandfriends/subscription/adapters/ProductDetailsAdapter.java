package com.care360.findmyfamilyandfriends.subscription.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.ProductDetails;
import com.care360.findmyfamilyandfriends.R;

import java.util.List;

public class ProductDetailsAdapter extends RecyclerView.Adapter<ProductDetailsAdapter.ProductDetailsViewHolder> {

    private List<ProductDetails> productDetails;
    private ProductSelected selected;


    public ProductDetailsAdapter(ProductSelected selected) {
        this.selected = selected;
    }

    public void setProductDetails(List<ProductDetails> productDetails) {
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

    }

    @Override
    public int getItemCount() {

        if (productDetails != null)
            return productDetails.size();
        else
            return 0;
    }

    static class ProductDetailsViewHolder extends RecyclerView.ViewHolder{

        public ProductDetailsViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }


    public interface ProductSelected{
        void clickedProduct(int position);
    }
}
