package com.example.chatapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.database.ChildEventListener;
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
public class ChatsFragment extends Fragment {
    private RecyclerView mConvList;
    private DatabaseReference mConvDatabase;
    private DatabaseReference mMessageDatabase;
    private DatabaseReference mUserDatabase;

    private FirebaseAuth mAuth;
    private String mCurent_user_id;

    private View mMainView;
    private LinearLayoutManager linearLayoutManager;

    private Query conversationQuery;

    private FirebaseRecyclerOptions<Conv> options;
    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;

    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.fragment_chats, container, false);
        mConvList = mMainView.findViewById(R.id.conv_list);
        mAuth = FirebaseAuth.getInstance();

        mCurent_user_id = mAuth.getCurrentUser().getUid();
        mConvDatabase = FirebaseDatabase.getInstance().getReference().child("Chat").child(mCurent_user_id);
        mConvDatabase.keepSynced(true);

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mMessageDatabase = FirebaseDatabase.getInstance().getReference().child("messages").child(mCurent_user_id);
        mUserDatabase.keepSynced(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

      //  mConvList.setHasFixedSize(true);
        mConvList.setLayoutManager(linearLayoutManager);

        conversationQuery = mConvDatabase.orderByChild("timestamp");
        conversationQuery.keepSynced(true);
        //query = FirebaseDatabase.getInstance().getReference("Users");
        options = new FirebaseRecyclerOptions.Builder<Conv>().setQuery(conversationQuery, Conv.class).build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Conv, ChatsFragment.ConvViewHolder>(
                options
        ) {

            @NonNull
            @Override
            public ChatsFragment.ConvViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_single_layout, parent, false);
                return new ChatsFragment.ConvViewHolder(view);

            }

            @Override
            protected void onBindViewHolder(@NonNull final ChatsFragment.ConvViewHolder holder, int position, @NonNull final Conv model) {
                // holder.setDate(model.getDate());
                final String list_user_id = getRef(position).getKey();

                Query lastMessageQuery = mMessageDatabase.child(list_user_id).limitToLast(1);

                lastMessageQuery.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        String data = dataSnapshot.child("message").getValue().toString();
                        holder.setMessage(data,model.isSeen());
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                mUserDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final String userName = dataSnapshot.child("name").getValue().toString();
                        String userThumb = dataSnapshot.child("thumb_image").getValue().toString();

                        if(dataSnapshot.hasChild("online")){
                            String userOnline = dataSnapshot.child("online").getValue().toString();
                            holder.setUserOnline(userOnline);
                        }
                        holder.setName(userName);
                        holder.setUserImage(userThumb);

                        holder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent chatIntent = new Intent(getContext(),ChatActivity.class);
                                chatIntent.putExtra("user_id",list_user_id);
                                chatIntent.putExtra("username",holder.userNameView.getText().toString());
                                startActivity(chatIntent);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



            }
        };

        mConvList.setAdapter(firebaseRecyclerAdapter);
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

    public static class ConvViewHolder extends RecyclerView.ViewHolder{
        View mView;
        TextView userStatusView;
        TextView userNameView;
        CircleImageView userImageView;
        ImageView userOnlineView;


                public ConvViewHolder(@NonNull View itemView) {
                    super(itemView);
                    mView = itemView;
                }

                public void setMessage(String message,boolean isSeen){
                    userStatusView = mView.findViewById(R.id.user_single_status);
                    userStatusView.setText(message);
                    if(!isSeen){
                        userStatusView.setTypeface(userStatusView.getTypeface(), Typeface.BOLD);
                    }
                    else{
                        userStatusView.setTypeface(userStatusView.getTypeface(), Typeface.NORMAL);
                    }
                }

                public void setName(String name){
                    userNameView = mView.findViewById(R.id.user_single_name);
                    userNameView.setText(name);
                }

                public void setUserImage(String thumb_image){
                    userImageView  = mView.findViewById(R.id.user_single_image);
                    Picasso.get().load(thumb_image).placeholder(R.drawable.ic_launcher_background).into(userImageView);
                }

                public void setUserOnline(String online_status){
                    userOnlineView  = mView.findViewById(R.id.user_single_icon);
                    if(online_status.equals("true")){
                        userOnlineView.setVisibility(View.VISIBLE);
                    }
                    else{
                        userOnlineView.setVisibility(View.INVISIBLE);
                    }

                }


            }


        }


