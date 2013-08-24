package models.link;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import models.User;
import play.db.DB;

public class Minecraft implements Link {
	private String uid;
	private String name;
	private User user;
	private boolean exists = true;
	/**
	 * Provider cache, allows multiple requests to be made without more queries
	 */
	private static Map<Integer, Minecraft> minecraftLink = new HashMap<Integer, Minecraft>();

	public static Minecraft getMinecraftLink(User user) {
		if (minecraftLink.containsKey(user.getId())) {
			return minecraftLink.get(user.getId());
		}
		Minecraft minecraft = new Minecraft(user);
		minecraftLink.put(user.getId(), minecraft);
		return minecraft;
	}

	/*
	 * Main Body
	 */

	private Minecraft(User user) {
		this.user = user;

		try {
			PreparedStatement statement = DB.getConnection().prepareStatement("SELECT uid, name FROM minecraft WHERE userid = ?");
			statement.setInt(1, user.getId());
			ResultSet result = statement.executeQuery();

			if (result.first()) {
				this.uid = result.getString("uid");
				this.name = result.getString("name");
				return;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		this.exists = false;
	}

	private boolean verifyLogin(String user, String pass) {
		try {
			URL url = new URL("https://minecraft.net/login?username=" + user + "&password=" + pass);
			HttpURLConnection con = (HttpURLConnection) (url.openConnection());
			con.setRequestMethod("POST");
			System.setProperty("http.agent", "");
			con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/534.30 (KHTML, like Gecko) Chrome/12.0.742.100 Safari/534.30");

			for (String value : con.getHeaderFields().get("Set-Cookie")) {
				if (value.matches("PLAY_SESSION=.*username%3A([a-zA-Z0-9_%40\\.]+)%00;Path=/")) {
					System.out.println("match");
					return true;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	private String[] isPremium(String user, String pass) {
		String result = "Error while querying mc.net";
		try {
			URL url = new URL("https://login.minecraft.net/?version=99999&user=" + user + "&password=" + pass);
			HttpURLConnection con = (HttpURLConnection) (url.openConnection());
			System.setProperty("http.agent", "");
			con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/534.30 (KHTML, like Gecko) Chrome/12.0.742.100 Safari/534.30");
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			result = in.readLine();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (!"Bad login".equals(result)) {
			if (result.contains(":")) {
				String[] info = result.split(":");
				return new String[] {info[2], info[4]};
			} else {
				return new String[] {result};
			}
		}
		return null;
	}

	public String create(String user, String pass) {
		if (!this.exists()) {
			String[] premium = isPremium(user, pass);
			if (premium != null) {
				if (premium.length > 1) {
					try {
						PreparedStatement statement = DB.getConnection().prepareStatement("INSERT INTO minecraft (userid, uid, name) VALUES (?, ?, ?)");
						statement.setInt(1, this.user.getId());
						statement.setString(2, premium[1]);
						statement.setString(3, premium[0]);
						statement.execute();

						this.exists = true;
						this.uid = premium[1];
						this.name = premium[0];
						return null;
					} catch (SQLException e) {
						return "Error processing link";
					}
				} else {
					return premium[0];
				}
			} else if (verifyLogin(user, pass)) {
				return "Account not premium";
			}
			return "Invalid login";
		}
		return "Already linked";
	}

	@Override
	public boolean destroy() {
		if (this.exists()) {
			try {
				PreparedStatement statement = DB.getConnection().prepareStatement("DELETE FROM minecraft WHERE userid = ?");
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
		return "Minecraft";
	}

	@Override
	public String getHref() {
		return null;
	}
}
