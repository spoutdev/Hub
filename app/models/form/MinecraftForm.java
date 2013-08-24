package models.form;

import models.User;
import play.data.validation.Constraints.Required;

public class MinecraftForm {
	@Required
	public String username;
	@Required
	public String password;

	public String validate() {
		User user = User.getSession();
		if (user != null) {
			return user.getMinecraftLink().create(username, password);
		}
		return null;
	}
}
