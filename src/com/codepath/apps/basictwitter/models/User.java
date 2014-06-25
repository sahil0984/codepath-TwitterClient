package com.codepath.apps.basictwitter.models;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "Users")
public class User extends Model implements Serializable  {
	private static final long serialVersionUID = -4310710562272456418L;
	
	@Column(name = "name")
	private String name;
    @Column(name = "uid")//, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
	private long uid;
    @Column(name = "screenName")
	private String screenName;
    @Column(name = "profileImageUrl")
	private String profileImageUrl;
    @Column(name = "verifiedStatus")
	private boolean verifiedStatus;


	public String getName() {
		return name;
	}

	public long getUid() {
		return uid;
	}

	public String getScreenName() {
		return screenName;
	}

	public String getProfileImageUrl() {
		return profileImageUrl;
	}
	
	public boolean getVerifiedStatus() {
		return verifiedStatus;
	}
	
	//User.fromJSON(...)
	public static User fromJSON(JSONObject jsonObject) {
		User user = new User();
		//Extract the values from JSON and populate the model
		try {
			user.name = jsonObject.getString("name");
			user.uid = jsonObject.getLong("id");
			user.screenName = jsonObject.getString("screen_name");
			user.profileImageUrl = jsonObject.getString("profile_image_url");
			user.verifiedStatus = (jsonObject.getString("verified")=="true")?true:false;
		}
		catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return user;
	}
	
}
