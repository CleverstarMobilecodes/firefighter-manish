package com.firefighterscalendar.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.firefighterscalendar.ImageListActivity;
import com.firefighterscalendar.R;
import com.firefighterscalendar.bean.AlbumBean;
import com.firefighterscalendar.inapp.InAppPurchaseActivity;

import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {
    private List<AlbumBean> listAlbumBean;
    private Context context;
    private String subscription;

    public AlbumAdapter(Context context, List<AlbumBean> listAlbumBean, String subscription) {
        this.context = context;
        this.listAlbumBean = listAlbumBean;
        this.subscription = subscription;
    }

    @Override
    public AlbumAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_album, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AlbumAdapter.ViewHolder viewHolder, final int position) {
        viewHolder.imgPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPreview(position);
            }
        });

        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPreview(position);
            }
        });

        viewHolder.imgUnlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, InAppPurchaseActivity.class));
            }
        });

        if (subscription.equalsIgnoreCase("no")) {
            viewHolder.imgUnlock.setVisibility(View.VISIBLE);
            viewHolder.imgPreview.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.preview));
        } else {
            viewHolder.imgUnlock.setVisibility(View.INVISIBLE);
            viewHolder.imgPreview.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.view));
        }

        Glide.with(context).load(getItem(position).coverPicture).priority(Priority.HIGH).crossFade().into(viewHolder.imgPhoto);

        viewHolder.txtAlbumName.setText(getItem(position).albumName);
        viewHolder.txtAlbumYear.setText(getItem(position).albumYear);
    }

    private AlbumBean getItem(int position) {
        return listAlbumBean.get(position);
    }

    private void openPreview(int position) {
        Intent intent = new Intent(context, ImageListActivity.class);
        intent.putExtra("id", getItem(position).albumId);
        intent.putExtra("title", getItem(position).albumName);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return listAlbumBean.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgPreview, imgPhoto, imgUnlock;
        private TextView txtAlbumName, txtAlbumYear;
        private View mView;

        public ViewHolder(View view) {
            super(view);

            imgPreview = (ImageView) view.findViewById(R.id.imgPreview);
            imgUnlock = (ImageView) view.findViewById(R.id.imgUnlock);
            imgPhoto = (ImageView) view.findViewById(R.id.imgPhoto);

            txtAlbumName = (TextView) view.findViewById(R.id.txtAlbumName);
            txtAlbumYear = (TextView) view.findViewById(R.id.txtAlbumYear);
            mView = view;
        }
    }
}