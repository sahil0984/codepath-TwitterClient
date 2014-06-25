package com.codepath.apps.basictwitter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import android.R.drawable;
import android.content.Context;
import android.text.Html;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codepath.apps.basictwitter.models.Tweet;
import com.nostra13.universalimageloader.core.ImageLoader;

public class TweetArrayAdapter extends ArrayAdapter<Tweet> {
	private static final int FOCUS_BLOCK_DESCENDANTS = 393216;

	public TweetArrayAdapter(Context context, List<Tweet> objects) {
		super(context, 0, objects);
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	       // Get the data item for this position
	       Tweet tweet = getItem(position);    
	       ViewHolder holder;
	       
	       // Check if an existing view is being reused, otherwise inflate the view
	       if (convertView == null) {
	    	   
	  		LayoutInflater inflater = LayoutInflater.from(getContext());
	  		convertView = inflater.inflate(R.layout.tweet_item, parent, false);
	  	    holder = new ViewHolder();
	  	    
		    // Lookup view for data population
	  	    holder.ivProfileImage = (ImageView) convertView.findViewById(R.id.ivProfileImage);
	  	    holder.tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
	  	    holder.tvScreenName = (TextView) convertView.findViewById(R.id.tvScreenName);
	  	    holder.tvBody = (TextView) convertView.findViewById(R.id.tvBody);
	  	    holder.tvTimestamp = (TextView) convertView.findViewById(R.id.tvTimestamp);
	  	    holder.ivVerifiedStatus = (ImageView) convertView.findViewById(R.id.ivVerifiedStatus);
	  
	  		convertView.setTag(holder);
	       } else {
	    	  
	   	    holder = (ViewHolder) convertView.getTag();
	   	    //To remove the flickering use the holder and the following clear command
	   	    //holder.ivProfileImage.setImageResource(null);
	   	   }
	       
	        //holder.tvBody.setMovementMethod(LinkMovementMethod.getInstance());

	       // Populate the data into the template view using the data object
	       holder.ivProfileImage.setImageResource(android.R.color.transparent);
	       ImageLoader imageLoader = ImageLoader.getInstance();
	       
	       
	       imageLoader.displayImage(tweet.getUser().getProfileImageUrl(), holder.ivProfileImage);
	       holder.tvUserName.setText(tweet.getUser().getName());
	       holder.tvScreenName.setText("@" + tweet.getUser().getScreenName());
	       holder.tvBody.setText(Html.fromHtml(tweet.getBody()).toString());
	       //holder.tvBody.setText(Long.toString(tweet.getUid()));
	       holder.tvTimestamp.setText(getRelativeTimeAgo(tweet.getCreatedAt()));
	       holder.ivVerifiedStatus.setImageResource(android.R.color.transparent);
	       if (tweet.getUser().getVerifiedStatus() == true) {
	    	   holder.ivVerifiedStatus.setImageResource(R.drawable.verified_status);
	       } else {
	    	/*   RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
	    	            LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    	   params.addRule(RelativeLayout.RIGHT_OF, holder.tvUserName.getId());
	    	   holder.tvScreenName.setLayoutParams(params); */
	       }
	       
	       holder.tvBody.setLinksClickable(false);
	       //holder.tvBody.setClickable(true);
	       holder.tvBody.setFocusable(false);
	       holder.tvBody.setFocusableInTouchMode(false);


	       // Return the completed view to render on screen
	       return convertView;
	}


	static class ViewHolder {
		ImageView ivProfileImage;
		TextView tvUserName;
		TextView tvScreenName;
		TextView tvBody;
		TextView tvTimestamp;
		ImageView ivVerifiedStatus;	
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
		
		String formattedTime =  relativeDate.replaceAll(" hour.* ago", "h");
		formattedTime =  formattedTime.replaceAll(" minute.* ago", "m");
		formattedTime =  formattedTime.replaceAll(" second.* ago", "s");
		formattedTime =  formattedTime.replaceAll(" day.* ago", "d");
		formattedTime =  formattedTime.replaceAll("Yesterday", "1d");
		formattedTime =  formattedTime.replaceAll(" year.* ago", "y");

		return formattedTime;
	}

	
}
