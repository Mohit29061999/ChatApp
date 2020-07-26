package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private TextInputLayout mStatus;
    private Button mSaveButton;
    private DatabaseReference mStatusDatabase;
    private FirebaseUser mCurrentUser;

    //progress
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);


        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();

        String status_value = getIntent().getStringExtra("status_value");


        mStatusDatabase= FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);


        mToolbar = findViewById(R.id.status_appBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mStatus  = findViewById(R.id.status_input);
        mSaveButton = findViewById(R.id.status_save_button);

        mStatus.getEditText().setText(status_value);




        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgress = new ProgressDialog(StatusActivity.this);
                mProgress.setTitle("Saving Changes");
                mProgress.setMessage("please wait while we are saving changes");
                mProgress.show();
                String status = mStatus.getEditText().getText().toString();
                mStatusDatabase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            mProgress.dismiss();
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"Some error occured",Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });
    }
}
