package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
   private TextView mProfileName,mProfileStatus,mProfileFriendsCount;
   private Button mProfileSendRequestBtn;
   private ImageView mProfileImage;

   private DatabaseReference mUsersDatabase;
   private ProgressDialog mProgressDialog;

   private DatabaseReference mFriendReqDatabase;
   private FirebaseUser mCurrent_user;
   private DatabaseReference mFriendDatabse;
   private DatabaseReference mNotificationDatabse;

    private DatabaseReference mUserRef;
    private FirebaseAuth mAuth;

   private Button mDeclineBtn;

   private String mCurrent_state;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final String user_id = getIntent().getStringExtra("user_id");

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mFriendReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mFriendDatabse = FirebaseDatabase.getInstance().getReference().child("Friends");
        mNotificationDatabse = FirebaseDatabase.getInstance().getReference().child("notifications");
        mAuth=FirebaseAuth.getInstance();
        mUserRef  = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();

        mProfileImage = findViewById(R.id.profile_image);
        mProfileName = findViewById(R.id.profile_displayName);
        mProfileSendRequestBtn = findViewById(R.id.profile_send_req_btn);
        mProfileStatus = findViewById(R.id.profile_status);
        mProfileFriendsCount = findViewById(R.id.profile_totalFriends);
        mDeclineBtn = findViewById(R.id.profile_decline_btn);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Loading User Data");
        mProgressDialog.setMessage("Please wait while we load user data");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        mCurrent_state="not_friends";



        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String display_name = dataSnapshot.child("name").getValue().toString();
                String status= dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                mProfileName.setText(display_name);
                mProfileStatus.setText(status);
                Picasso.get().load(image).placeholder(R.drawable.ic_launcher_background).into(mProfileImage);

                //freind list/Request feature

                mFriendReqDatabase.child(mCurrent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                          if(dataSnapshot.hasChild(user_id)){
                              String req_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();
                              if(req_type.equals("received")){
                                  mCurrent_state="req_received";
                               //   mProfileSendRequestBtn.setEnabled(true);
                                  mProfileSendRequestBtn.setText("Accept Friend Request");

                                  mDeclineBtn.setVisibility(View.VISIBLE);
                                  mDeclineBtn.setEnabled(true);
                              }
                              else if(req_type.equals("sent")){
                                  mCurrent_state="req_sent";
                                  mProfileSendRequestBtn.setText("Cancel Friend Request");

                                  mDeclineBtn.setVisibility(View.INVISIBLE);
                                  mDeclineBtn.setEnabled(false);
                              }

                              else{
                                  mFriendDatabse.child(mCurrent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                      @Override
                                      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                          if(dataSnapshot.hasChild(user_id)){
                                              mCurrent_state="friends";
                                            //  mProfileSendRequestBtn.setEnabled(true);
                                              mProfileSendRequestBtn.setText("Unfriend The User");

                                              mDeclineBtn.setVisibility(View.INVISIBLE);
                                              mDeclineBtn.setEnabled(false);
                                          }
                                      }

                                      @Override
                                      public void onCancelled(@NonNull DatabaseError databaseError) {

                                      }
                                  });
                              }
                          }
                        mProgressDialog.dismiss();
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });







            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mFriendDatabse.child(mCurrent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(user_id)){
                    mCurrent_state="friends";
                    //  mProfileSendRequestBtn.setEnabled(true);
                    mProfileSendRequestBtn.setText("Unfriend The User");

                    mDeclineBtn.setVisibility(View.INVISIBLE);
                    mDeclineBtn.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

      mProfileSendRequestBtn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              mProfileSendRequestBtn.setEnabled(false);

              //Not friend state
              if(mCurrent_state.equals("not_friends")){
                  mFriendReqDatabase.child(mCurrent_user.getUid()).child(user_id).child("request_type").setValue("sent")
                          .addOnCompleteListener(new OnCompleteListener<Void>() {
                              @Override
                              public void onComplete(@NonNull Task<Void> task) {
                                  if(task.isSuccessful()){
                                      mFriendReqDatabase.child(user_id).child(mCurrent_user.getUid())
                                              .child("request_type").setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                          @Override
                                          public void onSuccess(Void aVoid) {
                                            Toast.makeText(ProfileActivity.this,"Request Send Succesfully",Toast.LENGTH_SHORT).show();
                                              HashMap<String,String> notificationsData = new HashMap<>();
                                              notificationsData.put("from",mCurrent_user.getUid());
                                              notificationsData.put("type","request");

                                            mNotificationDatabse.child(user_id).push().setValue(notificationsData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    mCurrent_state="req_sent";
                                                    mProfileSendRequestBtn.setEnabled(true);
                                                    mProfileSendRequestBtn.setText("Cancel Friend Request");

                                                    mDeclineBtn.setVisibility(View.INVISIBLE);
                                                    mDeclineBtn.setEnabled(false);
                                                }
                                            });


                                          }
                                      });


                                  }
                                  else{
                                      mProfileSendRequestBtn.setEnabled(true);
                                      Toast.makeText(ProfileActivity.this,"Failed to send Request",Toast.LENGTH_SHORT).show();
                                  }
                              }
                          });
              }

              //cancel request state

              if(mCurrent_state.equals("req_sent")){
                  mFriendReqDatabase.child(mCurrent_user.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                      @Override
                      public void onSuccess(Void aVoid) {
                          mFriendReqDatabase.child(user_id).child(mCurrent_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                              @Override
                              public void onSuccess(Void aVoid) {
                                  mCurrent_state="not_friends";
                                  mProfileSendRequestBtn.setEnabled(true);
                                  mProfileSendRequestBtn.setText("Send Friend Request");

                              }
                          });
                      }
                  });
              }

              // req received state
              if(mCurrent_state.equals("req_received")){
                  final String currentDate = DateFormat.getDateTimeInstance().format(new Date());
                  mFriendDatabse.child(mCurrent_user.getUid()).child(user_id).child("Date").setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                      @Override
                      public void onSuccess(Void aVoid) {
                          mFriendDatabse.child(user_id).child(mCurrent_user.getUid()).child("Date").setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                              @Override
                              public void onSuccess(Void aVoid) {
                                  mFriendReqDatabase.child(mCurrent_user.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                      @Override
                                      public void onSuccess(Void aVoid) {
                                          mFriendReqDatabase.child(user_id).child(mCurrent_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                              @Override
                                              public void onSuccess(Void aVoid) {
                                                  mCurrent_state="friends";
                                                  mProfileSendRequestBtn.setEnabled(true);
                                                  mProfileSendRequestBtn.setText("Unfriend The User");

                                                  mDeclineBtn.setVisibility(View.INVISIBLE);
                                                  mDeclineBtn.setEnabled(false);
                                              }
                                          });
                                      }
                                  });
                              }
                          });
                      }
                  });
              }

              //unfriend;
              if(mCurrent_state.equals("friends")){
                  Map unfriendMap = new HashMap();
                  unfriendMap.put("Friends/"+mCurrent_user.getUid()+"/"+user_id,null);
                  unfriendMap.put("Friends/"+user_id+"/"+mCurrent_user.getUid(),null);

                  mFriendDatabse.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
                      @Override
                      public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                          if(databaseError == null){
                              mProfileSendRequestBtn.setEnabled(true);
                              mCurrent_state="not_friends";
                              mProfileSendRequestBtn.setText("Send Friend Request ");
                              mDeclineBtn.setEnabled(false);
                              //<include layout="@layout/app_bar_layout" android:id="@+id/users_appBar"/>

                          }
                      }
                  });

              }


          }
      });


    }

    @Override
    protected void onStart() {
        super.onStart();
        //  FirebaseAuth.getInstance().signOut();
        FirebaseAuth m1=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = m1.getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();


        if(firebaseUser!=null) {
            databaseReference.child("Users").child(m1.getCurrentUser().getUid()).child("online").setValue("true");
            databaseReference.child("Users").child(m1.getCurrentUser().getUid()).child("lastSeen").setValue(ServerValue.TIMESTAMP);
        }




    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseAuth m1=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = m1.getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();


        if(firebaseUser!=null) {
            databaseReference.child("Users").child(m1.getCurrentUser().getUid()).child("online").setValue("false");
            databaseReference.child("Users").child(m1.getCurrentUser().getUid()).child("lastSeen").setValue(ServerValue.TIMESTAMP);
        }

    }
}
