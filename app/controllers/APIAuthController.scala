package controllers

import play.api.mvc._
import securesocial.core._
import play.api.Logger
import play.api.i18n.Messages
import securesocial.core.LoginEvent
import securesocial.core.AccessDeniedException
import scala.Some
import play.api.libs.json.Json

object APIAuthController extends Controller {
  /**
   * The authentication flow for all providers starts here.
   *
   * @param provider The id of the provider that needs to handle the call
   * @return
   */
  def authenticate(provider: String) = handleAuth(provider)

  def authenticateByPost(provider: String) = handleAuth(provider)

  private def handleAuth(provider: String) = Action {
    implicit request =>
      Registry.providers.get(provider) match {
        case Some(p) => {
          try {
            p.authenticate().fold(result => result, {
              user => completeAuthentication(user, session)
            })
          } catch {
            case ex: AccessDeniedException => {
              Ok(Messages("securesocial.login.accessDenied"))
            }

            case other: Throwable => {
              Logger.error("Unable to log user in. An exception was thrown", other)
              Ok(Messages("securesocial.login.errorLoggingIn"))
            }
          }
        }
        case _ => NotFound
      }
  }

  def completeAuthentication(user: Identity, session: Session)(implicit request: RequestHeader): PlainResult = {
    if (Logger.isDebugEnabled) {
      Logger.debug("[securesocial] user logged in : [" + user + "]")
    }
    val withSession = Events.fire(new LoginEvent(user)).getOrElse(session)
    Authenticator.create(user) match {
      case Right(authenticator) => {
        Ok(Json.obj("status" -> "success", "data" -> Json.obj("firstName" -> user.firstName, "lastName" -> user.lastName, "avatarURL" -> user.avatarUrl.get))).withSession(withSession -
          SecureSocial.OriginalUrlKey -
          IdentityProvider.SessionId -
          OAuth1Provider.CacheKey).withCookies(authenticator.toCookie)
      }
      case Left(error) => {
        // improve this
        throw new RuntimeException("Error creating authenticator")
      }
    }
  }
}
