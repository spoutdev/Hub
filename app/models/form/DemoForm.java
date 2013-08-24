package models.form;

import java.util.Calendar;

import models.User;
import play.data.validation.Constraints.Max;
import play.data.validation.Constraints.Min;
import play.data.validation.Constraints.MinLength;
import play.data.validation.Constraints.Required;
import validator.SpoutConstraints.Date;

public class DemoForm {
	@Required
	@MinLength (3)
	public String firstname;
	@Required
	@MinLength (2)
	public String lastname;
	public String dob_day;
	public String dob_month;
	public String dob_year;
	@Date (value = "dob", message = "Invalid date")
	public String dob;
	@Min (0)
	@Max (2)
	public byte gender;
	public String timezone = "0.0";

	public DemoForm() {

	}

	public DemoForm(User user) {
		firstname = user.getFirstName();
		lastname = user.getLastName();

		Calendar cal = Calendar.getInstance();
		cal.setTime(user.getDOB());
		dob_day = "" + cal.get(Calendar.DAY_OF_MONTH);
		dob_month = "" + (cal.get(Calendar.MONTH) + 1);
		dob_year = "" + cal.get(Calendar.YEAR);

		gender = (byte) user.getGender();
		timezone = "" + user.getTimezone().getOffset();
	}
}
