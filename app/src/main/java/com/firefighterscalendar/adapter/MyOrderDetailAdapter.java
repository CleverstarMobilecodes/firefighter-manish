package com.firefighterscalendar.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firefighterscalendar.R;
import com.firefighterscalendar.bean.MyOrderBean;

import java.util.List;

public class MyOrderDetailAdapter extends RecyclerView.Adapter<MyOrderDetailAdapter.ViewHolder> {

    private Context context;
    private List<MyOrderBean> listMyOrder;

    public MyOrderDetailAdapter(Context context, List<MyOrderBean> listMyOrder) {
        this.context = context;
        this.listMyOrder = listMyOrder;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.row_my_order_item, null);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Glide.with(context).load(getItem(position).txImage).centerCrop().into(holder.imgOrderPic);
        holder.textProductName.setText(getItem(position).vProductName);
        holder.textTag.setText(getItem(position).vTagLine);
        holder.textQty.setText("Qty: " + getItem(position).iQty);
        holder.textItemPrice.setText("Price: $" + getItem(position).dTotalPrice);
    }

    private MyOrderBean getItem(int position) {
        return listMyOrder.get(position);
    }

    @Override
    public int getItemCount() {
        return listMyOrder.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgOrderPic;
        private TextView textProductName, textTag, textQty, textItemPrice;

        public ViewHolder(View itemView) {
            super(itemView);
            imgOrderPic = (ImageView) itemView.findViewById(R.id.imgOrderPic);
            textProductName = (TextView) itemView.findViewById(R.id.textProductName);
            textTag = (TextView) itemView.findViewById(R.id.textTag);
            textQty = (TextView) itemView.findViewById(R.id.textQty);
            textItemPrice = (TextView) itemView.findViewById(R.id.textItemPrice);
        }
    }
}
