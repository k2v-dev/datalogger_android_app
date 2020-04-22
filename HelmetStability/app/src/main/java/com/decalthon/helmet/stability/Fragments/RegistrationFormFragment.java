package com.decalthon.helmet.stability.Fragments;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.chaos.view.PinView;
import com.decalthon.helmet.stability.R;
import com.decalthon.helmet.stability.Utilities.Common;
import com.decalthon.helmet.stability.Utilities.Constants;
import com.decalthon.helmet.stability.Utilities.Helper;
import com.decalthon.helmet.stability.Utilities.UniqueKeyGen;
import com.decalthon.helmet.stability.firestore.FirestoreUserModel;
import com.decalthon.helmet.stability.model.InternetCheck;
import com.decalthon.helmet.stability.preferences.ProfilePreferences;
import com.decalthon.helmet.stability.preferences.UserPreferences;
import com.decalthon.helmet.stability.webservice.requests.ProfileReq;
import com.decalthon.helmet.stability.webservice.requests.UserInfoReq;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.hbb20.CountryCodePicker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RegistrationFormFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class RegistrationFormFragment extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private String TAG = "RegistrationFormFragment";
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
    private PinView pinView;
    private Button registerBtn;
    private TextView topText,textU;
    private TextView resend_otp_tv;
    private TextView error_msg_tv;
    private EditText userName, userPhone, userEmail;
    private ConstraintLayout first, second;
    private CountryCodePicker ccp;
    private View view;
    private ProgressBar pb_bar;
    FirebaseAuth mAuth;
    private UserInfoReq userInfoReq;
    String mVerificationId = "";
    FirebaseFirestore firestoreDb;
    String user_id = "";
    private PhoneAuthProvider.ForceResendingToken phone_token = null;

    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public RegistrationFormFragment() {
        // Required empty public constructor
    }

//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment RegistrationFormFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static RegistrationFormFragment newInstance(String param1, String param2) {
//        RegistrationFormFragment fragment = new RegistrationFormFragment();
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
        view = inflater.inflate(R.layout.fragment_registration_form, container, false);

        topText = view.findViewById(R.id.topText);
        pinView =  view.findViewById(R.id.pinView);

        registerBtn = view.findViewById(R.id.button);
        userName = view.findViewById(R.id.username);
        userPhone = view.findViewById(R.id.userPhone);
        userEmail = view.findViewById(R.id.userEmail);
        ccp = view.findViewById(R.id.ccp);
        first = view.findViewById(R.id.first_step);
        second = view.findViewById(R.id.secondStep);
        textU = view.findViewById(R.id.textView_noti);
        pb_bar = view.findViewById(R.id.pb_bar);
        resend_otp_tv = view.findViewById(R.id.resend_otp);
        error_msg_tv = view.findViewById(R.id.error_msg_tv);

        mAuth = FirebaseAuth.getInstance();

        pb_bar.setVisibility(View.GONE);
        first.setVisibility(View.VISIBLE);

        registerBtn.setOnClickListener(this);
        ccp.registerCarrierNumberEditText(userPhone);

        firestoreDb = FirebaseFirestore.getInstance();

        resend_otp_tv.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View v) {
                                                 if(phone_token != null){
                                                     resendVerificationCode(userInfoReq.phone_no, phone_token);
                                                 }
                                             }
                                         }
        );

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
                if (registerBtn.getText().equals(getResources().getString(R.string.register))) {
                    String name = userName.getText().toString();
                    String phone = userPhone.getText().toString();
                    String email = userEmail.getText().toString();
                    String ccp_text = ccp.getFullNumberWithPlus();

                    Log.d(TAG, "name="+name+", phone="+(ccp_text+phone)+", email="+email);

                    if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(phone) &&  !TextUtils.isEmpty(email)) {
                        if (!Helper.isValidEmail(email)) {
                            //Toast.makeText(getActivity(), "Please enter the valid email address", Toast.LENGTH_SHORT).show();
                            error_msg_tv.setText(getString(R.string.enter_email_add));
                        }else if( !ccp.isValidFullNumber()){
                            //Toast.makeText(getActivity(), "Please enter the valid phone number", Toast.LENGTH_SHORT).show();
                            error_msg_tv.setText(getString(R.string.enter_phone_num));
                        }else{
                            userInfoReq = new UserInfoReq();
                            userInfoReq.email = email;
                            userInfoReq.phone_no = ccp_text;
                            userInfoReq.userName = name;
                            userInfoReq.deviceId = UserPreferences.getInstance(getContext()).getDeviceID();
//                    sendVerificationCode(ccp_text);
                            getLoginUserByPhone(userInfoReq);
                            pb_bar.setVisibility(View.VISIBLE);
                        }
                    } else {
//                        Toast.makeText(getActivity(), "Please enter the details", Toast.LENGTH_SHORT).show();
                        error_msg_tv.setText(R.string.enter_details);
                    }
                }else if (registerBtn.getText().equals(getResources().getString(R.string.verify))) {
                    String OTP = pinView.getText().toString();
                    if(OTP.length() == 6){
                        verifyOtp(OTP);
                        pb_bar.setVisibility(View.VISIBLE);
                    }else{
//                        Toast.makeText(getActivity(), "Please enter the 6 digit OTP", Toast.LENGTH_SHORT).show();
                        error_msg_tv.setText(getString(R.string.enter_six_digit_otp));
                    }
                }
            }else{
                Common.noInternetAlert(getContext());
            }
        });


