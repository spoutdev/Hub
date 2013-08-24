package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum Timezones {
	UM12(-12, "-12:00", "Baker/Howland Island"),
	UM11(-11, "-11:00", "Samoa Time Zone, Niue"),
	UM10(-10, "-10:00", "Hawaii-Aleutian Standard Time, Cook Islands, Tahiti"),
	UM95(-9.5, "-09:30", "Marquesas Islands"),
	UM9(-9, "-09:00", "Alaska Standard Time, Gambier Islands"),
	UM8(-8, "-08:00", "Pacific Standard Time, Clipperton Island"),
	UM7(-7, "-07:00", "Mountain Standard Time"),
	UM6(-6, "-06:00", "Central Standard Time"),
	UM5(-5, "-05:00", "Eastern Standard Time, Western Caribbean Standard Time"),
	UM45(-4.5, "-04:30", "Venezuelan Standard Time"),
	UM4(-4, "-04:00", "Atlantic Standard Time, Eastern Caribbean Standard Time"),
	UM35(-3.5, "-03:30", "Newfoundland Standard Time"),
	UM3(-3, "-03:00", "Argentina, Brazil, French Guiana, Uruguay"),
	UM2(-2, "-02:00", "South Georgia/South Sandwich Islands"),
	UM1(-1, "-01:00", "Azores, Cape Verde Islands"),
	UTC(0, "+00:00", "Greenwich Mean Time, Western European Time"),
	UP1(1, "+01:00", "Central European Time, West Africa Time"),
	UP2(2, "+02:00", "Central Africa Time, Eastern European Time, Kaliningrad Time"),
	UP3(3, "+03:00", "Moscow Time, East Africa Time"),
	UP35(3.5, "+03:30", "Iran Standard Time"),
	UP4(4, "+04:00", "Azerbaijan Standard Time, Samara Time"),
	UP45(4.5, "+04:30", "Afghanistan"),
	UP5(5, "+05:00", "Pakistan Standard Time, Yekaterinburg Time"),
	UP55(5.5, "+05:30", "Indian Standard Time, Sri Lanka Time"),
	UP575(5.75, "+05:45", "Nepal Time"),
	UP6(6, "+06:00", "Bangladesh Standard Time, Bhutan Time, Omsk Time"),
	UP65(6.5, "+06:30", "Cocos Islands, Myanmar"),
	UP7(7, "+07:00", "Krasnoyarsk Time, Cambodia, Laos, Thailand, Vietnam"),
	UP8(8, "+08:00", "Australian Western Standard Time, Beijing Time, Irkutsk Time"),
	UP875(8.75, "+08:45", "Australian Central Western Standard Time"),
	UP9(9, "+09:00", "Japan Standard Time, Korea Standard Time, Yakutsk Time"),
	UP95(9.5, "+09:30", "Australian Central Standard Time"),
	UP10(10, "+10:00", "Australian Eastern Standard Time, Vladivostok Time"),
	UP105(10.5, "+10:30", "Lord Howe Island"),
	UP11(11, "+11:00", "Magadan Time, Solomon Islands, Vanuatu"),
	UP115(11.5, "+11:30", "Norfolk Island"),
	UP12(12, "+12:00", "Fiji, Gilbert Islands, Kamchatka Time, New Zealand Standard Time"),
	UP1275(12.75, "+12:45", "Chatham Islands Standard Time"),
	UP13(13, "+13:00", "Phoenix Islands Time, Tonga"),
	UP14(14, "+14:00", "Line Islands"),;
	private double offset;
	private String offsetTxt;
	private String name;

	private Timezones(double offset, String offsetTxt, String name) {
		this.offset = offset;
		this.offsetTxt = offsetTxt;
		this.name = name;
	}

	public static Map<String, String> getSelect() {
		Map<String, String> result = new HashMap<String, String>();
		for (Timezones tz : values()) {
			result.put(String.valueOf(tz.offset), "(UTC" + tz.offsetTxt + ") " + tz.name);
		}
		return result;
	}

	public static List<String> getKeys() {
		List<String> keys = new ArrayList<String>();
		for (Timezones tz : values()) {
			keys.add(String.valueOf(tz.offset));
		}
		return keys;
	}

	public double getOffset() {
		return offset;
	}

	private static Map<Double, Timezones> dToTz = new HashMap<Double, Timezones>();

	static {
		for (Timezones tz : values()) {
			dToTz.put(tz.getOffset(), tz);
		}
	}

	public static Timezones getFromOffset(double offset) {
		return dToTz.get(offset);
	}

	public String getStringOffset() {
		return offsetTxt;
	}

	public String getName() {
		return name;
	}
}
