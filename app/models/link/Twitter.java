package models.link;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import models.User;
import play.db.DB;

public class Twitter implements Link {
	private String name;
	private String token;
	private String token2;
	private User user;
	private boolean exists = true;
	/**
	 * Provider cache, allows multiple requests to be made without more queries
	 */
	private static Map<Integer, Twitter> twitterLink = new HashMap<Integer, Twitter>();

	public static Twitter getTwitterLink(User user) {
		if (twitterLink.containsKey(user.getId())) {
			return twitterLink.get(user.getId());
		}
		Twitter twitter = new Twitter(user);
		twitterLink.put(user.getId(), twitter);
		return twitter;
	}

	/*
	 * Main Body
	 */

	private Twitter(User user) {
		this.user = user;

		try {
			PreparedStatement statement = DB.getConnection().prepareStatement("SELECT token, token_secret, name FROM twitter WHERE userid = ?");
			statement.setInt(1, user.getId());
			ResultSet result = statement.executeQuery();

			if (result.first()) {
				this.token = result.getString("token");
				this.token2 = result.getString("token_secret");
				this.name = result.getString("name");
				return;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		this.exists = false;
	}

	public boolean create(String name, String token, String token2) {
		if (!this.exists()) {
			try {
				PreparedStatement statement = DB.getConnection().prepareStatement("INSERT INTO twitter (userid, token, token_secret, name) VALUES (?, ?, ?, ?)");
				statement.setInt(1, this.user.getId());
				statement.setString(2, token);
				statement.setString(3, token2);
				statement.setString(4, name);
				if (statement.executeUpdate() > 0) {
					this.exists = true;
					this.token = token;
					this.token2 = token2;
					this.name = name;
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
				PreparedStatement statement = DB.getConnection().prepareStatement("DELETE FROM twitter WHERE userid = ?");
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

	@Override
	public boolean exists() {
		return this.exists;
	}

	@Override
	public String getHumanReadableToken() {
		return "Twitter";
	}

	@Override
	public String getHref() {
		return "https://twitter.com/" + getName();
	}
}