//        if (registerBtn.getText().equals(getResources().getString(R.string.register))) {
//            String name = userName.getText().toString();
//            String phone = userPhone.getText().toString();
//            String email = userEmail.getText().toString();
//            String ccp_text = ccp.getFullNumberWithPlus();
//
//            Log.d(TAG, "name="+name+", phone="+(ccp_text+phone)+", email="+email);
//            if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(phone)&&  ccp.isValidFullNumber()) {
//                registerBtn.setText(getResources().getString(R.string.verify));
//                first.setVisibility(View.GONE);
//                second.setVisibility(View.VISIBLE);
//                topText.setText("I Still don't trust you.\nTell me something that only two of us know.");
//                pb_bar.setVisibility(View.VISIBLE);
//            } else {
//                Toast.makeText(getActivity(), "Please enter the details", Toast.LENGTH_SHORT).show();
//            }
//        } else if (registerBtn.getText().equals(getResources().getString(R.string.verify))) {
//            String OTP = pinView.getText().toString();
//            if (OTP.equals("3456")) {
//                pinView.setLineColor(Color.GREEN);
//                textU.setText(getResources().getString(R.string.otp_verified));
//                textU.setTextColor(Color.GREEN);
//                registerBtn.setText(getResources().getString(R.string.next));
//            } else {
//                pinView.setLineColor(Color.RED);
//                textU.setText(getResources().getString(R.string.incorrect_otp));
//                textU.setTextColor(Color.RED);
//            }
//        }
    }


    /**
     * Request Phone Auth Provider for sending OTP
     * @param phoneNumber
     */
    public void sendVerificationCode(String phoneNumber){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                getActivity(),
                mCall
        );
    }

    /**
     * Resend OTP using same token
     * @param phoneNumber
     * @param token
     */
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
            e.printStackTrace();
            pb_bar.setVisibility(View.GONE);
        }

        @Override
        public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
            phone_token = token;
//            pb_bar.setVisibility(View.GONE);
            mVerificationId = verificationId;
            Log.e("MapActivity" , "Verification id : " + verificationId);
            registerBtn.setText(getResources().getString(R.string.verify));
            first.setVisibility(View.GONE);
            second.setVisibility(View.VISIBLE);
            topText.setText(getResources().getString(R.string.enter_otp_msg));

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
                            UserPreferences userPreferences = UserPreferences.getInstance(getContext());
