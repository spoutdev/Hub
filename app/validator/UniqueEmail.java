package validator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import play.data.validation.Constraints.Validator;
import play.db.DB;
import play.libs.F.Tuple;

public class UniqueEmail extends Validator<String> {
	public final static String errorMessage = "Account with this email already exists";

	@Override
	public boolean isValid(String email) {
		try {
			PreparedStatement statement = DB.getConnection().prepareStatement("SELECT COUNT(Id) as c FROM users WHERE email = ?");
			statement.setString(1, email);
			ResultSet result = statement.executeQuery();
			if (result.first()) {
				return result.getInt("c") == 0;
			}
		} catch (SQLException e) {

		}
		return false;
	}

	/**
	 * Not implemented yet
	 */
	@Override
	public Tuple<String, Object[]> getErrorMessageKey() {
		return new Tuple<String, Object[]>(errorMessage, new Object[] {});
	}
}
