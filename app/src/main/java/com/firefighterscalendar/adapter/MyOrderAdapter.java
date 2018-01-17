package com.firefighterscalendar.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firefighterscalendar.R;
import com.firefighterscalendar.bean.MyOrderBean;
import com.firefighterscalendar.myorder.MyOrderDetailsActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.ViewHolder> {

    private Context context;
    private List<MyOrderBean> listMyOrder;
    private SimpleDateFormat simpleDateFormat;

    public MyOrderAdapter(Context context, List<MyOrderBean> listMyOrder) {
        this.context = context;
        this.listMyOrder = listMyOrder;
        simpleDateFormat = new SimpleDateFormat("dd MMM yy hh:mm aaa", Locale.getDefault());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.row_my_order, null);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Glide.with(context).load(getItem(position).txImage).centerCrop().into(holder.imgOrderPic);

        holder.textOrderDate.setText(simpleDateFormat.format(new Date(Long.parseLong(getItem(position).iOrderTime) * 1000)));
        holder.textOrderNo.setText("Order No. " + getItem(position).iOrderNumber);
        holder.textPrice.setText("Price: $" + getItem(position).dTotalPrice);
        holder.textStatus.setText("Status: " + getItem(position).eOrderStatus);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, MyOrderDetailsActivity.class).putExtra("order", getItem(position)));
            }
        });
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
        private TextView textOrderNo, textPrice, textStatus, textOrderDate;
        private View view;

        public ViewHolder(View itemView) {
            super(itemView);
            imgOrderPic = (ImageView) itemView.findViewById(R.id.imgOrderPic);
            textOrderNo = (TextView) itemView.findViewById(R.id.textOrderNo);
            textPrice = (TextView) itemView.findViewById(R.id.textPrice);
            textStatus = (TextView) itemView.findViewById(R.id.textStatus);
            textOrderDate = (TextView) itemView.findViewById(R.id.textOrderDate);
            view = itemView;
        }
    }
}
