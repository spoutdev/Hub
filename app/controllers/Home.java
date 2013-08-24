package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.global.template;
import views.html.pages.home;

public class Home extends Controller {
	public static Result index() {
		return ok(template.render(home.render()));
	}
}
