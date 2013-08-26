package service;

import java.util.List;

import enums.ACLValue;
import models.GroupACL;
import models.User;
import securesocial.core.Identity;
import securesocial.core.java.Authorization;

/**
 * A sample authorization implementation that lets you filter requests based on the provider that authenticated the user
 */
public class GeneralACLCheck implements Authorization {
	public boolean isAuthorized(Identity identity, String params[]) {
		boolean result = false;
		User subject = User.find.byId(identity.identityId().userId());
		if (subject != null) {
			List<GroupACL> permissionsList = (List<GroupACL>) subject.getPermissions();
			for (GroupACL acl : permissionsList) {
				if (acl.path.equals(params[0])) {
					ACLValue value = acl.permission;
					if (value.equals(ACLValue.BLOCKED)) {
						result = false;
						break;
					} else if (value.equals(ACLValue.YES)) {
						result = true;
					}
				}
			}
		}
		return result;
	}
}
