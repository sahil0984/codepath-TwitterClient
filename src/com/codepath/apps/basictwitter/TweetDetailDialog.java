package com.codepath.apps.basictwitter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.json.JSONObject;

import com.codepath.apps.basictwitter.models.Tweet;
import com.codepath.apps.basictwitter.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class TweetDetailDialog  extends DialogFragment {
	
    ImageView ivProfileImage;
    TextView tvUserName;
    TextView tvScreenName;
    TextView tvBody;
    TextView tvTimestamp;
    ImageView ivVerifiedStatus;
    ImageView ivEmbedImage;
    TextView tvRetweetCount;
    
    Button btnReply;
    Button btnRetweet;
    Button btnFavorite;
    
    Tweet tweet;
    User myUser;
    
    boolean tmpFav;
    
	private TwitterClient client;

	
	public TweetDetailDialog () {
		//Empty constructor required for Dialog Fragment
	}
	
	public static TweetDetailDialog newInstance(Tweet tweet, User user) {
		TweetDetailDialog  frag = new TweetDetailDialog();
		Bundle args = new Bundle();
				
		args.putSerializable("tweet", tweet);
		args.putSerializable("myuser", user);
	    frag.setArguments(args);

	    return frag;
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        //getDialog().setTitle("Edit Filters");
		
        
		View view = inflater.inflate(R.layout.fragment_tweet_detail, container);
		
	    ivProfileImage = (ImageView) view.findViewById(R.id.ivProfileImage);
	    tvUserName = (TextView) view.findViewById(R.id.tvUserName);
	    tvScreenName = (TextView) view.findViewById(R.id.tvScreenName);
	    tvBody = (TextView) view.findViewById(R.id.tvBody);
	    tvTimestamp = (TextView) view.findViewById(R.id.tvTimestamp);
	    ivVerifiedStatus = (ImageView) view.findViewById(R.id.ivVerifiedStatus);
	    ivEmbedImage = (ImageView) view.findViewById(R.id.ivEmbedImage);
	    tvRetweetCount = (TextView) view.findViewById(R.id.tvRetweetCount);
	    btnReply = (Button) view.findViewById(R.id.btnReply);
	    btnRetweet = (Button) view.findViewById(R.id.btnRetweet);
	    btnFavorite = (Button) view.findViewById(R.id.btnFavorite);
	    		
	    btnReply.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				android.app.FragmentManager fm = getFragmentManager();
				ComposeDialog editFilterDialog = ComposeDialog.newInstance("reply", tweet, myUser);
				editFilterDialog.show(fm, "fragment_compose");
			}
		});
	    
	    btnRetweet.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				android.app.FragmentManager fm = getFragmentManager();
				ComposeDialog editFilterDialog = ComposeDialog.newInstance("retweet", tweet, tweet.getUser());
				editFilterDialog.show(fm, "fragment_compose");
			}
		});
	    
	    btnFavorite.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				client = TwitterApplication.getRestClient();

				if (tmpFav == false) {	
					client.postFavorite(tweet.getUid(), new JsonHttpResponseHandler(){
						@Override
						public void onSuccess(JSONObject json) {
				        	btnFavorite.setTextColor(Color.parseColor("yellow"));
				        	tmpFav = true;
						}
						
						@Override
						public void onFailure(Throwable e, String s) {
							Log.d("debug", e.toString());
							Log.d("debug", s.toString());
						}
					});
				} else {
					client.postUnFavorite(tweet.getUid(), new JsonHttpResponseHandler(){
						@Override
						public void onSuccess(JSONObject json) {
				        	btnFavorite.setTextColor(Color.parseColor("black"));
				        	tmpFav = false;
						}
						
						@Override
						public void onFailure(Throwable e, String s) {
							Log.d("debug", e.toString());
							Log.d("debug", s.toString());
						}
					});
				}
			}
		});
		
	    
		tweet = (Tweet) getArguments().getSerializable("tweet");
		myUser = (User) getArguments().getSerializable("myuser");
	    
	    ivProfileImage.setImageResource(android.R.color.transparent);
	    ImageLoader imageLoader = ImageLoader.getInstance();
	    
	    imageLoader.displayImage(tweet.getUser().getProfileImageUrl(), ivProfileImage);
	    tvUserName.setText(tweet.getUser().getName());
	    tvScreenName.setText("@" + tweet.getUser().getScreenName());
	    tvBody.setText(Html.fromHtml(tweet.getBody()).toString());
	    tvTimestamp.setText(getRelativeTimeAgo(tweet.getCreatedAt()));
	    
        ivVerifiedStatus.setImageResource(android.R.color.transparent);
        if (tweet.getUser().getVerifiedStatus() == true) {
     	   ivVerifiedStatus.setImageResource(R.drawable.verified_status);
        }
	    
        ivEmbedImage.setImageResource(android.R.color.transparent);
        if (tweet.getMediaUrl() != null) {
    	    imageLoader.displayImage(tweet.getMediaUrl(), ivEmbedImage);
         }

        tvRetweetCount.setText("Retweets " + tweet.getRetweetsCount());

		tmpFav = tweet.getFavorited();

        if (tweet.getFavorited() == true) {
        	btnFavorite.setTextColor(Color.parseColor("yellow"));
        } else {
        	btnFavorite.setTextColor(Color.parseColor("black"));
        }
		
		return view;
	}
	
	
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	  Dialog dialog = super.onCreateDialog(savedInstanceState);

	  // request a window without the title
	  dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
	  return dialog;
	}
	
	// getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
	public String getRelativeTimeAgo(String rawJsonDate) {
		String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
		SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
		sf.setLenient(true);
	 
		String relativeDate = "";
		try {
			long dateMillis = sf.parse(rawJsonDate).getTime();
			relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
					System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	 
		return relativeDate;
	}
	
}
