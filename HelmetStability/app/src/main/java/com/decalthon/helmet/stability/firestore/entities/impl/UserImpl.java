package com.decalthon.helmet.stability.firestore.entities.impl;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.decalthon.helmet.stability.utilities.Constants;
import com.decalthon.helmet.stability.utilities.UniqueKeyGen;
import com.decalthon.helmet.stability.firestore.FirestoreUserModel;
import com.decalthon.helmet.stability.firestore.entities.IUserInterface;
import com.decalthon.helmet.stability.webservice.requests.ProfileReq;
import com.decalthon.helmet.stability.webservice.requests.UserInfoReq;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;

public class UserImpl implements IUserInterface {

    private static final String TAG = UserImpl.class.getSimpleName();

    private Context context;

    private FirebaseFirestore db;

    public UserImpl(Context context) {
        this.context = context;
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public void doesUserExist(UserInfoReq firestoreUserModel) {
        String user_id = UniqueKeyGen.genUserId(firestoreUserModel.email, firestoreUserModel.phone_no);

        DocumentReference docRef = db.collection(Constants.USER_COLLECTION).document(user_id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    System.out.println("User id"+task.getResult().getId());
                    FirestoreUserModel user = Objects.requireNonNull(task.getResult()).toObject(FirestoreUserModel.class);
                    //Navigate to profile page
                    if(user != null){
                        Toast.makeText(context, "User email and phone already exist in the database ", Toast.LENGTH_SHORT).show();
                    }else{
                        addNewRegisteredUser(user_id, firestoreUserModel);
                    }
                }else{
                    String excep = Objects.requireNonNull(task.getException()).getMessage();
                    Log.d(TAG, "Error reading user data " + excep);

                }
            }
        });

//        Query query = db.collection(Constants.USER_COLLECTION).whereEqualTo(Constants.UserFields.EMAIL, firestoreUserModel.getEmail()).whereEqualTo(Constants.UserFields.PHONE_NO, firestoreUserModel.getPhone_no());
//        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if(Objects.requireNonNull(task.getResult()).size() > 0){
//                    Toast.makeText(context, "User email and phone already exist in the database ", Toast.LENGTH_SHORT).show();
//                }else{
//                    //add a new user to Firestore database
//                    addNewRegisteredUser(firestoreUserModel);
//                }
//            }
//        });
    }

    @Override
    public void addNewRegisteredUser(String user_id, UserInfoReq firestoreUserModel) {

        Map<String, Object> user = new HashMap<>();
//        user.put(Constants.UserFields.USERNAME, firestoreUserModel.getName());
        user.put(Constants.UserFields.EMAIL, firestoreUserModel.email);
        user.put(Constants.UserFields.PHONE_NO, firestoreUserModel.phone_no);
        user.put(Constants.UserFields.DEVICE_ID, firestoreUserModel.deviceId);

        ProfileReq firestoreProfileModel = new ProfileReq();
        firestoreProfileModel.name = firestoreUserModel.userName;
        Map<String, Object> profile = new HashMap<>();
        profile.put(Constants.ProfileFields.USERNAME, firestoreProfileModel.name);
        profile.put(Constants.ProfileFields.WEIGHT, firestoreProfileModel.wt);
        profile.put(Constants.ProfileFields.HEIGHT, firestoreProfileModel.ht);
        profile.put(Constants.ProfileFields.DOB, firestoreProfileModel.dob);
        profile.put(Constants.ProfileFields.GENDER, firestoreProfileModel.gender);

        Task<Void> newUser = db.collection(Constants.USER_COLLECTION).document(user_id).set(user);
        Task<Void> newProfile = db.collection(Constants.PROFILE_COLLECTION).document(user_id).set(profile);
        Task combinedTask = Tasks.whenAllSuccess(newUser, newProfile).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
            @Override
            public void onSuccess(List<Object> list) {
                Log.d(TAG, "User & Profile was successfully added");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Error has occurred " + e.getMessage());
            }
        });

//        newUser.addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                Log.d(TAG, "User was successfully added");
//                //NavUtil.moveToNextPage(context, ProfileActivity.class, userEnteredName);
//                Map<String, Object> profile = new HashMap<>();
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.d(TAG, "Error has occurred " + e.getMessage());
//            }
//        });

    }

    @Override
    public void getLoginUserByPhone(String phone_num) {
        Query query = db.collection(Constants.USER_COLLECTION).whereEqualTo(Constants.UserFields.PHONE_NO, phone_num);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(Objects.requireNonNull(task.getResult()).size() > 0){
                    Toast.makeText(context, "User phone already exist in the database ", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context, "Missing user record ", Toast.LENGTH_SHORT).show();
                }
            }
        });


//        DocumentReference docRef = db.collection(Constants.USER_COLLECTION).document(email);
//        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if(task.isSuccessful()){
//                    FirestoreUserModel user = Objects.requireNonNull(task.getResult()).toObject(FirestoreUserModel.class);
//                    //Navigate to profile page
//                    if(user != null){
//                        NavUtil.moveToNextPage(context, ProfileActivity.class, user.getUsername());
//                    }else{
//                        Toast.makeText(context, "Missing user record ", Toast.LENGTH_SHORT).show();
//                    }
//
//                }else{
//                    String excep = Objects.requireNonNull(task.getException()).getMessage();
//                    Log.d(TAG, "Error reading user data " + excep);
//
//                }
//            }
//        });

    }
}
