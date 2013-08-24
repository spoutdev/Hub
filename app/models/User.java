package models;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import lombok.Getter;
import models.link.Facebook;
import models.link.Github;
import models.link.Minecraft;
import models.link.Steam;
import models.link.Twitter;
import play.db.DB;
import play.mvc.Controller;

public class User {
	private int id;
	@Getter
	private String email;
	private byte[] salt;
	private String password;
	@Getter
	private String firstName;
	@Getter
	private String lastName;
	@Getter
	private int gender;
	private Date dob;
	@Getter
	private Timezones timezone;
	@Getter
	private int rank;
	@Getter
	private boolean activated;
	private Date lastseen;
	private boolean exists = true;
	
	/*
	 * Helper Functions
	 */

	/**
	 * Performs a hash on a pre-salted password This can be used to alter the method for hashing in future. Although the database would also need to be updated.
	 */
	public static String hash(byte[] saltedPassword) {
		return CryptoHelper.whirlpool(saltedPassword, 1000);
	}

	/**
	 * Creates a new user, generating a salt to be permanantly used by them.
	 */
	public User(String email, String firstName, String lastName, String rawPassword, long timestamp, int gender, Timezones timezone) throws SQLException {
		this.password = rawPassword;
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
		this.gender = gender;
		this.dob = new Date(timestamp);
		this.timezone = timezone;

		salt = CryptoHelper.generateRandomBlock(32);
		byte[] toHash = CryptoHelper.concat(salt, rawPassword.getBytes());
		String hash = hash(toHash);

		PreparedStatement statement = DB.getConnection().prepareStatement("INSERT INTO users (email, salt, password, firstname, lastname, dob, gender, timezone) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
		statement.setString(1, email);
		statement.setBytes(2, salt);
		statement.setString(3, hash);
		statement.setString(4, firstName);
		statement.setString(5, lastName);
		statement.setDate(6, dob);
		statement.setInt(7, gender);
		statement.setDouble(8, timezone.getOffset());
		this.id = statement.executeUpdate();

		NewEmail.getNewEmail(this).generate(email);
		beenSeen();
	}

	/**
	 * Stores the user in the session so we can bypass the login screen next time
	 */
	public static void storeSession(User user) {
		Controller.session("user", user.getEmail());
		Controller.session("userid", String.valueOf(user.getId()));
	}

	/**
	 * Retrieves a stored user from the session
	 */
	public static User getSession() {
		String id = Controller.session("userid");
		if (id != null) {
			User user = User.getUser(Integer.parseInt(id));
			if (user.exists() && user.getEmail().equals(Controller.session("user"))) {
				return user;
			}
		}
		return null;
	}

	public static void destroySession() {
		Controller.session("user", null);
	}

	/**
	 * User cache, allows multiple requests for one user to be made without more queries
	 */
	private static Map<Integer, User> users = new HashMap<Integer, User>();

	public static User getUser(int id) {
		if (users.containsKey(id)) {
			return users.get(id);
		}
		User user = new User();
		user.initiate(id);
		users.put(id, user);
		return user;
	}

	public static User getUserFromEmail(String email) {
		int id = 0;
		try {
			PreparedStatement statement = DB.getConnection().prepareStatement("SELECT Id FROM users WHERE email = ?");
			statement.setString(1, email);
			ResultSet result = statement.executeQuery();

			if (result.first()) {
				id = result.getInt("Id");
			}
		} catch (SQLException e) {
			//Invalid user?
		}
		return User.getUser(id);
	}
	
	/*
	 * Main Body
	 */

	public User() {

	}

	/**
	 * Initialise this object by querying about the user
	 */
	private void initiate(int id) {
		this.id = id;

		try {
			PreparedStatement statement = DB.getConnection().prepareStatement("SELECT email, salt, password, firstname, lastname, timezone, rank, active, last_seen, gender, dob FROM users WHERE Id = ?");
			statement.setInt(1, id);
			ResultSet result = statement.executeQuery();

			if (result.first()) {
				this.email = result.getString("email");
				this.salt = result.getBytes("salt");
				this.password = result.getString("password");
				this.activated = result.getBoolean("active");
				this.firstName = result.getString("firstname");
				this.lastName = result.getString("lastname");
				this.lastseen = new Date(result.getTimestamp("last_seen").getTime());
				this.gender = result.getInt("gender");
				this.rank = result.getInt("rank");
				this.dob = result.getDate("dob");
				this.timezone = Timezones.getFromOffset(result.getDouble("timezone"));
				return;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		this.exists = false;
	}

	/**
	 * Takes a raw password and checks its hash is the same of the stored password
	 */
	public boolean checkPassword(String rawPassword) {
		byte[] toHash = CryptoHelper.concat(this.salt, rawPassword.getBytes());
		String hash = hash(toHash);

		return CryptoHelper.slowEquals(hash, this.password) && isActivated();
	}

	/**
	 * Gets the identifying feature of the user
	 */
	public int getIdent() {
		return getId();
	}

	/**
	 * Gets the user's id for internal data use
	 */
	public int getId() {
		return this.id;
	}

	public String getAvatar(Map<String, Object> data) {
		if (!data.containsKey("size")) {
			data.put("size", 250);
		}
		if (!data.containsKey("style")) {
			data.put("style", "");
		}

		return "<img src='" + getAvatarUrl((Integer) data.get("size")) + "' style='width: " + ((Integer) data.get("size")) + "px; height: " + ((Integer) data.get("size")) + "px;" + ((String) data.get("style")) + "' />";
	}

	public String getAvatarUrl() {
		return getAvatarUrl(250);
	}

	public String getAvatarUrl(int size) {
		return "http://www.gravatar.com/avatar/" + CryptoHelper.md5(this.getEmail().trim().toLowerCase()) + "?s=" + size;
	}

	public boolean setPassword(String rawPassword) {
		byte[] salt = CryptoHelper.generateRandomBlock(32);
		byte[] toHash = CryptoHelper.concat(salt, rawPassword.getBytes());
		String hash = hash(toHash);

		try {
			PreparedStatement statement = DB.getConnection().prepareStatement("UPDATE users SET salt = ?, password = ? WHERE email = ?");
			statement.setBytes(1, salt);
			statement.setString(2, password);
			statement.setString(3, getEmail());
			int uCount = statement.executeUpdate();
			if (uCount > 0) {
				this.salt = salt;
				this.password = hash;
				return true;
			}
		} catch (SQLException e) {
			//We'll return false below
		}
		return false;
	}

	public boolean setName(String firstname, String lastname) {
		if (firstname != this.getFirstName() || lastname != this.getLastName()) {
			try {
				PreparedStatement statement = DB.getConnection().prepareStatement("UPDATE users SET firstname = ?, lastname = ? WHERE email = ?");
				statement.setString(1, firstname);
				statement.setString(2, lastname);
				statement.setString(3, getEmail());
				int uCount = statement.executeUpdate();
				if (uCount > 0) {
					this.firstName = firstname;
					this.lastName = lastname;
					return true;
				}
			} catch (SQLException e) {
				//We'll return false below
			}
		}
		return false;
	}

	public String getName() {
		return this.getFirstName() + " " + this.getLastName();
	}

	public String getReverseEmail() {
		char[] email = getEmail().toCharArray();
		char[] rEmail = new char[email.length];

		for (int i = 0; i < email.length; i++) {
			rEmail[i] = email[email.length - i - 1];
		}

		return String.valueOf(rEmail);
	}

	public boolean setEmail(String email) {
		return setEmail(email, true);
	}

	public boolean setEmail(String email, boolean requireActivation) {
		if (this.isActivated() && this.getEmail() != email) {
			if (requireActivation) {
				NewEmail.getNewEmail(this).generate(email);
			} else {
				try {
					PreparedStatement statement = DB.getConnection().prepareStatement("UPDATE users SET email = ? WHERE email = ?");
					statement.setString(1, email);
					statement.setString(2, this.getEmail());
					int uCount = statement.executeUpdate();
					if (uCount > 0) {
						this.email = email;
						return true;
					}
				} catch (SQLException e) {
					//We'll return false below
				}
			}
		}
		return false;
	}

	public boolean setGender(int gender) {
		if (gender != this.getGender()) {
			try {
				PreparedStatement statement = DB.getConnection().prepareStatement("UPDATE users SET gender = ? WHERE email = ?");
				statement.setInt(1, gender);
				statement.setString(2, getEmail());
				int uCount = statement.executeUpdate();
				if (uCount > 0) {
					this.gender = gender;
					return true;
				}
			} catch (SQLException e) {
				//We'll return false below
			}
		}
		return false;
	}

	public String getGenderText() {
		switch (getGender()) {
			case 1:
				return "Male";
			case 2:
				return "Female";
			default:
				return "Not specified";
		}
	}

	public boolean setDOB(short year, byte month, byte day) {
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, day);
		Date date = new Date(cal.getTimeInMillis());
		if (!date.equals(this.dob)) {
			try {
				PreparedStatement statement = DB.getConnection().prepareStatement("UPDATE users SET dob = ? WHERE email = ?");
				statement.setDate(1, date);
				statement.setString(2, getEmail());
				int uCount = statement.executeUpdate();
				if (uCount > 0) {
					this.dob = date;
					return true;
				}
			} catch (SQLException e) {
				//We'll return false below
			}
		}
		return false;
	}

	public Date getDOB() {
		return dob;
	}

	public String getTimeZoneString() {
		return getTimezone().getName();
	}

	public boolean setTimezone(Timezones timezone) {
		if (timezone != this.getTimezone()) {
			try {
				PreparedStatement statement = DB.getConnection().prepareStatement("UPDATE users SET timezone = ? WHERE email = ?");
				statement.setDouble(1, timezone.getOffset());
				statement.setString(2, getEmail());
				int uCount = statement.executeUpdate();
				if (uCount > 0) {
					this.timezone = timezone;
					return true;
				}
			} catch (SQLException e) {
				//We'll return false below
			}
		}
		return false;
	}

	public String localTime(Date date) {
		return localTime(date, "yyyy-MM-dd HH:mm");
	}

	public String localTime(Date date, String format) {
		TimeZone tz = TimeZone.getTimeZone("GMT" + getTimezone().getStringOffset());
		Calendar calendar = Calendar.getInstance(tz);
		calendar.setTime(date);

		return new SimpleDateFormat(format).format(calendar.getTime());
	}

	public boolean isAdmin() {
		return getRank() >= 2;
	}

	/**
	 * This object could refer to a nonexistant user, this will only return true if this user exists
	 */
	public boolean exists() {
		return this.exists;
	}

	public void beenSeen() {
		try {
			PreparedStatement statement = DB.getConnection().prepareStatement("UPDATE users SET last_seen = CURRENT_TIMESTAMP() WHERE email = ?");
			statement.setString(1, getEmail());
			statement.execute();
			this.lastseen = new Date(new java.util.Date().getTime());
		} catch (SQLException e) {

		}
	}

	public Date getLastSeen() {
		return lastseen;
	}

	public Github getGithubLink() {
		return Github.getGithubLink(this);
	}

	public Twitter getTwitterLink() {
		return Twitter.getTwitterLink(this);
	}

	public Facebook getFacebookLink() {
		return Facebook.getFacebookLink(this);
	}

	public Minecraft getMinecraftLink() {
		return Minecraft.getMinecraftLink(this);
	}

	public Steam getSteamLink() {
		return Steam.getSteamLink(this);
	}

	/**
	 * Deletes the user, BE CAREFUL
	 */
	public boolean delete() {
		boolean done = false;
		try {
			PreparedStatement statement = DB.getConnection().prepareStatement("DELETE FROM users WHERE Id = ?");
			statement.setInt(1, getId());
			done = statement.executeUpdate() > 0;
			if (done) {
				this.exists = false;
				NewEmail.getNewEmail(this).delete();
			}
		} catch (SQLException e) {
			//We'll return false below
		}
		return done;
	}
}
