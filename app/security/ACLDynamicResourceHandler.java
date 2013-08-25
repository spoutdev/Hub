package security;

import java.util.List;

import be.objectify.deadbolt.core.models.Subject;
import be.objectify.deadbolt.java.DeadboltHandler;
import be.objectify.deadbolt.java.DynamicResourceHandler;

import enums.ACLValue;
import models.GroupACL;
import play.mvc.Http;

public class ACLDynamicResourceHandler implements DynamicResourceHandler {
	@Override
	public boolean isAllowed(String name, String meta, DeadboltHandler deadboltHandler, Http.Context context) {
		boolean result = false;
		System.out.println(name);
		System.out.println(meta);
		Subject subject = deadboltHandler.getSubject(context);
		if (subject != null) {
			List<GroupACL> permissionsList = (List<GroupACL>) subject.getPermissions();
			for (GroupACL acl : permissionsList) {
				System.out.println(acl);
				//System.out.println(acl.acl);
				//System.out.println(acl.acl.id);
				if (acl.path.equals(name)) {
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

	@Override
	public boolean checkPermission(String permissionValue, DeadboltHandler deadboltHandler, Http.Context context) {
		return false;
	}
}
