package com.firefighterscalendar.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.firefighterscalendar.AddTextActivity;
import com.firefighterscalendar.FullviewActivity;
import com.firefighterscalendar.R;
import com.firefighterscalendar.activity.FullImageActivity;
import com.firefighterscalendar.bean.ImageListBean;
import com.firefighterscalendar.inapp.InAppPurchaseActivity;

import java.io.Serializable;
import java.util.List;

public class ImageSavedAdapter extends RecyclerView.Adapter<ImageSavedAdapter.ViewHolder> {

    private Context context;
    private List<ImageListBean> listImages;
    private String subscription;
    boolean isCalendar;

    public ImageSavedAdapter(Context context, List<ImageListBean> listImages, String subscription) {
        this.context = context;
        this.listImages = listImages;
        this.subscription = subscription;
    }

    public ImageSavedAdapter(Context context, List<ImageListBean> listImages, String s, boolean isCalendar) {
        this.context = context;
        this.listImages = listImages;
        subscription = s;
        this.isCalendar = isCalendar;
    }

    @Override
    public ImageSavedAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_image, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ImageSavedAdapter.ViewHolder viewHolder, final int position) {

        Glide.with(context).load(listImages.get(position).imageUrl).priority(Priority.HIGH).into(viewHolder.imagePhoto);

        viewHolder.imagePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (subscription.equalsIgnoreCase("show")) {
                    context.startActivity(new Intent(context, FullImageActivity.class).putExtra("imagebean", listImages.get(position)));
                }
                else if (subscription.equalsIgnoreCase("text")) {
                    Intent intent = new Intent(context, AddTextActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("image", listImages.get(position));
                    intent.putExtra("calendar", isCalendar);
                    context.startActivity(intent);
                }
                else {
                    Intent intent = new Intent(context, FullviewActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("listImages", (Serializable) listImages);
                    intent.putExtra("position", position);
                    context.startActivity(intent);
                }
            }
        });

        if (subscription.equalsIgnoreCase("yes")) {
            viewHolder.imgUnlock.setVisibility(View.INVISIBLE);
        }
        else if (subscription.equalsIgnoreCase("no") && position != 0) {
            viewHolder.imgUnlock.setVisibility(View.VISIBLE);
            viewHolder.imgUnlock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, InAppPurchaseActivity.class));
                }
            });
        }
        else {
            viewHolder.imgUnlock.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return listImages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imagePhoto, imgUnlock;

        public ViewHolder(View view) {
            super(view);
            imagePhoto = (ImageView) view.findViewById(R.id.imagePhoto);
            imgUnlock = (ImageView) view.findViewById(R.id.imgUnlock);
        }
    }
}