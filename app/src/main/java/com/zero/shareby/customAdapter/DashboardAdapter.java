package com.zero.shareby.customAdapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.zero.shareby.DashboardData;
import com.zero.shareby.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class DashboardAdapter extends ArrayAdapter<DashboardData> {

    public DashboardAdapter(@NonNull Context context, ArrayList<DashboardData> list) {
        super(context, R.layout.shared_item_layout,list);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater=LayoutInflater.from(getContext());
        View newView=convertView;
        if (convertView==null){
            newView=layoutInflater.inflate(R.layout.shared_item_layout,parent,false);
        }
        DashboardData data=getItem(position);
        TextView titleTextView=newView.findViewById(R.id.offering_user);
        TextView descriptionTextView=newView.findViewById(R.id.offered_to_para);
        titleTextView.setText(data.user1);
        String sentence="shared a/an "+data.itemName+" to "+data.user2;
        descriptionTextView.setText(sentence);
        CircleImageView imageView=newView.findViewById(R.id.offering_user_profile);
        if (FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()!=null){
            Glide.with(getContext())
                    .load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl())
                    .into(imageView);
        }
        return newView;
    }
}
