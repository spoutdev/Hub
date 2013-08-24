package controllers;

import models.User;
import models.form.MinecraftForm;
import models.oauth.FacebookProvider;
import models.oauth.GithubProvider;
import models.oauth.OAuth;
import models.oauth.OAuthResult;
import models.oauth.TwitterProvider;
import org.codehaus.jackson.JsonNode;
import play.data.Form;
import play.libs.F.Promise;
import play.libs.Json;
import play.libs.OpenID;
import play.libs.OpenID.UserInfo;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.global.template;
import views.html.pages.profile.links;
import views.html.pages.profile.minecraftlink;

import static play.data.Form.form;

public class Link extends Controller {
	public static Result index() {
		User user = User.getSession();
		if (user != null) {
			return ok(template.render(links.render(user)));
		}
		return redirect("/");
	}

	public static Result githubRemove() {
		return oauth(new GithubProvider(), true);
	}

	public static Result github() {
		return oauth(new GithubProvider(), false);
	}

	public static Result facebookRemove() {
		return oauth(new FacebookProvider(), true);
	}

	public static Result facebook() {
		return oauth(new FacebookProvider(), false);
	}

	public static Result twitterRemove() {
		return oauth(new TwitterProvider(), true);
	}

	public static Result twitter() {
		return oauth(new TwitterProvider(), false);
	}

	public static Result steamRemove() {
		User user = User.getSession();
		if (user != null) {
			user.getSteamLink().destroy();
			return redirect("/link");
		}
		return redirect("/");
	}

	public static Result steam() {
		User user = User.getSession();
		if (user != null) {
			if (request().queryString().containsKey("openid.ns")) {
				Promise<UserInfo> userInfoPromise = OpenID.verifiedId();
				UserInfo userInfo = userInfoPromise.get();
				JsonNode json = Json.toJson(userInfo);
				String[] url = json.get("id").getValueAsText().split("/");
				String id = url[url.length - 1];
				user.getSteamLink().create(Long.valueOf(id));
			} else {
				Promise<String> redirectUrl = OpenID.redirectURL("https://steamcommunity.com/openid", "http://my.spout.org/link/steam", null, null, "http://my.spout.org");

				// Steam is wrong, not play, but we have to play ball if we want a response
				return redirect(redirectUrl.get().replace("https%3A%2F%2Fsteamcommunity.com%2Fopenid", "http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0%2Fidentifier_select"));
			}
			return redirect("/link");
		}
		return redirect("/");
	}

	public static Result minecraftRemove() {
		User user = User.getSession();
		if (user != null) {
			user.getMinecraftLink().destroy();
			return redirect("/link");
		}
		return redirect("/");
	}

	public static Result minecraft() {
		User user = User.getSession();
		if (user != null) {
			Form<MinecraftForm> userForm = form(MinecraftForm.class).fill(new MinecraftForm());

			if (request().body().asFormUrlEncoded() != null) {
				userForm = userForm.bindFromRequest();
				if (!userForm.hasErrors()) {
					return redirect("/link");
				} else if (!userForm.errors().containsKey("username") && userForm.globalErrors().size() > 0) {
					userForm.errors().put("username", userForm.globalErrors());
				}
			}
			return ok(template.render(minecraftlink.render(userForm)));
		}
		return redirect("/");
	}

	public static Result minecraftSubmit() {
		return minecraft();
	}

	private static Result oauth(OAuth provider, boolean remove) {
		User user = User.getSession();
		if (user != null) {
			if (remove) {
				provider.destroy(user);
			} else if (request().queryString().containsKey("state") || request().queryString().containsKey("oauth_token")) {
				OAuthResult result = provider.authoriseResult();
				if (result.isSuccess()) {
					OAuthResult getAuth = provider.access(result);
					if (getAuth.isSuccess()) {
						provider.create(user);
					}
				}
			} else {
				return provider.authorise();
			}
			return redirect("/link");
		}
		return redirect("/");
	}
}
