package com.sultanapp.moralstories.app;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.widget.LinearLayout;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.sultanapp.moralstories.R;
import com.sultanapp.moralstories.helper.ApppreferenceManager;

public class MyApplication extends Application {
    private InterstitialAd mInterstitialAd;
    private com.facebook.ads.InterstitialAd interstitialAd;
    InterstitialListner interstitialListner;
    AdRequest adRequest;
    ApppreferenceManager apppreferenceManager;


    public int count, adsCount = 3;

    public boolean isDarkTheme() {
        return isDarkTheme;
    }

    public void setDarkTheme(boolean darkTheme) {
        isDarkTheme = darkTheme;
    }

    private  boolean isDarkTheme;


    public static MyApplication intance = null;

    public static MyApplication getInstance() {
        return intance;
    }

    //if you have set true then show admob and if you have set false then show facebook
    boolean isAdmobShow = true;

    @Override
    public void onCreate() {
        super.onCreate();
        intance = this;

        FirebaseMessaging.getInstance().subscribeToTopic("storiesappnotification");
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
//        AdSettings.setDebugBuild(true);

    }

    public void setInterstitialListner(InterstitialListner interstitialListner) {
        this.interstitialListner = interstitialListner;
    }

    public interface InterstitialListner {
        void onAdClosed();

        void onAdFailedToLoad();
    }

    public void showdAdmobInterstitial(final Activity activity) {
        count++;
        if (count == adsCount) {
            count = 0;
            if (activity != null) {
                final Dialog dialog = new Dialog(activity);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.ad_loading_dialog);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.show();

                if (isAdmobShow) {
                    adRequest = new AdRequest.Builder().build();
                    mInterstitialAd = new InterstitialAd(activity);
                    mInterstitialAd.setAdUnitId(getResources().getString(R.string.admob_interstitial));
                    mInterstitialAd.setAdListener(new AdListener() {
                        @Override
                        public void onAdOpened() {
                            super.onAdOpened();
                            dialog.dismiss();

                        }

                        @Override
                        public void onAdFailedToLoad(LoadAdError loadAdError) {
                            super.onAdFailedToLoad(loadAdError);

                            interstitialAd = new com.facebook.ads.InterstitialAd(activity, getResources().getString(R.string.facebook_interstitial));
                            // Create listeners for the Interstitial Ad
                            InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
                                 @Override
                                public void onInterstitialDisplayed(Ad ad) {
                                    // Interstitial ad displayed callback
                                    dialog.dismiss();
                                }

                                @Override
                                public void onInterstitialDismissed(Ad ad) {
                                    // Interstitial dismissed callback
                                    dialog.dismiss();
                                    interstitialListner.onAdClosed();
                                    // Code to be executed when the interstitial ad is closed.
                                }

                                @Override
                                public void onError(Ad ad, AdError adError) {
                                    // Ad error callback
                                    dialog.dismiss();
                                    interstitialListner.onAdFailedToLoad();
                                }

                                @Override
                                public void onAdLoaded(Ad ad) {
                                    if (interstitialAd.isAdLoaded())
                                        interstitialAd.show();
                                    // Interstitial ad is loaded and ready to be displayed
                                }

                                @Override
                                public void onAdClicked(Ad ad) {
                                    // Ad clicked callback
                                }

                                @Override
                                public void onLoggingImpression(Ad ad) {
                                    // Ad impression logged callback
                                }
                            };
                            interstitialAd.loadAd(
                                    interstitialAd.buildLoadAdConfig()
                                            .withAdListener(interstitialAdListener)
                                            .build());
                        }

                        @Override
                        public void onAdLoaded() {
                            super.onAdLoaded();
                            if (mInterstitialAd.isLoaded())
                                mInterstitialAd.show();
                        }

                        @Override
                        public void onAdClicked() {
                            super.onAdClicked();
                        }

                        @Override
                        public void onAdClosed() {
                            super.onAdClosed();
                            dialog.dismiss();
                            interstitialListner.onAdClosed();
                            // Code to be executed when the interstitial ad is closed.
                        }
                    });

                    mInterstitialAd.loadAd(adRequest);
                } else {
                    interstitialAd = new com.facebook.ads.InterstitialAd(activity, getResources().getString(R.string.facebook_interstitial));
                    // Create listeners for the Interstitial Ad
                    InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
                        @Override
                        public void onInterstitialDisplayed(Ad ad) {
                            // Interstitial ad displayed callback
                            dialog.dismiss();
                        }

                        @Override
                        public void onInterstitialDismissed(Ad ad) {
                            // Interstitial dismissed callback
                            dialog.dismiss();
                            interstitialListner.onAdClosed();
                            // Code to be executed when the interstitial ad is closed.
                        }

                        @Override
                        public void onError(Ad ad, AdError adError) {
                            // Ad error callback
                            dialog.dismiss();
                            interstitialListner.onAdFailedToLoad();
                        }

                        @Override
                        public void onAdLoaded(Ad ad) {
                            if (interstitialAd.isAdLoaded())
                                interstitialAd.show();
                            // Interstitial ad is loaded and ready to be displayed
                        }

                        @Override
                        public void onAdClicked(Ad ad) {
                            // Ad clicked callback
                        }

                        @Override
                        public void onLoggingImpression(Ad ad) {
                            // Ad impression logged callback
                        }
                    };
                    interstitialAd.loadAd(
                            interstitialAd.buildLoadAdConfig()
                                    .withAdListener(interstitialAdListener)
                                    .build());
                }
            }
        } else {
            interstitialListner.onAdFailedToLoad();
        }
    }


    public void showBannerADs(final Activity activity, final LinearLayout linearLayout) {
        linearLayout.removeAllViews();
        if (isAdmobShow) {
            com.google.android.gms.ads.AdView adView = new com.google.android.gms.ads.AdView(activity);
            adView.setAdSize(com.google.android.gms.ads.AdSize.SMART_BANNER);
            adView.setAdUnitId(getResources().getString(R.string.admob_banner));
            linearLayout.addView(adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    // Code to be executed when an ad finishes loading.
                }

                @Override
                public void onAdFailedToLoad(LoadAdError adError) {
                    // Code to be executed when an ad request fails.
                    linearLayout.removeAllViews();
                    AdView adView = new AdView(activity, getResources().getString(R.string.facebook_banner), AdSize.BANNER_HEIGHT_50);
                    linearLayout.addView(adView);
                    adView.loadAd();
                }

                @Override
                public void onAdOpened() {
                    // Code to be executed when an ad opens an overlay that
                    // covers the screen.
                }

                @Override
                public void onAdClicked() {
                    // Code to be executed when the user clicks on an ad.
                }

                @Override
                public void onAdLeftApplication() {
                    // Code to be executed when the user has left the app.
                }

                @Override
                public void onAdClosed() {
                    // Code to be executed when the user is about to return
                    // to the app after tapping on an ad.
                }
            });
            adView.loadAd(adRequest);
        } else {
            AdView adView = new AdView(activity, getResources().getString(R.string.facebook_banner), AdSize.BANNER_HEIGHT_50);
            linearLayout.addView(adView);
            adView.loadAd();
        }
    }
}
