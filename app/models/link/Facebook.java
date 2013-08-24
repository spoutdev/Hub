package models.link;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import models.User;
import play.db.DB;

public class Facebook implements Link {
	private String name;
	private String username;
	private String token;
	private User user;
	private boolean exists = true;
	/**
	 * Provider cache, allows multiple requests to be made without more queries
	 */
	private static Map<Integer, Facebook> facebookLink = new HashMap<Integer, Facebook>();

	public static Facebook getFacebookLink(User user) {
		if (facebookLink.containsKey(user.getId())) {
			return facebookLink.get(user.getId());
		}
		Facebook facebook = new Facebook(user);
		facebookLink.put(user.getId(), facebook);
		return facebook;
	}

	/*
	 * Main Body
	 */

	private Facebook(User user) {
		this.user = user;

		try {
			PreparedStatement statement = DB.getConnection().prepareStatement("SELECT token, name, username FROM facebook WHERE userid = ?");
			statement.setInt(1, user.getId());
			ResultSet result = statement.executeQuery();

			if (result.first()) {
				this.token = result.getString("token");
				this.name = result.getString("name");
				this.username = result.getString("username");
				return;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		this.exists = false;
	}

	public boolean create(String name, String username, String token) {
		if (!this.exists()) {
			try {
				PreparedStatement statement = DB.getConnection().prepareStatement("INSERT INTO facebook (userid, token, name, username) VALUES (?, ?, ?, ?)");
				statement.setInt(1, this.user.getId());
				statement.setString(2, token);
				statement.setString(3, name);
				statement.setString(4, username);
				if (statement.executeUpdate() > 0) {
					this.exists = true;
					this.token = token;
					this.name = name;
					this.username = username;
					return true;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public boolean destroy() {
		if (this.exists()) {
			try {
				PreparedStatement statement = DB.getConnection().prepareStatement("DELETE FROM facebook WHERE userid = ?");
				statement.setInt(1, user.getId());
				if (statement.executeUpdate() > 0) {
					this.exists = false;
				}
			} catch (SQLException e) {
				//We'll return false below
			}
		}
		return !this.exists();
	}

	@Override
	public String getName() {
		return name;
	}

	public String getUserName() {
		return username;
	}

	@Override
	public boolean exists() {
		return this.exists;
	}

	@Override
	public String getHumanReadableToken() {
		return "Facebook";
	}

	@Override
	public String getHref() {
		return "https://facebook.com/" + getUserName();
	}
}
