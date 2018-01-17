package com.firefighterscalendar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.firefighterscalendar.utils.Utility;
import com.firefighterscalendar.utils.onConfirm;

import java.io.IOException;

public class ShareCardsActivity extends BaseActivity implements View.OnClickListener {

    private ImageView imgGenerate, imgFbShare, imgInsta, imgMail, imgChat;
    private Toolbar toolbar;
    private FrameLayout flGenerateImg;
    private LinearLayout layoutRight;
    private CallbackManager callbackManager;
    private ShareDialog shareDialog;
    private TextView textHeader;
    private String filepath;
    private String pathPrint;
    private int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);

        setContentView(R.layout.activity_share_card);
        initControl();
    }

    private void initControl() {

        Log.d("Kunal", "Path: " + getIntent().getStringExtra("path"));
        filepath = getIntent().getStringExtra("path");
        pathPrint = getIntent().getStringExtra("pathPrint");
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
                Intent intent = new Intent(ShareCardsActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        textHeader = (TextView) findViewById(R.id.textHeader);
        textHeader.setText("Greeting Card");

        flGenerateImg = (FrameLayout) findViewById(R.id.flGenerateImg);

        imgGenerate = (ImageView) findViewById(R.id.imgGenerate);
        imgFbShare = (ImageView) findViewById(R.id.imgFbShare);
        imgInsta = (ImageView) findViewById(R.id.imgInsta);
        imgMail = (ImageView) findViewById(R.id.imgMail);
        imgChat = (ImageView) findViewById(R.id.imgChat);

        imgFbShare.setOnClickListener(this);
        imgInsta.setOnClickListener(this);
        imgMail.setOnClickListener(this);
        imgChat.setOnClickListener(this);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(getIntent().getStringExtra("path"), options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        Log.i("size ", "" + imageHeight + "img width" + imageWidth);
        String file1 = "file://" + filepath;
        String file2 = "file://" + pathPrint;
        Glide.with(this).load(file1).into(imgGenerate);
        Log.d("Kunal", file1);
        Log.d("Kunal", file2);
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
                    utility.SaveImagefrom(utility.setImageToImageView(filepath));
                    Intent sendIntent = new Intent(Intent.ACTION_SEND);
//                    sendIntent.setClassName("com.android.mms", "com.android.mms.ui.ComposeMessageActivity");
                    sendIntent.setPackage("com.android.mms");
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
                setSingleTimeShare(0);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (flag == 1)
            redirectToHome();
    }

    private void redirectToHome() {
        utility.showYesNoDialog("OK", "", "You have successfully shared this file", new onConfirm() {
            @Override
            public void onChooseYes(boolean isTrue) {
                Intent intent = new Intent(ShareCardsActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    private void createInstagramIntent() {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image");
        share.putExtra(Intent.EXTRA_STREAM, Utility.getFileURI(pathPrint));
        share.setPackage("com.instagram.android");
        utility.SaveImagefrom(utility.setImageToImageView(filepath));
        if (utility.verificaInstagram()) {
            flag = 1;
            startActivityForResult(share, 2);
        }
        else
            utility.downloadAPP("Instagram app is not installed in your device." + "Would you like to install Instagram app ?", "com.instagram.android");
    }

    private void facebookSharing() {
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Log.i("fbshareresponse", result.toString());
                flag = 1;
                utility.SaveImagefrom(utility.setImageToImageView(filepath));
            }

            @Override
            public void onCancel() {
                Log.i("fbshareresponse", "cancel execute");
            }

            @Override
            public void onError(FacebookException error) {
                error.printStackTrace();
                utility.downloadAPP("Facebook app is not installed in your device." +
                        "Would you like to install Facebook app ?", "com.facebook.katana");
            }
        });

        if (ShareDialog.canShow(ShareLinkContent.class)) {
            try {
                Bitmap resource = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Utility.getFileURI(pathPrint));
                SharePhoto photo = new SharePhoto.Builder().setBitmap(resource)
                        .build();
                SharePhotoContent content = new SharePhotoContent.Builder()
                        .addPhoto(photo)
                        .build();
                Log.d("Kunal", "bitmap: " + resource.toString());
                shareDialog.show(content);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMailFromGmail() {

        Intent mailIntent = new Intent(Intent.ACTION_SEND);
        mailIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
        mailIntent.setAction(Intent.ACTION_SEND);
            mailIntent.putExtra(Intent.EXTRA_SUBJECT, "Australian Firefighters Card");
        mailIntent.putExtra(Intent.EXTRA_STREAM, Utility.getFileURI(pathPrint));
        try {
            utility.SaveImagefrom(utility.setImageToImageView(filepath));
            flag = 1;
            startActivity(mailIntent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(ShareCardsActivity.this, "Your device not support this feature", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != 2)
            callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
