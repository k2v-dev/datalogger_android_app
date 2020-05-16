package com.decalthon.helmet.stability.firestore.entities;

import com.decalthon.helmet.stability.webservice.requests.CollectiveSummaryReq;
import com.decalthon.helmet.stability.webservice.requests.ProfileReq;

public interface ICollectiveSummary {
    void updateUserData(String user_id, CollectiveSummaryReq firestoreProfileModel);

    void getUserDataByUserID(String user_id);
}
