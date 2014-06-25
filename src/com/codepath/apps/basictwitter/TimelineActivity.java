package com.codepath.apps.basictwitter;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.codepath.apps.basictwitter.ComposeDialog.OnDataPass;
import com.codepath.apps.basictwitter.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import eu.erikw.PullToRefreshListView.OnRefreshListener;

public class TimelineActivity extends FragmentActivity implements OnDataPass {
	private TwitterClient client;
	private ArrayList<Tweet> tweets;
	private ArrayAdapter<Tweet> aTweets;
	private eu.erikw.PullToRefreshListView lvTweets;
	
	private long oldestTweetId;
	private long youngestTweetId;
	
	private int numQueries;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeline);
		client = TwitterApplication.getRestClient();
		numQueries = 0;
		oldestTweetId = 1;
		youngestTweetId = 1;
		//populateTimeline(oldestTweetId, youngestTweetId, 0);
		lvTweets = (eu.erikw.PullToRefreshListView) findViewById(R.id.lvTweets);
		tweets = new ArrayList<Tweet>();
		aTweets = new TweetArrayAdapter(this, tweets);
		lvTweets.setAdapter(aTweets);
		
		
		populateTimelineFromLocalDb();
		
		lvTweets.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View parent, int position,
					long arg3) {
				Tweet tweet = tweets.get(position);
				android.app.FragmentManager fm = getFragmentManager();
				TweetDetailDialog editFilterDialog = TweetDetailDialog.newInstance(tweet);
				editFilterDialog.show(fm, "fragment_filters");
			}
		});
		
		lvTweets.setOnScrollListener(new EndlessScrollListener() {
			
			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				customLoadMoreDataFromApi(page);
			}
			
		});
		lvTweets.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
                // Your code to refresh the list contents
                // Make sure you call listView.onRefreshComplete()
                // once the loading is done. This can be done from here or any
                // place such as when the network request has completed successfully.
				long since_id;
				long max_id;
				try {
					since_id = aTweets.getItem(0).getUid();
					//max_id = 1;
				} catch (Exception e) {
					since_id = 1;
					//max_id = 1;
					//populateTimeline(since_id, max_id);
					//lvTweets.onRefreshComplete();
					//return;
				}
				//Toast.makeText(getApplicationContext(), "uid at 0:" + tweets.get(0).getUid(), Toast.LENGTH_LONG)
				//.show();
				max_id = 1;
				populateTimeline(since_id, max_id, 1);
				
				max_id = oldestTweetId;
				
				//Turn it back on
				while (max_id>since_id) {
					populateTimeline(since_id, max_id, 1);
					max_id = oldestTweetId;
				}
				
				lvTweets.onRefreshComplete();
			}
		});
	}
	
	private void populateTimelineFromLocalDb() {
		//try {
			aTweets.clear();
			aTweets.notifyDataSetInvalidated();
  			
			List<Tweet> allSavedTweets = (List<Tweet>) Tweet.getAll();
			aTweets.addAll(allSavedTweets);
	}

	protected void customLoadMoreDataFromApi(int page) {
		long since_id = 1;
		long max_id;
		if (aTweets.getCount() == 0) {
			max_id = 1;
		} else {
			max_id = (aTweets.getItem(aTweets.getCount()-1).getUid())-1;
		}
		populateTimeline(since_id, max_id, 0);
	}

	public void populateTimeline (long since_id, long max_id, final int type){
		//long max_id = since_id + numTweets;
		if (isNetworkAvailable() == true) {
			Toast.makeText(getApplicationContext(), "since_id " + since_id + "max_id " + max_id, Toast.LENGTH_SHORT)
			.show();
			numQueries = numQueries + 1;
			
			if (aTweets.getCount() == 0) {
				Tweet.deleteAll();
				aTweets.clear();
				aTweets.notifyDataSetInvalidated();
			}
			
			client.getHomeTimeline(since_id, max_id, new JsonHttpResponseHandler(){
				@Override
				public void onSuccess(JSONArray json) {
					ArrayList<Tweet> tmpTweets = Tweet.fromJSONArray(json);
					if (type == 0) { //Scroll - Add more data at the end
						
						aTweets.addAll(tmpTweets);
						
					} else if (type == 1) { //Refresh - Add new data at the top
						for (int i=0; i<json.length(); i++) {
							aTweets.insert(tmpTweets.get(i), i);
						}
						
						if (json.length() != 0) {
							try {
								oldestTweetId = Tweet.fromJSON(json.getJSONObject(json.length()-1)).getUid()-1;
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
					for (int i=0; i<json.length(); i++) {
						tmpTweets.get(i).getUser().save();
						tmpTweets.get(i).save();
					}

					//Toast.makeText(getApplicationContext(), "oldestTweetId " + oldestTweetId + ". actual_length " + json.length(), Toast.LENGTH_LONG)
					//		.show();
				}
				
				@Override
				public void onFailure(Throwable e, String s) {
					Log.d("debug", e.toString());
					Log.d("debug", s.toString());
				}
				
			});
		} else {
			Toast.makeText(getApplicationContext(), "Check your internet connection!", Toast.LENGTH_LONG)
			.show();
			populateTimelineFromLocalDb();
		}
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.timeline_activity_actions, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.miCompose:
                composeMessage();
                return true;
            case R.id.miProfile:
                //showProfileView();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

	private void composeMessage() {
			android.app.FragmentManager fm = getFragmentManager();
			ComposeDialog editFilterDialog = ComposeDialog.newInstance("timeline");
			editFilterDialog.show(fm, "fragment_compose");
	}
	
	@Override
	public void onDataPass(String newTweet, long replyTweetUid) {
	    //Log.d("LOG","hello " + data);

		client.postUpdate(newTweet, replyTweetUid, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject json) {
				aTweets.insert(Tweet.fromJSON(json), 0);
			}
			
			@Override
			public void onFailure(Throwable e, String s) {
				Log.d("debug", e.toString());
				Log.d("debug", s.toString());
			}
		});
	}
	
	
	
	//TODO: Add a dialog alert??
	private Boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
	}

}
