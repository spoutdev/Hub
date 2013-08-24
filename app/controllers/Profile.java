package controllers;

import java.util.HashMap;
import java.util.Map;

import models.Timezones;
import models.User;
import models.form.DemoForm;
import models.form.PassForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.global.template;
import views.html.pages.profile.edit.demo;
import views.html.pages.profile.edit.pass;
import views.html.pages.profile.info;

import static play.data.Form.form;

public class Profile extends Controller {
	public static Result userIndex() {
		return index("0");
	}

	public static Result index(String sUid) {
		int uid = Integer.parseInt(sUid);

		User user = User.getSession();
		User profile = user;
		if (uid > 0) {
			profile = User.getUser(uid);
		}

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("style", "float: left; margin-right: 20px");
		String avatar = profile.getAvatar(data);

		return ok(template.render(info.render(user, profile, avatar)));
	}

	public static Result editEmail(String sUid) {
		User profile = User.getUser(Integer.parseInt(sUid));
		return ok("D:");
	}

	public static Result submitPassword(String sUid) {
		return editPassword(sUid);
	}

	public static Result editPassword(String sUid) {
		User profile = User.getUser(Integer.parseInt(sUid));
		User session = User.getSession();
		if (profile.equals(session) || session.isAdmin()) {
			Form<PassForm> passForm = form(PassForm.class).fill(new PassForm(profile));
			if (request().body().asFormUrlEncoded() != null) {
				passForm = passForm.bindFromRequest();
				if (!passForm.hasErrors()) {
					PassForm pass = passForm.get();
					profile.setPassword(pass.password);
					return redirect("/profile/" + profile.getId());
				}
			}
			return ok(template.render(pass.render(passForm, profile)));
		} else {
			return redirect("/");
		}
	}

	public static Result submitDemo(String sUid) {
		return editDemo(sUid);
	}

	public static Result editDemo(String sUid) {
		User profile = User.getUser(Integer.parseInt(sUid));
		User session = User.getSession();
		if (profile.equals(session) || session.isAdmin()) {
			Form<DemoForm> demoForm = form(DemoForm.class).fill(new DemoForm(profile));
			if (request().body().asFormUrlEncoded() != null) {
				demoForm = demoForm.bindFromRequest();
				if (!demoForm.hasErrors()) {
					DemoForm demo = demoForm.get();
					profile.setDOB(Short.parseShort(demo.dob_year), (byte) (Byte.parseByte(demo.dob_month) - 1), Byte.parseByte(demo.dob_day));
					profile.setGender(demo.gender);
					profile.setName(demo.firstname, demo.lastname);
					profile.setTimezone(Timezones.getFromOffset(Double.parseDouble(demo.timezone)));
					return redirect("/profile/" + profile.getId());
				}
			}
			return ok(template.render(demo.render(demoForm, profile)));
		} else {
			return redirect("/");
		}
	}
}
