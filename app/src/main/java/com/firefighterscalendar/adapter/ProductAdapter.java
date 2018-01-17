package com.firefighterscalendar.adapter;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firefighterscalendar.R;
import com.firefighterscalendar.bean.ProductBean;

import java.util.List;

public class ProductAdapter extends BaseAdapter {

    private Context context;
    private List<ProductBean> listProduct;

    public ProductAdapter(Context context, List<ProductBean> listProduct) {
        this.context = context;
        this.listProduct = listProduct;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();

        if (convertView == null) {
            convertView = View.inflate(context, R.layout.row_products, null);
            viewHolder.imgProduct = (ImageView) convertView.findViewById(R.id.imgProduct);
            viewHolder.textProductTitle = (TextView) convertView.findViewById(R.id.textProductTitle);
            viewHolder.textProductTag = (TextView) convertView.findViewById(R.id.textProductTag);
            viewHolder.textPrice = (TextView) convertView.findViewById(R.id.textPrice);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.textPrice.setText(Html.fromHtml("<sup><small>$</small></sup><big>" + String.format("%.2f", Double.parseDouble(getItem(position).dPrice)).substring(0, getItem(position).dPrice.indexOf(".")) + "</big><sup><small>"
                + String.format("%.2f", Double.parseDouble(getItem(position).dPrice)).substring(getItem(position).dPrice.indexOf(".") + 1) + "</small></sup>"));

        Glide.with(context).load(getItem(position).txImage).placeholder(R.drawable.placeholder).centerCrop().into(viewHolder.imgProduct);
        viewHolder.textProductTag.setText(getItem(position).vTagLine);
        viewHolder.textProductTitle.setText(getItem(position).vProductName);


        return convertView;
    }

    @Override
    public int getCount() {
        return listProduct.size();
    }

    @Override
    public ProductBean getItem(int position) {
        return listProduct.get(position);
    }

    class ViewHolder {
        private ImageView imgProduct;
        private TextView textProductTitle, textProductTag, textPrice;
    }
}
