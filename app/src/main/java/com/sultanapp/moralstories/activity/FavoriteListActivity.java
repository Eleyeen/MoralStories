package com.sultanapp.moralstories.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sultanapp.moralstories.R;
import com.sultanapp.moralstories.adapters.FavoriteAdapter;
import com.sultanapp.moralstories.app.MyApplication;
import com.sultanapp.moralstories.data.constant.AppConstant;
import com.sultanapp.moralstories.data.sqlite.FavoriteDbController;
import com.sultanapp.moralstories.helper.ApppreferenceManager;
import com.sultanapp.moralstories.listeners.ListItemClickListener;
import com.sultanapp.moralstories.models.content.Contents;
import com.sultanapp.moralstories.models.favorite.FavoriteModel;
import com.sultanapp.moralstories.utility.ActivityUtilities;
import com.sultanapp.moralstories.utility.DialogUtilities;

import java.util.ArrayList;


public class FavoriteListActivity extends BaseActivity {

    private Activity mActivity;
    private Context mContext;

    private ArrayList<FavoriteModel> mFavoriteList;
    private FavoriteAdapter mFavoriteAdapter = null;
    private RecyclerView mRecycler;

    private FavoriteDbController mFavoriteDbController;
    private MenuItem mMenuItemDeleteAll;
    private int mAdapterPosition;
ApppreferenceManager apppreferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        mActivity = FavoriteListActivity.this;
        mContext = mActivity.getApplicationContext();

        mFavoriteList = new ArrayList<>();
    }

    private void initView() {
        setContentView(R.layout.activity_item_list);

        mRecycler = (RecyclerView) findViewById(R.id.rvContent);
        mRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mFavoriteAdapter = new FavoriteAdapter(mContext, mActivity, mFavoriteList);
        mRecycler.setAdapter(mFavoriteAdapter);

        initToolbar(true);
        setToolbarTitle(getString(R.string.site_menu_fav));
        enableUpButton();
        initLoader();
    }

    private void initFunctionality() {

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.adsView);
        MyApplication.getInstance().showBannerADs(FavoriteListActivity.this, linearLayout);
    }

    public void updateUI() {
        showLoader();

        if (mFavoriteDbController == null) {
            mFavoriteDbController = new FavoriteDbController(mContext);
        }
        mFavoriteList.clear();
        mFavoriteList.addAll(mFavoriteDbController.getAllData());

        mFavoriteAdapter.notifyDataSetChanged();

        hideLoader();

        if (mFavoriteList.size() == 0) {
            showEmptyView();
            if (mMenuItemDeleteAll != null) {
                mMenuItemDeleteAll.setVisible(false);
            }
        } else {
            if (mMenuItemDeleteAll != null) {
                mMenuItemDeleteAll.setVisible(true);
            }
        }
    }

    public void initListener() {
        // recycler list item click listener
        mFavoriteAdapter.setItemClickListener(new ListItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                mAdapterPosition = position;
                final Contents model = new Contents(mFavoriteList.get(position).getTitle(), mFavoriteList.get(position).getSubTitle(), AppConstant.EMPTY_STRING, mFavoriteList.get(position).getImageUrl(), mFavoriteList.get(position).getDetails(), true);

                switch (view.getId()) {
                    case R.id.btn_fav:
                        FragmentManager manager = getSupportFragmentManager();
                        DialogUtilities dialog = DialogUtilities.newInstance(getString(R.string.site_menu_fav), getString(R.string.delete_fav_item), getString(R.string.yes), getString(R.string.no), AppConstant.BUNDLE_KEY_DELETE_EACH_FAV);
                        dialog.show(manager, AppConstant.BUNDLE_KEY_DIALOG_FRAGMENT);
                        break;
                    case R.id.card_view_top:
                        MyApplication.getInstance().setInterstitialListner(new MyApplication.InterstitialListner() {
                            @Override
                            public void onAdClosed() {
                                ActivityUtilities.getInstance().invokeDetailsActiviy(mActivity, DetailsActivity.class, model, false);
                            }

                            @Override
                            public void onAdFailedToLoad() {
                                ActivityUtilities.getInstance().invokeDetailsActiviy(mActivity, DetailsActivity.class, model, false);
                            }
                        });

                        MyApplication.getInstance().showdAdmobInterstitial(FavoriteListActivity.this);

                        break;
                    default:
                        break;
                }
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ActivityUtilities.getInstance().invokeNewActivity(mActivity, MainActivity.class, true);
                return true;
            case R.id.menus_delete_all:
                FragmentManager manager = getSupportFragmentManager();
                DialogUtilities dialog = DialogUtilities.newInstance(getString(R.string.site_menu_fav), getString(R.string.delete_all_fav_item), getString(R.string.yes), getString(R.string.no), AppConstant.BUNDLE_KEY_DELETE_ALL_FAV);
                dialog.show(manager, AppConstant.BUNDLE_KEY_DIALOG_FRAGMENT);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityUtilities.getInstance().invokeNewActivity(mActivity, MainActivity.class, true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_delete_all, menu);
        mMenuItemDeleteAll = menu.findItem(R.id.menus_delete_all);

        updateUI();

        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mFavoriteAdapter != null) {
            updateUI();
        }
    }

    @Override
    public void onComplete(Boolean isOkPressed, String viewIdText) {
        if (isOkPressed) {
            if (viewIdText.equals(AppConstant.BUNDLE_KEY_DELETE_ALL_FAV)) {
                mFavoriteDbController.deleteAllFav();
                updateUI();
            } else if (viewIdText.equals(AppConstant.BUNDLE_KEY_DELETE_EACH_FAV)) {
                mFavoriteDbController.deleteEachFav(mFavoriteList.get(mAdapterPosition).getTitle());
                updateUI();
            }
        }
    }
}
