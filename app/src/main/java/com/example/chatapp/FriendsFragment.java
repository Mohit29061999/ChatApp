package com.example.chatapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {
    private RecyclerView mFriendsList;

    private DatabaseReference mFriendDatabase;
    private DatabaseReference mUserDatabase;
    private FirebaseAuth mAuth;

    private String mCurrent_user_id;
    private View mMainView;

    private Query query;

    private FirebaseRecyclerOptions<Friends> options;
    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;

    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView =  inflater.inflate(R.layout.fragment_friends, container, false);

        mFriendsList = mMainView.findViewById(R.id.friends_list);
        mAuth = FirebaseAuth.getInstance();

        mCurrent_user_id = mAuth.getCurrentUser().getUid();
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrent_user_id);
       mFriendDatabase.keepSynced(true);
        mUserDatabase = FirebaseDatabase.getInstance().getReference("Users");
        mUserDatabase.keepSynced(true);

       // mFriendsList.setHasFixedSize(true);
        mFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));

        //query = FirebaseDatabase.getInstance().getReference("Friends").child(mCurrent_user_id).child("Date");
        query = FirebaseDatabase.getInstance().getReference("Friends").child(mCurrent_user_id);
        query.keepSynced(true);
        //query = FirebaseDatabase.getInstance().getReference("Users");
        options = new FirebaseRecyclerOptions.Builder<Friends>().setQuery(query,Friends.class).build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(
                options
        ) {

            @NonNull
            @Override
            public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_single_layout,parent,false);
                return new FriendsViewHolder(view);

            }

            @Override
            protected void onBindViewHolder(@NonNull final FriendsViewHolder holder, int position, @NonNull Friends model) {
               // holder.setDate(model.getDate());
                 final String user_id = getRef(position).getKey();


                 mFriendDatabase.child(user_id).addValueEventListener(new ValueEventListener() {
                     @Override
                     public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                         String date = dataSnapshot.child("Date").getValue().toString();
                         holder.setDate(date);
                     }

                     @Override
                     public void onCancelled(@NonNull DatabaseError databaseError) {

                     }
                 });

                 mUserDatabase.child(user_id).addValueEventListener(new ValueEventListener() {
                     @Override
                     public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String username= dataSnapshot.child("name").getValue().toString();

                         String status = dataSnapshot.child("status").getValue().toString();
                         String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();



                         holder.setname(username);
                         holder.setimage(thumb_image);

                         if(dataSnapshot.hasChild("online")) {
                             String userOnline = dataSnapshot.child("online").getValue().toString();
                             holder.setUserOnline(userOnline);
                         }





                     }

                     @Override
                     public void onCancelled(@NonNull DatabaseError databaseError) {

                     }
                 });

              //  holder.setDate(model.getDate());
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CharSequence options[] = new CharSequence[]{"Open Profile","Send message"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Select Options");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //click event for each item
                            if(which==0){
                                Intent profileIntent = new Intent(getContext(),ProfileActivity.class);
                                profileIntent.putExtra("user_id",user_id);
                                startActivity(profileIntent);
                            }
                            if(which==1){
                                Intent chatIntent = new Intent(getContext(),ChatActivity.class);
                                chatIntent.putExtra("user_id",user_id);
                                chatIntent.putExtra("username",holder.name.getText().toString());
                                startActivity(chatIntent);
                            }


                        }
                    });
                    builder.show();
                }
            });


            }
        };

        mFriendsList.setAdapter(firebaseRecyclerAdapter);


        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseRecyclerAdapter.startListening();

    }

    @Override
    public void onStop() {
        super.onStop();
        firebaseRecyclerAdapter.stopListening();
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public TextView name;
        private CircleImageView image;
        private TextView userNameView;
        private ImageView userOnline;

        public FriendsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

        }

        public void setDate(String date){
            userNameView = mView.findViewById(R.id.user_single_status);
            userNameView.setText(date);
        }
        public void setname(String name1){
             name = mView.findViewById(R.id.user_single_name);
            name.setText(name1);
        }
        public void setimage(String name1){
           // TextView name = mView.findViewById(R.id.user_single_name);
            image = mView.findViewById(R.id.user_single_image);
            if(!name1.equals("default")){
                Picasso.get().load(name1).placeholder(R.drawable.ic_launcher_background).into(image);
            }


        }

        public void setUserOnline(String online_status){
            userOnline = mView.findViewById(R.id.user_single_icon);

            if(online_status.equals("true")){
                userOnline.setVisibility(View.VISIBLE);
            }
            else{
                userOnline.setVisibility(View.INVISIBLE);
            }

        }
    }
}
