package com.decalthon.helmet.stability.fragments;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.chaos.view.PinView;
import com.decalthon.helmet.stability.R;
import com.decalthon.helmet.stability.firestore.entities.impl.CollectiveSummaryImpl;
import com.decalthon.helmet.stability.utilities.Common;
import com.decalthon.helmet.stability.utilities.Constants;
import com.decalthon.helmet.stability.firestore.FirebaseStorageManager;
import com.decalthon.helmet.stability.firestore.entities.impl.ProfileImpl;
import com.decalthon.helmet.stability.model.InternetCheck;
import com.decalthon.helmet.stability.preferences.UserPreferences;
import com.decalthon.helmet.stability.webservice.requests.UserInfoReq;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.hbb20.CountryCodePicker;

import java.util.Objects;
import java.util.concurrent.TimeUnit;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class LoginFragment extends Fragment implements View.OnClickListener{
    private String TAG = "LoginFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;
    private PinView pinView;
    private Button loginBtn;
    private TextView topText,textU;
    private TextView resend_otp_tv;
    private TextView register_now_tv;
    private TextView error_msg_tv;
    private EditText userPhone;
    private ConstraintLayout first, second;
    private CountryCodePicker ccp;
    private View view;
    private ProgressBar pb_bar;
    FirebaseAuth mAuth;
    private String phone_number;
    private PhoneAuthProvider.ForceResendingToken phone_token = null;

    String mVerificationId = "";

    private OnFragmentInteractionListener mListener;

    FirebaseFirestore firestoreDb;
    UserInfoReq userInfo;
    String userId = "";

    public LoginFragment() {
        // Required empty public constructor
    }

//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment LoginFragment.
//     */
    // TODO: Rename and change types and number of parameters
//    public static LoginFragment newInstance(String param1, String param2) {
//        LoginFragment fragment = new LoginFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_login, container, false);
        topText = view.findViewById(R.id.topText);
        pinView =  view.findViewById(R.id.pinView);

        loginBtn = view.findViewById(R.id.button);
        userPhone = view.findViewById(R.id.userPhone);
        ccp = view.findViewById(R.id.ccp);
        first = view.findViewById(R.id.first_step);
        second = view.findViewById(R.id.secondStep);
        textU = view.findViewById(R.id.textView_noti);
        pb_bar = view.findViewById(R.id.pb_bar);
        register_now_tv = view.findViewById(R.id.registration_tv);
        resend_otp_tv = view.findViewById(R.id.resend_otp);
        error_msg_tv = view.findViewById(R.id.error_msg_tv);

        mAuth = FirebaseAuth.getInstance();

        pb_bar.setVisibility(View.GONE);
        first.setVisibility(View.VISIBLE);

        loginBtn.setOnClickListener(this);
        ccp.registerCarrierNumberEditText(userPhone);

        firestoreDb = FirebaseFirestore.getInstance();

        register_now_tv.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   navigateToRegistrationform();
               }
           }
        );

        resend_otp_tv.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View v) {
                                                   if(phone_token != null){
                                                       resendVerificationCode(userInfo.phone_no, phone_token);
                                                   }
                                               }
                                           }
        );
        Common.dismiss_wait_bar();
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        error_msg_tv.setText("");
        new InternetCheck(isInternet -> {
            if(isInternet) {
                if (loginBtn.getText().equals(getResources().getString(R.string.login))) {

                    String phone = userPhone.getText().toString();
                    String ccp_text = ccp.getFullNumberWithPlus();

                    Log.d(TAG, " phone="+(ccp_text+phone));
                    if (!TextUtils.isEmpty(phone)&&  ccp.isValidFullNumber()) {
                        phone_number = ccp_text;
//                sendVerificationCode(ccp_text);
                        pb_bar.setVisibility(View.VISIBLE);
                        getLoginUserByPhone(phone_number);
                    } else {
                        //Toast.makeText(getActivity(), "Please enter the details", Toast.LENGTH_SHORT).show();
                        error_msg_tv.setText(getString(R.string.enter_phone_num));
                    }
                }else if (loginBtn.getText().equals(getResources().getString(R.string.verify))) {
                    String OTP = pinView.getText().toString();
                    if(OTP.length() == 6){
                        verifyOtp(OTP);
                        pb_bar.setVisibility(View.VISIBLE);
                    }else{
                        //Toast.makeText(getActivity(), "Please enter the 6 digit OTP", Toast.LENGTH_SHORT).show();
                        error_msg_tv.setText(getString(R.string.enter_six_digit_otp));
                    }
                }
            }else{
                Common.noInternetAlert(getContext());
            }
        });


    }

    public void getLoginUserByPhone(String phone_num) {
        userInfo = null;
        userId = "";
        Query query = firestoreDb.collection(Constants.USER_COLLECTION).whereEqualTo(Constants.UserFields.PHONE_NO, phone_num);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                try{
                    if(Objects.requireNonNull(task.getResult()).size() > 0){
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        userInfo = documentSnapshot.toObject(UserInfoReq.class);
                        userId = documentSnapshot.getId();

                        //Toast.makeText(getContext(), "User phone already exist in the database ", Toast.LENGTH_SHORT).show();
                        if(userInfo != null){
                            sendVerificationCode(phone_num);
//                        UserPreferences userPreferences = UserPreferences.getInstance(getContext());
//                        userPreferences.savePhoneNo(phone_number);
//                        userPreferences.saveName(userInfo.userName);
//                        userPreferences.saveUserID(userId);
//                        new ProfileImpl(getContext()).getProfileByUserID(userId);
//                        FirebaseStorageManager.downloadImage(getContext(), userId, new FirebaseStorageManager.DownloadListener() {
//                            @Override
//                            public void onComplete(boolean isSuccess, String filepath) {
//                                if(isSuccess){
//                                    userPreferences.saveProfilePhoto(filepath);
//
//                                }
//                            }
//                        });
//                        FragmentManager fm = getActivity()
//                                .getSupportFragmentManager();
//                        fm.popBackStack(Constants.HOME_FRAGMENT, 0);
                        }
                    }else{
                        error_msg_tv.setText(getString(R.string.user_not_exists));
                        // Toast.makeText(getContext(), "Phone number doesn't exists. Please register new login.", Toast.LENGTH_SHORT).show();
                        showOriginalUI();
                    }
                }catch (Exception ex){
                    error_msg_tv.setText("No permission");
                    showOriginalUI();
                }
            }
        });
    }

    /**
     * Request Phone Auth Provider for sending OTP
     * @param phone_number
     */
    public void sendVerificationCode(String phone_number){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone_number,
                120,
                TimeUnit.SECONDS,
                getActivity(),
                mCall
        );
    }

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                120,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                getActivity(),               // Activity (for callback binding)
                mCall,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }


    /**
     * Implemented Call back method of above method verifyPhoneNumber.
     */
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCall = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            //Toast.makeText(getContext(), "No valid phone number", Toast.LENGTH_SHORT).show();
            error_msg_tv.setText(getString(R.string.no_valid_phone));
            pb_bar.setVisibility(View.GONE);
        }

        @Override
        public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
