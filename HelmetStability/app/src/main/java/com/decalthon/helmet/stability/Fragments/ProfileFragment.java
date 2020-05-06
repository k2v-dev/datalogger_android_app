package com.decalthon.helmet.stability.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.decalthon.helmet.stability.Activities.MainActivity;
import com.decalthon.helmet.stability.R;
import com.decalthon.helmet.stability.Utilities.Common;
import com.decalthon.helmet.stability.Utilities.Constants;
import com.decalthon.helmet.stability.Utilities.FileUtilities;
import com.decalthon.helmet.stability.firestore.FirebaseStorageManager;
import com.decalthon.helmet.stability.preferences.ProfilePreferences;
import com.decalthon.helmet.stability.preferences.UserPreferences;
import com.decalthon.helmet.stability.webservice.requests.ProfileReq;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.common.io.Files;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import pl.aprilapps.easyphotopicker.ChooserType;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.aprilapps.easyphotopicker.MediaFile;
import pl.aprilapps.easyphotopicker.MediaSource;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends DialogFragment {
    public static final String TAG = ProfileFragment.class.getSimpleName();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private boolean isUploadedPhoto = false;

    private static int ageIndex = 0,genderIndex = 0,heightIndex = 0,weightIndex = 0;

    private static final String PHOTOS_KEY = "easy_image_photos_list";
//    private static final int CHOOSER_PERMISSIONS_REQUEST_CODE = 7459;
    private static final int CAMERA_REQUEST_CODE = 7500;
//    private static final int CAMERA_VIDEO_REQUEST_CODE = 7501;
    private static final int GALLERY_REQUEST_CODE = 7502;
    private static final int DOCUMENTS_REQUEST_CODE = 7503;

// Fragment-specific variables
    protected View galleryButton;

    private ArrayList<MediaFile> photos = new ArrayList<>();

    private EasyImage easyImage;

    private OnFragmentInteractionListener mListener;

    private BottomSheetDialog bottomSheetDialog;

    private CircleImageView photoEdit;

    private Context mContext;

    FirebaseFirestore firestoreDb;

    public ProfileFragment() {
        // Required empty public constructor

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        if (savedInstanceState != null) {
            photos = savedInstanceState.getParcelableArrayList(PHOTOS_KEY);
        }
    }


    /**
     * Initializes profile UI and inflates the fragment
     * @param inflater The callback argument which inflates a {@link ProfileFragment}
     * @param container The parent container which holds the profile fragment
     * @param savedInstanceState Any restore-able data
     * @return A view with editable profile
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View profileView = inflater.inflate
                (R.layout.fragment_profile, container, false);

        firestoreDb = FirebaseFirestore.getInstance();

        ImageView backLink =
                profileView.findViewById(R.id.close_profile_page_popup);
        backLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ProfilePreferences.getInstance(getContext()).isEmpty()) {
                    Common.okAlertMessage(getContext(), getString(R.string.enter_all_details));
                }else {
                    dismiss();
                }
            }
        });

        CircleImageView photoEdit = profileView.findViewById(R.id.edit_profile_photo);
        String defaultImagePath = UserPreferences.getInstance(getContext()).
                getProfilePhoto();
        String name = UserPreferences.getInstance(getContext()).getName();
        EditText editText =
                profileView.findViewById(R.id.edit_profile_name);
        editText.setText(name);

        int storedAge = ProfilePreferences.getInstance(getContext()).getAge();
        char storedGender =
                ProfilePreferences.getInstance(getContext()).getGender();
        int storedHeight =
                (int) ProfilePreferences.getInstance(getContext()).getHeight();
        int storedWeight =
                (int) ProfilePreferences.getInstance(getContext()).getWeight();
        isUploadedPhoto = false;
        //Retain default anonymous photo, unless there is a user-preference photo
        if(defaultImagePath.equals("default")) {
            ;
        }
        else{
            photoEdit.setImageBitmap(BitmapFactory.decodeFile(defaultImagePath));
        }

        registerEditEvent(profileView);

        return  profileView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        registerEditEvent(view);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(PHOTOS_KEY, photos);
    }

    //Based on requested permissions, any one of the following conditionals execute
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            easyImage.openCameraForImage(ProfileFragment.this);
        } else if (requestCode == GALLERY_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            easyImage.openGallery(ProfileFragment.this);
        } else if (requestCode == DOCUMENTS_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            easyImage.openDocuments(ProfileFragment.this);
        }
    }

    /**
     * Used to collect the result of the image picker
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        easyImage.handleActivityResult(requestCode, resultCode, data, getActivity(), new DefaultCallback() {
            @Override
            public void onMediaFilesPicked(MediaFile[] imageFiles, MediaSource source) {
                for (MediaFile imageFile : imageFiles) {
                    Log.d("EasyImage", "Image file returned: " + imageFile.getFile().toString() + "Source "+source);
                }
                //The on photos returned method is called once the media file has been chosen
                onPhotosReturned(imageFiles);
            }

            @Override
            public void onImagePickerError(@NonNull Throwable error, @NonNull MediaSource source) {
                //Some error handling
                error.printStackTrace();
            }

            @Override
            public void onCanceled(@NonNull MediaSource source) {
                //Not necessary to remove any files manually anymore
            }
        });
    }


    /**
     * Adapter are attached to individual editable UI elements
     * @param profileView
     */
    private void registerEditEvent(
             final View profileView) {
        //TODO Improve look and feel of existing UI

        //Extract all spinner elements from popup
        Spinner ageSpinner = profileView.findViewById(R.id.age_edit);
        Spinner genderSpinner = profileView.findViewById(R.id.gender_edit);
        Spinner heightSpinner = profileView.findViewById(R.id.height_edit);
        Spinner weightSpinner = profileView.findViewById(R.id.weight_edit);

        //Extract Name edit elements from popup
        EditText profileName = profileView.findViewById(R.id.edit_profile_name);

        //1. Assign linear layout manager for each type of spinner element
        RecyclerView.LayoutManager ageLayoutManager = new LinearLayoutManager
                (getContext());
        RecyclerView ageEditor = profileView.findViewById(R.id.age_editable);
        ageEditor.setHasFixedSize(true);
        ageEditor.setLayoutManager(ageLayoutManager);

        //2. Populate each spinner's recycler view using the template list item style
        ArrayAdapter adapter = ArrayAdapter.createFromResource(mContext,
                R.array.ages_array, R.layout.list_item_view);
        ageSpinner.setAdapter(adapter);
        int current_age = ProfilePreferences.getInstance(mContext).getAge();
        String [] allowedAges =
                getResources().getStringArray(R.array.ages_array);
        int startingAge = Integer.parseInt(allowedAges[1]);
        int max_age = Integer.parseInt(allowedAges[allowedAges.length-1]);
        int range = max_age - startingAge;
        ageSpinner.setSelection( current_age - (max_age - range) );

        //3. Set the spinner adapter

        //4. Record the spinner value for the next time the popup loads
//        ageSpinner.setSelection(ageIndex);

        //Repeating steps 1 to 4 for all the spinners.

        //TODO Migrate repetitive code to a new method

        RecyclerView.LayoutManager genderLayoutManager =
                new LinearLayoutManager(getContext());

        RecyclerView genderEditor = profileView.findViewById
                (R.id.gender_editable);
        genderEditor.setLayoutManager(genderLayoutManager);
        adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.gender_array, R.layout.list_item_view);
        genderSpinner.setAdapter(adapter);

        char selected_gender =
                ProfilePreferences.getInstance(mContext).getGender();
        genderIndex = selected_gender == 'M'? 1 : selected_gender == 'F' ? 2
                : 3;

        genderSpinner.setSelection(genderIndex);


        RecyclerView.LayoutManager heightLayoutManager =
                new LinearLayoutManager(getContext());

        RecyclerView heightEditor = profileView.findViewById
                (R.id.height_editable);
        heightEditor.setLayoutManager(heightLayoutManager);
        adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.height_array, R.layout.list_item_view);
        heightSpinner.setAdapter(adapter);


        String [] allowedHeights =
                getResources().getStringArray(R.array.height_array);
        int current_height =
                (int) ProfilePreferences.getInstance(mContext).getHeight();
        int starting_height = Integer.parseInt(allowedHeights[1]);
        int end_height = Integer.parseInt(allowedHeights[allowedHeights.length-1]);
        int height_range = end_height - starting_height;
        heightSpinner.setSelection(current_height - (end_height -  height_range));

        RecyclerView.LayoutManager weightLayoutManager =
                new LinearLayoutManager(getContext());

        RecyclerView weightEditor = profileView.findViewById
                (R.id.weight_editable);
        weightEditor.setLayoutManager(weightLayoutManager);
        adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.weight_array, R.layout.list_item_view);
        weightSpinner.setAdapter(adapter);

        String [] allowedWeights =
                getResources().getStringArray(R.array.weight_array);
        int current_weight =
                (int) ProfilePreferences.getInstance(mContext).getWeight();
        int starting_weight = Integer.parseInt(allowedWeights[1]);
        int end_weight = Integer.parseInt(allowedWeights[allowedWeights.length-1]);
        int weight_range = end_weight - starting_weight;
        weightSpinner.setSelection(current_weight - (end_weight - weight_range));
        //Extract photo edit elements from the popup

        CircleImageView photoView = profileView
                .findViewById(R.id.edit_image);

        photoEdit = profileView
                .findViewById(R.id.edit_profile_photo);

        //The photo edit listener shows a bottom dialog fragment with a custom media chooser
        photoView.setOnClickListener(v12 -> {

            //Display the bottom sheet dialog for the chooser
            View dialogView = getLayoutInflater().inflate(R.layout.bottom_sheet, null);
            bottomSheetDialog = new BottomSheetDialog(getContext());
            bottomSheetDialog.setContentView(dialogView);
            bottomSheetDialog.show();

            //Build the easy image instance to manipulate image selections
            easyImage = new EasyImage.Builder(getContext())
                    .setChooserTitle("Pick media")
                    .setCopyImagesToPublicGalleryFolder(false)
                    .setChooserType(ChooserType.CAMERA_AND_GALLERY)
                    .setFolderName("EasyImage sample")
                    .allowMultiple(false)
                    .build();

            //Three event listeners are defined, for sourcing images.
            //These are camera, gallery and file manager / documents

            //Only one choice is allowed for each bottom sheet appearance

            dialogView.findViewById(R.id.choice_camera).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*If necessary permissions are granted, a separate fragment
                    * is created for the extracting the image from the source*/
                    String[] necessaryPermissions = new String[]{Manifest.permission.CAMERA};
                    if (arePermissionsGranted(necessaryPermissions)) {
                        easyImage.openCameraForImage(ProfileFragment.this);
                    } else {
                        requestPermissionsCompat(necessaryPermissions, CAMERA_REQUEST_CODE);
                    }

                    //The bottom sheet is hidden whether or not permissions are granted
                    bottomSheetDialog.hide();
                }
            });

            //Similar event listeners are defined for the corresponding chooser action
            dialogView.findViewById(R.id.choice_gallery).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String[] necessaryPermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    if (arePermissionsGranted(necessaryPermissions)) {
                        easyImage.openGallery(ProfileFragment.this);
                    } else {
                        requestPermissionsCompat(necessaryPermissions, GALLERY_REQUEST_CODE);
                    }
                    bottomSheetDialog.hide();
                }
            });


            dialogView.findViewById(R.id.choice_files).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String[] necessaryPermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    if (arePermissionsGranted(necessaryPermissions)) {
                        easyImage.openDocuments(ProfileFragment.this);
                    } else {
                        requestPermissionsCompat(necessaryPermissions, DOCUMENTS_REQUEST_CODE);
                    }
                    bottomSheetDialog.hide();
                }

            });


            //A simple cancel does not create or inflate a fragment instance
            dialogView.findViewById(R.id.choice_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottomSheetDialog.hide();
                }
            });

            //Checked for compatibility with phone
            checkGalleryAppAvailability();
        });
        photoEdit.setOnClickListener(v1 -> {
//            View dialogView = getLayoutInflater().inflate(R.layout.bottom_sheet, null);
//            bottomSheetDialog = new BottomSheetDialog(getContext());
//            bottomSheetDialog.setContentView(dialogView);
//            bottomSheetDialog.show();
            System.out.println("Click is registered successfully");

            easyImage = new EasyImage.Builder(getContext())
                    .setChooserTitle("Pick media")
                    .setCopyImagesToPublicGalleryFolder(false)
//                .setChooserType(ChooserType.CAMERA_AND_DOCUMENTS)
                    .setChooserType(ChooserType.CAMERA_AND_GALLERY)
                    .setFolderName("EasyImage sample")
                    .allowMultiple(true)
                    .build();

            checkGalleryAppAvailability();
            });



