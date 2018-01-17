package com.firefighterscalendar.custom;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.EditText;

import com.firefighterscalendar.R;


public class CustomEditText extends EditText {


    public CustomEditText(Context context) {
        super(context);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context, attrs);
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCustomFont(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setCustomFont(context, attrs);
    }

    private void setCustomFont(Context ctx, AttributeSet attrs) {
        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.vFont);
        int customFont = a.getInt(R.styleable.vFont_typeface, 0);
        setCustomFont(ctx, customFont);
        a.recycle();
    }

    public boolean setCustomFont(Context context, int asset) {

        switch (asset) {
            case 0:
                setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Palatino_BoldItalic.ttf"));
                break;
            case 1:
                setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Palatino.ttc"));
                break;
            case 2:
                setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Helvetica.ttf"));
                break;
            case 3:
                setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Helvetica-Light.ttf"));
                break;
            case 4:
                setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/swis721-bt-roman.ttf"));
                break;
            case 5:
                setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/swis721-cn-bt-bold.ttf"));
                break;
            case 6:
                setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/swiss-721-swa.ttf"));
                break;
            default:
                setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Helvetica.ttf"));
                break;
        }

        return true;
    }
}