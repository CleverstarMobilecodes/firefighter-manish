package com.firefighterscalendar.calendar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.firefighterscalendar.BaseActivity;
import com.firefighterscalendar.MainActivity;
import com.firefighterscalendar.R;
import com.firefighterscalendar.utils.Utility;
import com.firefighterscalendar.utils.onConfirm;
import com.marcohc.robotocalendar.RobotoCalendarView;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class PrintCalendarTextActivity extends BaseActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private LinearLayout layoutRight;
    private TextView textHeader;
    String path = "";
    Calendar calendar;
    private RobotoCalendarView robotoCalendarView;
    private ImageView imgCalendarImage, leftButton, rightButton;
    private LinearLayout llShare;
    private ImageView imgGenerate, imgFbShare, imgInsta, imgMail, imgChat;
    private CallbackManager callbackManager;
    private ShareDialog shareDialog;
    private FrameLayout flCalendar;
    private Bitmap screenBitmap;
    private String pathPrint = "";
    private int isShare = 0;
    int width, height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);
        setContentView(R.layout.activity_printable_calendar_text);
        initControl();
    }

    private void initControl() {
        path = getIntent().getStringExtra("path");

        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(getIntent().getLongExtra("calendar", 0));

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
                Intent intent = new Intent(PrintCalendarTextActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        flCalendar = (FrameLayout) findViewById(R.id.flCalendar);

        textHeader = (TextView) findViewById(R.id.textHeader);
        textHeader.setText("Calendar");
        findViewById(R.id.textSelectDate).setVisibility(View.GONE);

        robotoCalendarView = (RobotoCalendarView) findViewById(R.id.robotoCalendarView);
        robotoCalendarView.setVisibility(View.VISIBLE);

        imgCalendarImage = (ImageView) findViewById(R.id.imgCalendarImage);
        leftButton = (ImageView) robotoCalendarView.findViewById(R.id.leftButton);
        rightButton = (ImageView) robotoCalendarView.findViewById(R.id.rightButton);

        leftButton.setVisibility(View.GONE);
        rightButton.setVisibility(View.GONE);

        findViewById(R.id.textNext).setVisibility(View.GONE);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(getIntent().getStringExtra("path"), options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        Log.i("size ", "" + imageHeight + "img width" + imageWidth);

        flCalendar.setLayoutParams(new LinearLayout.LayoutParams(imageWidth, ViewGroup.LayoutParams.MATCH_PARENT));
        flCalendar.setDrawingCacheEnabled(true);
        Glide.with(this).load(new File(path)).listener(new RequestListener<File, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, File model, Target<GlideDrawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, File model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                return false;
            }
        }).into(imgCalendarImage);

        robotoCalendarView.initializeCalendar(calendar);

        llShare = (LinearLayout) findViewById(R.id.llShare);
        llShare.setVisibility(View.VISIBLE);

        imgGenerate = (ImageView) findViewById(R.id.imgGenerate);
        imgFbShare = (ImageView) findViewById(R.id.imgFbShare);
        imgInsta = (ImageView) findViewById(R.id.imgInsta);
        imgMail = (ImageView) findViewById(R.id.imgMail);
        imgChat = (ImageView) findViewById(R.id.imgChat);

        imgFbShare.setOnClickListener(this);
        imgInsta.setOnClickListener(this);
        imgMail.setOnClickListener(this);
        imgChat.setOnClickListener(this);

        robotoCalendarView.setRobotoCalendarListener(new RobotoCalendarView.RobotoCalendarListener() {
            @Override
            public void onDateSelected(Date date) {

            }

            @Override
            public void onRightButtonClick() {

            }

            @Override
            public void onLeftButtonClick() {

            }
        });

        ViewTreeObserver vto = flCalendar.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                flCalendar.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                width = flCalendar.getMeasuredWidth();
                height = flCalendar.getMeasuredHeight();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == imgFbShare) {
            facebookSharing();
        }
        else if (v == imgInsta) {
            createInstagramIntent();
        }
        else if (v == imgMail) {
            sendMailFromGmail();
        }
        else if (v == imgChat) {

            // Change done by Manish Dutta
            ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null) { // connected to the internet
                if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                    if (pathPrint.length() == 0) {
                        Bitmap originalImage = createSavedBitmap();
                        Bitmap a4Bitmap = utility.getResizedBitmap(originalImage, 595, 842);
                        pathPrint = utility.SaveImagefrom(a4Bitmap);
                    }
                    Intent sendIntent = new Intent(Intent.ACTION_SEND);
                    sendIntent.setPackage("com.android.mms");
//                    sendIntent.setClassName("com.android.mms", "com.android.mms.ui.ComposeMessageActivity");
                    sendIntent.setType("text/x-vcard");
                    sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(pathPrint));
                    startActivity(sendIntent);
                }
                else {
                    utility.showOkDialog(getString(R.string.change_network_type));
                }
            }
            else {
                Toast.makeText(this, getResources().getString(R.string.msg_internet_connection), Toast.LENGTH_LONG).show();
            }
        }

        if (sessionManager.getStringDetail("subscription").equalsIgnoreCase("no")) {
            if (utility.checkInternetConnection()) {
                setSingleTimeShare(1);
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isShare == 1)
            redirectToHome();
    }

    private void redirectToHome() {
        utility.showYesNoDialog("OK", "", "You have successfully shared this file", new onConfirm() {
            @Override
            public void onChooseYes(boolean isTrue) {
                Intent intent = new Intent(PrintCalendarTextActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    private Bitmap createSavedBitmap() {
        try {
            if (screenBitmap != null) {
                screenBitmap.recycle();
                screenBitmap = null;
            }
            Log.d("Kunal", "width: " + flCalendar.getWidth());
            Log.d("Kunal", "height: " + flCalendar.getHeight());
            screenBitmap = Bitmap.createBitmap(flCalendar.getWidth(), flCalendar.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(screenBitmap);
            flCalendar.draw(canvas);
            return screenBitmap;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            System.gc();
        }
        return null;
    }


    private void createInstagramIntent() {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");
        share.setPackage("com.instagram.android");

        if (pathPrint.length() == 0) {
            Bitmap originalImage = createSavedBitmap();
            Bitmap a4Bitmap = utility.getResizedBitmap(originalImage, 595, 842);
            pathPrint = utility.SaveImagefrom(a4Bitmap);
        }

        share.putExtra(Intent.EXTRA_STREAM, Utility.getFileURI(pathPrint));

        if (utility.verificaInstagram()) {
            isShare = 1;
            startActivity(share);
        }
        else
            utility.downloadAPP("Instagram app is not installed in your device." +
                    "Would you like to install Instagram app ?", "com.instagram.android");
    }

    private void facebookSharing() {
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Log.i("share response", result.toString());
                isShare = 1;
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
                error.printStackTrace();
                utility.downloadAPP("Facebook app is not installed in your device." +
                        "Would you like to install Facebook app ?", "com.facebook.katana");
            }
        });

        if (ShareDialog.canShow(ShareLinkContent.class)) {

            if (pathPrint.length() == 0) {
                Bitmap originalImage = createSavedBitmap();
                Bitmap a4Bitmap = utility.getResizedBitmap(originalImage, 595, 842);
                pathPrint = utility.SaveImagefrom(a4Bitmap);
            }
            try {
                Bitmap resource = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Utility.getFileURI(pathPrint));
                SharePhoto photo = new SharePhoto.Builder().setBitmap(resource).build();
                SharePhotoContent content = new SharePhotoContent.Builder()
                        .addPhoto(photo)
                        .build();
//                Log.d("Kunal", "bitmap: " + resource.toString());
                shareDialog.show(content);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMailFromGmail() {

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");

        if (pathPrint.length() == 0) {
            Bitmap originalImage = createSavedBitmap();
            Bitmap a4Bitmap = utility.getResizedBitmap(originalImage, 595, 842);
            pathPrint = utility.SaveImagefrom(a4Bitmap);
        }
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Australian Firefighters Calendar");
        emailIntent.putExtra(Intent.EXTRA_STREAM, Utility.getFileURI(pathPrint));
        try {
            isShare = 1;
            startActivity(emailIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
