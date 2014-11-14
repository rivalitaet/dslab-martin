package controller;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

import model.computation.CalculationException;
import model.computation.Computation;
import shell.AbstractShell;
import shell.Command;
import shell.SocketShell;
import util.StringUtils;

public class ClientConnection implements Runnable {

	private final Socket socket;
	private final CloudController controller;
	private final AbstractShell shell;

	private User user = null;

	public ClientConnection(Socket socket, CloudController controller) throws IOException {
		this.socket = socket;
		this.controller = controller;
		this.shell = new SocketShell("login shell", socket.getInputStream(), socket.getOutputStream());
		shell.register(this);
	}

	@Override
	public void run() {
		shell.run();
		close();
	}

	public void close() {
		logout();

		try {
			socket.close();
		} catch (IOException e) {
			// We can't do much about it
			e.printStackTrace();
		}

		shell.close();
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

	@Command("BUY")
	public String buy(long newCredits) {
		if (!isLoggedIn()) {
			return "error:login_first";
		}

		try {
			user.addCredits(newCredits);
		} catch (CommandException e) {
			return "error:negative_add";
		}

		return "success:credits:" + user.getCredits();
	}

	@Command("LIST")
	public String listCommands() {
		if (!isLoggedIn()) {
			return "error:login_first";
		}

		String commands = StringUtils.join("", controller.getCalc().getOperations());

		return "list:" + commands;
	}

	@Command("COMPUTE")
	public String compute(String calculation) {
		if (!isLoggedIn()) {
			return "error:login_first";
		}

		calculation = StringUtils.join(" ", Arrays.asList(calculation.split("\\s+")));

		Computation computation = null;
		try {
			computation = Computation.getComputation(calculation, controller.getCalc());
		} catch (CalculationException e) {
			e.printStackTrace();
			return "error:calculation_error:" + e.getMessage();
		}

		try {
			user.charge(computation.getMaxPrice());

			int result = computation.getResult();
			return "result:" + computation.toString() + ":" + result;

		} catch (CommandException e) {
			return "error:" + e.getMessage();
		} catch (CalculationException e) {
			try {
				// refund, what has been charged too much
				user.addCredits(computation.getMaxPrice() - computation.getPrice());
			} catch (CommandException commandException) {
				// this should never happen!
				return "error:" + commandException.getMessage();
			}
			return "error:calculation_error:" + e.getMessage();
		} finally {

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
			shell.writeLine("success:logged_out");
		} catch (IOException e) {
			// We don't need to handle these errors, because the client is logged out anyways
			e.printStackTrace();
		}

		return null;
	}

}