//            pb_bar.setVisibility(View.GONE);
            phone_token = token;
            mVerificationId = verificationId;
            Log.e("MapActivity" , "Verification id : " + verificationId);
            loginBtn.setText(getResources().getString(R.string.verify));
            first.setVisibility(View.GONE);
            second.setVisibility(View.VISIBLE);
            topText.setText(getString(R.string.enter_otp));
            register_now_tv.setVisibility(View.INVISIBLE);
        }
    };


    private void verifyOtp(String otp) {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId,otp);

        //sign in user
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
//                            String userUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
//                            Log.d(TAG, "user UID = "+userUID);
//                            pb_bar.setVisibility(View.INVISIBLE);
                            pinView.setLineColor(Color.GREEN);
                            textU.setText(getResources().getString(R.string.otp_verified));
                            textU.setTextColor(Color.GREEN);
                            // ToDo
                            // Http request for registration
                            // After successfully registration, save to user details and token to local storage
                            // go back to Home page
//                            getLoginUserByPhone(phone_number);
                            UserPreferences userPreferences = UserPreferences.getInstance(getContext());
                            userPreferences.savePhoneNo(phone_number);
                            userPreferences.saveName(userInfo.userName);
                            userPreferences.saveUserID(userId);
                            new ProfileImpl(getContext()).getProfileByUserID(userId);
                            new CollectiveSummaryImpl(getContext()).getUserDataByUserID(userId);
                            FirebaseStorageManager.downloadImage(getContext(), userId, new FirebaseStorageManager.DownloadListener() {
                                @Override
                                public void onComplete(boolean isSuccess, String filepath) {
                                    if(isSuccess){
                                        userPreferences.saveProfilePhoto(filepath);
                                        //Todo: Update the photo in action bar
                                    }
                                    Constants.isPhotoChanged = true;
                                    if(getActivity() != null){
                                        getActivity().finish();
                                    }
                                }

                            });


