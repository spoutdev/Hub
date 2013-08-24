package models.oauth;

import com.google.gson.JsonObject;
import lombok.Getter;
import models.User;

public class FacebookProvider extends OAuth2 {
	@Getter
	private String id;
	@Getter
	private String name;
	@Getter
	private String username;
	@Getter
	private String email;

	public FacebookProvider() {
		super("https://www.facebook.com/dialog/oauth", "https://graph.facebook.com/oauth/access_token", "https://graph.facebook.com/me", "176342222512595", "04cad5894fb76bf418662c84e7af17a2");
	}

	@Override
	protected String getAuthParams() {
		return super.getAuthParams() + "&scope=email";
	}

	@Override
	protected String getServiceName() {
		return "Facebook";
	}

	@Override
	protected void populateData(JsonObject jsonObject) {
		id = jsonObject.get("id").getAsString();
		name = jsonObject.get("name").getAsString();
		username = jsonObject.get("username").getAsString();
		email = jsonObject.get("email").getAsString();
	}

	@Override
	public void create(User user) {
		user.getFacebookLink().create(name, username, getAccessToken());
	}

	@Override
	public void destroy(User user) {
		user.getFacebookLink().destroy();
	}
}
