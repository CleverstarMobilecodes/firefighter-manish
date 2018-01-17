package com.firefighterscalendar.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.firefighterscalendar.MainActivity;
import com.firefighterscalendar.R;
import com.firefighterscalendar.calendar.DigitalCalendarActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

public class Utility {

    private Context context;
    private ProgressDialog dialog;

    public Utility(Context context) {
        this.context = context;
    }

    public boolean checkInternetConnection() {
        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable() && conMgr.getActiveNetworkInfo().isConnected())
            return true;
        else {
            Toast.makeText(context, context.getResources().getString(R.string.msg_internet_connection), Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public String getTimeStamp() {
        return (DateFormat.format("ddMMyyyy_hhmmss", new java.util.Date()).toString());
    }

    public String getIMEI() {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        if (telephonyManager.getDeviceId() == null) {
            return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        else
            return telephonyManager.getDeviceId();
    }

    public void showProgress() {
        dialog = new ProgressDialog(context);
        dialog.setIndeterminate(true);
        dialog.setInverseBackgroundForced(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setTitle(R.string.app_name);
        dialog.setMessage("Please wait...");
        if(dialog != null && !dialog.isShowing())
            dialog.show();
    }

    public void hideProgress() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public boolean isEmptyText(EditText editText) {
        if (!TextUtils.isEmpty(editText.getText().toString().trim()))
            return true;
        else {
            editText.requestFocus();
            return false;
        }
    }

    public boolean isValidPassword(EditText editText) {
        if (editText.getText().length() < 6) {
            editText.requestFocus();
            editText.setError("Password must be min 6 characters");
            return false;
        }
        else if (TextUtils.isEmpty(editText.getText().toString())) {
            editText.setError("Please enter password");
            return false;
        }
        else
            return true;
    }

    public boolean isValidEmail(EditText edtEmail) {
        if (!isEmptyText(edtEmail)) {
            edtEmail.setError("Please enter valid email address");
            edtEmail.requestFocus();
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(edtEmail.getText().toString()).matches()) {
            edtEmail.requestFocus();
        }
        else {
            return true;
        }
        return false;
    }

    public boolean isValidPassword(EditText edtCurrentPassword, EditText edtRtypePassword) {
        String mCurrentPass = edtCurrentPassword.getText().toString().trim();
        String mRetpyePass = edtRtypePassword.getText().toString().trim();
        return mCurrentPass.equals(mRetpyePass);
    }

    public void showOkDialog(String message) {
        AlertDialog.Builder adb = new AlertDialog.Builder(context);
        adb.setTitle(context.getResources().getString(R.string.app_name));
        adb.setMessage(message);
        adb.setPositiveButton("OK", new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        adb.show();
    }

    public void hideSoftKeyboard(Activity activity) {
        if (activity.getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void showYesNoDialog(String btnYes, String btnNo, String message, final onConfirm onConfirm) {
        AlertDialog.Builder adb = new AlertDialog.Builder(context);
        adb.setTitle(context.getResources().getString(R.string.app_name));
        adb.setMessage(message);
        adb.setCancelable(false);
        adb.setPositiveButton(btnYes, new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onConfirm.onChooseYes(true);
                dialog.dismiss();
            }
        });
        adb.setNegativeButton(btnNo, new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        adb.show();
    }

    public boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(context);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    public File resize(String path) {
        try {
            File file = new File(path);
            file.getParentFile().createNewFile();
            Bitmap bitmapOriginal = rotateImages(path);
            OutputStream outStream = new FileOutputStream(file);
            bitmapOriginal.compress(Bitmap.CompressFormat.JPEG, 60, outStream);
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void downloadAPP(String msg, final String packagename) {
        showYesNoDialog("Accept", "Decline", msg, new onConfirm() {
            @Override
            public void onChooseYes(boolean isTrue) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packagename)));
            }
        });
    }

    public Bitmap rotateImages(String imagePath) {
        ExifInterface ei;
        Bitmap bitmap;
        if (imagePath == null) {
            Toast.makeText(context, "Unable to fetch image.Please try another image.", Toast.LENGTH_LONG).show();
        }
        try {
            ei = new ExifInterface(imagePath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            bitmap = setImageToImageView(imagePath);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return RotateBitmap(bitmap, 90);
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return RotateBitmap(bitmap, 180);
                default:
                    return bitmap;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public Bitmap setImageToImageView(String filePath) {
        // Decode image size
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 1024;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = options.outWidth, height_tmp = options.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }
        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeFile(filePath, o2);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void sendNotification(int code, String msg, boolean isLocal) {

        NotificationManager notificationManager;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentTitle(context.getString(R.string.app_name));
        builder.setContentText(msg);
        Intent resultIntent;

        if (isLocal) {
            resultIntent = new Intent(context, DigitalCalendarActivity.class);
            resultIntent.putExtra("frompush", true);
        }
        else
            resultIntent = new Intent(context, MainActivity.class);

        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pending = PendingIntent.getActivity(context.getApplicationContext(), 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setColor(ContextCompat.getColor(context, R.color.colorPrimary));
        }
        else
            builder.setSmallIcon(R.mipmap.ic_launcher);

        builder.setAutoCancel(true);
        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        builder.build().flags |= Notification.FLAG_AUTO_CANCEL;
        builder.setAutoCancel(true);
        builder.setStyle(new Notification.BigTextStyle().bigText(msg));
        builder.setContentIntent(pending);
        Notification notification = builder.build();
        notificationManager.notify(code, notification);
    }

    public String getStorageDirectory(String path) {
        String rootDir = "";
        if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED) && !Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            rootDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        else {
            rootDir = context.getFilesDir().getAbsolutePath();
        }
        return rootDir + path;
    }


    public String SaveImage(Bitmap finalBitmap) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/FireFighter");

        myDir.mkdirs();
        Random random = new Random();
        String fname = "ImageText" + random.nextInt() + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }

    public int getScreenWidth() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics.widthPixels;
    }

    public int getDynamicImageHeight(int imgWidth, int imgHeight) {
        int height;
        Log.i("width", " imgWidth:" + imgWidth + " imgHeight" + imgHeight);
        height = (getScreenWidth() * imgHeight) / imgWidth;
        Log.i("height", "height: " + (height));
        return height;
    }

    public boolean verificaInstagram() {
        boolean installed;
        try {
            context.getPackageManager().getApplicationInfo("com.instagram.android", 0);
            installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            installed = false;
        }
        return installed;
    }

    public static Uri getFileURI(String url) {
        File f = new File(url);
        Uri uri = Uri.fromFile(f);
        Log.d("Kunal", "uri->" + uri);
        return uri;
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

    public String SaveImagefrom(Bitmap finalBitmap) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/FireFighter_saved");
        myDir.mkdirs();
        Random random = new Random();
        String fname = "ImageText" + random.nextInt() + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }

    public Bitmap getBitmapFromView(View view, int totalWidth, int totalHeight) {

        int height = Math.min(842, totalHeight);
        float percent = height / (float) totalHeight;

        Bitmap canvasBitmap = Bitmap.createBitmap((int) (totalWidth * percent), (int) (totalHeight * percent), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(canvasBitmap);

        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);

        canvas.save();
        canvas.scale(percent, percent);
        view.draw(canvas);
        canvas.restore();

        return canvasBitmap;
    }

    public void deleteAllFiles() {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/FireFighter");
        if (myDir.exists()) {
            if (myDir.isDirectory()) {
                String[] children = myDir.list();
                if (children != null)
                    for (String aChildren : children) {
                        new File(myDir, aChildren).delete();
                    }
            }
        }
    }
}
