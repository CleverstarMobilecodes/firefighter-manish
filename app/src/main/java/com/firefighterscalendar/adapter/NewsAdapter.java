package com.firefighterscalendar.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firefighterscalendar.R;
import com.firefighterscalendar.bean.NewsBean;

import java.util.List;

public class NewsAdapter extends BaseAdapter {

    private Context context;
    private List<NewsBean> listNews;

    public NewsAdapter(Context context, List<NewsBean> listNews) {
        this.context = context;
        this.listNews = listNews;
    }

    @Override
    public int getCount() {
        return listNews.size();
    }

    @Override
    public NewsBean getItem(int position) {
        return listNews.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup group) {
        ViewHolder viewHolder = new ViewHolder();
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.row_news, null);
            viewHolder.textTitle = (TextView) convertView.findViewById(R.id.textTitle);
            viewHolder.textDesc = (TextView) convertView.findViewById(R.id.textDesc);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.textTitle.setText(getItem(position).vTitle);
        viewHolder.textDesc.setText(getItem(position).txDescription);
        Glide.with(context).load(getItem(position).NewsImage).placeholder(R.drawable.placeholder).centerCrop().into(viewHolder.imageView);

        return convertView;
    }

    private class ViewHolder {
        private TextView textTitle, textDesc;
        private ImageView imageView;
    }
}
