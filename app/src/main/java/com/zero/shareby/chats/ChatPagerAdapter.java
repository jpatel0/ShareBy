package com.zero.shareby.chats;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


public class ChatPagerAdapter extends FragmentStatePagerAdapter {

    public ChatPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                return new GroupChatFragment();

            case 1:
                return new RecentChats();

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch(position){
            case 0:
                return "Group Chat";

            case 1:
                return "Recent Chats";

            default:
                return "default";
        }
    }
}
