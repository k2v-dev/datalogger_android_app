package com.decalthon.helmet.stability.webservice.services;

import android.content.Context;

import com.decalthon.helmet.stability.preferences.UserPreferences;
import com.decalthon.helmet.stability.webservice.Service;
import com.decalthon.helmet.stability.webservice.requests.AvatarReq;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AvatarService extends Service {

    public Request getAvtarImg(Context context, AvatarReq avatarReq) {

        String user_id = UserPreferences.getInstance(context).getUserID();
        String token = UserPreferences.getInstance(context).getToken();

        RequestBody body = RequestBody.create("", MEDIA_TYPE_JSON);

        Request request = new Request.Builder()
                .url(BASE_URL+"/avatar/get_img/" + user_id)
                .method("GET", body)
                .addHeader("Authorization", "gts@dkt "+token)
                .addHeader("Content-Type", "application/json")
                .build();
        return request;
//        try {
//            Response response = client.newCall(request).execute();
////            InputStream in = response.body().byteStream();
////            writeToFile(in, "/Users/gts/download.jpeg");
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
    }


    public Request uploadAvatarImg(Context context, File file){
        String user_id = UserPreferences.getInstance(context).getUserID();
        RequestBody formBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("img_", file.getName(),
                        RequestBody.create(MediaType.parse("image/png"), file))
                .addFormDataPart("user_id", user_id)
                .build();
        Request request = new Request.Builder().url(BASE_URL+"/avatar/put_img/").post(formBody).build();
        return request;
    }


}
