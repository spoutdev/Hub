package models.form;

import play.data.validation.Constraints.Email;
import play.data.validation.Constraints.Max;
import play.data.validation.Constraints.Min;
import play.data.validation.Constraints.MinLength;
import play.data.validation.Constraints.Required;
import play.data.validation.Constraints.ValidateWith;
import validator.SpoutConstraints.Captcha;
import validator.SpoutConstraints.Date;
import validator.SpoutConstraints.Equal;
import validator.UniqueEmail;

public class RegisterForm {
	@Required
	@MinLength (3)
	public String firstname;
	@Required
	@MinLength (2)
	public String lastname;
	@Required
	@Email
	@ValidateWith (value = UniqueEmail.class, message = UniqueEmail.errorMessage)
	public String email;
	@Required
	@MinLength (6)
	@Equal (otherField = "password2", message = "Passwords don't match")
	public String password;
	@Required
	public String password2;
	public String dob_day;
	public String dob_month;
	public String dob_year;
	@Date (value = "dob", message = "Invalid date")
	public String dob;
	@Min (0)
	@Max (2)
	public byte gender;
	public String timezone = "0.0";
	@Captcha
	public String captcha;
}
