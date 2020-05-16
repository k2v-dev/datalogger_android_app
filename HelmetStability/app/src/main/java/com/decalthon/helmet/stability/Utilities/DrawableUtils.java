package com.decalthon.helmet.stability.utilities;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.LayerDrawable;
import androidx.core.content.ContextCompat;

import com.applandeo.materialcalendarview.CalendarUtils;
import com.decalthon.helmet.stability.R;

/**
 * Created by Mateusz Kornakiewicz on 02.08.2018.
 */

public final class DrawableUtils {

    public static Drawable getCircleDrawableWithText(Context context, String string) {
        Drawable background = ContextCompat.getDrawable(context, R.drawable.sample_circle);
        Drawable text = CalendarUtils.getDrawableText(context, string, null, android.R.color.white, 12);

        Drawable[] layers = {background, text};
        return new LayerDrawable(layers);
    }

    public static Drawable getOneDot(Context context){
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.one_icon);

        //Add padding to too large icon
        return new InsetDrawable(drawable, 150, 0, 150, 0);
    }

    public static Drawable getThreeDots(Context context){
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.three_icons);
        //Add padding to too large icon
        return new InsetDrawable(drawable, 50, 0, 50, 0);
    }

    public static Drawable getTwoDots(Context context){
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.two_icons);
        //Add padding to too large icon
        return new InsetDrawable(drawable, 100, 0, 100, 0);
    }

    public static Drawable getSixDots(Context context){
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.six_icons);
        //Add padding to too large icon
        return new InsetDrawable(drawable, 50, 0, 50, 0);
    }

    private DrawableUtils() {
    }
}
