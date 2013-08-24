package models;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import play.db.DB;

public class NewEmail {
	@Getter
	private User user;
	@Getter
	private String email;
	@Getter
	private byte[] nonce;
	private Date expire;
	private boolean exists = true;
	private static Map<Integer, NewEmail> newEmails = new HashMap<Integer, NewEmail>();

	public static NewEmail getNewEmail(User user) {
		if (newEmails.containsKey(user.getIdent())) {
			return newEmails.get(user.getIdent());
		}
		NewEmail newEmail = new NewEmail(user);
		newEmails.put(user.getIdent(), newEmail);
		return newEmail;
	}

	public static NewEmail getNewEmailFromNonce(String email, String strNonce) {
		byte[] nonce = CryptoHelper.asciiToHex(strNonce);

		try {
			PreparedStatement statement = DB.getConnection().prepareStatement("SELECT userid FROM new_email WHERE new_email.email = ? && new_email.nonce = ?");
			statement.setString(1, email);
			statement.setBytes(2, nonce);
			ResultSet result = statement.executeQuery();

			if (result.first()) {
				return getNewEmail(User.getUser(result.getInt("userid")));
			}
		} catch (SQLException e) {
		}
		return null;
	}

	/*
	 * Main Body
	 */

	public NewEmail(User user) {
		this.user = user;

		try {
			PreparedStatement statement = DB.getConnection().prepareStatement("SELECT email, nonce, expire FROM new_email WHERE userid = ?");
			statement.setInt(1, user.getId());
			ResultSet result = statement.executeQuery();

			if (result.first()) {
				this.email = result.getString("email");
				this.nonce = result.getBytes("nonce");
				this.expire = result.getDate("expire");
				return;
			}
		} catch (SQLException e) {
		}
		this.exists = false;
	}

	public boolean generate(String email) {
		try {
			PreparedStatement statement;
			if (this.exists()) {
				statement = DB.getConnection().prepareStatement("UPDATE new_email SET email = ?, nonce = ?, expire = ? WHERE userid = ?");
			} else {
				statement = DB.getConnection().prepareStatement("INSERT INTO new_email (email, nonce, expire, userid) VALUES (?, ?, ?, ?)");
			}

			byte[] nonce = CryptoHelper.generateRandomBlock(32);
			Date expire = new Date(new java.util.Date().getTime() + 2 * 60 * 60);
			statement.setString(1, email);
			statement.setBytes(2, nonce);
			statement.setDate(3, expire);
			statement.setInt(4, user.getId());
			int uCount = statement.executeUpdate();

			if (uCount > 0) {
				this.email = email;
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
			PreparedStatement statement = DB.getConnection().prepareStatement("DELETE FROM new_email WHERE userid = ?");
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
