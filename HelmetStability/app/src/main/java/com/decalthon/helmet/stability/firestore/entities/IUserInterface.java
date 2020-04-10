package com.decalthon.helmet.stability.firestore.entities;

import com.decalthon.helmet.stability.firestore.FirestoreUserModel;
import com.decalthon.helmet.stability.webservice.requests.UserInfoReq;

public interface IUserInterface {
    void doesUserExist(UserInfoReq firestoreUserModel);

    void addNewRegisteredUser(String user_id, UserInfoReq firestoreUserModel);

    void getLoginUserByPhone(String phone_num);
}

