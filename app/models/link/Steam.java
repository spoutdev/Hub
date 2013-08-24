package models.link;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import models.User;
import play.db.DB;

public class Steam implements Link {
	private long steamID;
	private String name;
	private Date updated;
	private User user;
	private boolean exists = true;
	private String apiKey = "A5012BAD44942A50814740D121272150";
	/**
	 * Provider cache, allows multiple requests to be made without more queries
	 */
	private static Map<Integer, Steam> steamLink = new HashMap<Integer, Steam>();

	public static Steam getSteamLink(User user) {
		if (steamLink.containsKey(user.getId())) {
			return steamLink.get(user.getId());
		}
		Steam steam = new Steam(user);
		steamLink.put(user.getId(), steam);
		return steam;
	}

	/*
	 * Main Body
	 */

	private Steam(User user) {
		this.user = user;

		try {
			PreparedStatement statement = DB.getConnection().prepareStatement("SELECT steamid, name, updated FROM steam WHERE userid = ?");
			statement.setInt(1, user.getId());
			ResultSet result = statement.executeQuery();

			if (result.first()) {
				this.steamID = result.getLong("steamid");
				this.name = result.getString("name");
				this.updated = result.getDate("updated");
				return;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		this.exists = false;
	}

	private void ensureUpToDate() {
		if (this.updated.getTime() < new java.util.Date().getTime() - (60 * 60 * 24 * 7)) {
			// Update steam info once per week
			try {
				URL url = new URL("http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key=" + this.apiKey + "&steamids=" + this.steamID);
				HttpURLConnection con = (HttpURLConnection) (url.openConnection());
				System.setProperty("http.agent", "");
				con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/534.30 (KHTML, like Gecko) Chrome/12.0.742.100 Safari/534.30");
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

				JsonObject json = new JsonParser().parse(in).getAsJsonObject();

				JsonObject player = json.getAsJsonObject("response").getAsJsonArray("players").get(0).getAsJsonObject();
				setName(player.get("personaname").getAsString());

				PreparedStatement statement = DB.getConnection().prepareStatement("UPDATE steam SET updated = CURRENT_TIMESTAMP() WHERE userid = ?");
				statement.setInt(1, user.getId());

				if (statement.executeUpdate() > 0) {
					this.updated = new Date(new java.util.Date().getTime());
				}

				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean create(long steamid) {
		if (!this.exists()) {
			try {
				PreparedStatement statement = DB.getConnection().prepareStatement("INSERT INTO steam (userid, steamid) VALUES (?, ?)");
				statement.setInt(1, this.user.getId());
				statement.setLong(2, steamid);
				if (statement.executeUpdate() > 0) {
					this.exists = true;
					this.steamID = steamid;
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
				PreparedStatement statement = DB.getConnection().prepareStatement("DELETE FROM steam WHERE userid = ?");
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

	public boolean setName(String name) {
		if (this.name != name) {
			try {
				PreparedStatement statement = DB.getConnection().prepareStatement("UPDATE steam SET name = ? WHERE userid = ?");
				statement.setString(1, name);
				statement.setInt(2, user.getId());
				if (statement.executeUpdate() > 0) {
					this.name = name;
					return true;
				}
			} catch (SQLException e) {
				//We'll return false below
			}
		}
		return false;
	}

	@Override
	public String getName() {
		this.ensureUpToDate();
		return name;
	}

	public long getSteamID() {
		return steamID;
	}

	@Override
	public boolean exists() {
		return this.exists;
	}

	@Override
	public String getHumanReadableToken() {
		return "Steam";
	}

	@Override
	public String getHref() {
		return "http://steamcommunity.com/profiles/" + getSteamID();
	}
}
