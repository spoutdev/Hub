package controllers;

import play.mvc.Controller;
import play.mvc.Result;

public class _404 extends Controller {
	public static Result index(String uri) {
		return redirect("/");
	}
}
