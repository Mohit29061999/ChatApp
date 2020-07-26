package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.squareup.picasso.Picasso.*;

public class UsersActivity extends AppCompatActivity {
   private Toolbar mToolbar;
   private RecyclerView mUsersList;
   private DatabaseReference mUsersDatabase;
   private Query query;

    private FirebaseRecyclerOptions<Users> options;
    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;

    private DatabaseReference mUserRef;
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

       mToolbar =findViewById(R.id.users_appBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mAuth=FirebaseAuth.getInstance();
        mUserRef  = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());



        mUsersList = findViewById(R.id.users_list);

     //   mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(new LinearLayoutManager(this));

        query = FirebaseDatabase.getInstance().getReference("Users");

         options = new FirebaseRecyclerOptions.Builder<Users>().setQuery(query,Users.class).build();
         firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(
                options
        ) {
            @Override
            protected void onBindViewHolder(@NonNull UsersViewHolder holder, int position, @NonNull Users model) {
                holder.setName(model.getName());
                holder.setStatus(model.getStatus());
                holder.setUserImage(model.getThumb_image());

                final String user_id = getRef(position).getKey();


                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent profileIntent = new Intent(UsersActivity.this,ProfileActivity.class);
                        profileIntent.putExtra("user_id",user_id);
                        startActivity(profileIntent);
                    }
                });


            }

            @NonNull
            @Override
            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_single_layout,parent,false);
                return new UsersViewHolder(view);
            }
        };

        mUsersList.setAdapter(firebaseRecyclerAdapter);


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseAuth m1=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = m1.getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();


        if(firebaseUser!=null) {
            databaseReference.child("Users").child(m1.getCurrentUser().getUid()).child("online").setValue("true");
            databaseReference.child("Users").child(m1.getCurrentUser().getUid()).child("lastSeen").setValue(ServerValue.TIMESTAMP);
        }




        firebaseRecyclerAdapter.startListening();




    }

    @Override
    protected void onStop() {
        super.onStop();

        firebaseRecyclerAdapter.stopListening();

        FirebaseAuth m1=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = m1.getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();


        if(firebaseUser!=null) {
            databaseReference.child("Users").child(m1.getCurrentUser().getUid()).child("online").setValue("false");
            databaseReference.child("Users").child(m1.getCurrentUser().getUid()).child("lastSeen").setValue(ServerValue.TIMESTAMP);
        }






    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setName(String name){
            TextView userNameView = mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);
        }


        public void setStatus(String status){
            TextView userStatusView = mView.findViewById(R.id.user_single_status);
            userStatusView.setText(status);
        }

        public void setUserImage(String thumb_image){
            CircleImageView userImageView = mView.findViewById(R.id.user_single_image);
            Picasso.get().load(thumb_image).placeholder(R.drawable.ic_launcher_background).into(userImageView);

        }
    }
}
