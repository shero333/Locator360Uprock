package com.care360.findmyfamilyandfriends.HomeScreen.ui.subscription;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.care360.findmyfamilyandfriends.HomeScreen.ui.Driving.DrivingViewModel;
import com.care360.findmyfamilyandfriends.HomeScreen.ui.subscription.adapters.FeaturesListAdapter;
import com.care360.findmyfamilyandfriends.HomeScreen.ui.subscription.adapters.ProductDetailsAdapter;
import com.care360.findmyfamilyandfriends.HomeScreen.ui.subscription.model.Features;
import com.care360.findmyfamilyandfriends.HomeScreen.ui.subscription.model.ProductDetailsModel;
import com.care360.findmyfamilyandfriends.R;
import com.care360.findmyfamilyandfriends.SharedPreference.SharedPreference;
import com.care360.findmyfamilyandfriends.databinding.FragmentSubscriptionBinding;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

public class SubscriptionFragment extends Fragment implements ProductDetailsAdapter.ProductSelected, FeaturesListAdapter.itemSelected {

    private SubscriptionViewModel viewModel;
    private InterstitialAd mInterstitialAd;
    private AdRequest adRequest;
    private FragmentSubscriptionBinding binding;

    private BillingClient billingClient;
    private List<ProductDetails> productDetailsList;
    private Handler handler;
    private ProductDetailsAdapter adapter;

    private MutableLiveData<Integer> clickedItemPosition;
    private FeaturesListAdapter featuresAdapter;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        viewModel = new ViewModelProvider(this).get(SubscriptionViewModel.class);

        binding = FragmentSubscriptionBinding.inflate(inflater, container, false);

        MobileAds.initialize(requireContext());

        //banner
        adRequest = new AdRequest.Builder().build();


        setAd();

        productDetailsList = new ArrayList<>();

        adapter = new ProductDetailsAdapter(requireContext(),this);
        featuresAdapter = new FeaturesListAdapter(requireContext(),this);

        handler = new Handler();

        billingClient = BillingClient.newBuilder(requireContext())
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

        viewModel.getDataFirestoreDatabase();
        viewModel.getFeaturesMutableLiveData().observe(getViewLifecycleOwner(), features -> {

            Log.d( "onCreateViewFeatures: ",features.toString());

            if (!features.isEmpty()){
                featuresAdapter.setFeaturesArrayList(features);
                binding.features.setAdapter(featuresAdapter);
                featuresAdapter.notifyDataSetChanged();
            }else{

                //UI change
            }

        });


        binding.productList.setHasFixedSize(true);
        binding.productList.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false));

        binding.features.setHasFixedSize(true);
        binding.features.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false));

        binding.startTrialButton.setOnClickListener(v -> {

            clickedItemPosition.observe(getViewLifecycleOwner(), integer -> {

                launchPurchaseFlow(productDetailsList.get(integer));

            });

        });

        return binding.getRoot();
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

                    handler.postDelayed(() -> {

                        if (prodDetailsList.size() > 0){

                            productDetailsList.addAll(prodDetailsList);

                            Log.d(" number of products", "" + productDetailsList.size());

                            List<ProductDetailsModel> productDetailsModelList = new ArrayList<>();

                            for (ProductDetails details:productDetailsList){
                                productDetailsModelList.add(new ProductDetailsModel(details,false));
                            }

                            adapter.setProductDetails(productDetailsModelList);
                            binding.productList.setAdapter(adapter);
                        }else{

                            //make UI change
                        }


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

        billingClient.launchBillingFlow(requireActivity(), billingFlowParams);
    }

    void verifySubPurchase(Purchase purchases) {

        AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams
                .newBuilder()
                .setPurchaseToken(purchases.getPurchaseToken())
                .build();

        billingClient.acknowledgePurchase(acknowledgePurchaseParams, billingResult -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                //user prefs to set premium
                Toast.makeText(requireContext(), "Subscription activated, Enjoy!", Toast.LENGTH_SHORT).show();
                //Setting premium to 1
                // true - premium
                // false - no premium
                SharedPreference.setPremiumState(1);
                startActivity(new Intent(requireActivity(), HomeActivity.class));
                requireActivity().finish();
            }
        });

        Log.d("Purchase Token: ", "" + purchases.getPurchaseToken());
        Log.d("Purchase Time: ", "" + purchases.getPurchaseTime());
        Log.d("Purchase OrderID: ", "" + purchases.getOrderId());
    }

    private void setAd() {

        InterstitialAd.load(
                requireContext(),
                "ca-app-pub-3940256099942544/1033173712",
                adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError adError) {

                        Log.d("AdError", adError.toString());
                        mInterstitialAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        Log.d("AdError", "Ad was loaded.");
                        mInterstitialAd = interstitialAd;
                    }
                });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.bannerAd.loadAd(adRequest);

    }

    @Override
    public void clickedProduct(int position) {

        clickedItemPosition.setValue(position);
    }

    @Override
    public void itemClicked(Features feature) {

        //Full view activity

    }
}