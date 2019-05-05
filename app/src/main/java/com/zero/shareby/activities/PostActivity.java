package com.zero.shareby.activities;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zero.shareby.R;
import com.zero.shareby.models.Post;


public class PostActivity extends AppCompatActivity {

    Button postButton;
    EditText titleEditText,descriptionEditText;
    TextView titleTextView,descTextView;
    SharedPreferences preferences;
    FirebaseUser user;
    Spinner categorySpinner;
    ProgressBar progressBar;
    LinearLayout selectionLayout;
    int type = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        ActionBar actionBar=getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        selectionLayout = findViewById(R.id.post_selection_view);
        titleTextView=findViewById(R.id.post_title_text_view);
        descTextView=findViewById(R.id.post_description_text_view);
        preferences= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        user=FirebaseAuth.getInstance().getCurrentUser();
        categorySpinner = findViewById(R.id.post_category_spinner);
        titleEditText=findViewById(R.id.title_edit_text);
        descriptionEditText=findViewById(R.id.description_edit_text);
        postButton=findViewById(R.id.post_button);
        postButton.setBackgroundTintList(getResources().getColorStateList(android.R.color.darker_gray));
        postButton.setTextColor(getResources().getColor(android.R.color.white));

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
              @Override
              public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                  if (position==1){
                      type =0;
                      titleTextView.setText("What is your Query?");
                      titleEditText.setHint("Let others know your question");
                      descTextView.setText("Give a Little Explanation(optional)");
                      descriptionEditText.setHint("A description helps others to understand your query better");
                      selectionLayout.setVisibility(View.VISIBLE);
                  }else if (position==2){
                      type =1;
                      titleTextView.setText("What do you want?");
                      titleEditText.setHint("Your Request Item Title");
                      descTextView.setText("Item Description(optional)");
                      descriptionEditText.setHint("Let your neighbors know exactly what you want..");
                      selectionLayout.setVisibility(View.VISIBLE);
                  }else {
                      selectionLayout.setVisibility(View.GONE);
                  }
              }

              @Override
              public void onNothingSelected(AdapterView<?> parent) {
                  selectionLayout.setVisibility(View.GONE);
              }
          }
        );



        titleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length()>0 && categorySpinner.getSelectedItemPosition()!=0) {
                    postButton.setEnabled(true);
                    postButton.setTextColor(getResources().getColor(android.R.color.white));
                    postButton.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccent));
                }
                else {
                    postButton.setEnabled(false);
                    postButton.setBackgroundTintList(getResources().getColorStateList(android.R.color.darker_gray));
                    postButton.setTextColor(getResources().getColor(android.R.color.white));
                }
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
        progressBar = findViewById(R.id.post_progress);
        AdView adView = findViewById(R.id.post_ad);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    private void postMessage() {
        progressBar.setVisibility(View.VISIBLE);
        String title=titleEditText.getText().toString().trim();
        String description=null;
        if (descriptionEditText.getText().toString().trim().length()>0){
            description=descriptionEditText.getText().toString().trim();
        }
        if (user!=null){
            String country = preferences.getString(getString(R.string.pref_country), "null");
            String pin = preferences.getString(getString(R.string.pref_pin), "null");
            String key1 = preferences.getString(getString(R.string.pref_key1), "null");
            String key2 = preferences.getString(getString(R.string.pref_key2), "null");

            DatabaseReference uploadPost = FirebaseDatabase.getInstance().getReference().child("Groups").child(country).child(pin)
                    .child(key1).child(key2).child("posts");
            Post newPost=new Post(user.getUid(),null,user.getDisplayName(),title,description,1,0,categorySpinner.getSelectedItemPosition()-1);
            uploadPost.push().setValue(newPost).addOnSuccessListener(this, new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(PostActivity.this,"Your Post Request Sent",Toast.LENGTH_SHORT).show();
                    finish();
                }
            });

        }
        else {
            Toast.makeText(this, "An Error occurred", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
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
