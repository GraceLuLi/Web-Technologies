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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.lang.Object;
import java.awt.*;
//import java.awt.Component;//
//import java.awt.TextComponent;//
//import java.awt.TextField;//
//import java.applet.*;
//import java.awt.*;
//import java.awt.event.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;//
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;//
import android.widget.Spinner;
import android.widget.Toast;

import com.facebook.android.SessionEvents.AuthListener;
import com.facebook.android.SessionEvents.LogoutListener;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

public class hotelsearch extends ListActivity {
	private String cityName;
	private String hotelName;
	private ListView mapListView;
	private String jsonData;
	private static String[] no_stars = new String[10];
	private static String[] review_url = new String[10];
	private static String[] location = new String[10];
	private static String[] no_reviews = new String[10];
	private static String[] name = new String[10];
	private static String[] image = new String[10];
	private Facebook mFacebook;
	 public static final String APP_ID = "297263870342079";
	 private Context context = this;

	public static Bitmap getBitmapFromURL(String src) {
		try {
			URL url = new URL(src);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			Bitmap myBitmap = BitmapFactory.decodeStream(input);
			return myBitmap;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		cityName = bundle.getString("city");
		hotelName = bundle.getString("hotel");
		cityName = cityName.replace(' ', '+');
		//cityName = cityName.replace('\n',' ');
       /*
		if (cityName == null || cityName.length() <= 0) {
			TextView text = (TextView) findViewById(R.id.txt);
			text.setText("Please Enter a City Name");
			//return;//*********lalalalala*********
			//Log.d("FACEBOOK", "Please Enter a City Name!");
			//Toast.makeText(context, "Please Enter a City Name!",
					//Toast.LENGTH_LONG).show();
			
		}*/
		
		
		//else{//for test *********lalalalal*******
		/* parse url */
		URL url;
		try {
			url = new URL(
					"http://cs-server.usc.edu:19596/examples/servlet/ajax_hotel?city="
							+ cityName + "&hotel=" + hotelName);
			URLConnection urlConnection = url.openConnection();
			urlConnection.setAllowUserInteraction(false);
			InputStream inStream = urlConnection.getInputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					inStream));
			jsonData = in.readLine();

		}

		catch (MalformedURLException e1) { // TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// hashtable for image, hotelname and location
		ArrayList<HashMap<String, Object>> maplist = new ArrayList<HashMap<String, Object>>();
		

		try {
			JSONObject jsonObject = new JSONObject(jsonData);
			JSONObject contentObject = jsonObject.getJSONObject("hotels");

			JSONArray contentArray = contentObject.getJSONArray("hotel");
			//test at today 4:30pm
			if(contentArray.length()==0)
			{
				Log.d("FACEBOOK", "No hotels found!");
				Toast.makeText(context, "No hotels found!",
						Toast.LENGTH_LONG).show();
			}
			//test at today 4:30pm
			for (int i = 0; i < contentArray.length(); i++) {

				JSONObject item = contentArray.getJSONObject(i);
				no_stars[i] = item.getString("no_of_stars");
				review_url[i] = item.getString("review_url");
				location[i] = item.getString("location");
				no_reviews[i] = item.getString("no_of_reviews");
				name[i] = item.getString("name");
				image[i] = item.getString("image_url");
				Bitmap bm;
				bm = getBitmapFromURL(item.getString("image_url"));
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("imageview", bm);
				map.put("hotelname", item.getString("name"));
				map.put("location", item.getString("location"));
				maplist.add(map);
			}
			SimpleAdapter listAdapter = new SimpleAdapter(this, maplist,
					R.layout.hotelviewlist, new String[] { "imageview",
							"hotelname", "location" }, new int[] {
							R.id.imageview, R.id.hotelname, R.id.location });

			listAdapter.setViewBinder(new ViewBinder() {

				public boolean setViewValue(View view, Object data,
						String textRepresentation) {
					if (view instanceof ImageView && data instanceof Bitmap) {
						ImageView iv = (ImageView) view;
						iv.setImageBitmap((Bitmap) data);
						iv.setScaleType(ScaleType.FIT_XY);
						return true;
					} else
						return false;
				}
			});

			setListAdapter(listAdapter);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	//}//for else test******lalala*******
	}

	
	public void onListItemClick(ListView l, View v, int index, long id) {

		// custom dialog
		final Dialog popup = new Dialog(context);
		popup.setContentView(R.layout.popup_window);

		// set the custom dialog components - text, image and button
		TextView pHotelName = (TextView) popup
				.findViewById(R.id.popUpHotelname);
		pHotelName.setText(name[index]);
		
		ImageView pImage = (ImageView) popup
				.findViewById(R.id.popUpImage);
		pImage.setImageBitmap(getBitmapFromURL(image[index]));
		pImage.setScaleType(ScaleType.FIT_XY);

		TextView pReview = (TextView) popup
				.findViewById(R.id.popUpReview);
		pReview.setText("Reviews:" + no_reviews[index]);

		RatingBar pStars = (RatingBar) popup
				.findViewById(R.id.popUpStars);
		pStars.setRating(Float.valueOf(no_stars[index]));
		
		Button pPostButton = (Button) popup.findViewById(R.id.popUpPostbutton);

		final int anIndex = index;
		// if button is clicked, close the custom dialog
		pPostButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				popup.dismiss();
				publishPhoto(name[anIndex], image[anIndex], review_url[anIndex],location[anIndex],no_stars[anIndex]);

				
			}
		});

		popup.show();
	}

	private void publishPhoto(String hotelName, String imageURL, String reviewUrl, String location, String rating) {
		mFacebook = new Facebook(APP_ID);
		Log.d("FACEBOOK", "Post to Facebook!");

		try {

			JSONObject attachment = new JSONObject();
			attachment.put("name", hotelName);
			attachment.put("caption", "Check this hotel");
			attachment.put("description",  "This hotel is located at "+location+" and has a rating "+rating +"/5");
			
			JSONObject media = new JSONObject();
			media.put("type", "image");
			media.put("src", imageURL);
			media.put("href", reviewUrl);
			attachment.put("media", new JSONArray().put(media));		
			
			JSONObject properties = new JSONObject();
			JSONObject prop = new JSONObject();
			prop.put("text", "here");
			prop.put("href", reviewUrl);
			properties.put("Find the Hotel Reviews ", prop);

			attachment.put("properties", properties);

			Log.d("FACEBOOK", attachment.toString());

			Bundle params = new Bundle();
			params.putString("attachment", attachment.toString());
			mFacebook.dialog(context, "stream.publish", params,
					new PostPhotoDialogListener());
			

		} catch (JSONException e) {
			Log.e("FACEBOOK", e.getLocalizedMessage(), e);
		}
	}

	public class PostPhotoDialogListener extends BaseDialogListener {

		public void onComplete(Bundle values) {
			final String postId = values.getString("post_id");
			if (postId != null) {
				Log.d("FACEBOOK", "Dialog Success! post_id=" + postId);
				Toast.makeText(context, "Successfully shared on Facebook!",
						Toast.LENGTH_LONG).show();
				
			} else {
				Log.d("FACEBOOK", "No wall post made");
				Toast.makeText(context, "No wall post made!",
						Toast.LENGTH_LONG).show();
			}
		}
	}

}
