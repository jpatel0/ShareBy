package com.zero.shareby;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.zero.shareby.customAdapter.PostAdapter;

import java.util.ArrayList;

public class PostDashboard extends Fragment {

    public PostDashboard() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_post_dashboard, container, false);
        ArrayList<MyData> data=new ArrayList<>();
        data.add(new MyData("I know kfberf","Just a description"));
        data.add(new MyData("I know fvfvfv","Just a description"));
        data.add(new MyData("I know fvfvedwe","Just a description"));
        data.add(new MyData("I know  wdwwwwwww","Just a description"));
        data.add(new MyData("I know oeurerfiur","Just a description"));
        data.add(new MyData("I know ekbvkjerb","Just a description"));
        data.add(new MyData("I know erbherb","Just a description"));

        PostAdapter postAdapter=new PostAdapter(getActivity(),data);
        ListView listView=rootView.findViewById(R.id.post_dashboard_list_view);
        listView.setAdapter(postAdapter);
        FloatingActionButton newPostButton=rootView.findViewById(R.id.post_floating_button);
        newPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),PostActivity.class));
            }
        });
        return rootView;
    }


}
