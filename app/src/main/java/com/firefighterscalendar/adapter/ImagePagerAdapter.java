package com.firefighterscalendar.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.firefighterscalendar.R;
import com.firefighterscalendar.bean.ImageListBean;

import java.util.List;


public class ImagePagerAdapter extends RecyclingPagerAdapter {

    private Context mContext;
    private List<ImageListBean> imageListBean;
    private boolean isSlide;
    private boolean isInfiniteLoop;
    private ImageView imageView;

    public ImagePagerAdapter(Context context, List<ImageListBean> imageListBean, boolean isSlide) {
        mContext = context;
        this.imageListBean = imageListBean;
        this.isSlide = isSlide;
        isInfiniteLoop = true;
    }

    @Override
    public int getCount() {
        if (isSlide)
            return isInfiniteLoop ? Integer.MAX_VALUE : imageListBean.size();
        else
            return 1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup container) {

//        if (convertView == null) {
//            holder = new ViewHolder();
//            convertView = holder.imageView = new ImageView(mContext);
//            convertView.setTag(holder);
//        } else {
//            holder = (ViewHolder) convertView.getTag();
//        }
//        Glide.with(mContext).load(imageListBean.get(getPosition(position)).imageUrl).centerCrop().placeholder(R.drawable.placeholder).priority(Priority.HIGH).into(holder.imageView);


        imageView = new ImageView(mContext);

        Glide.with(mContext).load(imageListBean.get(getPosition(position)).imageUrl).centerCrop().placeholder(R.drawable.placeholder).priority(Priority.HIGH).into(imageView);

        ViewGroup.LayoutParams imageParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(imageParams);

        LinearLayout layout = new LinearLayout(mContext);
        layout.setOrientation(LinearLayout.VERTICAL);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        layout.setLayoutParams(layoutParams);
        getItemPosition(layout);

        if (imageView.getParent() != null) {
            ((ViewGroup) imageView.getParent()).removeView(imageView);
        }

        layout.addView(imageView);
        return layout;
    }

    private int getPosition(int position) {
        return isInfiniteLoop ? position % imageListBean.size() : position;
    }

   /* @Override
    public Object instantiateItem(ViewGroup container, int position) {
        imageView = new ImageView(mContext);

        Glide.with(mContext).load(imageListBean.get(position).imageUrl).centerCrop().placeholder(R.drawable.placeholder).priority(Priority.HIGH).into(imageView);

        ViewGroup.LayoutParams imageParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(imageParams);

        LinearLayout layout = new LinearLayout(mContext);
        layout.setOrientation(LinearLayout.VERTICAL);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        layout.setLayoutParams(layoutParams);
        getItemPosition(layout);
        layout.addView(imageView);

        container.addView(layout);
        return layout;
    }*/
}
