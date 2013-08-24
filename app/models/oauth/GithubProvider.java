package models.oauth;

import com.google.gson.JsonObject;
import models.User;

public class GithubProvider extends OAuth2 {
	private String user;

	public GithubProvider() {
		super("https://github.com/login/oauth/authorize", "https://github.com/login/oauth/access_token", "https://api.github.com/user", "cff2169a2f99f7c51b21", "f57f0911a09d7619519a4141420631cbd9b1fec2");
	}

	@Override
	protected String getServiceName() {
		return "Github";
	}

	@Override
	protected void populateData(JsonObject jsonObject) {
		user = jsonObject.get("login").getAsString();
	}

	@Override
	public void create(User user) {
		user.getGithubLink().create(this.user, getAccessToken());
	}

	@Override
	public void destroy(User user) {
		user.getGithubLink().destroy();
	}
}
