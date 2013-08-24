package models.form;

import models.User;
import play.data.validation.Constraints.MinLength;
import play.data.validation.Constraints.Required;
import validator.SpoutConstraints.Equal;

public class PassForm {
	@Required
	public String oldpassword; // Good luck validating this thing...
	@Required
	@MinLength (6)
	@Equal (otherField = "password2", message = "Passwords don't match")
	public String password;
	@Required
	public String password2;
	private User user;

	public PassForm() {

	}

	public PassForm(User user) {
		this.user = user;
	}
}
