package controller;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class User {

	private final String username;
	private final String hash;

	private long credits;
	private ClientConnection connection = null;

	public synchronized boolean isLoggedIn() {
		return this.connection != null;
	}

	public synchronized void login(ClientConnection newConnection) throws CommandException {
		if (this.connection != null) {
			throw new CommandException("You are already logged in");
		}

		this.connection = newConnection;
	}

	public synchronized void logout() {
		this.connection = null;
	}

	public User(String username, String password, int credits) {
		this.username = username;
		this.hash = calcHash(username, password);
		this.credits = credits;
	}

	public String getUsername() {
		return username;
	}

	public String getHash() {
		return hash;
	}

	public synchronized long getCredits() {
		return credits;
	}

	@Override
	public synchronized String toString() {
		String onoff = isLoggedIn() ? "online" : "offline";
		return String.format("%-10s %-7s Credits: %3d", getUsername(), onoff, getCredits());
	}

	public synchronized void changeCredits(long delta) throws CommandException {
		if (delta < 0 && credits + delta < 0) {
			throw new CommandException("Not enough money");
		}

		credits += delta;
	}

	public void addCredits(long newCredits) throws CommandException {
		if (newCredits < 0) {
			throw new CommandException("New credits < 0");
		}

		changeCredits(newCredits);
	}

	public void charge(long price) throws CommandException {
		if (price < 0) {
			throw new CommandException("User.charged(< 0)! This should not happend");
		}

		changeCredits(-price);
	}

	public final static String calcHash(String username, String password) {
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

}
