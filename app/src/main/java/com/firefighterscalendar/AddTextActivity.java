package com.firefighterscalendar;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.firefighterscalendar.bean.ImageListBean;
import com.firefighterscalendar.calendar.PrintableCalendarActivity;
import com.firefighterscalendar.utils.Utility;

public class AddTextActivity extends BaseActivity implements View.OnClickListener {

    private TextView txtOnImageTop, txtOnImageBottom, textToptext, textBottomtext;
    private EditText editTextTop, editTextBottom;
    private ImageView imageViewNext, imgCover;
    private Toolbar toolbar;
    private LinearLayout layoutRight;
    private RelativeLayout flEditContains;
    private TextView textHeader;
    private boolean isCalendar;
    private ImageListBean imageListBean;
    Bitmap screenBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_text);
        initControls();
    }

    private void initControls() {
        imageListBean = (ImageListBean) getIntent().getSerializableExtra("image");
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

        textHeader = (TextView) findViewById(R.id.textHeader);
        textHeader.setText("Add Text");

        flEditContains = (RelativeLayout) findViewById(R.id.flEditContains);
        flEditContains.setDrawingCacheEnabled(true);

        layoutRight = (LinearLayout) toolbar.findViewById(R.id.layoutRight);
        layoutRight.setOnClickListener(this);

        textToptext = (TextView) findViewById(R.id.textToptext);
        textBottomtext = (TextView) findViewById(R.id.textBottomtext);

        textToptext.setPaintFlags(textToptext.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        textBottomtext.setPaintFlags(textBottomtext.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        imageViewNext = (ImageView) findViewById(R.id.imageViewNext);
        imgCover = (ImageView) findViewById(R.id.imgCover);
        imageViewNext.setOnClickListener(this);

        txtOnImageTop = (TextView) findViewById(R.id.txtOnImageTop);
        txtOnImageBottom = (TextView) findViewById(R.id.txtOnImageBottom);

        editTextTop = (EditText) findViewById(R.id.editTextTop);
        editTextBottom = (EditText) findViewById(R.id.editTextBottom);

        // Top
        editTextTop.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                txtOnImageTop.setText(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // bottom
        editTextBottom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                txtOnImageBottom.setText(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // This condition is added by Manish Dutta as (imageListBean.imageWidth and imageListBean.imageHeight) value is null
        if (imageListBean.imageWidth != null || imageListBean.imageHeight != null) {
            flEditContains.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, utility.getDynamicImageHeight(Integer.parseInt(imageListBean.imageWidth), Integer.parseInt(imageListBean.imageHeight))));
            imgCover.getLayoutParams().height = utility.getDynamicImageHeight(Integer.parseInt(imageListBean.imageWidth), Integer.parseInt(imageListBean.imageHeight));
            imgCover.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
        }

        Glide.with(AddTextActivity.this).load(imageListBean.imageUrl).placeholder(R.drawable.placeholder).priority(Priority.HIGH).into(imgCover);

        flEditContains.requestLayout();
        imgCover.requestLayout();
    }

    @Override
    public void onClick(View v) {
        if (v == imageViewNext) {
            if (isCalendar) {
                Bitmap originalImage = createSavedBitmap();
                startActivity(new Intent(AddTextActivity.this, PrintableCalendarActivity.class).putExtra("path", utility.SaveImage(originalImage)));
            } else {
                Bitmap originalImage = createSavedBitmap();
                Bitmap a4Bitmap = getResizedBitmap(originalImage, 595, 842);
                if(a4Bitmap == null) {
                   android.app.AlertDialog.Builder adb = new android.app.AlertDialog.Builder(AddTextActivity.this);
                    adb.setTitle(AddTextActivity.this.getResources().getString(R.string.app_name));
                    adb.setMessage("Can't load image.");
                    adb.setPositiveButton("OK", new android.app.AlertDialog.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    adb.show();
                } else {
                    startActivity(new Intent(AddTextActivity.this, ShareCardsActivity.class).putExtra("path", utility.SaveImage(originalImage)).putExtra("pathPrint", utility.SaveImage(a4Bitmap)));
                }
            }

        } else if (v == layoutRight) {
            Intent intent = new Intent(AddTextActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }
    }



    private Bitmap createSavedBitmap() {
        try {
            if (screenBitmap != null) {
                screenBitmap.recycle();
                screenBitmap = null;
            }
            screenBitmap = Bitmap.createBitmap(flEditContains.getWidth(), flEditContains.getHeight(), Bitmap.Config.ARGB_8888);

            Log.d("Kunal", "width: " + flEditContains.getWidth());
            Log.d("Kunal", "height: " + flEditContains.getHeight());

            Canvas canvas = new Canvas(screenBitmap);
            flEditContains.draw(canvas);
            return screenBitmap;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            System.gc();
        }
        return null;
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {

        if(bm == null) {
            return null;
        }
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
    }
}