//                            FragmentManager fm = getActivity()
//                                    .getSupportFragmentManager();
//                            fm.popBackStack(HomeFragment.class.getSimpleName(),FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                            MainActivity.shared().onBackPressed();
                            /*try{
                                UserInfoService userInfoService = new UserInfoService();
                                Request request = userInfoService.login(phone_number);
                                if(request != null){

                                    OkHttpClient okHttpClient = new OkHttpClient.Builder()
                                            .connectTimeout(10, TimeUnit.SECONDS)
                                            .writeTimeout(10, TimeUnit.SECONDS)
                                            .readTimeout(30, TimeUnit.SECONDS)
                                            .build();
                                    okHttpClient.newCall(request).enqueue(new Callback() {
                                        @Override public void onFailure(Call call, IOException e) {
                                            e.printStackTrace();
                                            showOriginalUI();
                                            getActivity().runOnUiThread(new Runnable() {
                                                public void run() {
                                                    Toast.makeText(getContext(), getString(R.string.server_error), Toast.LENGTH_LONG).show();
                                                }
                                            });
//                                            registerBtn.setText(getResources().getString(R.string.register));
//                                            first.setVisibility(View.VISIBLE);
//                                            second.setVisibility(View.GONE);
//                                            pb_bar.setVisibility(View.GONE);
                                        }

                                        @Override public void onResponse(Call call, Response response) throws IOException {
                                            try (ResponseBody responseBody = response.body()) {
                                                String json = response.body().string();
                                                if (response.isSuccessful()) {
                                                    AuthenticationDetails authenticationDetails = null;
                                                    authenticationDetails = new GsonBuilder().create().fromJson(json, AuthenticationDetails.class);
                                                    UserPreferences userPreferences = UserPreferences.getInstance(getContext());
                                                    userPreferences.savePhoneNo(phone_number);
                                                    userPreferences.saveName(authenticationDetails.name);
                                                    userPreferences.saveUserID(authenticationDetails.id);
                                                    userPreferences.saveToken(authenticationDetails.token);
                                                    FragmentManager fm = getActivity()
                                                            .getSupportFragmentManager();
                                                    fm.popBackStack(Constants.HOME_FRAGMENT, 0);

                                                }else if(json.contains("errorMessage")){
                                                    ErrorMessages errorMessages = new GsonBuilder().create().fromJson(json, ErrorMessages.class);
                                                    getActivity().runOnUiThread(new Runnable() {
                                                        public void run() {
                                                            Toast.makeText(getContext(), errorMessages.errorMessage, Toast.LENGTH_LONG).show();
                                                        }
                                                    });
                                                    if(errorMessages.errorCode == ErrorCodes.NO_USER_FOUND.getCode() ||
                                                            errorMessages.errorCode == ErrorCodes.NO_RECORD_FOUND.getCode()) {
                                                        showOriginalUI();
//                                                        registerBtn.setText(getResources().getString(R.string.register));
//                                                        first.setVisibility(View.VISIBLE);
//                                                        second.setVisibility(View.GONE);
//                                                        pb_bar.setVisibility(View.GONE);
                                                    }
                                                }
                                            }catch (Exception ex){
                                                ex.printStackTrace();
                                                showOriginalUI();
                                                getActivity().runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        Toast.makeText(getContext(), getString(R.string.server_error), Toast.LENGTH_LONG).show();
                                                    }
                                                });
//                                                registerBtn.setText(getResources().getString(R.string.register));
//                                                first.setVisibility(View.VISIBLE);
//                                                second.setVisibility(View.GONE);
//                                                pb_bar.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                                }

                            }catch (Exception ex){
                                Log.d(TAG, ex.getMessage());
                            }*/

//                            try{
//                                FragmentManager fm = getActivity()
//                                        .getSupportFragmentManager();
//                                fm.popBackStack();
//                            }catch (Exception ex){
//                                Log.d(TAG, ex.getMessage());
//                            }

                        }else {
                            //pb_bar.setVisibility(View.INVISIBLE);
                            pinView.setLineColor(Color.RED);
                            textU.setText(getResources().getString(R.string.incorrect_otp));
                            textU.setTextColor(Color.RED);
                            String message = "Verification failed , Please try again later.";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }
                            error_msg_tv.setText(message);
                           // Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void navigateToRegistrationform(){
        Fragment fragment = new RegistrationFormFragment();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
//        fragmentTransaction.replace(R.id.fragment, fragment);
        fragmentTransaction.replace(R.id.fragment,
                fragment,RegistrationFormFragment.class.getSimpleName());
//        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.addToBackStack(LoginFragment.class.getSimpleName());
        fragmentTransaction.commit();
    }

    public void showOriginalUI(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loginBtn.setText(getResources().getString(R.string.login));
                first.setVisibility(View.VISIBLE);
                second.setVisibility(View.GONE);
                pb_bar.setVisibility(View.GONE);
                pinView.setLineColor(getResources().getColor(android.R.color.darker_gray));

                topText.setText(getString(R.string.welcome_msg));
            }
        });
    }
}
