package com.decalthon.helmet.stability.firestore;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.util.Log;
import androidx.annotation.NonNull;

import com.decalthon.helmet.stability.Utilities.Constants;
import com.decalthon.helmet.stability.Utilities.FileUtilities;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;


public class FirebaseStorageManager {

    public interface UploadListener {
        void onComplete(boolean isSuccess);
    }

    public interface DownloadListener {
        void onComplete(boolean isSuccess, String filepath);
    }

    /**
     * Upload the image file to cloud storage
     * @param bitmap        image file
     * @param userid user of user
     * @param completionHandler // Callback methods for success/failure upload
     */
    public static void UploadBitmap(Bitmap bitmap, String userid, final UploadListener completionHandler) {

        if (bitmap == null) {
            completionHandler.onComplete(false);
        }
        final String serverFileName = userid.concat(".png");
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        // Create a reference to the file you want to upload
        String directory = Constants.PHOTO_DIR + "/";
        final StorageReference fileRef = storageRef.child(directory + serverFileName);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] data = stream.toByteArray();

        UploadTask uploadTask = fileRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.e("oops","error in bitmap uploading");
                completionHandler.onComplete(false);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                completionHandler.onComplete(true);
            }
        });

        // Now we need to download url of uploaded file using a separate task
//        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//            @Override
//            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//                if (!task.isSuccessful()) {
//                    throw task.getException();
//                }
//                // Continue with the task to get the download URL
//                return fileRef.getDownloadUrl();
//            }
//        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//            @Override
//            public void onComplete(@NonNull Task<Uri> task) {
//                if (task.isSuccessful()) {
//                    Uri downloadUri = task.getResult();
//                    String stringUrl = downloadUri.toString();
//                    completionHandler.onComplete(true, stringUrl);
//                } else {
//                    // Handle failures
//                    completionHandler.onComplete(false, "");
//                }
//            }
//        });
    }

    /**
     * Download profile image of user
     * @param context
     * @param userid user id of user
     * @param completionHandler callback method success/failure of method
     */
    public static void downloadImage(Context context, String userid, final DownloadListener completionHandler){
        final String serverFileName = userid.concat(".png");
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        // Create a reference to the file you want to upload
        String directory = Constants.PHOTO_DIR + "/";
        final StorageReference fileRef = storageRef.child(directory + serverFileName);

//        File root = null;
//        try {
//            String path = context.getPackageName() + File.separator + Constants.PHOTO_DIR;
//            root = FileUtilities.createDirIfNotExists(path);
//            if(root==null){
//                completionHandler.onComplete(false, "");
//                return;
//            }else {
//                // code to make the files visible on the PC using MTP protocol
//                if(root.setExecutable(true)&&root.setReadable(true)&&root.setWritable(true)){
//                    Log.i("SET FILE PERMISSION ", "Set read , write and execuatable permission for log file");
//                }
//
//                MediaScannerConnection.scanFile(context, new String[]{root.toString()}, null, null);
//            }
//
//        }catch (Exception ex){
//            completionHandler.onComplete(false, "");
//        }
//
//        File fileNameOnDevice = new File(root, Constants.PROFILE_PIC);
        File fileNameOnDevice = FileUtilities.getProfileFile(context);
        if(fileNameOnDevice == null){
            completionHandler.onComplete(false, "");
            return;
        }

        fileRef.getFile(fileNameOnDevice).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Log.e("firebase ",";local tem file created  created " +fileNameOnDevice.toString());
                completionHandler.onComplete(true, fileNameOnDevice.getAbsolutePath());
                //  updateDb(timestamp,localFile.toString(),position);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("firebase ",";local tem file not created  created " +exception.toString());
                completionHandler.onComplete(false, "");
            }
        });

    }

    /**
     * Upload non image file to cloud storage
     * @param file non image file
     * @param serverFileName file name will be in cloud storage after successfully uploaded
     * @param completionHandler callbacks method for success/failure upload
     */
    public static void UploadFile(File file, String serverFileName, final UploadListener completionHandler) {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        // Create a reference to the file you want to upload
        String directory = Constants.UPLOAD_DIR + "/";
        final StorageReference fileRef = storageRef.child(directory + serverFileName);

        try {
            InputStream stream = new FileInputStream(file);
            UploadTask uploadTask = fileRef.putStream(stream);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    Log.e("oops","error in file uploading");
                    completionHandler.onComplete(false);
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                }
            });
//            // Now we need to download url of uploaded file using a separate task
//            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//                @Override
//                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//                    if (!task.isSuccessful()) {
//                        throw task.getException();
//                    }
//                    // Continue with the task to get the download URL
//                    return fileRef.getDownloadUrl();
//                }
//            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//                @Override
//                public void onComplete(@NonNull Task<Uri> task) {
//                    if (task.isSuccessful()) {
//                        Uri downloadUri = task.getResult();
//                        String stringUrl = downloadUri.toString();
//                        completionHandler.onComplete(true, stringUrl);
//                    } else {
//                        // Handle failures
//                        completionHandler.onComplete(false, "");
//                    }
//                }
//            });
        }catch (Exception e){
            completionHandler.onComplete(false);
        }
    }

}
