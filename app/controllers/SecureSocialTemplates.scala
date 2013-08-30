import play.api.templates.{Txt, Html}
import play.api.data.Form
import play.api.mvc.{RequestHeader, Request}
import securesocial.controllers.PasswordChange.ChangeInfo
import securesocial.controllers.Registration.RegistrationInfo
import securesocial.controllers.TemplatesPlugin
import securesocial.core.{Identity, SocialUser, SecuredRequest}

class SecureSocialTemplates(application: play.Application) extends TemplatesPlugin {
	override def getLoginPage[A](implicit request: Request[A], form: Form[(String, String)], msg: Option[String] = None): Html = {
		views.html.auth.login(request, form, msg)
	}

	override def getSignUpPage[A](implicit request: Request[A], form: Form[RegistrationInfo], token: String): Html = {
		views.html.auth.registration.signUp(request, form, token)
	}

	override def getStartSignUpPage[A](implicit request: Request[A], form: Form[String]): Html = {
		views.html.auth.registration.startSignUp(request, form)
	}

	override def getStartResetPasswordPage[A](implicit request: Request[A], form: Form[String]): Html = {
		views.html.auth.registration.startResetPassword(request, form)
	}

	override def getResetPasswordPage[A](implicit request: Request[A], form: Form[(String, String)], token: String): Html = {
		views.html.auth.registration.resetPasswordPage(request, form, token)
	}

	override def getPasswordChangePage[A](implicit request: SecuredRequest[A], form: Form[ChangeInfo]): Html = {
		views.html.auth.passwordChange(request, form)
	}

	override def getNotAuthorizedPage[A](implicit request: Request[A]): Html = {
		views.html.auth.notAuthorized()
	}

	override def getSignUpEmail(token: String)(implicit request: RequestHeader): (Option[Txt], Option[Html]) = {
		(None, Some(views.html.auth.email.signUpEmail(request, token)))
	}

	override def getAlreadyRegisteredEmail(user: Identity)(implicit request: RequestHeader): (Option[Txt], Option[Html]) = {
		(None, Some(views.html.auth.email.alreadyRegisteredEmail(request, user)))
	}

	override def getWelcomeEmail(user: Identity)(implicit request: RequestHeader): (Option[Txt], Option[Html]) = {
		(None, Some(views.html.auth.email.welcomeEmail(request, user)))
	}

	override def getUnknownEmailNotice()(implicit request: RequestHeader): (Option[Txt], Option[Html]) = {
		(None, Some(views.html.auth.email.unknownEmailNotice(request)))
	}

	override def getSendPasswordResetEmail(user: Identity, token: String)(implicit request: RequestHeader): (Option[Txt], Option[Html]) = {
		(None, Some(views.html.auth.email.passwordResetEmail(request, user, token)))
	}

	override def getPasswordChangedNoticeEmail(user: Identity)(implicit request: RequestHeader): (Option[Txt], Option[Html]) = {
		(None, Some(views.html.auth.email.passwordChangedNotice(request, user)))
	}
}
