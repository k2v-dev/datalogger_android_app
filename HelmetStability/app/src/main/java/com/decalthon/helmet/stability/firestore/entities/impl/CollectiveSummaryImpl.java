package com.decalthon.helmet.stability.firestore.entities.impl;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.decalthon.helmet.stability.firestore.entities.ICollectiveSummary;
import com.decalthon.helmet.stability.preferences.CollectiveSummaryPreference;
import com.decalthon.helmet.stability.preferences.ProfilePreferences;
import com.decalthon.helmet.stability.preferences.UserPreferences;
import com.decalthon.helmet.stability.utilities.Constants;
import com.decalthon.helmet.stability.webservice.requests.CollectiveSummaryReq;
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

public class CollectiveSummaryImpl implements ICollectiveSummary {
    public static final String TAG = CollectiveSummaryReq.class.getSimpleName();

    private Context context;

    private FirebaseFirestore db;

    public CollectiveSummaryImpl(Context context) {
        this.context = context;
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public void updateUserData(String user_id, CollectiveSummaryReq collectiveSummaryReq) {
        Map<String, Object> user_data = new HashMap<>();
        user_data.put(Constants.CollSumFields.TOT_SESSION, collectiveSummaryReq.total_sessions);
        user_data.put(Constants.CollSumFields.TOT_DURATION, collectiveSummaryReq.total_duration);
        user_data.put(Constants.CollSumFields.TOT_SIZE, collectiveSummaryReq.total_size);
        user_data.put(Constants.CollSumFields.ACT_CODE, collectiveSummaryReq.activity_types);

        Task<Void> newUser = db.collection(Constants.USER_DATA_COLLECTION).document(user_id).set(user_data);
        newUser.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "User data was successfully added");
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
    public void getUserDataByUserID(String user_id) {
        DocumentReference docRef = db.collection(Constants.USER_DATA_COLLECTION).document(user_id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    CollectiveSummaryReq collectiveSummaryReq = Objects.requireNonNull(task.getResult()).toObject(CollectiveSummaryReq.class);
                    //Navigate to profile page
                    if(collectiveSummaryReq != null){
                        Constants.isUpdateCollectiveSummary = true;
                        //Toast.makeText(context, "Profile already exist in the database ", Toast.LENGTH_SHORT).show();
                        CollectiveSummaryPreference collSumPreferences = new CollectiveSummaryPreference(context);
                        collSumPreferences.setTotDuration(collectiveSummaryReq.total_duration);
                        collSumPreferences.setTotSession(collectiveSummaryReq.total_sessions);
                        collSumPreferences.setTotSize(collectiveSummaryReq.total_size);
                        collSumPreferences.setActCodes(collectiveSummaryReq.activity_types);
                    }else{
                        Log.d(TAG,"Collective summary not found");
                    }
                }else{
                    String excep = Objects.requireNonNull(task.getException()).getMessage();
                    Log.d(TAG, "Error reading user data " + excep);
                }
            }
        });
    }
}
