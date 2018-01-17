package com.firefighterscalendar.album;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firefighterscalendar.BaseActivity;
import com.firefighterscalendar.MainActivity;
import com.firefighterscalendar.R;
import com.firefighterscalendar.adapter.ImageSavedAdapter;
import com.firefighterscalendar.bean.ImageListBean;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SavedImageActivity extends BaseActivity {

    private Toolbar toolbar;
    private LinearLayout layoutRight;
    private RecyclerView recyclerView;
    private ImageSavedAdapter imageSavedAdapter;
    private List<ImageListBean> listImages;
    private TextView textHeader, textNoImages;
    private boolean isCalendar = false;
    private File[] allFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_images);
        initControls();
    }

    private void initControls() {
        listImages = new ArrayList<>();
        isCalendar = getIntent().getBooleanExtra("calendar", false);

        toolbar = (Toolbar) findViewById(R.id.toolbar_inner);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });

        layoutRight = (LinearLayout) toolbar.findViewById(R.id.layoutRight);
        layoutRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SavedImageActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        textHeader = (TextView) findViewById(R.id.textHeader);
        textNoImages = (TextView) findViewById(R.id.textNoImages);
        textHeader.setText(R.string.my_saved_images);

        recyclerView = (RecyclerView) findViewById(R.id.recycleviewImages);
        recyclerView.setHasFixedSize(false);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(layoutManager);

        imageSavedAdapter = new ImageSavedAdapter(this, listImages, "show", isCalendar);
        recyclerView.setAdapter(imageSavedAdapter);

        File folder = new File(Environment.getExternalStorageDirectory().getPath() + "/FireFighter_saved/");
        if (folder.exists()) {
            allFiles = folder.listFiles();
            if (allFiles != null) {
                for (int i = 0; i < allFiles.length; i++) {
                    ImageListBean imageListBean = new ImageListBean();
                    imageListBean.imageUrl = allFiles[i].getAbsolutePath();
                    listImages.add(imageListBean);
                }
            }
        }
        imageSavedAdapter.notifyDataSetChanged();

        if (listImages.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            textNoImages.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            textNoImages.setVisibility(View.GONE);
        }
    }
}