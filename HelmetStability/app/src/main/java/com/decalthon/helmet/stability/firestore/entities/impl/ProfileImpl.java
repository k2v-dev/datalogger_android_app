package com.decalthon.helmet.stability.firestore.entities.impl;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.decalthon.helmet.stability.utilities.Constants;
import com.decalthon.helmet.stability.firestore.entities.IProfileInterface;
import com.decalthon.helmet.stability.preferences.ProfilePreferences;
import com.decalthon.helmet.stability.preferences.UserPreferences;
import com.decalthon.helmet.stability.webservice.requests.ProfileReq;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;

public class ProfileImpl implements IProfileInterface {
    public static final String TAG = ProfileImpl.class.getSimpleName();

    private Context context;

    private FirebaseFirestore db;

    public ProfileImpl(Context context) {
        this.context = context;
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public void updateProfile(String user_id, ProfileReq firestoreProfileModel) {
        Map<String, Object> profile = new HashMap<>();
        profile.put(Constants.ProfileFields.USERNAME, firestoreProfileModel.name);
        profile.put(Constants.ProfileFields.WEIGHT, firestoreProfileModel.wt);
        profile.put(Constants.ProfileFields.HEIGHT, firestoreProfileModel.ht);
        profile.put(Constants.ProfileFields.DOB, firestoreProfileModel.dob);
        profile.put(Constants.ProfileFields.GENDER, firestoreProfileModel.gender);

        Task<Void> newUser = db.collection(Constants.PROFILE_COLLECTION).document(user_id).set(profile);
        newUser.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Profile was successfully added");
                //NavUtil.moveToNextPage(context, ProfileActivity.class, userEnteredName);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Error has occurred " + e.getMessage());
            }
        });

    }

    @Override
    public void getProfileByUserID(String user_id) {
        DocumentReference docRef = db.collection(Constants.PROFILE_COLLECTION).document(user_id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    ProfileReq profile = Objects.requireNonNull(task.getResult()).toObject(ProfileReq.class);
                    //Navigate to profile page
                    if(profile != null){
                        Toast.makeText(context, "Profile already exist in the database ", Toast.LENGTH_SHORT).show();
                        ProfilePreferences profilePreferences = ProfilePreferences.getInstance(context);
                        UserPreferences.getInstance(context).saveName(profile.name);
                        profilePreferences.saveDob(profile.dob);
                        profilePreferences.saveGender(profile.gender);
                        profilePreferences.saveHeight((float)profile.ht);
                        profilePreferences.saveWeight((float)profile.wt);
                    }else{
                        Toast.makeText(context, "Profile does not exists ", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    String excep = Objects.requireNonNull(task.getException()).getMessage();
                    Log.d(TAG, "Error reading user data " + excep);
                }
            }
        });
    }
}
