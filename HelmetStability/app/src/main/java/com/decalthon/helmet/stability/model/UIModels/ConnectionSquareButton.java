package com.decalthon.helmet.stability.model.uimodels;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

// Use in layout for make a view square.
public class ConnectionSquareButton extends LinearLayout {

    public ConnectionSquareButton(Context context) {
        super(context);
    }

    public ConnectionSquareButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    // This is used to make square buttons.
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}