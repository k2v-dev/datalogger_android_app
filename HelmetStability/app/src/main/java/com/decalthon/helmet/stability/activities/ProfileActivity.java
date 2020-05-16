package com.decalthon.helmet.stability.activities;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.decalthon.helmet.stability.fragments.ProfileFragment;
import com.decalthon.helmet.stability.R;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

public class ProfileActivity extends FragmentActivity implements ProfileFragment.OnFragmentInteractionListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onBackPressed() {
        Button saveButton = findViewById(R.id.save_profile);
        if(saveButton.isEnabled()){
            showUnsavedAlertDialog();
        }else{
            super.onBackPressed();
        }
    }

    public void showUnsavedAlertDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Alert")
                .setMessage(R.string.unsaved_changes_message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        ProfileActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .create();
        alertDialog.show();
    }
}
