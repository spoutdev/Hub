package service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.joda.time.DateTime;

import securesocial.core.AuthenticationMethod;
import securesocial.core.Identity;
import securesocial.core.IdentityId;
import securesocial.core.OAuth1Info;
import securesocial.core.OAuth2Info;
import securesocial.core.PasswordInfo;
import securesocial.core.SocialUser;
import securesocial.core.java.BaseUserService;
import securesocial.core.java.Token;

import models.LocalToken;
import models.SecurityRole;
import models.User;
import play.Application;
import play.Logger;
import scala.Option;
import scala.Some;

public class UserService extends BaseUserService {
	public UserService(Application application) {
		super(application);
	}

	@Override
	public Identity doSave(Identity identity) {
		User user = null;
		user = User.find.byId(identity.identityId().userId());
		System.out.println("THE USER:" + user);
		if (user == null) {
			System.out.println("NULL");
			setUser(user, identity).save();
		} else {
			setUser(user, identity).update();
		}
		return identity;
	}

	private User setUser(User user, Identity identity) {
		if (user == null) {
			user = new User();
			user.uuid = identity.identityId().userId();
		}
		user.provider = identity.identityId().providerId();
		user.firstName = identity.firstName();
		user.lastName = identity.lastName();
		if (identity.oAuth1Info().isDefined()) {
			user.oauth1Secret = identity.oAuth1Info().get().secret();
			user.oauth1Token = identity.oAuth1Info().get().token();
		}
		if (identity.oAuth2Info().isDefined()) {
			user.oauth2accessToken = identity.oAuth2Info().get().accessToken();
			if (identity.oAuth2Info().get().expiresIn().isDefined()) {
				user.oauth2expiresIn = (Integer) identity.oAuth2Info().get().expiresIn().get();
			}
			if (identity.oAuth2Info().get().refreshToken().isDefined()) {
				user.oauth2refreshToken = identity.oAuth2Info().get().refreshToken().get();
			}
			if (identity.oAuth2Info().get().tokenType().isDefined()) {
				user.oauth2tokenType = identity.oAuth2Info().get().tokenType().get();
			}
		}
		if (identity.email().isDefined()) {
			user.email = identity.email().get();
		}

		if (identity.passwordInfo().isDefined()) {
			user.password = identity.passwordInfo().get().password();
		}
		if (identity.avatarUrl().isDefined()) {
			user.avatarURL = identity.avatarUrl().get();
		}
		if (user.roles.size() == 0) {
			user.roles.add(SecurityRole.findByRoleName("user"));
		}
		return user;
	}

	@Override
	public void doSave(Token token) {
		LocalToken localToken = new LocalToken();
		localToken.uuid = token.uuid;
		localToken.email = token.email;
		try {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			localToken.createdAt = df.parse(token.creationTime.toString("yyyy-MM-dd HH:mm:ss"));
			localToken.expireAt = df.parse(token.expirationTime.toString("yyyy-MM-dd HH:mm:ss"));
		} catch (ParseException e) {
			Logger.error("SqlUserService.doSave(): ", e);
		}
		localToken.isSignUp = token.isSignUp;
		localToken.save();
	}

	@Override
	public Identity doFind(IdentityId identityId) {
		Logger.debug("test");
		User localUser = User.find.byId(identityId.userId());
		if (localUser == null) {
			return null;
		}
		SocialUser socialUser = new SocialUser(
				new IdentityId(localUser.uuid, localUser.provider),
				localUser.firstName,
				localUser.lastName,
				String.format("%s %s", localUser.firstName, localUser.lastName),
				Option.apply(localUser.email),
				Option.apply(localUser.avatarURL),
				new AuthenticationMethod("userPassword"),
				Option.apply(new OAuth1Info(localUser.oauth1Token, localUser.oauth1Secret)),
				Option.apply(new OAuth2Info(localUser.oauth2accessToken, Some.apply(localUser.oauth2tokenType), Some.apply((Object) localUser.oauth2expiresIn), Some.apply(localUser.oauth2refreshToken))),
				Option.apply(new PasswordInfo("bcrypt", localUser.password, null))
		);
		if (Logger.isDebugEnabled()) {
			Logger.debug(String.format("socialUser = %s", socialUser));
		}
		return socialUser;
	}

	@Override
	public Token doFindToken(String token) {
		LocalToken localToken = LocalToken.find.byId(token);
		if (localToken == null) {
			return null;
		}
		Token result = new Token();
		result.uuid = localToken.uuid;
		result.creationTime = new DateTime(localToken.createdAt);
		result.email = localToken.email;
		result.expirationTime = new DateTime(localToken.expireAt);
		result.isSignUp = localToken.isSignUp;
		if (Logger.isDebugEnabled()) {
			Logger.debug(String.format("foundToken = %s", result));
		}
		return result;
	}

	@Override
	public Identity doFindByEmailAndProvider(String email, String providerId) {
		List<User> list = User.find.where().eq("email", email).eq("provider", providerId).findList();
		if (list.size() != 1) {
			return null;
		}
		User localUser = list.get(0);

		SocialUser socialUser = new SocialUser(new IdentityId(localUser.uuid, localUser.provider),
				localUser.firstName, localUser.lastName, String.format("%s %s", localUser.firstName, localUser.lastName),
				Option.apply(localUser.email), null, new AuthenticationMethod("userPassword"),
				null, null, Some.apply(new PasswordInfo("bcrypt", localUser.password, null))
		);
		if (Logger.isDebugEnabled()) {
			Logger.debug(String.format("socialUser = %s", socialUser));
		}
		return socialUser;
	}

	@Override
	public void doDeleteToken(String uuid) {
		LocalToken localToken = LocalToken.find.byId(uuid);
		if (localToken != null) {
			localToken.delete();
		}
	}

	@Override
	public void doDeleteExpiredTokens() {
		List<LocalToken> list = LocalToken.find.where().lt("expireAt", new DateTime().toString()).findList();
		for (LocalToken localToken : list) {
			localToken.delete();
		}
	}
}
