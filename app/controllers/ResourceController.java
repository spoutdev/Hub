package controllers;

import models.resource.Resource;
import play.mvc.Result;

import static play.mvc.Results.TODO;
import static play.mvc.Results.ok;
import static play.mvc.Results.redirect;

public class ResourceController {
	/**
	 * This result directly redirect to application home.
	 */
	public static Result GO_HOME = redirect(
			routes.ResourceController.list(0, "name", "asc", "")
	);

	/**
	 * Handle default path requests, redirect to computers list
	 */
	public static Result index() {
		return GO_HOME;
	}

	/**
	 * Display the paginated list of computers.
	 *
	 * @param page Current page number (starts from 0)
	 * @param sortBy Column to be sorted
	 * @param order Sort order (either asc or desc)
	 * @param filter Filter applied on computer names
	 */
	public static Result list(int page, String sortBy, String order, String filter) {

		return ok(
				views.html.resource.list.render(
						Resource.page(page, 10, sortBy, order, filter),
						sortBy, order, filter
				)
		);
	}

	public static Result show(Long id) {
		return TODO;
	}
}
