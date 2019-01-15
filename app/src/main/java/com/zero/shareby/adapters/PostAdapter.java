package com.zero.shareby.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import com.zero.shareby.models.Post;
import com.zero.shareby.R;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.zero.shareby.utils.Utilities.calculateTimeDisplay;

public class PostAdapter extends ArrayAdapter<Post> {

    private int mExpandedPosition=-1;
    private MyPostButtonClickListener listener;
    public interface MyPostButtonClickListener{
        void onConfirmButtonClick(Post post);
        void onDeleteButtonClick(Post post);
    }
    public PostAdapter(@NonNull Context context, ArrayList<Post> list, MyPostButtonClickListener listener) {
        super(context, R.layout.post_item_layout,list);
        this.listener = listener;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater=LayoutInflater.from(getContext());
        View newView=convertView;
        if (convertView==null){
            newView=layoutInflater.inflate(R.layout.post_item_layout,parent,false);
        }
        final Post data=getItem(position);
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

//       handling card collapse
        final ListView postListView = parent.findViewById(R.id.post_dashboard_list_view);


        //handling replies
        final Button confirmButton = newView.findViewById(R.id.confirm_post_button);
        final TextView repliesTextView=newView.findViewById(R.id.post_replies);

        if (data.getRepliedUid()!=null && data.getSharedUid()==null){
//            when the requester has to confirm the help offer
            final boolean isExpanded = position==mExpandedPosition;
            deleteImageButton.setVisibility(isExpanded?View.VISIBLE:View.GONE);
            newView.setActivated(isExpanded);
            newView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mExpandedPosition = isExpanded ? -1:position;
                    TransitionManager.beginDelayedTransition(postListView);
                    notifyDataSetChanged();
                }
            });
            deleteImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onDeleteButtonClick(data);
                }
            });
            deleteImageButton.setVisibility(View.VISIBLE);
            confirmButton.setVisibility(View.VISIBLE);
            repliesTextView.setVisibility(View.VISIBLE);
            repliesTextView.setText(data.getRepliedName()+" has offered your request, Confirm?");
            confirmButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onConfirmButtonClick(data);
                    TransitionManager.beginDelayedTransition(postListView);
                }
            });
        }else if (data.getSharedUid()!=null){

//            when the request is fulfilled and completed

            repliesTextView.setVisibility(View.VISIBLE);
            confirmButton.setVisibility(View.GONE);
            deleteImageButton.setVisibility(View.GONE);
            repliesTextView.setText(data.getTitle()+" accepted from "+data.getRepliedName());
        }else {

//            when a post is new and no one has offered any help

            repliesTextView.setVisibility(View.GONE);
            confirmButton.setVisibility(View.GONE);
            final boolean isExpanded = position==mExpandedPosition;
            deleteImageButton.setVisibility(isExpanded?View.VISIBLE:View.GONE);
            newView.setActivated(isExpanded);
            newView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mExpandedPosition = isExpanded ? -1:position;
                    TransitionManager.beginDelayedTransition(postListView);
                    notifyDataSetChanged();
                }
            });
            deleteImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onDeleteButtonClick(data);
                }
            });
        }

        return newView;
    }




}
