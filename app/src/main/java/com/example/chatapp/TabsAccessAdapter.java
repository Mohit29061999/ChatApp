package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TabsAccessAdapter extends FragmentPagerAdapter {

    public TabsAccessAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                //  Fragment friends_fragment = new FriendsFragment();
                RequestsFragment requestsFragment = new RequestsFragment();
                return requestsFragment;

            case 1:
                //  Fragment friends_fragment = new FriendsFragment();
                ChatsFragment chatFragment = new ChatsFragment();
                return chatFragment;

            case 2:
                //  Fragment friends_fragment = new FriendsFragment();
                FriendsFragment friendsFragment = new FriendsFragment();
                return friendsFragment;

            default:
                return null;


        }

    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                //  Fragment friends_fragment = new FriendsFragment();
                //RequestsFragment requestsFragment = new RequestsFragment();
                return "RequestsFragment";

            case 1:
                //  Fragment friends_fragment = new FriendsFragment();
                //ChatsFragment chatFragment = new ChatsFragment();
                return "ChatFragment";

            case 2:
                //  Fragment friends_fragment = new FriendsFragment();
                // FriendsFragment friendsFragment = new FriendsFragment();
                return "FriendsFragment";

            default:
                return null;
        }
    }


}
