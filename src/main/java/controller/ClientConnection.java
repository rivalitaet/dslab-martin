package controller;

import java.io.IOException;
import java.net.Socket;

import shell.Command;
import shell.Shell;
import shell.SocketShell;

public class ClientConnection implements Runnable {

	private final Socket socket;
	private final CloudController controller;
	private final Shell loginShell;

	private User user = null;

	public ClientConnection(Socket socket, CloudController controller) throws IOException {
		this.socket = socket;
		this.controller = controller;
		this.loginShell = new SocketShell("login shell", socket.getInputStream(), socket.getOutputStream());
		loginShell.register(this);
	}

	@Override
	public void run() {
		loginShell.run();
	}

	public void close() throws Exception {
		System.err.println("ClientConnection.close() was called");
		socket.close();
		loginShell.close();
	}

	public User getUser() {
		return user;
	}

	public boolean isLoggedIn() {
		return getUser() != null && getUser().isLoggedIn();
	}

	@Command("@LOGIN")
	public String login(String username, String password) {
		try {
			user = controller.login(username, password, this);
		} catch (CommandException e) {
			return "error:" + e.getMessage() + ".";
		}

		return "Successfully logged in.";
	}

	@Command("@LOGOUT")
	public String logout() {
		if (!isLoggedIn()) {
			return "Please login first!";
		}

		getUser().logout();
		user = null;

		try {
			loginShell.writeLine("Auf Wiedersehen!");
		} catch (IOException e) {
			// We don't need to handle these errors, because the client is logged out anyways
			e.printStackTrace();
		}

		return null;
	}
}
