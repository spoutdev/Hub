package models;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import play.db.DB;

public class NewPassword {
	@Getter
	private User user;
	@Getter
	private byte[] nonce;
	private Date expire;
	private boolean exists = true;
	private static Map<Integer, NewPassword> newPasswords = new HashMap<Integer, NewPassword>();

	public static NewPassword getNewPassword(User user) {
		if (newPasswords.containsKey(user.getIdent())) {
			return newPasswords.get(user.getIdent());
		}
		NewPassword newPassword = new NewPassword(user);
		newPasswords.put(user.getIdent(), newPassword);
		return newPassword;
	}

	/*
	 * Main Body
	 */

	public NewPassword(User user) {
		this.user = user;

		try {
			PreparedStatement statement = DB.getConnection().prepareStatement("SELECT nonce, expire FROM new_password WHERE userid = ?");
			statement.setInt(1, user.getId());
			ResultSet result = statement.executeQuery();

			if (result.first()) {
				this.nonce = result.getBytes("nonce");
				this.expire = result.getDate("expire");
				return;
			}
		} catch (SQLException e) {
		}
		this.exists = false;
	}

	public boolean generate() {
		try {
			PreparedStatement statement;
			if (this.exists()) {
				statement = DB.getConnection().prepareStatement("UPDATE new_password SET nonce = ?, expire = ? WHERE userid = ?");
			} else {
				statement = DB.getConnection().prepareStatement("INSERT INTO new_password (nonce, expire, userid) VALUES (?, ?, ?)");
			}

			byte[] nonce = CryptoHelper.generateRandomBlock(32);
			Date expire = new Date(new java.util.Date().getTime() + 2 * 60 * 60);
			statement.setBytes(1, nonce);
			statement.setDate(2, expire);
			statement.setInt(3, user.getId());
			int uCount = statement.executeUpdate();

			if (uCount > 0) {
				this.nonce = nonce;
				this.expire = expire;
				this.exists = true;
				return true;
			}
		} catch (SQLException e) {
		}
		return false;
	}

	public boolean hasExpired() {
		return this.expire.getTime() < new java.util.Date().getTime();
	}

	public boolean delete() {
		boolean done = false;
		try {
			PreparedStatement statement = DB.getConnection().prepareStatement("DELETE FROM new_password WHERE userid = ?");
			statement.setInt(1, user.getId());
			done = statement.executeUpdate() > 0;
			if (done) {
				this.exists = false;
			}
		} catch (SQLException e) {
			//We'll return false below
		}
		return done;
	}

	public boolean exists() {
		return this.exists;
	}
}
