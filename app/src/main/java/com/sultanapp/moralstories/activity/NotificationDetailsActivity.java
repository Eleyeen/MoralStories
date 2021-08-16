package com.sultanapp.moralstories.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sultanapp.moralstories.R;
import com.sultanapp.moralstories.app.MyApplication;
import com.sultanapp.moralstories.data.constant.AppConstant;
import com.sultanapp.moralstories.helper.ApppreferenceManager;
import com.sultanapp.moralstories.utility.ActivityUtilities;

public class NotificationDetailsActivity extends BaseActivity {

    private Context mContext;
    private Activity mActivity;

    private TextView mTitleView, mMessageView;
    private Button mLinkButton;
    private String mTitle, mMessage, mUrl;
    private boolean mFromPush = false;
    ApppreferenceManager apppreferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = NotificationDetailsActivity.this;
        mContext = mActivity.getApplicationContext();
        apppreferenceManager = new ApppreferenceManager(this);
        if(apppreferenceManager.getDarkModeState()){
            setTheme(R.style.AppTheme);
        }else {
            setTheme(R.style.AppThemeDark);
        }
        initVar();
        initView();
        initFunctionality();
        initListener();
    }

    private void initVar() {
        Bundle extras = getIntent().getExtras();
        mTitle = extras.getString(AppConstant.BUNDLE_KEY_TITLE);
        mMessage = extras.getString(AppConstant.BUNDLE_KEY_MESSAGE);
        mUrl = extras.getString(AppConstant.BUNDLE_KEY_URL);
        mFromPush = extras.getBoolean(AppConstant.BUNDLE_FROM_PUSH, false);
    }

    private void initView() {
        setContentView(R.layout.activity_notification_details);

        mTitleView = (TextView) findViewById(R.id.title);
        mMessageView = (TextView) findViewById(R.id.message);
        mLinkButton = (Button) findViewById(R.id.btn_read);

        initToolbar(true);
        setToolbarTitle(getString(R.string.notifications));
        enableUpButton();
    }


    private void initFunctionality() {

        mTitleView.setText(mTitle);
        mMessageView.setText(mMessage);

        if (mUrl != null && !mUrl.isEmpty()) {
            mLinkButton.setEnabled(true);
        } else {
            mLinkButton.setEnabled(false);
        }

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.adsView);
        MyApplication.getInstance().showBannerADs(NotificationDetailsActivity.this, linearLayout);
    }

    private void initListener() {
        mLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.getInstance().setInterstitialListner(new MyApplication.InterstitialListner() {
                    @Override
                    public void onAdClosed() {
                        ActivityUtilities.getInstance().invokeCustomUrlActivity(mActivity, CustomUrlActivity.class, mTitle, mUrl, false);
                    }

                    @Override
                    public void onAdFailedToLoad() {
                        ActivityUtilities.getInstance().invokeCustomUrlActivity(mActivity, CustomUrlActivity.class, mTitle, mUrl, false);
                    }
                });

                MyApplication.getInstance().showdAdmobInterstitial(NotificationDetailsActivity.this);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                goToHome();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onBackPressed() {
        goToHome();
    }

    private void goToHome() {
        if (mFromPush) {
            Intent intent = new Intent(NotificationDetailsActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            finish();
        }
    }
}
