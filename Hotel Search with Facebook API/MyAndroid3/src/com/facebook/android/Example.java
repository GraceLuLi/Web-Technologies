/*
 * Copyright 2010 Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.facebook.android;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.lang.Object;
import java.awt.*;


import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;//
import android.widget.EditText;
import android.widget.TextView;//
import android.widget.Spinner;
import android.widget.Toast;

import com.facebook.android.SessionEvents.AuthListener;
import com.facebook.android.SessionEvents.LogoutListener;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;


public class Example extends Activity {

    // Your Facebook Application ID must be set before running this example
    // See http://www.facebook.com/developers/createapp.php
    public static final String APP_ID = "110048365793935";

    private LoginButton mLoginButton;
    private TextView mLoginText;
    private TextView mChooseText;//
    private Button mRequestButton;
    private Button mPostButton;
    private Button mDeleteButton;
    private Button mUploadButton;
    private Button mSearchButton;


    private Facebook mFacebook;
    private AsyncFacebookRunner mAsyncFacebookRunner;

	private EditText mCityInput;
	private Spinner mHotels;
	private String mHotelChoose;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (APP_ID == null) {
            Util.showAlert(this, "Warning", "Facebook Applicaton ID must be " +
                    "specified before running this example: see Example.java");
        }

        setContentView(R.layout.main);
        mLoginButton = (LoginButton) findViewById(R.id.login);
        mLoginText = (TextView) Example.this.findViewById(R.id.txt);
        mChooseText = (TextView) Example.this.findViewById(R.id.txt);//
        
        
        
        mRequestButton = (Button) findViewById(R.id.requestButton);
        mPostButton = (Button) findViewById(R.id.postButton);
        mDeleteButton = (Button) findViewById(R.id.deletePostButton);
        mUploadButton = (Button) findViewById(R.id.uploadButton);
        mSearchButton = (Button) findViewById(R.id.searchButton);//new button by me
        mCityInput = (EditText) findViewById(R.id.city);
        mHotels = (Spinner) findViewById(R.id.spinner);
        mHotels.setOnItemSelectedListener(new MyOnItemSelectedListener());


        
		   //Search button: hotel choose //LuLi
        mSearchButton.setOnClickListener(new Button.OnClickListener() {
            
            public void onClick(View v) {
            	if( mCityInput.getText().toString().trim().equals("")){///****
					Toast.makeText(Example.this, "please enter city name",///****
							Toast.LENGTH_LONG).show();///****
				}else{
                Intent intent = new Intent();
                intent.putExtra("city", mCityInput.getText().toString());
                intent.putExtra("hotel", mHotelChoose);//add some content, give it toDisplay
                intent.setClass(v.getContext(), hotelsearch.class);//define the Activity class that should transfer
                startActivity(intent);}
            }
        });
           
        //Spinner : hotel choose //LuLi
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.hotels_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


       	mFacebook = new Facebook(APP_ID);
       	mAsyncFacebookRunner = new AsyncFacebookRunner(mFacebook);

        SessionStore.restore(mFacebook, this);
        SessionEvents.addAuthListener(new SampleAuthListener());
        SessionEvents.addLogoutListener(new SampleLogoutListener());
        mLoginButton.init(this, mFacebook);

        mRequestButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	mAsyncFacebookRunner.request("me", new SampleRequestListener());
            }
        });
        mRequestButton.setVisibility(mFacebook.isSessionValid() ?
                View.VISIBLE :
                View.INVISIBLE);

        mUploadButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Bundle params = new Bundle();
                params.putString("method", "photos.upload");

                URL uploadFileUrl = null;
                try {
                    uploadFileUrl = new URL(
                        "http://www.facebook.com/images/devsite/iphone_connect_btn.jpg");
                } catch (MalformedURLException e) {
                	e.printStackTrace();
                }
                try {
                    HttpURLConnection conn= (HttpURLConnection)uploadFileUrl.openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    int length = conn.getContentLength();

                    byte[] imgData =new byte[length];
                    InputStream is = conn.getInputStream();
                    is.read(imgData);
                    params.putByteArray("picture", imgData);

                } catch  (IOException e) {
                    e.printStackTrace();
                }

                mAsyncFacebookRunner.request(null, params, "POST",
                        new SampleUploadListener(), null);
            }
        });
        mUploadButton.setVisibility(mFacebook.isSessionValid() ?
                View.VISIBLE :
                View.INVISIBLE);

        mPostButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mFacebook.dialog(Example.this, "feed",
                        new SampleDialogListener());
            }
        });
        mPostButton.setVisibility(mFacebook.isSessionValid() ?
                View.VISIBLE :
                View.INVISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        mFacebook.authorizeCallback(requestCode, resultCode, data);
    }
    //log in
    public class SampleAuthListener implements AuthListener {

        public void onAuthSucceed() {
            mLoginText.setText("You have logged in! ");
            mChooseText.setText("Choose a hotel chain & Input the city name");
            
        }

        public void onAuthFail(String error) {
            mLoginText.setText("Login Failed: " + error);
        }
    }
    
    //search result listener//LuLi
    public class MyOnItemSelectedListener implements OnItemSelectedListener {

        

		public void onItemSelected(AdapterView<?> parent,
            View view, int pos, long id) {
        	mHotelChoose=parent.getItemAtPosition(pos).toString();
      
        }

        public void onNothingSelected(AdapterView parent) {
          // Do nothing.
        }
    }
    
    //log out
    public class SampleLogoutListener implements LogoutListener {
        public void onLogoutBegin() {
            mLoginText.setText("Logging out...");
        }

        public void onLogoutFinish() {
            mLoginText.setText("You have logged out! ");
            mRequestButton.setVisibility(View.INVISIBLE);
            mUploadButton.setVisibility(View.INVISIBLE);
            mPostButton.setVisibility(View.INVISIBLE);
        }
    }
    //reuest button
    public class SampleRequestListener extends BaseRequestListener {

        public void onComplete(final String response, final Object state) {
            try {
                // process the response here: executed in background thread
                Log.d("Facebook-Example", "Response: " + response.toString());
                JSONObject json = Util.parseJson(response);
                final String name = json.getString("name");

                // then post the processed result back to the UI thread
                Example.this.runOnUiThread(new Runnable() {
                    public void run() {
                        mLoginText.setText("Hello there, " + name + "!");
                    }
                });
            } catch (JSONException e) {
                Log.w("Facebook-Example", "JSON Error in response");
            } catch (FacebookError e) {
                Log.w("Facebook-Example", "Facebook Error: " + e.getMessage());
            }
        }
    }
    //up load button
    public class SampleUploadListener extends BaseRequestListener {
        public void onComplete(final String response, final Object state) {
            try {
                // process the response here: (executed in background thread)
                Log.d("Facebook-Example", "Response: " + response.toString());
                JSONObject json = Util.parseJson(response);
                final String src = json.getString("src");

                // then post the processed result back to the UI thread

                Example.this.runOnUiThread(new Runnable() {
                    public void run() {
                        mLoginText.setText("Hello there, photo has been uploaded at \n" + src);
                    }
                });
            } catch (JSONException e) {
                Log.w("Facebook-Example", "JSON Error in response");
            } catch (FacebookError e) {
                Log.w("Facebook-Example", "Facebook Error: " + e.getMessage());
            }
        }
    }
    //post
    public class WallPostRequestListener extends BaseRequestListener {

        public void onComplete(final String response, final Object state) {
            Log.d("Facebook-Example", "Got response: " + response);
            String message = "<empty>";
            try {
                JSONObject json = Util.parseJson(response);
                message = json.getString("message");
            } catch (JSONException e) {
                Log.w("Facebook-Example", "JSON Error in response");
            } catch (FacebookError e) {
                Log.w("Facebook-Example", "Facebook Error: " + e.getMessage());
            }
            final String text = "Your Wall Post: " + message;
            Example.this.runOnUiThread(new Runnable() {
                public void run() {
                    mLoginText.setText(text);
                }
            });
        }
    }
    //post
    public class WallPostDeleteListener extends BaseRequestListener {

        public void onComplete(final String response, final Object state) {
            if (response.equals("true")) {
                Log.d("Facebook-Example", "Successfully deleted wall post");
                Example.this.runOnUiThread(new Runnable() {
                    public void run() {
                        mDeleteButton.setVisibility(View.INVISIBLE);
                        mLoginText.setText("Deleted Wall Post");
                    }
                });
            } else {
                Log.d("Facebook-Example", "Could not delete wall post");
            }
        }
    }

    public class SampleDialogListener extends BaseDialogListener {

        public void onComplete(Bundle values) {
            final String postId = values.getString("post_id");
            if (postId != null) {
                Log.d("Facebook-Example", "Dialog Success! post_id=" + postId);
                mAsyncFacebookRunner.request(postId, new WallPostRequestListener());
                mDeleteButton.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        mAsyncFacebookRunner.request(postId, new Bundle(), "DELETE",
                                new WallPostDeleteListener(), null);
                    }
                });
                mDeleteButton.setVisibility(View.VISIBLE);
            } else {
                Log.d("Facebook-Example", "No wall post made");
            }
        }
    }

}
