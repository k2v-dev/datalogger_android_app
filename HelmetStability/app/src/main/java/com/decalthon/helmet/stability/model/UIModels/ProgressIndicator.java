package com.decalthon.helmet.stability.model.uimodels;

import android.app.ProgressDialog;
import android.content.Context;

// Use for show waiting cursor
public class ProgressIndicator {

    private ProgressDialog pd;
    private Context context;

    public ProgressIndicator(Context context){
        this.context = context;
    }

    public void show( String title, String message){
        pd = new ProgressDialog(context);
        pd.setMax(10);
        pd.setMessage(message);
        pd.setTitle(title);
        pd.setCancelable(true);
        pd.setCanceledOnTouchOutside(false);
        pd.show();
    }

    public void dismiss() {
        if (pd != null) {

            if (pd.isShowing()) {
                pd.dismiss();
                pd = null;
            }
        }
    }
}
