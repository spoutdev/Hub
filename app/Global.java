import java.util.Arrays;

import models.SecurityRole;
import play.Application;
import play.GlobalSettings;

public class Global extends GlobalSettings{
	private final String[] values = {"user", "moderator", "administrator"};

	@Override
	public void onStart(Application application) {
		initialData();
	}

	// Initialise the base groups.
	private void initialData() {
		if (SecurityRole.find.findRowCount() == 0) {
			for (final String roleName : Arrays.asList(values)) {
				final SecurityRole role = new SecurityRole();
				role.roleName = roleName;
				role.save();
			}
		}
	}
}
