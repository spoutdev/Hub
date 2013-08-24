package models;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.crypto.digests.MD5Digest;
import org.bouncycastle.crypto.digests.WhirlpoolDigest;
import org.bouncycastle.util.encoders.Base64;

/**
 * Provides Crypto functions used in Steam protocols
 */
public class CryptoHelper {
	private CryptoHelper() {
		throw new AssertionError();
	}

	public static String hexToAscii(byte[] buf) {
		StringBuffer result = new StringBuffer();
		for (byte b : buf) {
			String hex = Integer.toHexString(0xFF & b);
			if (hex.length() == 1) {
				result.append('0');
			}
			result.append(hex);
		}
		return result.toString();
	}

	public static byte[] asciiToHex(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
		}
		return data;
	}

	public static String whirlpool(byte[] input, int iterations) {
		WhirlpoolDigest digest = new WhirlpoolDigest();
		byte[] inputBytes = input;
		String result = "";

		for (int i = 0; i < iterations; i++) {
			byte[] resBuf = new byte[digest.getDigestSize()];
			digest.update(inputBytes, 0, inputBytes.length);
			digest.doFinal(resBuf, 0);

			result = hexToAscii(resBuf);
			inputBytes = concat(result.getBytes(), input);
		}
		return result;
	}

	public static String md5(String input) {
		MD5Digest digest = new MD5Digest();

		byte[] resBuf = new byte[digest.getDigestSize()];
		digest.update(input.getBytes(), 0, input.getBytes().length);
		digest.doFinal(resBuf, 0);

		return hexToAscii(resBuf);
	}

	public static byte[] concat(byte[] first, byte[] second) {
		byte[] result = Arrays.copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}

	/**
	 * Generate an array of random bytes given the input length
	 */
	public static byte[] generateRandomBlock(int size) {
		final byte[] block = new byte[size];
		final SecureRandom random = new SecureRandom();
		random.nextBytes(block);
		return block;
	}

	public static byte[] generateRandomBlock() {
		return generateRandomBlock(16);
	}

	public static boolean slowEquals(String a, String b) {
		int diff = a.length() ^ b.length();
		for (int i = 0; i < a.length() && i < b.length(); i++) {
			diff |= a.charAt(i) ^ b.charAt(i);
		}
		return diff == 0;
	}

	public static String encodeHash(String input, byte[] key) {
		return encodeHash(input, key, "hmacSHA256");
	}

	public static String encodeHash(String input, byte[] key, String hashFunc) {
		try {
			Mac hmacSha256 = Mac.getInstance(hashFunc);
			SecretKeySpec keySpec = new SecretKeySpec(key, hashFunc);
			hmacSha256.init(keySpec);
			hmacSha256.update(input.getBytes(), 0, input.getBytes().length);
			byte[] resBuf = hmacSha256.doFinal();
			//return hexToAscii(Base64.encode(hexToAscii(resBuf).getBytes()));
			return new String(Base64.encode(resBuf));
		} catch (NoSuchAlgorithmException e) {

		} catch (InvalidKeyException e) {

		}
		return "";
	}
}
