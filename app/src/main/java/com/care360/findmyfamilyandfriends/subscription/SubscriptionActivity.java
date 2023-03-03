package com.care360.findmyfamilyandfriends.subscription;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;
import com.care360.findmyfamilyandfriends.HomeScreen.HomeActivity;
import com.care360.findmyfamilyandfriends.SharedPreference.SharedPreference;
import com.care360.findmyfamilyandfriends.databinding.ActivitySubscriptionBinding;
import com.care360.findmyfamilyandfriends.subscription.adapters.ProductDetailsAdapter;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;

public class SubscriptionActivity extends AppCompatActivity implements ProductDetailsAdapter.ProductSelected {

    private BillingClient billingClient;
    private List<ProductDetails> productDetailsList;
    private Handler handler;
    private ProductDetailsAdapter adapter;

    private ActivitySubscriptionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySubscriptionBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        productDetailsList = new ArrayList<>();

        adapter = new ProductDetailsAdapter(this);


        handler = new Handler();

        billingClient = BillingClient.newBuilder(this)
                .enablePendingPurchases()
                .setListener(
                        (billingResult, list) -> {
                            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null) {
                                for (Purchase purchase : list) {
                                    verifySubPurchase(purchase);
                                }
                            }
                        }
                ).build();

        //start the connection after initializing the billing client
        establishConnection();
    }

    void establishConnection() {

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    showProducts();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                establishConnection();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    void showProducts() {

        ImmutableList<QueryProductDetailsParams.Product> productList = ImmutableList.of(
                //Product 1
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId("one_week")
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build(),

                //Product 2
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId("one_month")
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build(),

                //Product 3
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId("one_year")
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build()
        );

        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build();

        billingClient.queryProductDetailsAsync(params, (billingResult, prodDetailsList) -> {

                    // Process the result
                    productDetailsList.clear();
                    handler.postDelayed((Runnable) () -> {

                        productDetailsList.addAll(prodDetailsList);

                        Log.d(" number of products", "" + productDetailsList.size());


                        adapter.setProductDetails(prodDetailsList);
                        binding.productList.setHasFixedSize(true);
                        binding.productList.setLayoutManager(new LinearLayoutManager(SubscriptionActivity.this, LinearLayoutManager.VERTICAL, false));
                        binding.productList.setAdapter(adapter);
                    }, 2000);

                }
        );

    }

    void launchPurchaseFlow(ProductDetails productDetails) {
        assert productDetails.getSubscriptionOfferDetails() != null;
        ImmutableList<BillingFlowParams.ProductDetailsParams> productDetailsParamsList =
                ImmutableList.of(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                                .setProductDetails(productDetails)
                                .setOfferToken(productDetails.getSubscriptionOfferDetails().get(0).getOfferToken())
                                .build()
                );
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build();

        billingClient.launchBillingFlow(SubscriptionActivity.this, billingFlowParams);
    }

    void verifySubPurchase(Purchase purchases) {

        AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams
                .newBuilder()
                .setPurchaseToken(purchases.getPurchaseToken())
                .build();

        billingClient.acknowledgePurchase(acknowledgePurchaseParams, billingResult -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                //user prefs to set premium
                Toast.makeText(SubscriptionActivity.this, "Subscription activated, Enjoy!", Toast.LENGTH_SHORT).show();
                //Setting premium to 1
                // true - premium
                // false - no premium
                SharedPreference.setPremiumState(1);
                startActivity(new Intent(this, HomeActivity.class));
                finish();
            }
        });

        Log.d("Purchase Token: ", "" + purchases.getPurchaseToken());
        Log.d("Purchase Time: ", "" + purchases.getPurchaseTime());
        Log.d("Purchase OrderID: ", "" + purchases.getOrderId());
    }

    protected void onResume() {
        super.onResume();
        billingClient.queryPurchasesAsync(
                QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build(),
                (billingResult, list) -> {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        for (Purchase purchase : list) {
                            if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged()) {
                                verifySubPurchase(purchase);
                            }
                        }
                    }
                }
        );

    }

    @Override
    public void clickedProduct(int position) {
        launchPurchaseFlow(productDetailsList.get(position));
    }
}