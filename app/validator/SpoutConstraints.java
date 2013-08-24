package validator;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.Payload;

import net.tanesha.recaptcha.ReCaptchaImpl;
import net.tanesha.recaptcha.ReCaptchaResponse;
import play.data.validation.Constraints.Validator;
import play.mvc.Controller;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static play.libs.F.Tuple;

/**
 * Defines a set of built-in validation constraints.
 */
public class SpoutConstraints {
	/**
	 * Defines a custom validator.
	 */
	@Target ({FIELD})
	@Retention (RUNTIME)
	@Constraint (validatedBy = EqualValidator.class)
	@play.data.Form.Display (name = "constraint.validateequal", attributes = {})
	public static @interface Equal {
		String message() default EqualValidator.message;

		Class<?>[] groups() default {};

		Class<? extends Payload>[] payload() default {};

		String otherField();
	}

	/**
	 * Validator for <code>@Equal</code> fields.
	 */
	public static class EqualValidator extends Validator<Object> implements ConstraintValidator<Equal, Object> {
		final static public String message = "error.invalid";
		String otherField = null;

		public EqualValidator() {
		}

		public void initialize(Equal constraintAnnotation) {
			this.otherField = constraintAnnotation.otherField();
		}

		public boolean isValid(Object object) {
			return object.equals(Controller.request().body().asFormUrlEncoded().get(otherField)[0]);
		}

		public Tuple<String, Object[]> getErrorMessageKey() {
			return Tuple(message, new Object[] {});
		}
	}

	/**
	 * Defines a custom validator.
	 */
	@Target ({FIELD})
	@Retention (RUNTIME)
	@Constraint (validatedBy = DateValidator.class)
	@play.data.Form.Display (name = "constraint.validatedate", attributes = {})
	public static @interface Date {
		String message() default DateValidator.message;

		Class<?>[] groups() default {};

		Class<? extends Payload>[] payload() default {};

		String value();
	}

	/**
	 * Validator for <code>@Date</code> fields.
	 */
	public static class DateValidator extends Validator<Object> implements ConstraintValidator<Date, Object> {
		final static public String message = "error.invalid";
		String id = null;

		public DateValidator() {
		}

		public void initialize(Date constraintAnnotation) {
			this.id = constraintAnnotation.value();
		}

		public boolean isValid(Object object) {
			Map<String, String[]> post = Controller.request().body().asFormUrlEncoded();

			String dateToValidate = post.get(id + "_day")[0] + "/" + post.get(id + "_month")[0] + "/" + post.get(id + "_year")[0];
			String dateFormat = "dd/MM/yyyy";

			SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
			sdf.setLenient(false);

			try {
				sdf.parse(dateToValidate);
			} catch (ParseException e) {
				return false;
			}
			return true;
		}

		public Tuple<String, Object[]> getErrorMessageKey() {
			return Tuple(message, new Object[] {});
		}
	}

	/**
	 * Defines a custom validator.
	 */
	@Target ({FIELD})
	@Retention (RUNTIME)
	@Constraint (validatedBy = CaptchaValidator.class)
	@play.data.Form.Display (name = "constraint.validatecaptcha", attributes = {})
	public static @interface Captcha {
		String message() default CaptchaValidator.message;

		Class<?>[] groups() default {};

		Class<? extends Payload>[] payload() default {};
	}

	/**
	 * Validator for <code>@Captcha</code> fields.
	 */
	public static class CaptchaValidator extends Validator<Object> implements ConstraintValidator<Captcha, Object> {
		final static public String message = "error.invalid";

		public CaptchaValidator() {
		}

		public void initialize(Captcha constraintAnnotation) {

		}

		public boolean isValid(Object object) {
			Map<String, String[]> post = Controller.request().body().asFormUrlEncoded();

			String remoteAddr = Controller.request().remoteAddress();
			ReCaptchaImpl reCaptcha = new ReCaptchaImpl();
			reCaptcha.setPrivateKey("6Ld9NdwSAAAAAGD6N9OAF6NO3dQkg2RcpHawID0w");

			String challenge = post.get("recaptcha_challenge_field")[0];
			String uresponse = post.get("recaptcha_response_field")[0];
			ReCaptchaResponse reCaptchaResponse = reCaptcha.checkAnswer(remoteAddr, challenge, uresponse);

			return reCaptchaResponse.isValid();
		}

		public Tuple<String, Object[]> getErrorMessageKey() {
			return Tuple(message, new Object[] {});
		}
	}
}
