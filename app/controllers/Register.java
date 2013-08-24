package controllers;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import models.Timezones;
import models.User;
import models.form.RegisterForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.global.template;
import views.html.pages.account.register;

import static play.data.Form.form;

public class Register extends Controller {
	public static Result index() {
		Form<RegisterForm> userForm = form(RegisterForm.class).fill(new RegisterForm());

		if (request().body().asFormUrlEncoded() != null) {
			userForm = userForm.bindFromRequest();
			if (!userForm.hasErrors()) {
				RegisterForm regForm = userForm.get();

				String dob = regForm.dob_day + "/" + regForm.dob_month + "/" + regForm.dob_year;
				String dateFormat = "dd/MM/yyyy";

				SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
				sdf.setLenient(false);

				try {
					new User(regForm.email, regForm.firstname, regForm.lastname, regForm.password, sdf.parse(dob).getTime(), regForm.gender, Timezones.getFromOffset(Double.parseDouble(regForm.timezone)));
				} catch (ParseException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return ok(template.render(register.render(userForm)));
	}

	public static Result submit() {
		return index();
	}
}
