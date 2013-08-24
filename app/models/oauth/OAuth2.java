package models.oauth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Map;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import models.CryptoHelper;
import play.mvc.Controller;
import play.mvc.Result;

public abstract class OAuth2 extends OAuth {
	public OAuth2(String endPoint, String tokenEndPoint, String apiUrl, String publicKey, String privateKey) {
		super(endPoint, tokenEndPoint, apiUrl, publicKey, privateKey);
	}

	public final Result authorise() {
		String state = "" + new Date().getTime() + CryptoHelper.hexToAscii(CryptoHelper.generateRandomBlock());
		Controller.session("state", state);

		String params = getAuthParams() + "&client_id=" + getPublicKey() + "&redirect_uri=http://my.spout.org/link/" + getServiceName().toLowerCase() + "&state=" + state;

		return Controller.redirect(getEndPoint() + params);
	}

	public final OAuthResult authoriseResult() {
		Map<String, String[]> get = Controller.request().queryString();
		if (get.containsKey("code")) {
			if (Controller.session("state").equals(get.get("state")[0])) {
				return new OAuthResult(true, get.get("code")[0]);
			} else {
				return new OAuthResult(false, "You may be the victim of a cross-site forgery request.");
			}
		} else {
			return new OAuthResult(false, get.get("error_description")[0]);
		}
	}

	public final OAuthResult access(OAuthResult result) {
		String Url = getTokenEndPoint() + "?client_id=" + getPublicKey() + "&redirect_uri=http://my.spout.org/link/" + getServiceName().toLowerCase() + "&client_secret=" + getPrivateKey() + "&code=" + result.getToken() + "&state=" + Controller.session("state");

		BufferedReader in = null;
		try {
			Map<String, String> params = request(Url);

			if (params.containsKey("error")) {
				return new OAuthResult(false, params.get("error"));
			} else {
				setAccessToken(params.get("access_token"));

				String apiUrl = getApiUrl() + "?access_token=" + getAccessToken();

				URL url = new URL(apiUrl);
				HttpURLConnection con = (HttpURLConnection) (url.openConnection());
				con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/534.30 (KHTML, like Gecko) Chrome/12.0.742.100 Safari/534.30");
				in = new BufferedReader(new InputStreamReader(con.getInputStream()));

				populateData(new JsonParser().parse(in).getAsJsonObject());
				in.close();

				return new OAuthResult(true, null);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	protected abstract void populateData(JsonObject jsonObject);
}
