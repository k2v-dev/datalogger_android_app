package com.decalthon.helmet.stability.network;

import android.app.Activity;
import android.util.Log;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneOtpValidation {

    private Activity activity;

    public PhoneOtpValidation(Activity activity) {
        this.activity = activity;
    }

    public void sendVerificationCode(String phoneNumber){

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                this.activity,
                mCall
        );

    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCall = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {

            //pb_bar.setVisibility(View.GONE);
        }

        @Override
        public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
//            pb_bar.setVisibility(View.GONE);
            String mVerificationId = verificationId;
            Log.e("MapActivity" , "Verification id : " + verificationId);
//            Intent intent = new Intent(MapActivity.this , OtpActivity.class);
//            intent.putExtra("verificationId" , mVerificationId);
//            startActivity(intent);
//            finish();
        }
    };
}
