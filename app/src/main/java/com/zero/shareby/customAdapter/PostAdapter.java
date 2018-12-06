package com.zero.shareby.customAdapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import com.zero.shareby.Utilities.Post;
import com.zero.shareby.R;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.zero.shareby.Utilities.Utilities.calculateTimeDisplay;

public class PostAdapter extends ArrayAdapter<Post> {

    public PostAdapter(@NonNull Context context, ArrayList<Post> list) {
        super(context, R.layout.post_item_layout,list);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater=LayoutInflater.from(getContext());
        View newView=convertView;
        if (convertView==null){
            newView=layoutInflater.inflate(R.layout.post_item_layout,parent,false);
        }
        Post data=getItem(position);
        TextView titleTextView=newView.findViewById(R.id.card_title);
        TextView descriptionTextView=newView.findViewById(R.id.card_description);
        TextView timestampTextView=newView.findViewById(R.id.timestamp_post_dashboard);
        titleTextView.setText(data.getTitle());
        if (data.getDesc()!=null)
            descriptionTextView.setText(data.getDesc());
        else descriptionTextView.setHeight(0);
        timestampTextView.setText(calculateTimeDisplay(data.getTimestamp()));
        CircleImageView imageView=newView.findViewById(R.id.card_profile_image);
        if (FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()!=null){
            Glide.with(getContext())
                    .load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl())
                    .into(imageView);
        }
        final ImageButton deleteImageButton=newView.findViewById(R.id.card_delete_icon);
        deleteImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"Delete clicked",Toast.LENGTH_SHORT).show();
            }
        });
        return newView;
    }




}
