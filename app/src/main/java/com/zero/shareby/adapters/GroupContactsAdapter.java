package com.zero.shareby.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zero.shareby.R;
import com.zero.shareby.Utils.UserDetails;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupContactsAdapter extends ArrayAdapter<UserDetails> {

    private MyListenerInterface listenerInterface;

    public interface MyListenerInterface{
        void clicked(UserDetails user);
    }


    public GroupContactsAdapter(@NonNull Context context, @NonNull List<UserDetails> objects,MyListenerInterface listener) {
        super(context, R.layout.recent_chat_list_user, objects);
        listenerInterface = listener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v=convertView;
        if (convertView==null)
            v=LayoutInflater.from(getContext()).inflate(R.layout.recent_chat_list_user,parent,false);
        final UserDetails user = getItem(position);
        CircleImageView userImage = v.findViewById(R.id.friend_profile_image);
        TextView userNameTextView = v.findViewById(R.id.friend_name);
        Glide.with(getContext()).load(user.getPhotoUrl()).into(userImage);
        userNameTextView.setText(user.getName());
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listenerInterface.clicked(user);
            }
        });
        return v;
    }
}
