package com.sultanapp.moralstories.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sultanapp.moralstories.R;
import com.sultanapp.moralstories.adapters.ContentAdapter;
import com.sultanapp.moralstories.app.MyApplication;
import com.sultanapp.moralstories.data.constant.AppConstant;
import com.sultanapp.moralstories.data.sqlite.FavoriteDbController;
import com.sultanapp.moralstories.helper.ApppreferenceManager;
import com.sultanapp.moralstories.listeners.ListItemClickListener;
import com.sultanapp.moralstories.models.content.Contents;
import com.sultanapp.moralstories.models.favorite.FavoriteModel;
import com.sultanapp.moralstories.utility.ActivityUtilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends BaseActivity {

    private Activity mActivity;
    private Context mContext;

    private ArrayList<Contents> mContentList;
    private ArrayList<Contents> mSearchList;
    private ContentAdapter mAdapter = null;
    private RecyclerView mRecycler;
    ApppreferenceManager apppreferenceManager;

    // Favourites view
    private List<FavoriteModel> mFavoriteList;
    private FavoriteDbController mFavoriteDbController;

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
        mActivity = SearchActivity.this;
        mContext = mActivity.getApplicationContext();

        mContentList = new ArrayList<>();
        mFavoriteList = new ArrayList<>();
        mSearchList = new ArrayList<>();
    }

    private void initView() {
        setContentView(R.layout.activity_item_list);

        mRecycler = (RecyclerView) findViewById(R.id.rvContent);
        mRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new ContentAdapter(mContext, mActivity, mSearchList);
        mRecycler.setAdapter(mAdapter);

        initLoader();
        initToolbar(true);
        enableUpButton();
        setToolbarTitle(getString(R.string.search));
    }

    private void initFunctionality() {
        loadData();

        // show full-screen ads
//        AdsUtilities.getInstance(mContext).showFullScreenAd();
    }

    public void initListener() {
        // recycler list item click listener
        mAdapter.setItemClickListener(new ListItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                final Contents model = mSearchList.get(position);

                switch (view.getId()) {
                    case R.id.btn_fav:
                        if (model.isFavorite()) {
                            mFavoriteDbController.deleteEachFav(mContentList.get(position).getTitle());
                            mContentList.get(position).setFavorite(false);
                            mAdapter.notifyDataSetChanged();
                            Toast.makeText(getApplicationContext(), getString(R.string.removed_from_fav), Toast.LENGTH_SHORT).show();

                        } else {
                            mFavoriteDbController.insertData(model.getTitle(), model.getSubTitle(), model.getDetails(), model.getImageUrl());
                            mContentList.get(position).setFavorite(true);
                            mAdapter.notifyDataSetChanged();
                            Toast.makeText(getApplicationContext(), getString(R.string.added_to_fav), Toast.LENGTH_SHORT).show();
                        }
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

                        MyApplication.getInstance().showdAdmobInterstitial(SearchActivity.this);
                        break;
                    default:
                        break;
                }
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setQueryHint(getString(R.string.search));
        searchView.postDelayed(new Runnable() {
            @Override
            public void run() {
                searchView.setIconifiedByDefault(true);
                searchView.setFocusable(true);
                searchView.setIconified(false);
                searchView.requestFocusFromTouch();
            }
        }, 200);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //some texts here
                showLoader();
                mSearchList.clear();

                for (int i = 0; i < mContentList.size(); i++) {
                    if (mContentList.get(i).getDetails().toLowerCase().contains(newText)) {
                        mSearchList.add(mContentList.get(i));
                    }
                }

                mAdapter.notifyDataSetChanged();

                if (!mSearchList.isEmpty() && mSearchList.size() > 0) {
                    hideLoader();
                } else {
                    showEmptyView();
                }

                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadData() {
        showLoader();

        // Initialize Favorite Database
        mFavoriteDbController = new FavoriteDbController(mContext);
        mFavoriteList.addAll(mFavoriteDbController.getAllData());

        loadJson();

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.adsView);
        MyApplication.getInstance().showBannerADs(SearchActivity.this, linearLayout);
    }

    private void loadJson() {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(getAssets().open(AppConstant.CONTENT_FILE)));
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
        parseJson(sb.toString());
    }

    private void parseJson(String jsonData) {
        try {

            JSONObject jsonObjMain = new JSONObject(jsonData);
            JSONArray jsonArray1 = jsonObjMain.getJSONArray(AppConstant.JSON_KEY_ITEMS);

            for (int i = 0; i < jsonArray1.length(); i++) {
                JSONObject jsonObj = jsonArray1.getJSONObject(i);

                String title = jsonObj.getString(AppConstant.JSON_KEY_TITLE);
                String subTitle = jsonObj.getString(AppConstant.JSON_KEY_SUB_TITLE);
                String category = jsonObj.getString(AppConstant.JSON_KEY_CATEGORY);
                String imageUrl = jsonObj.getString(AppConstant.JSON_KEY_IMAGE_URL);
                String details = jsonObj.getString(AppConstant.JSON_KEY_DETAILS);

                // Check for favorite
                boolean isFavorite = false;
                for (int j = 0; j < mFavoriteList.size(); j++) {
                    if (mFavoriteList.get(j).getTitle().equals(title)) {
                        isFavorite = true;
                        break;
                    }
                }

                mContentList.add(new Contents(title, subTitle, category, imageUrl, details, isFavorite));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        hideLoader();
        mAdapter.notifyDataSetChanged();
    }

}
