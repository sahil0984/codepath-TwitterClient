package com.codepath.apps.basictwitter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import com.codepath.apps.basictwitter.models.Tweet;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.Html;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
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
    Button btnReply;
    
    Tweet tweet;

	
	public TweetDetailDialog () {
		//Empty constructor required for Dialog Fragment
	}
	
	public static TweetDetailDialog newInstance(Tweet tweet) {
		TweetDetailDialog  frag = new TweetDetailDialog();
		Bundle args = new Bundle();
				
		args.putSerializable("tweet", tweet);
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
	    btnReply = (Button) view.findViewById(R.id.btnReply);
	    
	    btnReply.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				android.app.FragmentManager fm = getFragmentManager();
				ComposeDialog editFilterDialog = ComposeDialog.newInstance("detailDialog", tweet);
				editFilterDialog.show(fm, "fragment_compose");
			}
		});
		
	    
		tweet = (Tweet) getArguments().getSerializable("tweet");
	    
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
