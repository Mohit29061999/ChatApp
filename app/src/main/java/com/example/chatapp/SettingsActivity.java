package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import okhttp3.Dispatcher;

public class SettingsActivity extends AppCompatActivity {

    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;

    private CircleImageView mDisplayImage;
    private TextView mName;
    private TextView mStatus;

    private Button mStatusButton;
    private Button mImageButton;

    private static final int GALLERY_PICK =1;

    private StorageReference mImageStorage;
    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mDisplayImage = findViewById(R.id.settings_image);
        mName = findViewById(R.id.display_name);
        mStatus = findViewById(R.id.settings_status);

      mStatusButton = findViewById(R.id.settings_status_btn);
      mImageButton = findViewById(R.id.settings_image_btn);

        mCurrentUser= FirebaseAuth.getInstance().getCurrentUser();

        String current_uid = mCurrentUser.getUid();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        mUserDatabase.keepSynced(true);

        mImageStorage = FirebaseStorage.getInstance().getReference();

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                final String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                mName.setText(name);
                mStatus.setText(status);
                if(!image.equals("default"))
                Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.ic_launcher_background).into(mDisplayImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(image)
                                .placeholder(R.drawable.ic_launcher_background).into(mDisplayImage);
                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status_value = mStatus.getText().toString();


                Intent status_intent = new Intent(getApplicationContext(),StatusActivity.class);
                status_intent.putExtra("status_value",status_value);
                startActivity(status_intent);
            }
        });

        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent,"SELECT IMAGE"),GALLERY_PICK);


             //   CropImage.activity().setGuidelines(CropImageview.Guidelines.ON).start(SettingsActivity.this);

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_PICK && resultCode==RESULT_OK){
            Uri imageUri = data.getData();
            CropImage.activity(imageUri).setAspectRatio(1,1).start(this);

            //Toast.makeText(SettingsActivity.this,imageUri,Toast.LENGTH_SHORT).show();
        }
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mProgressDialog = new ProgressDialog(SettingsActivity.this);
                mProgressDialog.setTitle("Uploading image");
                mProgressDialog.setMessage("Please wait while we are uploading image");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();

                final Uri resultUri = result.getUri();

                File thumb_path = new File(resultUri.getPath());

                String current_user_id = mCurrentUser.getUid();

                try {
                    Bitmap thumb_bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);
                    final byte[] thumb_byte = baos.toByteArray();

                    final StorageReference filepath = mImageStorage.child("profile_images").child(current_user_id + ".jpg");
                    final StorageReference thumb_filepath = mImageStorage.child("profile_images").child("thumbs").child(current_user_id + ".jpg");


                    filepath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                            // String download_url = taskSnapshot.getStorage().getDownloadUrl().toString();
                            // mDisplayImage.setImageURI(resultUri);
                            //     Task<Uri> downloadUri = taskSnapshot.getStorage().getDownloadUrl();

                            //     String download_url  = downloadUri.getResult().toString();
                            taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    final String download_url = task.getResult().toString();
                                  //  Toast.makeText(getApplicationContext(), download_url, Toast.LENGTH_SHORT).show();
                                    mUserDatabase.child("image").setValue(download_url).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                mProgressDialog.dismiss();

                                                UploadTask uploadTask =thumb_filepath.putBytes(thumb_byte);
                                                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                                        taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Uri> task) {
                                                                if(task.isSuccessful()) {
                                                                    String thumb_downloadUrl = task.getResult().toString();
                                                                    Map update_hashMap = new HashMap();
                                                                    update_hashMap.put("image", download_url);
                                                                    update_hashMap.put("thumb_image", thumb_downloadUrl);

                                                                    mUserDatabase.updateChildren(update_hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if(task.isSuccessful()){
                                                                          //      Toast.makeText(getApplicationContext(),"success",Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }
                                                                    });
                                                                }

                                                            }
                                                        });
                                                    }
                                                });

                                                //  mProgressDialog.dismiss();
                                            }
                                            else{
                                                Toast.makeText(getApplicationContext(),"Some error occurred",Toast.LENGTH_SHORT).show();
                                                mProgressDialog.dismiss();

                                            }
                                        }

                                    });
                                }
                            });


                            //    mUserDatabase
                        }
                    });


                }


                //  mImageStorage =


                catch (Exception e) {
                    e.printStackTrace();
                }
            }

                  else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                    Toast.makeText(getApplicationContext(), "some error occured", Toast.LENGTH_SHORT).show();
                }
            //   Bitmap thumb_bitmap =  Compressor.compress(this,resultUri);





        }

    }
    public static String random(){
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(10);
        char tempChar;
        for(int i=0;i<randomLength;i++){
            tempChar = (char) (generator.nextInt(96)+32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }
}






/* filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"failed to load image",Toast.LENGTH_SHORT).show();

                        }
                        else{
                           // filepath.getDownloadUrl()
                            String download_url = filepath.getDownloadUrl().toString();
                            mUserDatabase.child("image").setValue(download_url).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        mProgressDialog.dismiss();
                                    }
                                }
                            });
                           // Toast.makeText(getApplicationContext(),"done",Toast.LENGTH_SHORT).show();

                        }
                    }
                }); */
