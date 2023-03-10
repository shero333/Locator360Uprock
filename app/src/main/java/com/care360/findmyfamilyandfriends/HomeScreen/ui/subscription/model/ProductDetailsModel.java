package com.care360.findmyfamilyandfriends.HomeScreen.ui.subscription.model;

import androidx.annotation.NonNull;
import com.android.billingclient.api.ProductDetails;
import java.io.Serializable;

public class ProductDetailsModel implements Serializable {

    private ProductDetails product;
    private boolean selected;

    public ProductDetailsModel(ProductDetails product,boolean selected) {
        this.product = product;
        this.selected = selected;
    }

    @NonNull
    @Override
    public String toString() {
        return "ProductDetailsModel{" +
                "product=" + product +
                ", selected=" + selected +
                '}';
    }

    public ProductDetails getProduct() {
        return product;
    }

    public void setProduct(ProductDetails product) {
        this.product = product;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