//            popupWindow.getContentView().findViewById(R.id.camera_button).setOnClickListener(new View.OnClickListener() {



        //A save click saves the current state of the profile

        /*A ProfilePreferences instance is used to save concise profile-specific
        * data. This includes age, gender, height and weight */

        //The name of the user and his profile photo is saved as a user preference
        Button save = profileView.findViewById(R.id.save_profile);
        save.setOnClickListener(v -> {


            final String name = profileName.getText().toString();

            final String age = ageSpinner.getSelectedItem().toString();

            final String height = heightSpinner.getSelectedItem().toString();

            final String weight = weightSpinner.getSelectedItem().toString();

            final String gender = genderSpinner.getSelectedItem().toString();

            if(name == null || name.trim().length() == 0 ||
               age.equalsIgnoreCase("Select") || height.equalsIgnoreCase("Select") ||
               weight.equalsIgnoreCase("Select") || gender.equalsIgnoreCase("Select")){
                Common.okAlertMessage(getContext(), getString(R.string.enter_all_details));
                return;
            }


            ProfilePreferences profilePreferences = ProfilePreferences.getInstance
                    (getContext());

            UserPreferences userPreferences = UserPreferences.getInstance(getContext());
            userPreferences.saveName(name);

            ageIndex = ageSpinner.getSelectedItemPosition();
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.YEAR, -(Integer.parseInt(age)));
            Date date = calendar.getTime();

            profilePreferences.saveHeight(Float.parseFloat(height));
            heightIndex = heightSpinner.getSelectedItemPosition();

            profilePreferences.saveWeight(Float.parseFloat(weight));
            System.out.println("Got weight value-->"+ ProfilePreferences.
                    getInstance(getContext()).getWeight());
            weightIndex = weightSpinner.getSelectedItemPosition();

            profilePreferences.saveGender(gender);
            System.out.println("Got gender"+profilePreferences.getGender());
            genderIndex = genderSpinner.getSelectedItemPosition();


            String user_id = UserPreferences.getInstance(getContext()).getUserID();
            if(user_id.length() > 0){
                ProfileReq profileReq = new ProfileReq();
                profileReq.dob = date.getTime()/1000;
                profileReq.gender = gender;
                profileReq.weight = Float.parseFloat(weight);
                profileReq.height = Float.parseFloat(height);
                profileReq.name = name;
                updateProfile(user_id, profileReq);
            }


            /*On Entry to the edit popup, the last used image is restored
            * in a circular image view */
            try{
                String filePath = userPreferences.getProfilePhoto();
                if(!filePath.equalsIgnoreCase("default") && isUploadedPhoto){
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                    Bitmap bitmap = BitmapFactory.decodeFile(filePath, bitmapOptions);
                    photoEdit.setImageBitmap(bitmap);
                    FirebaseStorageManager.UploadBitmap(bitmap, user_id, new FirebaseStorageManager.UploadListener() {
                        @Override
                        public void onComplete(boolean isSuccess) {
                            Log.d("fileupload",isSuccess +"");
                        }
                    });
                    isUploadedPhoto = false;
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }

        });


        //The popup close button to the top right dismisses the popup window
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        MainActivity.shared().onBackPressed();
    }

    /**Manipulate one captured/selected image at a time
     *
     * @param returnedPhotos A one element array for receiving media,
     * */
    private void onPhotosReturned(@NonNull MediaFile[] returnedPhotos) {
        //Save the last chosen photo as a user preference
        photos.addAll(Arrays.asList(returnedPhotos));
        File fileNameOnDevice = FileUtilities.getProfileFile(getContext());
        File compressedImage = returnedPhotos[0].getFile();

        try {
            //If selected images's file size is more than 50KB then need to be compressed
            if (returnedPhotos[0].getFile().length()/1024 > 50){
                 compressedImage = new Compressor(getContext())
                        .setMaxHeight(100)
                        .setQuality(75)
                        .setCompressFormat(Bitmap.CompressFormat.WEBP)
                        .compressToFile(returnedPhotos[0].getFile());
            }
            if (fileNameOnDevice != null){
                Files.copy(compressedImage, fileNameOnDevice);
//                compressedImage.renameTo(fileNameOnDevice);
            }else{
                fileNameOnDevice = compressedImage;
            }
        } catch (IOException e) {
            fileNameOnDevice = returnedPhotos[0].getFile();
            e.printStackTrace();
        }




        UserPreferences userPreferences = UserPreferences.getInstance(getContext());
        userPreferences.saveProfilePhoto(fileNameOnDevice.getAbsolutePath());
        isUploadedPhoto = true;
        //Update the action bar with the currently chosen photo


        photoEdit.setImageBitmap(BitmapFactory.decodeFile(userPreferences.getProfilePhoto()));
    }

    private boolean arePermissionsGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(getContext(), permission) != PackageManager.PERMISSION_GRANTED)
                return false;
            System.out.println("Check self permission");
        }
        return true;
    }

    private void checkGalleryAppAvailability() {
        if (!easyImage.canDeviceHandleGallery()) {
            //Device has no app that handles gallery intent
            galleryButton.setVisibility(View.GONE);
        }
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
        this.mContext = context;
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;

        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    //Requests permissions from a permission string
    private void requestPermissionsCompat(String[] permissions, int requestCode) {
        ActivityCompat.requestPermissions(getActivity(), permissions, requestCode);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public void updateProfile(String user_id, ProfileReq firestoreProfileModel) {
        Map<String, Object> profile = new HashMap<>();
        profile.put(Constants.ProfileFields.USERNAME, firestoreProfileModel.name);
        profile.put(Constants.ProfileFields.WEIGHT, firestoreProfileModel.weight);
        profile.put(Constants.ProfileFields.HEIGHT, firestoreProfileModel.height);
        profile.put(Constants.ProfileFields.DOB, firestoreProfileModel.dob);
        profile.put(Constants.ProfileFields.GENDER, firestoreProfileModel.gender);

        Task<Void> newUser = firestoreDb.collection(Constants.PROFILE_COLLECTION).document(user_id).set(profile);
        newUser.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Profile was successfully added");
                //NavUtil.moveToNextPage(context, ProfileActivity.class, userEnteredName);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Error has occurred " + e.getMessage());
            }
        });

    }

//    public void getProfileByUserID(String user_id) {
//        DocumentReference docRef = firestoreDb.collection(Constants.PROFILE_COLLECTION).document(user_id);
//        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if(task.isSuccessful()){
//                    ProfileReq profile = Objects.requireNonNull(task.getResult()).toObject(ProfileReq.class);
//                    //Navigate to profile page
//                    if(profile != null){
//                        Toast.makeText(getContext(), "Profile already exist in the database ", Toast.LENGTH_SHORT).show();
//                    }else{
//                        Toast.makeText(getContext(), "Profile does not exists ", Toast.LENGTH_SHORT).show();
//                    }
//                }else{
//                    String excep = Objects.requireNonNull(task.getException()).getMessage();
//                    Log.d(TAG, "Error reading user data " + excep);
//                }
//            }
//        });
//    }

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
