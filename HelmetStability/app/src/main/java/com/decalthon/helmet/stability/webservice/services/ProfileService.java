package com.decalthon.helmet.stability.webservice.services;

import android.content.Context;

import com.decalthon.helmet.stability.preferences.UserPreferences;
import com.decalthon.helmet.stability.webservice.Service;
import com.decalthon.helmet.stability.webservice.requests.ProfileReq;
import com.decalthon.helmet.stability.webservice.requests.UserInfoReq;

import okhttp3.Request;
import okhttp3.RequestBody;

public class ProfileService  extends Service {

    public Request saveProfile(Context context, ProfileReq profileReq){
        try{
            String json_data = gson.toJson(profileReq);

            RequestBody body = RequestBody.create(json_data, MEDIA_TYPE_JSON);
            String user_id = UserPreferences.getInstance(context).getUserID();
            Request request = new Request.Builder()
                    .url(BASE_URL+"/profile/"+user_id)
                    .method(POST, body)
                    .addHeader(Content_Type, APPN_JSON)
                    .addHeader(Accept, APPN_JSON)
                    .build();
            return  request;
            //                });
        }catch (Exception ex){
            ex.printStackTrace();
        }

        return null;
    }

    public Request getProfile(Context context){
        try{

            RequestBody body = RequestBody.create("", MEDIA_TYPE_JSON);
            String user_id = UserPreferences.getInstance(context).getUserID();
            String token = UserPreferences.getInstance(context).getToken();
            Request request = new Request.Builder()
                    .url(BASE_URL+"/profile/"+user_id)
                    .method(GET, null)
                    .addHeader(Content_Type, APPN_JSON)
                    .addHeader(Accept, APPN_JSON)
                    .addHeader(Authorization, "gts@dkt "+token)
                    .build();
            return  request;
            //                });
        }catch (Exception ex){
            ex.printStackTrace();
        }

        return null;
    }

}