//                            pb_bar.setVisibility(View.INVISIBLE);
                            pinView.setLineColor(Color.GREEN);
                            textU.setText(getResources().getString(R.string.otp_verified));
                            textU.setTextColor(Color.GREEN);
                            // ToDo
                            // Http request for registration
                            // After successfully registration, save to user details and token to local storage
                            // go back to Home page
                            //doesUserExist(userInfoReq);
                            //getLoginUserByPhone(userInfoReq);
                            addNewRegisteredUser(user_id, userInfoReq);
  /*                          try{
                                UserInfoService userInfoService = new UserInfoService();
                                Request request = userInfoService.register(userInfoReq);
                                if(request != null){

                                     OkHttpClient okHttpClient = new OkHttpClient.Builder()
                                            .connectTimeout(10, TimeUnit.SECONDS)
                                            .writeTimeout(10, TimeUnit.SECONDS)
                                            .readTimeout(30, TimeUnit.SECONDS)
                                            .build();
                                     okHttpClient.newCall(request).enqueue(new Callback() {
                                        @Override public void onFailure(Call call, IOException e) {
                                            e.printStackTrace();
                                        }

                                        @Override public void onResponse(Call call, Response response) throws IOException {
                                            try (ResponseBody responseBody = response.body()) {
                                                String json = response.body().string();
                                                if (response.isSuccessful()) {
                                                    AuthenticationDetails authenticationDetails = null;
                                                    authenticationDetails = new GsonBuilder().create().fromJson(json, AuthenticationDetails.class);
                                                    UserPreferences userPreferences = UserPreferences.getInstance(getContext());
                                                    userPreferences.savePhoneNo(userInfoReq.phone_no);
                                                    userPreferences.saveEmail(userInfoReq.email);
                                                    userPreferences.saveName(authenticationDetails.name);
                                                    userPreferences.saveUserID(authenticationDetails.id);
                                                    userPreferences.saveToken(authenticationDetails.token);
                                                    FragmentManager fm = getActivity()
                                                            .getSupportFragmentManager();
                                                    fm.popBackStack(Constants.HOME_FRAGMENT, 0);

//                                                    else if(json.contains("errorMessage")){
//                                                        ErrorMessages errorMessages = new GsonBuilder().create().fromJson(json, ErrorMessages.class);
//                                                        Toast.makeText(getContext(), errorMessages.errorMessage, Toast.LENGTH_LONG).show();
//                                                        if(errorMessages.errorCode == ErrorCodes.RECORD_ALREADY_EXISTS.getCode()) {
//                                                            FragmentManager fm = getActivity()
//                                                                    .getSupportFragmentManager();
//                                                            fm.popBackStack(Constants.LOGIN_FRAGMENT, 0);
//                                                        }else{
//                                                            registerBtn.setText(getResources().getString(R.string.register));
//                                                            first.setVisibility(View.VISIBLE);
//                                                            second.setVisibility(View.GONE);
//                                                        }
//                                                    }
                                                }else if(json.contains("errorMessage")){
                                                    ErrorMessages errorMessages = new GsonBuilder().create().fromJson(json, ErrorMessages.class);
//                                                    Toast.makeText(getContext(), errorMessages.errorMessage, Toast.LENGTH_LONG).show();
                                                    getActivity().runOnUiThread(new Runnable() {
                                                        public void run() {
                                                            Toast.makeText(getContext(), errorMessages.errorMessage, Toast.LENGTH_LONG).show();
                                                        }
                                                    });
                                                    if(errorMessages.errorCode == ErrorCodes.RECORD_ALREADY_EXISTS.getCode()) {
                                                        FragmentManager fm = getActivity()
                                                                .getSupportFragmentManager();
                                                        fm.popBackStack(Constants.LOGIN_FRAGMENT, 0);
                                                    }else{

                                                        registerBtn.setText(getResources().getString(R.string.register));
                                                        first.setVisibility(View.VISIBLE);
                                                        second.setVisibility(View.GONE);
                                                        pb_bar.setVisibility(View.GONE);
                                                        topText.setText(getString(R.string.welcome_msg));
                                                    }
                                                }
                                            }catch (Exception ex){
                                                ex.printStackTrace();
                                            }
                                        }
                                    });
                                }

                            }catch (Exception ex){
                                Log.d(TAG, ex.getMessage());
                            }*/
                        }else {
                            //pb_bar.setVisibility(View.INVISIBLE);
                            pinView.setLineColor(Color.RED);
                            textU.setText(getResources().getString(R.string.incorrect_otp));
                            textU.setTextColor(Color.RED);
                            String message = "Verification failed , Please try again later.";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }

                            //Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                            error_msg_tv.setText(message);
                        }
                    }
                });
    }

    public void getLoginUserByPhone(UserInfoReq firestoreUserModel) {

        Query query = firestoreDb.collection(Constants.USER_COLLECTION).whereEqualTo(Constants.UserFields.PHONE_NO, firestoreUserModel.phone_no);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(Objects.requireNonNull(task.getResult()).size() > 0){
                    Toast.makeText(getContext(), "User phone already exist in the our records. ", Toast.LENGTH_SHORT).show();
                    FragmentManager fm = getActivity()
                                .getSupportFragmentManager();
                        fm.popBackStack(Constants.LOGIN_FRAGMENT, 0);
                }else{
                    doesUserExist(firestoreUserModel);
                }
            }
        });
    }

    public void doesUserExist(UserInfoReq firestoreUserModel) {
        user_id = UniqueKeyGen.genUserId(firestoreUserModel.email, firestoreUserModel.phone_no);
        DocumentReference docRef = firestoreDb.collection(Constants.USER_COLLECTION).document(user_id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    System.out.println("User id"+task.getResult().getId());
                    FirestoreUserModel user = Objects.requireNonNull(task.getResult()).toObject(FirestoreUserModel.class);
                    //Navigate to profile page
                    if(user != null){
//                        Toast.makeText(getContext(), "It seems that email id is used by someone. Please use other email id.", Toast.LENGTH_SHORT).show();
                        error_msg_tv.setText(getString(R.string.use_other_email_id));
                    }else{
                        sendVerificationCode(firestoreUserModel.phone_no);
                        //addNewRegisteredUser(user_id, firestoreUserModel);
                    }
                }else{
                    String excep = Objects.requireNonNull(task.getException()).getMessage();
                    Log.d(TAG, "Error reading user data " + excep);
                }
            }
        });
    }


    public void addNewRegisteredUser(String user_id, UserInfoReq firestoreUserModel) {

        Map<String, Object> user = new HashMap<>();
//        user.put(Constants.UserFields.USERNAME, firestoreUserModel.getName());
        user.put(Constants.UserFields.EMAIL, firestoreUserModel.email);
        user.put(Constants.UserFields.PHONE_NO, firestoreUserModel.phone_no);
        user.put(Constants.UserFields.DEVICE_ID, firestoreUserModel.deviceId);

        ProfileReq profileReq = new ProfileReq();
        profileReq.name = firestoreUserModel.userName;
        //profileReq.dob = Common.getTimestamp(25);
        Map<String, Object> profile = new HashMap<>();
        profile.put(Constants.ProfileFields.USERNAME, profileReq.name);
        profile.put(Constants.ProfileFields.WEIGHT, profileReq.weight);
        profile.put(Constants.ProfileFields.HEIGHT, profileReq.height);
        profile.put(Constants.ProfileFields.DOB, profileReq.dob);
        profile.put(Constants.ProfileFields.GENDER, profileReq.gender);

        Task<Void> newUser = firestoreDb
                .collection(Constants.USER_COLLECTION).document(user_id).set(user);
        Task<Void> newProfile = firestoreDb.collection(Constants.PROFILE_COLLECTION).document(user_id).set(profile);
        Task combinedTask = Tasks.whenAllSuccess(newUser, newProfile).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
            @Override
            public void onSuccess(List<Object> list) {
                Log.d(TAG, "User & Profile was successfully added");
                Toast.makeText(getContext(), "User registration is successfully done ", Toast.LENGTH_SHORT).show();
//                AuthenticationDetails authenticationDetails = null;
//                authenticationDetails = new GsonBuilder().create().fromJson(json, AuthenticationDetails.class);
                UserPreferences userPreferences = UserPreferences.getInstance(getContext());
                userPreferences.savePhoneNo(userInfoReq.phone_no);
                userPreferences.saveEmail(userInfoReq.email);
                userPreferences.saveName(userInfoReq.userName);
                userPreferences.saveUserID(user_id);

                ProfilePreferences profilePreferences = ProfilePreferences.getInstance(getContext());
                //profileReq.dob = Common.getTimestamp(25);
                profilePreferences.saveDob(profileReq.dob);
                profilePreferences.saveGender(profileReq.gender);
                profilePreferences.saveHeight(profileReq.height);
                profilePreferences.saveWeight(profileReq.weight);

                pb_bar.setVisibility(View.INVISIBLE);

                FragmentManager fm = getActivity()
                        .getSupportFragmentManager();
                fm.popBackStack(Constants.HOME_FRAGMENT, 0);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "User registration is failed. Please try again ", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Error has occurred " + e.getMessage());
                registerBtn.setText(getResources().getString(R.string.register));
                first.setVisibility(View.VISIBLE);
                second.setVisibility(View.GONE);
                pb_bar.setVisibility(View.GONE);
                topText.setText(getString(R.string.welcome_msg));
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
}
