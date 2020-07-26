package com.example.chatapp;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Messages> mMessageList;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference mRoot;

    public MessageAdapter(List<Messages> mMessageList) {
        this.mMessageList = mMessageList;
    }

    @NonNull
    @Override
    public MessageAdapter.MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_single_layout,parent,false);
        mRoot = FirebaseDatabase.getInstance().getReference();
        return new MessageViewHolder(v);

        //return null;
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{
        public TextView messageText;
        public CircleImageView profileImage;
        public ImageView messageImage;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_text_layout);
            profileImage = itemView.findViewById(R.id.message_profile_layout);
            messageImage = itemView.findViewById(R.id.message_image_layout);
        }


    }

    @Override
    public void onBindViewHolder(@NonNull final MessageAdapter.MessageViewHolder holder, int position) {

                        String current_user_id = mAuth.getCurrentUser().getUid();

     //   Toast.makeText(MessageAdapter.this,current_user_id,Toast.LENGTH_SHORT).show();
        Log.d("current_user",current_user_id);
                         Messages c=mMessageList.get(position);
                         String from_user = c.getFrom();
                         String message_type = c.getType();

                         mRoot.child("Users").child(from_user).addValueEventListener(new ValueEventListener() {
                             @Override
                             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                 String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();
                                 Picasso.get().load(thumb_image).placeholder(R.drawable.ic_launcher_background).into(holder.profileImage);

                             }

                             @Override
                             public void onCancelled(@NonNull DatabaseError databaseError) {

                             }
                         });



        if(message_type.equals("text")){

                         if(from_user!=null && current_user_id!=null) {

                             if (from_user.equals(current_user_id)) {
                                 holder.messageText.setBackgroundColor(Color.WHITE);
                                 holder.messageText.setTextColor(Color.BLACK);
                             } else {
                                 holder.messageText.setBackgroundResource(R.drawable.message_text_background);
                                 holder.messageText.setTextColor(Color.WHITE);
                             }
                         }
                         holder.messageImage.setVisibility(View.INVISIBLE);

                         holder.messageText.setText(c.getMessage()); }
                         else{
                             holder.messageText.setVisibility(View.INVISIBLE);
                             Picasso.get().load(c.getMessage()).placeholder(R.drawable.ic_launcher_background).into(holder.messageImage);
                         }

    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }


}
