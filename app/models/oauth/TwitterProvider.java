package models.oauth;

import java.util.Map;

import lombok.Getter;
import models.User;

public class TwitterProvider extends OAuth1 {
	@Getter
	private String name;

	public TwitterProvider() {
		super("https://api.twitter.com/oauth/request_token", "https://api.twitter.com/oauth/authenticate", "https://api.twitter.com/oauth/access_token", "VoR9tv7HMegVJMDGGoA", "U74VIgAilZJ2Uy1k98ZYEDt1YrUkztuw17Id4UO61sw");
	}

	@Override
	protected String getServiceName() {
		return "Twitter";
	}

	@Override
	protected void populateData(Map<String, String> result) {
		name = result.get("screen_name");
	}

	@Override
	public void create(User user) {
		user.getTwitterLink().create(name, getAccessToken(), getAccessTokenSecret());
	}

	@Override
	public void destroy(User user) {
		user.getTwitterLink().destroy();
	}
}
