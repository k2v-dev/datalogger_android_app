package com.decalthon.helmet.stability.webservice.services;

import android.os.Handler;
import android.os.Looper;

import com.decalthon.helmet.stability.webservice.Service;
import com.decalthon.helmet.stability.webservice.requests.UserInfoReq;
import com.decalthon.helmet.stability.webservice.responses.AuthenticationDetails;
import com.decalthon.helmet.stability.webservice.responses.ErrorMessages;
import com.decalthon.helmet.stability.webservice.responses.UserInfoRes;
import com.google.firebase.auth.UserInfo;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * UserInfoService
 * Implemented all http methods for user's registration & login
 */
public class UserInfoService extends Service {

    ErrorMessages errorMessages = null;
    /**
     * Registration of user
     * @param userInfo user details like name, email id & phone number
     * @return request object
     */
    public Request register(UserInfoReq userInfo){
        errorMessages = null;
//        AuthenticationDetails authenticationDetails = null;
        try{
            String json_data = gson.toJson(userInfo);

            RequestBody body = RequestBody.create(json_data, MEDIA_TYPE_JSON);
            Request request = new Request.Builder()
                    .url(BASE_URL+"/users/phone/")
                    .method(POST, body)
                    .addHeader(Content_Type, APPN_JSON)
                    .addHeader(Accept, APPN_JSON)
                    .build();
            return  request;
//           Response response = okHttpClient.newCall(request).execute();
//
//           if (response.code() >= 200 && response.code() < 300) {
//               String json = response.body().toString();
//               if(json.contains("errorMessage")){
//                   errorMessages = gson.fromJson(json, ErrorMessages.class);
//               }else{
//                   authenticationDetails = gson.fromJson(json, AuthenticationDetails.class);
//               }
//            }
//            okHttpClient.newCall(request).enqueue(new Callback() {
//                    @Override public void onFailure(Call call, IOException e) {
//                        e.printStackTrace();
//                    }
//
//                    @Override public void onResponse(Call call, Response response) throws IOException {
//                        try (ResponseBody responseBody = response.body()) {
//                            if (!response.isSuccessful())
//                                throw new IOException("Unexpected code " + response);
//
//                            AuthenticationDetails authenticationDetails = null;
//                            if (response.code() >= 200 && response.code() < 300) {
//                               String json = response.body().string();
//                               if(json.contains("errorMessage")){
//                                   errorMessages = gson.fromJson(json, ErrorMessages.class);
//                               }else{
//                                   authenticationDetails = gson.fromJson(json, AuthenticationDetails.class);
//                               }
//                             }
//
//                        }catch (Exception ex){
//                            ex.printStackTrace();
//                        }
//                    }
//                });
        }catch (Exception ex){
            ex.printStackTrace();
//            errorMessages = new ErrorMessages();
//            errorMessages.errorMessage = ex.getMessage();
        }

        return null;
    }

    /**
     * Login to server using phone number
     * @param phone
     * @return request object
     */
    public Request login(String phone) {
        try{
            RequestBody body = RequestBody.create("", MEDIA_TYPE_JSON);
            Request request = new Request.Builder()
                    .url(BASE_URL+"/users/login_phone/"+phone)
                    .method(POST, body)
                    .addHeader(Content_Type, APPN_JSON)
                    .addHeader(Accept, APPN_JSON)
                    .build();
            return request;
//            Response response = okHttpClient.newCall(request).execute();
//
//            if (response.code() >= 200 && response.code() < 300) {
//                String json = response.body().toString();
//                if(json.contains("errorMessage")){
//                    errorMessages = gson.fromJson(json, ErrorMessages.class);
//                }else{
//                    authenticationDetails = gson.fromJson(json, AuthenticationDetails.class);
//                }
//            }
        }catch (Exception ex){
            ex.printStackTrace();
//            errorMessages = new ErrorMessages();
//            errorMessages.errorMessage = ex.getMessage();
        }
        return null;
    }

}
