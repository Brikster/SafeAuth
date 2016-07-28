package ru.mrbrikster.safeauth;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;

public class Utils {

	public static String toSha256(String string) {
		MessageDigest messageDigest;
		try {
			messageDigest = MessageDigest.getInstance("SHA-256");
			messageDigest.reset();
			messageDigest.update(string.getBytes(Charset.forName("UTF8")));
			byte[] result = messageDigest.digest();
			return new String(Hex.encodeHex(result));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public static String toMd5(String string) {
		MessageDigest messageDigest;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();
			messageDigest.update(string.getBytes(Charset.forName("UTF8")));
			byte[] result = messageDigest.digest();
			return new String(Hex.encodeHex(result));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return null;
	}

}
