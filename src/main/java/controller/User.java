package controller;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class User {

	private final String username;
	private final String hash;
	private int credits;

	public User(String username, String password, int credits) {
		this.username = username;
		this.hash = calcHash(username, password);
		this.credits = credits;
	}

	public static String calcHash(String username, String password) {
		try {
			String key = username + password;

			MessageDigest crypt = MessageDigest.getInstance("SHA-1");
			crypt.reset();
			crypt.update(key.getBytes("UTF-8"));

			return new BigInteger(1, crypt.digest()).toString(36);

		} catch (NoSuchAlgorithmException e) {
			// this never happens
			e.printStackTrace();
			System.exit(-1);
		} catch (UnsupportedEncodingException e) {
			// this also never happens
			e.printStackTrace();
			System.exit(-1);
		}

		return null;
	}

	public String getUsername() {
		return username;
	}

	public String getHash() {
		return hash;
	}

	public int getCredits() {
		return credits;
	}

	public void setCredits(int credits) {
		this.credits = credits;
	}

	public String toString() {
		return String.format("%-10s %-7s Credits: %3d, %s", getUsername(), "on/off?", getCredits(), getHash());
	}
}
