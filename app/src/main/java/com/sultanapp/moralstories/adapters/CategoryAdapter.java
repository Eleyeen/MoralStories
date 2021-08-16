package com.sultanapp.moralstories.adapters;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sultanapp.moralstories.R;
import com.sultanapp.moralstories.listeners.ListItemClickListener;
import com.sultanapp.moralstories.models.categories.Categories;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private Context mContext;
    private Activity mActivity;

    private ArrayList<Categories> mCategoryList;
    private ListItemClickListener mItemClickListener;

    public CategoryAdapter(Context mContext, Activity mActivity, ArrayList<Categories> mCategoryList) {
        this.mContext = mContext;
        this.mActivity = mActivity;
        this.mCategoryList = mCategoryList;
    }

    public void setItemClickListener(ListItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_list, parent, false);
        return new ViewHolder(view, viewType, mItemClickListener);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private CardView cardView;
        private ImageView imgCategory;
        private TextView tvCategoryTitle, tvStoriesNum;
        private ListItemClickListener itemClickListener;


        public ViewHolder(View itemView, int viewType, ListItemClickListener itemClickListener) {
            super(itemView);

            this.itemClickListener = itemClickListener;
            // Find all views ids
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            imgCategory = (ImageView) itemView.findViewById(R.id.category_img);
            tvCategoryTitle = (TextView) itemView.findViewById(R.id.category_title);
            tvStoriesNum = itemView.findViewById(R.id.tvStoriesNum);
            cardView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(getLayoutPosition(), view);
            }
        }
    }

    @Override
    public int getItemCount() {
        return (null != mCategoryList ? mCategoryList.size() : 0);

    }

    @Override
    public void onBindViewHolder(CategoryAdapter.ViewHolder mainHolder, int position) {
        final Categories model = mCategoryList.get(position);

        // setting data over views
        String imgUrl = model.getCategoryImg();
        if (imgUrl != null && !imgUrl.isEmpty()) {  
            Glide.with(mContext)
                    .load(imgUrl)
                    .into(mainHolder.imgCategory);
        }

        mainHolder.tvCategoryTitle.setText(Html.fromHtml(model.getCategoryName()));
//        mainHolder.tvStoriesNum.setText(Html.fromHtml("stories : 20 " ));
        if (position == mCategoryList.size() - 1) {
            // load more data here.
            mainHolder.tvStoriesNum.setText(Html.fromHtml("stories : 5 " ));

        }else{
            mainHolder.tvStoriesNum.setText(Html.fromHtml("stories : 20 " ));

        }


//        Random rand = new Random();
//        int i = rand.nextInt(6) + 1;
//        switch (i) {
//            case 1:
//                mainHolder.tvCategoryTitle.setBackground(ContextCompat.getDrawable(mContext, R.drawable.rectangle_blue_normal));
//                break;
//            case 2:
//                mainHolder.tvCategoryTitle.setBackground(ContextCompat.getDrawable(mContext, R.drawable.rectangle_deep_blue_normal));
//                break;
//            case 3:
//                mainHolder.tvCategoryTitle.setBackground(ContextCompat.getDrawable(mContext, R.drawable.rectangle_green_normal));
//                break;
//            case 4:
//                mainHolder.tvCategoryTitle.setBackground(ContextCompat.getDrawable(mContext, R.drawable.rectangle_orange_normal));
//                break;
//
//            case 5:
//                mainHolder.tvCategoryTitle.setBackground(ContextCompat.getDrawable(mContext, R.drawable.rectangle_purple_normal));
//                break;
//            case 6:
//                mainHolder.tvCategoryTitle.setBackground(ContextCompat.getDrawable(mContext, R.drawable.rectangle_red_normal));
//                break;
//
//            default:
//                break;
//        }
    }
}