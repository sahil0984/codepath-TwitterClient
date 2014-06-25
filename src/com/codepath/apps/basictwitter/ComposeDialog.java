package com.codepath.apps.basictwitter;

import org.json.JSONArray;
import org.json.JSONObject;

import com.codepath.apps.basictwitter.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ComposeDialog extends DialogFragment {

	private EditText etTweetBody;
	private TextView tvTweetCharLeft;
	private Button btnSendTweet;
	
	private TwitterClient client;
	private Tweet newTweet;
	private String newTweetBody;
	
	private Tweet replyTweet;
	private String callFrom;
	
	public ComposeDialog () {
		//Empty constructor required for Dialog Fragment
	}
	
	public static ComposeDialog newInstance(String callFrom) {
		ComposeDialog  frag = new ComposeDialog();
		Bundle args = new Bundle();
		
		args.putSerializable("callFrom", callFrom);
	    frag.setArguments(args);
	    return frag;
	}
	
	public static ComposeDialog newInstance(String callFrom, Tweet replyTweet) {
		ComposeDialog  frag = new ComposeDialog();
		Bundle args = new Bundle();
		
		args.putSerializable("callFrom", callFrom);
		args.putSerializable("tweet", replyTweet);
	    frag.setArguments(args);

	    return frag;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        //getDialog().setTitle("Edit Filters");
        
		View view = inflater.inflate(R.layout.fragment_compose, container);
		etTweetBody = (EditText) view.findViewById(R.id.etTweetBody);
		tvTweetCharLeft = (TextView) view.findViewById(R.id.tvTweetCharLeft);
		btnSendTweet = (Button) view.findViewById(R.id.btnSendTweet); 
				
		
		callFrom = (String) getArguments().getSerializable("callFrom");
		if (callFrom == "timeline") {
			btnSendTweet.setText("Tweet");
		} else if (callFrom == "detailDialog") {
			replyTweet = (Tweet) getArguments().getSerializable("tweet");
			etTweetBody.setText("@" + replyTweet.getUser().getScreenName() + " ");
			etTweetBody.setSelection(etTweetBody.getText().length());
			etTweetBody.setFocusableInTouchMode(true);
			etTweetBody.requestFocus();
			btnSendTweet.setText("Reply");
		}
		
		//tvTweetCharLeft.setText("140");
		int charsLeft = 140 - etTweetBody.getText().length();
		tvTweetCharLeft.setText(Integer.toString(charsLeft));

		
		etTweetBody.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				try {
					int charsLeft = 140 - s.length();
					tvTweetCharLeft.setText(Integer.toString(charsLeft));
				} catch (Exception e) {
					tvTweetCharLeft.setText("140");
				}
			}
		});
		
		
		btnSendTweet.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//ImageFilter filter = new ImageFilter(0,0,0,"");
				//filter.setImageType((int) spnImageType.getSelectedItemId()); 
				//filter.setImageSize((int) spnImageSize.getSelectedItemId()); 
				//filter.setImageColorFilter((int) spnImageColorFilter.getSelectedItemId());
				//filter.setImageSite(etImageSite.getText().toString());
				
				newTweetBody = etTweetBody.getText().toString();
				//dataPasser.onDataPass(newTweetBody);
				
				if (callFrom == "timeline") {
					dataPasser.onDataPass(newTweetBody, 0);
				} else if (callFrom == "detailDialog") {
					dataPasser.onDataPass(newTweetBody, replyTweet.getUid());
				}

//				client.postUpdate(newTweetBody, new JsonHttpResponseHandler(){
//					@Override
//					public void onSuccess(JSONObject json) {
//						newTweet = Tweet.fromJSON(json);
//					    dataPasser.onDataPass(newTweet);
//
//					}
//					
//					@Override
//					public void onFailure(Throwable e, String s) {
//						Log.d("debug", e.toString());
//						Log.d("debug", s.toString());
//					}
//				});
			    
			    dismiss();

			}
		});
		
		return view;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	  Dialog dialog = super.onCreateDialog(savedInstanceState);

	  // request a window without the title
	  dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
	  return dialog;
	}
	
	//Passing data from fragment to activity
	//----------------------------------------
	//To pass data back to main activity, create an interface and implement it in main activity
	public interface OnDataPass {
	    public void onDataPass(String newTweetBody, long replyTweetUid);
	}
	OnDataPass dataPasser;
	@Override
	public void onAttach(Activity a) {
	    super.onAttach(a);
	    dataPasser = (OnDataPass) a;
	}


	
}
