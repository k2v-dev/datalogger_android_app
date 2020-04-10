package com.decalthon.helmet.stability.firestore.entities;

import com.decalthon.helmet.stability.firestore.FirestoreProfileModel;
import com.decalthon.helmet.stability.webservice.requests.ProfileReq;

public interface IProfileInterface {

    void updateProfile(String user_id, ProfileReq firestoreProfileModel);

    void getProfileByUserID(String user_id);
}
