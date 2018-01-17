package com.firefighterscalendar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.firefighterscalendar.activity.AboutCharityActivity;
import com.firefighterscalendar.activity.AboutUsActivity;
import com.firefighterscalendar.activity.NewsActivity;
import com.firefighterscalendar.activity.NotificationActivity;
import com.firefighterscalendar.activity.PrivacyPolicyActivity;
import com.firefighterscalendar.album.MonthlyPicActivity;
import com.firefighterscalendar.album.StartCreateActivity;
import com.firefighterscalendar.calendar.DigitalCalendarActivity;
import com.firefighterscalendar.inapp.InAppPurchaseActivity;
import com.firefighterscalendar.myorder.CheckOutOrderActivity;
import com.firefighterscalendar.myorder.MyOrderActivity;
import com.firefighterscalendar.product.ShopListActivity;
import com.firefighterscalendar.utils.onTaskComplete;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static final int RESULT_CODE_INAPP = 1210;
    private DrawerLayout drawer;
    private LinearLayout layoutMore, layoutCalendar, layoutGreetingCard, layoutShop,
            layoutFireFighter, layoutRight, layoutDigitalCal;
    private Toolbar toolbar;
    private TextView textUserName, textHeader, textAboutUs, textNotification, textAboutCharity,
            textMyOrder, textCart, textTerms, textPurchase, textRestore, textSubscribe;
    private ImageView imgPic, imgPlaceHolder;
    private boolean doubleBackToExitPressedOnce = false;
    private boolean subscribeClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String str = printKeyHash(MainActivity.this);
        Log.d("Key Hash= ", str);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        initControls();
    }

    private void initControls() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.app_name, R.string.app_name);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
            }
        });

        drawer.addDrawerListener(drawerListener);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        layoutRight = (LinearLayout) toolbar.findViewById(R.id.layoutRight);
        layoutRight.setOnClickListener(this);

        textHeader = (TextView) findViewById(R.id.textHeader);
        textAboutUs = (TextView) findViewById(R.id.textAboutUs);
        textNotification = (TextView) findViewById(R.id.textNotification);
        textAboutCharity = (TextView) findViewById(R.id.textAboutCharity);
        textCart = (TextView) findViewById(R.id.textCart);
        textTerms = (TextView) findViewById(R.id.textTerms);
        textPurchase = (TextView) findViewById(R.id.textPurchase);
        textRestore = (TextView) findViewById(R.id.textRestore);
        textSubscribe = (TextView) findViewById(R.id.textSubscribe);

        textAboutUs.setOnClickListener(this);
        textAboutCharity.setOnClickListener(this);
        textNotification.setOnClickListener(this);
        textCart.setOnClickListener(this);
        textTerms.setOnClickListener(this);
        textPurchase.setOnClickListener(this);
        textRestore.setOnClickListener(this);
        textSubscribe.setOnClickListener(this);

        textHeader.setText(HEADER_USER_NAME);

        layoutDigitalCal = (LinearLayout) findViewById(R.id.layoutDigitalCal);
        layoutFireFighter = (LinearLayout) findViewById(R.id.layoutFireFighter);
        layoutShop = (LinearLayout) findViewById(R.id.layoutShop);
        layoutGreetingCard = (LinearLayout) findViewById(R.id.layoutGreetingCard);
        layoutCalendar = (LinearLayout) findViewById(R.id.layoutCalendar);
        layoutMore = (LinearLayout) findViewById(R.id.layoutMore);

        layoutGreetingCard.setOnClickListener(this);
        layoutCalendar.setOnClickListener(this);
        layoutFireFighter.setOnClickListener(this);
        layoutShop.setOnClickListener(this);
        layoutDigitalCal.setOnClickListener(this);
        layoutMore.setOnClickListener(this);

        textUserName = (TextView) findViewById(R.id.textUserName);
        textMyOrder = (TextView) findViewById(R.id.textMyOrder);

        textUserName.setText(sessionManager.getStringDetail("vFirstName"));

        textMyOrder.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (utility.checkInternetConnection()) {
            checkUserSubscriptionAPICall(new onTaskComplete() {
                @Override
                public void onComplete(String response) {
                    if (sessionManager.getStringDetail("subscription").equalsIgnoreCase("yes") &&
                            sessionManager.getStringDetail("freeSubscription").equalsIgnoreCase("no")) {

                        layoutRight.setVisibility(View.INVISIBLE);
                        textSubscribe.setText("SUBSCRIBED");
                        textSubscribe.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.sub_green));
                        textSubscribe.setEnabled(false);
                    } else {
                        layoutRight.setVisibility(View.VISIBLE);
                        textSubscribe.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.red));
                        textSubscribe.setText("SUBSCRIBE");
                        textSubscribe.setEnabled(true);
                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    private void setImage(String uri) {
        imgPic = (ImageView) findViewById(R.id.imgPic);
        imgPlaceHolder = (ImageView) findViewById(R.id.imgPlaceHolder);

        Glide.with(this).load(uri).asBitmap().centerCrop().into(new BitmapImageViewTarget(imgPic) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                imgPic.setImageDrawable(circularBitmapDrawable);
            }
        });

        Glide.with(this).load(R.drawable.white_bg).asBitmap().centerCrop().into(new BitmapImageViewTarget(imgPlaceHolder) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                imgPlaceHolder.setImageDrawable(circularBitmapDrawable);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == layoutDigitalCal) {
            startActivity(new Intent(this, DigitalCalendarActivity.class));
            /*if (sessionManager.getStringDetail("subscription").equalsIgnoreCase("yes")) {
                startActivity(new Intent(this, DigitalCalendarActivity.class));
            } else {
                utility.showYesNoDialog("Unlock", "Cancel", "The digital calendar feature of the Firefighters Calendar app is available for joined supporters only. CLICK HERE for unlimited access!", new onConfirm() {
                    @Override
                    public void onChooseYes(boolean isTrue) {
                        startActivity(new Intent(MainActivity.this, InAppPurchaseActivity.class));
                    }
                });
            }*/

        } else if (v == layoutShop) {
            startActivity(new Intent(this, ShopListActivity.class));

        } else if (v == layoutGreetingCard) {
            Intent intent = new Intent(MainActivity.this, StartCreateActivity.class);
            startActivity(intent);

        } else if (v == layoutCalendar) {
            Intent intent = new Intent(MainActivity.this, NewsActivity.class);
            startActivity(intent);

        } else if (v == layoutFireFighter) {
            Intent intent = new Intent(MainActivity.this, MonthlyPicActivity.class);
            startActivity(intent);

        } else if (v == textMyOrder) {
            startActivity(new Intent(this, MyOrderActivity.class));
        } else if (v == textCart) {
            startActivity(new Intent(this, CheckOutOrderActivity.class));
        } else if (v == layoutRight) {
            if (drawer.isDrawerOpen(GravityCompat.END))
                drawer.closeDrawer(GravityCompat.END);
            else
                drawer.openDrawer(GravityCompat.END);
        } else if (v == textAboutUs) {
            startActivity(new Intent(this, AboutUsActivity.class).putExtra("pageid", "1"));
        } else if (v == textTerms) {
            startActivity(new Intent(this, PrivacyPolicyActivity.class).putExtra("pageid", "3"));
        } else if (v == textPurchase || v == textSubscribe || v == textRestore) {
            if (!subscribeClicked) {
                subscribeClicked = true;
                startActivityForResult(new Intent(MainActivity.this, InAppPurchaseActivity.class), RESULT_CODE_INAPP);
            }
        } else if (v == layoutMore) {
            drawer.openDrawer(GravityCompat.START);
        } else if (v == textAboutCharity) {
            startActivity(new Intent(this, AboutCharityActivity.class).putExtra("pageid", "2"));
        } else if (v == textNotification) {
            startActivity(new Intent(this, NotificationActivity.class));
        }

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        }
    }

    DrawerLayout.DrawerListener drawerListener = new DrawerLayout.DrawerListener() {
        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {
        }

        @Override
        public void onDrawerOpened(View drawerView) {
            textUserName.setText(sessionManager.getStringDetail("vFirstName"));
            setImage(sessionManager.getStringDetail("txProfilePic"));
        }

        @Override
        public void onDrawerClosed(View drawerView) {

        }

        @Override
        public void onDrawerStateChanged(int newState) {

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_CODE_INAPP) {
            subscribeClicked = false;
        }
    }

}
