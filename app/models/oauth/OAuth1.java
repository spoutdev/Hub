package models.oauth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import models.CryptoHelper;
import play.mvc.Controller;
import play.mvc.Result;

public abstract class OAuth1 extends OAuth {
	@Getter (AccessLevel.PROTECTED)
	@Setter (AccessLevel.PROTECTED)
	private String accessTokenSecret;

	public OAuth1(String requestTokenEndPoint, String authenticateEndPoint, String accessEndPoint, String publicKey, String privateKey) {
		super(requestTokenEndPoint, authenticateEndPoint, accessEndPoint, publicKey, privateKey);
	}

	public final Result authorise() {
		long time = new Date().getTime() / 1000;
		String state = "" + time + CryptoHelper.hexToAscii(CryptoHelper.generateRandomBlock());
		Controller.session("state", state);

		//String params = getAuthParams() + "&oauth_consumer_key=" + getPublicKey() + "&oauth_nonce=" + state + "&oauth_signature_method=HMAC-SHA1&oauth_version=1.0&oauth_timestamp=" + new Date().getTime();
		//System.out.println(getEndPoint() + params);
		Map<String, String> params = new HashMap<String, String>();
		params.put("oauth_consumer_key", getPublicKey());
		params.put("oauth_nonce", state);
		params.put("oauth_signature_method", "HMAC-SHA1");
		params.put("oauth_timestamp", "" + time);
		params.put("oauth_version", "1.0");
		Map<String, String> token = request(params, false, getEndPoint());

		return Controller.redirect(getTokenEndPoint() + "?oauth_token=" + token.get("oauth_token"));
	}

	public final OAuthResult authoriseResult() {
		Map<String, String[]> get = Controller.request().queryString();
		if (get.containsKey("oauth_verifier") && get.containsKey("oauth_token")) {
			return new OAuthResult(true, get.get("oauth_token")[0], get.get("oauth_verifier")[0]);
		} else {
			return new OAuthResult(false, "The user denied the request.");
		}
	}

	public final OAuthResult access(OAuthResult result) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("oauth_consumer_key", getPublicKey());
		params.put("oauth_nonce", Controller.session("state"));
		params.put("oauth_signature_method", "HMAC-SHA1");
		params.put("oauth_timestamp", "" + (new Date().getTime() / 1000));
		params.put("oauth_token", result.getToken());
		params.put("oauth_version", "1.0");
		params.put("oauth_verifier", result.getToken2());

		Map<String, String> results = request(params, true, getApiUrl());

		if (results != null) {
			setAccessToken(results.get("oauth_token"));
			setAccessTokenSecret(results.get("oauth_token_secret"));
			populateData(results);
			return new OAuthResult(true, null);
		} else {
			return new OAuthResult(false, "Error :c");
		}
	}

	protected Map<String, String> request(Map<String, String> params, boolean post, String Url) {
		BufferedReader in = null;
		try {
			Map<String, String> sortedParams = new TreeMap<String, String>(params);
			String concatenatedParams = "";
			for (String key : sortedParams.keySet()) {
				if (!"".equals(concatenatedParams)) {
					concatenatedParams += "&";
				}
				concatenatedParams += URLEncoder.encode(key) + "=" + URLEncoder.encode(sortedParams.get(key));
			}

			String baseString = (post ? "POST" : "GET") + "&" + URLEncoder.encode(Url) + "&" + URLEncoder.encode(concatenatedParams);
			String secret = getPrivateKey() + "&";
			String oauthSignature = CryptoHelper.encodeHash(baseString, secret.getBytes(), "hmacSHA1");
			sortedParams.put("oauth_signature", oauthSignature);

			concatenatedParams = "";
			for (String key : sortedParams.keySet()) {
				if (!"".equals(concatenatedParams)) {
					concatenatedParams += "&";
				}
				concatenatedParams += URLEncoder.encode(key) + "=" + URLEncoder.encode(sortedParams.get(key));
			}

			System.out.println(Url + "?" + concatenatedParams);
			URL url = new URL(Url + "?" + concatenatedParams);
			HttpURLConnection con = (HttpURLConnection) (url.openConnection());
			if (post) {
				con.setRequestMethod("POST");
			}
			System.setProperty("http.agent", "");
			con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/534.30 (KHTML, like Gecko) Chrome/12.0.742.100 Safari/534.30");
			in = new BufferedReader(new InputStreamReader(con.getInputStream()));

			Map<String, String> result = getQueryMap(in.readLine());
			in.close();
			return result;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	protected abstract void populateData(Map<String, String> object);
}
