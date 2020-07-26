package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {
   // Button login,register;
private Toolbar mToolbar;
    FirebaseUser firebaseUser;

    //this is to make tabs that we usually see on whatsapp(chats,status and calls)
    //it is made through fragemnts

    private ViewPager mViewPager;
  //  private SectionsPageAdapter mSectionPagerAdapter;
    private DatabaseReference mUserRef;
    private FirebaseAuth mAuth;

    private TabsAccessAdapter tabsAccessAdapter;

    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Welcome to chat App");

        mAuth=FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()!=null)
        mUserRef  = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());


         mViewPager = findViewById(R.id.main_tabPager);
         mTabLayout=(TabLayout) findViewById(R.id.main_tabs);


         //mSectionPagerAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        tabsAccessAdapter = new TabsAccessAdapter(getSupportFragmentManager());







         //setting adapter to view pager
         mViewPager.setAdapter(tabsAccessAdapter);

         //we save table layout with view pager used for scrolling through to same page
         mTabLayout.setupWithViewPager(mViewPager);



        FirebaseMessaging.getInstance().subscribeToTopic("genral").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                String message = "Successful";
                if(!task.isSuccessful()){
                    message = "failed";
                }
            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();
      //  FirebaseAuth.getInstance().signOut();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();


      if(firebaseUser==null){
          sendToStart();
        }
      else{

          FirebaseAuth m1=FirebaseAuth.getInstance();
          FirebaseUser firebaseUser = m1.getCurrentUser();
          DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();


          if(firebaseUser!=null) {
              databaseReference.child("Users").child(m1.getCurrentUser().getUid()).child("online").setValue("true");
              databaseReference.child("Users").child(m1.getCurrentUser().getUid()).child("lastSeen").setValue(ServerValue.TIMESTAMP);
          }

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

    void sendToStart(){
        Intent intent = new Intent(MainActivity.this,StartActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
         super.onOptionsItemSelected(item);

         if(item.getItemId() == R.id.main_logout_btn){
             FirebaseAuth.getInstance().signOut();
             sendToStart();
         }

         if(item.getItemId() ==R.id.main_settings_btn){
             Intent settingsIntent = new Intent(MainActivity.this,SettingsActivity.class);
             startActivity(settingsIntent);
         }

        if(item.getItemId() ==R.id.main_all_btn){
            Intent settingsIntent = new Intent(MainActivity.this,UsersActivity.class);
            startActivity(settingsIntent);
        }

         return true;

    }

}
