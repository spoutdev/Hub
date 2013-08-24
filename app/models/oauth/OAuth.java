package models.oauth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import models.User;
import play.mvc.Result;

public abstract class OAuth {
	@Getter (AccessLevel.PROTECTED)
	private String endPoint;
	@Getter (AccessLevel.PROTECTED)
	private String tokenEndPoint;
	@Getter (AccessLevel.PROTECTED)
	private String apiUrl;
	@Getter (AccessLevel.PROTECTED)
	private String publicKey;
	@Getter (AccessLevel.PROTECTED)
	private String privateKey;
	@Getter (AccessLevel.PROTECTED)
	@Setter (AccessLevel.PROTECTED)
	private String accessToken;

	public OAuth(String endPoint, String tokenEndPoint, String apiUrl, String publicKey, String privateKey) {
		this.endPoint = endPoint;
		this.tokenEndPoint = tokenEndPoint;
		this.apiUrl = apiUrl;

		this.publicKey = publicKey;
		this.privateKey = privateKey;
	}

	public abstract Result authorise();

	public abstract OAuthResult authoriseResult();

	public abstract OAuthResult access(OAuthResult result);

	protected String getAuthParams() {
		return "?";
	}

	protected abstract String getServiceName();

	public abstract void create(User user);

	public abstract void destroy(User user);

	protected Map<String, String> request(String Url) {
		return request(Url, false);
	}

	protected Map<String, String> request(String Url, boolean post) {
		BufferedReader in = null;
		try {
			URL url = new URL(Url);
			HttpURLConnection con = (HttpURLConnection) (url.openConnection());
			if (post) {
				con.setRequestMethod("POST");
			}
			System.setProperty("http.agent", "");
			con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/534.30 (KHTML, like Gecko) Chrome/12.0.742.100 Safari/534.30");
			in = new BufferedReader(new InputStreamReader(con.getInputStream()));

			Map<String, String> params = getQueryMap(in.readLine());
			in.close();
			return params;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	protected Map<String, String> getQueryMap(String query) {
		if (query.contains("&")) {
			String[] params = query.split("&");
			Map<String, String> map = new HashMap<String, String>();
			for (String param : params) {
				String[] paramArr = param.split("=");
				map.put(paramArr[0], paramArr[1]);
			}
			return map;
		}
		return null;
	}
}
