package controller;

import java.io.IOException;
import java.net.Socket;

import shell.Command;
import shell.Shell;
import shell.SocketShell;
import controller.computation.CalculationException;
import controller.computation.Computation;

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

	@Command("LOGIN")
	public String login(String username, String password) {
		try {
			user = controller.login(username, password, this);
			return "success:login_worked:" + user.getUsername();
		} catch (CommandException e) {
			return "error:exception:" + e.getMessage();
		}
	}

	@Command("CREDITS")
	public String credits() {
		if (!isLoggedIn()) {
			return "error:login_first";
		}

		return "credits:" + getUser().getCredits();
	}

	@Command("LIST")
	public String listCommands() {
		if (!isLoggedIn()) {
			return "error:login_first";
		}

		return "list:+-*/";
	}

	@Command("COMPUTE")
	public String compute(String calculation) {
		if (!isLoggedIn()) {
			return "error:login_first";
		}

		try {
			Computation computation = Computation.getComputation(calculation, controller.getCalc());

			int price = computation.getPrice();
			user.charge(price);

			int result = computation.getResult();
			return "result:" + computation.toString() + ":" + result;

		} catch (CommandException e) {
			return "error:" + e.getMessage();
		} catch (CalculationException e) {
			return "error:calculation_error:" + e.getMessage();
		}
	}

	@Command("LOGOUT")
	public String logout() {
		if (!isLoggedIn()) {
			return "error:login_first";
		}

		getUser().logout();
		user = null;

		try {
			loginShell.writeLine("success:logged_out");
		} catch (IOException e) {
			// We don't need to handle these errors, because the client is logged out anyways
			e.printStackTrace();
		}

		return null;
	}

}
