package com.decalthon.helmet.stability.activities;

import android.net.Uri;
import android.os.Bundle;

import com.decalthon.helmet.stability.fragments.LoginFragment;
import com.decalthon.helmet.stability.fragments.RegistrationFormFragment;
import com.decalthon.helmet.stability.R;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

public class LoginActivity extends FragmentActivity implements LoginFragment.OnFragmentInteractionListener, RegistrationFormFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_login);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
