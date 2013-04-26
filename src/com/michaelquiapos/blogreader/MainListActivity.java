package com.michaelquiapos.blogreader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


public class MainListActivity extends ListActivity {
	
	// We generally want to use protected for member variables unless we have specific reason to make them public
	// DATA
	protected String[] mBlogPostTitles;
	public static final int NUMBER_OF_POSTS	= 20;
	public static final String TAG = MainListActivity.class.getSimpleName();
	protected JSONObject mBlogData;
	protected ProgressBar mProgressBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_list);
		
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
		
		if(isNetworkAvailable()) {
		mProgressBar.setVisibility(View.VISIBLE);
		GetBlogPostTask getBlogPostTask = new GetBlogPostTask();
		getBlogPostTask.execute();
		
		}
		//Toast.makeText(this, getString(R.String.no_items), Toast.LENGTH_LONG).show();
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager manager = (ConnectivityManager) 
				getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getActiveNetworkInfo(); 
		
		boolean isAvailable = false;
		
		if (networkInfo != null && networkInfo.isConnected()) {
			isAvailable = true;
		} else {
			Toast.makeText(this, "Network is unvailable!", Toast.LENGTH_LONG).show();
		}
		
		return isAvailable;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_list, menu);
		return true;
	}
		
	public void handleBlogResponse() {
		mProgressBar.setVisibility(View.INVISIBLE);
		
		if (mBlogData == null) {
			
			updateDisplayForError();
			
		}
		else {
			try {
				JSONArray jsonPosts = mBlogData.getJSONArray("posts");
				mBlogPostTitles = new String[jsonPosts.length()];
				
				for (int i = 0; i < jsonPosts.length(); i++){
					
					JSONObject post = jsonPosts.getJSONObject(i);
					String title = post.getString("title");
					title = Html.fromHtml(title).toString();
					mBlogPostTitles[i] = title;
					
				}
				
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
						android.R.layout.simple_list_item_1, mBlogPostTitles);
				setListAdapter(adapter);
				
			} 
			catch (JSONException e) {
				Log.e(TAG, "Exception caught", e);
			}
		}
	}

	private void updateDisplayForError() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.error_title));
		builder.setMessage(getString(R.string.error_message));
		builder.setPositiveButton(android.R.string.ok, null);
		AlertDialog dialog = builder.create();
		dialog.show();
		
		TextView emptyTextView = (TextView) getListView().getEmptyView();
		emptyTextView.setText(getString(R.string.no_items));
	}
	
	private class GetBlogPostTask extends AsyncTask<Object, Void, JSONObject> {
				
		@Override
		protected JSONObject doInBackground(Object... arg0) {
			int responseCode = -1;
			JSONObject jsonResponse = null;
			
			try {
				URL blogFeedUrl = new URL("blog.teamtreehouse.com/api/get_recent_summary/?count=" + NUMBER_OF_POSTS);
				HttpURLConnection connection = (HttpURLConnection) blogFeedUrl.openConnection();
				connection.connect();
				
				if (responseCode == HttpURLConnection.HTTP_OK) {
					InputStream inputStream = connection.getInputStream();
					Reader reader = new InputStreamReader(inputStream);
					int contentLength = connection.getContentLength();
					char[] charArray = new char[contentLength];
					reader.read(charArray);
					String responseData = new String(charArray);
					//Log.v(TAG, responseData);
					
					jsonResponse = new JSONObject(responseData);							
				}
				else {
					Log.i(TAG, "Unsuccessful HTTP Response Code: " + responseCode);
				}
				
				Log.i(TAG, "Code: " + responseCode);
			}
			catch (MalformedURLException e) {
				Log.e(TAG, "Exception caught: ", e);
			} 
			catch (IOException e){
				Log.e(TAG, "Exception caught: ", e);
			}
			catch (Exception e){
				Log.e(TAG, "Exception caught: ", e);
			}
			return jsonResponse;
		}
		
		@Override
		protected void onPostExecute(JSONObject result) {
			mBlogData = result;
			handleBlogResponse();
		}
	}
}
