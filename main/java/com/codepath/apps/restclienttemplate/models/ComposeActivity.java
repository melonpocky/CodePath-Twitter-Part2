package com.codepath.apps.restclienttemplate.models;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {
    public static final String TAG = "";
    EditText etCompose;
    EditText etValue;
    Button btnTweet;
    TextView tvDisplay;
    //Android snackbar
    public static final int MAX_TWEET_LENGTH = 140;
    TwitterClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        client = TwitterApp.getRestClient(this);
        etCompose = findViewById(R.id.etCompose);
        btnTweet = findViewById(R.id.btnTweet);
        etValue = findViewById(R.id.etCompose);
        tvDisplay = findViewById(R.id.tvDisplay);
       etValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Fires right as the text is being changed (even supplies the range of text)

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // Fires right before text is changing
            }

            @Override
            public void afterTextChanged(Editable s) {

                // Fires right after the text has changed
                tvDisplay.setText(s.length() + "/140");
                if(s.length() > MAX_TWEET_LENGTH)
                {
                    btnTweet.setEnabled(false);
                }
                if(s.length() < MAX_TWEET_LENGTH)
                {
                    btnTweet.setEnabled(true);
                }
            }
        });
        //add click listener on button
        btnTweet.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                String tweetContent = etCompose.getText().toString();
                if(tweetContent.isEmpty())
                {
                    Toast.makeText(ComposeActivity.this,"Sorry, your Tweet cannot be empty", Toast.LENGTH_LONG).show();
                    return;
                }
                if(tweetContent.length() >MAX_TWEET_LENGTH)
                {
                    Toast.makeText(ComposeActivity.this,"Sorry, your Tweet is too long", Toast.LENGTH_LONG).show();
                    return;

                }
                Toast.makeText(ComposeActivity.this,tweetContent, Toast.LENGTH_LONG).show();

                //make api call to twitter to publish tweet
                client.publishTweet( tweetContent, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG, "onSuccess to publish tweet");
                        try {
                            Tweet tweet = Tweet.fromJson(json.jsonObject);
                            Log.i(TAG, "Published Tweet says:" + tweet.body);
                            Intent intent = new Intent();
                            //android doesnt know how to take default obj to define activity
                            intent.putExtra("tweet", Parcels.wrap(tweet));


                            // Activity finished ok, return the data
                            setResult(RESULT_OK, intent); // set result code and bundle data for response
                            finish(); // closes the activity, pass data to parent

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "onFailure to publish tweet", throwable);
                    }
                });
            }
        });
    }
}