package com.zero.shareby;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class PostActivity extends AppCompatActivity {

    Button postButton;
    EditText titleEditText,descriptionEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        ActionBar actionBar=getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        titleEditText=findViewById(R.id.title_edit_text);
        descriptionEditText=findViewById(R.id.description_edit_text);
        postButton=findViewById(R.id.post_button);

        titleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length()>0)
                    postButton.setEnabled(true);
                else
                    postButton.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postMessage();
            }
        });

    }

    private void postMessage() {
        String title=titleEditText.getText().toString().trim();
        String description=null;
        if (descriptionEditText.getText().toString().trim().length()>0){
            description=descriptionEditText.getText().toString().trim();
            Map<String,String> map=new HashMap<>();
            map.put("title",title);
            map.put("description",description);
            if (FirebaseAuth.getInstance().getCurrentUser()!=null){
                DatabaseReference postReference=FirebaseDatabase.getInstance().getReference();

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.post_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case android.R.id.home:
            case R.id.discard_changes_menu:
                finish();
                break;

        }
        return true;
    }
}
