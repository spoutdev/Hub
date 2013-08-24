package controllers;

import models.User;
import org.codehaus.jackson.JsonNode;
import play.libs.F.Promise;
import play.libs.Json;
import play.libs.OpenID;
import play.libs.OpenID.UserInfo;
import play.mvc.Controller;
import play.mvc.Result;

public class Auth extends Controller {
	public static Result login() {
		Promise<String> redirectUrl = OpenID.redirectURL("https://auth.spout.org", "http://my.spout.org/auth/verify");
		return redirect(redirectUrl.get());
	}

	public static Result verify() {
		Promise<UserInfo> userInfoPromise = OpenID.verifiedId();
		UserInfo userInfo = userInfoPromise.get();
		JsonNode json = Json.toJson(userInfo);
		if (json.has("id")) {
			String[] parts = json.get("id").asText().split("/");
			int id = Integer.parseInt(parts[parts.length - 1]);
			User.storeSession(User.getUser(id));
		}
		return redirect("/");
	}

	public static Result logout() {
		User.destroySession();
		return redirect("/");
	}
}
