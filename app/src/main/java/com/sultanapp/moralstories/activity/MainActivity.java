package com.sultanapp.moralstories.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sultanapp.moralstories.R;
import com.sultanapp.moralstories.adapters.CategoryAdapter;
import com.sultanapp.moralstories.app.MyApplication;
import com.sultanapp.moralstories.data.constant.AppConstant;
import com.sultanapp.moralstories.data.sqlite.NotificationDbController;
import com.sultanapp.moralstories.helper.ApppreferenceManager;
import com.sultanapp.moralstories.listeners.ListItemClickListener;
import com.sultanapp.moralstories.models.categories.Categories;
import com.sultanapp.moralstories.models.notification.NotificationModel;
import com.sultanapp.moralstories.utility.ActivityUtilities;
import com.sultanapp.moralstories.utility.AppUtilities;
import com.sultanapp.moralstories.utility.RateItDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends BaseActivity implements View.OnClickListener{

    private Activity mActivity;
    private Context mContext;

    private RelativeLayout mNotificationView;
    private ImageButton mImgBtnSearch;
    LinearLayoutManager linearLayoutManager;
    ApppreferenceManager apppreferenceManager;
    private ArrayList<Categories> mCategoryList;
    private CategoryAdapter mAdapter = null;


    private RecyclerView mRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RateItDialogFragment.show(this, getSupportFragmentManager());





        initVar();
        initView();
        loadData();
        initListener();

    }

    @Override
    protected void onPause() {
        super.onPause();

        //unregister broadcast receiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(newNotificationReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //register broadcast receiver
        IntentFilter intentFilter = new IntentFilter(AppConstant.NEW_NOTI);
        LocalBroadcastManager.getInstance(this).registerReceiver(newNotificationReceiver, intentFilter);

        initNotification();
    }

    // received new broadcast
    private BroadcastReceiver newNotificationReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            initNotification();
        }
    };


    @Override
    public void onBackPressed() {
        AppUtilities.tapPromptToExit(mActivity);
    }

    private void initVar() {
        mActivity = MainActivity.this;
        mContext = getApplicationContext();

        mCategoryList = new ArrayList<>();
    }

    private void initView() {
        setContentView(R.layout.activity_main);
        apppreferenceManager = new ApppreferenceManager(this);
        if(apppreferenceManager.getDarkModeState()){
            setTheme(R.style.AppTheme);
        }else {
            setTheme(R.style.AppThemeDark);
        }
       mNotificationView = (RelativeLayout) findViewById(R.id.notificationView);
        mImgBtnSearch = (ImageButton) findViewById(R.id.imgBtnSearch);

        mRecycler = (RecyclerView) findViewById(R.id.rvContent);
//        mRecycler.setLayoutManager(new GridLayoutManager(mActivity, 1, GridLayoutManager.VERTICAL, false));


        mRecycler.hasFixedSize();
        linearLayoutManager = new LinearLayoutManager(this);
        mRecycler.setLayoutManager(linearLayoutManager);
        mRecycler.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new CategoryAdapter(mContext, mActivity, mCategoryList);
        mRecycler.setAdapter(mAdapter);

        initToolbar(false);
        initDrawer();
        initLoader();
    }

    private void initListener() {
        //notification view click listener
        mNotificationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtilities.getInstance().invokeNewActivity(mActivity, NotificationListActivity.class, false);
            }
        });

        // Search button click listener
        mImgBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApplication.getInstance().setInterstitialListner(new MyApplication.InterstitialListner() {
                    @Override
                    public void onAdClosed() {
                        ActivityUtilities.getInstance().invokeNewActivity(mActivity, SearchActivity.class, false);
                    }

                    @Override
                    public void onAdFailedToLoad() {
                        ActivityUtilities.getInstance().invokeNewActivity(mActivity, SearchActivity.class, false);

                    }
                });

                MyApplication.getInstance().showdAdmobInterstitial(MainActivity.this);
            }
        });


        // recycler list item click listener
        mAdapter.setItemClickListener(new ListItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                final Categories model = mCategoryList.get(position);
                switch (view.getId()) {
                    case R.id.card_view:
                        MyApplication.getInstance().setInterstitialListner(new MyApplication.InterstitialListner() {
                            @Override
                            public void onAdClosed() {
                                ActivityUtilities.getInstance().invokeItemListActiviy(mActivity, ItemListActivity.class, model.getCategoryId(), model.getCategoryName(), false);
                            }

                            @Override
                            public void onAdFailedToLoad() {
                                ActivityUtilities.getInstance().invokeItemListActiviy(mActivity, ItemListActivity.class, model.getCategoryId(), model.getCategoryName(), false);
                            }
                        });
                        MyApplication.getInstance().showdAdmobInterstitial(MainActivity.this);
                        break;
                    default:
                        break;
                }
            }

        });
    }

    private void loadData() {
        showLoader();

        loadCategories();

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.adsView);
        MyApplication.getInstance().showBannerADs(MainActivity.this, linearLayout);
    }

    private void loadCategories() {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(getAssets().open(AppConstant.CATEGORY_FILE)));
            String temp;
            while ((temp = br.readLine()) != null)
                sb.append(temp);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        parseCategory(sb.toString());
    }

    private void parseCategory(String jsonData) {
        try {

            JSONObject jsonObjMain = new JSONObject(jsonData);
            JSONArray jsonArray1 = jsonObjMain.getJSONArray(AppConstant.JSON_KEY_ITEMS);

            for (int i = 0; i < jsonArray1.length(); i++) {
                JSONObject jsonObj = jsonArray1.getJSONObject(i);

                String categoryID = jsonObj.getString(AppConstant.JSON_KEY_CATEGORY_ID);
                String categoryName = jsonObj.getString(AppConstant.JSON_KEY_CATEGORY_TITLE);
                String categoryImg = jsonObj.getString(AppConstant.JSON_KEY_CATEGORY_IMAGE);

                mCategoryList.add(new Categories(categoryID, categoryName, categoryImg));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        hideLoader();
        mAdapter.notifyDataSetChanged();
    }

    public void initNotification() {
        NotificationDbController notificationDbController = new NotificationDbController(mContext);
        TextView notificationCount = (TextView) findViewById(R.id.notificationCount);
        notificationCount.setVisibility(View.INVISIBLE);

        ArrayList<NotificationModel> notiArrayList = notificationDbController.getUnreadData();

        if (notiArrayList != null && !notiArrayList.isEmpty()) {
            int totalUnread = notiArrayList.size();
            if (totalUnread > 0) {
                notificationCount.setVisibility(View.VISIBLE);
                notificationCount.setText(String.valueOf(totalUnread));
            } else {
                notificationCount.setVisibility(View.INVISIBLE);
            }
        }

    }

    @Override
    public void onClick(View view) {

    }
}
