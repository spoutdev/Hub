package controllers;

import play.Play;
import play.mvc.Controller;
import play.mvc.Result;
import securesocial.core.Identity;
import securesocial.core.Registry;
import service.UserService;

public class Api extends Controller {
	public static Result login(String email, String password) {
		Identity identity = Play.application().plugin(UserService.class).doFindByEmailAndProvider(email, "userpass");
		if (Registry.hashers().get("bcrypt").get().matches(identity.passwordInfo().get(), password)) {
			return ok("VALID");
		} else {
			return ok("INVALID");
		}
	}
}
