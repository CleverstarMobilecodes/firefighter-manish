package com.firefighterscalendar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HowtoCreateActivity extends BaseActivity implements View.OnClickListener {

    private ImageView imageStartCreating;
    private TextView txtStep2, txtStep3, txtStep4, textHeader;
    private Toolbar toolbar;
    private LinearLayout layoutRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_howto_create);
        initControls();
    }

    private void initControls() {
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
        layoutRight.setOnClickListener(this);

        textHeader = (TextView) findViewById(R.id.textHeader);
        textHeader.setText("How To");

        txtStep2 = (TextView) findViewById(R.id.txtStep2);
        txtStep3 = (TextView) findViewById(R.id.txtStep3);
        txtStep4 = (TextView) findViewById(R.id.txtStep4);

        Spannable wordtoSpan2 = new SpannableString(getResources().getString(R.string.step2));
        wordtoSpan2.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.red)), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        txtStep2.setText(wordtoSpan2);

        Spannable wordtoSpan3 = new SpannableString(getResources().getString(R.string.step3));
        wordtoSpan3.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.red)), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        txtStep3.setText(wordtoSpan3);

        Spannable wordtoSpan4 = new SpannableString(getResources().getString(R.string.step4));
        wordtoSpan4.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.red)), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        txtStep4.setText(wordtoSpan4);

        imageStartCreating = (ImageView) findViewById(R.id.imageStartCreating);
        imageStartCreating.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == imageStartCreating) {
            startActivity(new Intent(this, StartSelectionActivity.class));
            finish();
        } else if (v == layoutRight) {
            Intent intent = new Intent(HowtoCreateActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}
