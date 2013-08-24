package models.link;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import models.User;
import play.db.DB;

public class Github implements Link {
	private String name;
	private String token;
	private User user;
	private boolean exists = true;
	/**
	 * Provider cache, allows multiple requests to be made without more queries
	 */
	private static Map<Integer, Github> githubLink = new HashMap<Integer, Github>();

	public static Github getGithubLink(User user) {
		if (githubLink.containsKey(user.getId())) {
			return githubLink.get(user.getId());
		}
		Github github = new Github(user);
		githubLink.put(user.getId(), github);
		return github;
	}

	/*
	 * Main Body
	 */

	private Github(User user) {
		this.user = user;

		try {
			PreparedStatement statement = DB.getConnection().prepareStatement("SELECT token, name FROM github WHERE userid = ?");
			statement.setInt(1, user.getId());
			ResultSet result = statement.executeQuery();

			if (result.first()) {
				this.token = result.getString("token");
				this.name = result.getString("name");
				return;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		this.exists = false;
	}

	public boolean create(String name, String token) {
		if (!this.exists()) {
			try {
				PreparedStatement statement = DB.getConnection().prepareStatement("INSERT INTO github (userid, token, name) VALUES (?, ?, ?)");
				statement.setInt(1, this.user.getId());
				statement.setString(2, token);
				statement.setString(3, name);
				if (statement.executeUpdate() > 0) {
					this.exists = true;
					this.token = token;
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
				PreparedStatement statement = DB.getConnection().prepareStatement("DELETE FROM github WHERE userid = ?");
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
		return "Github";
	}

	@Override
	public String getHref() {
		return "https://github.com/" + getName();
	}
}
