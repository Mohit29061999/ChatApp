package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

class SectionsPageAdapter extends FragmentPagerAdapter {
    public SectionsPageAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                RequestsFragment requestsFragment = new RequestsFragment();
                return requestsFragment;
            case 1:
                ChatsFragment chatsFragment = new ChatsFragment();
                return chatsFragment;
            case 2:
                FriendsFragment friendsFragment = new FriendsFragment();
                return friendsFragment;
            default:
                return null;


        }
       // return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    public CharSequence getPageTitle(int position){
        switch (position){
            case 0:
                return "REQUESTS";
            case 1:
                return "Chats";
            case 2:
                return "Friends";
            default:
                return null;

        }
    }
}
