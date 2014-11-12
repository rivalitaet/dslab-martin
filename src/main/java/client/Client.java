package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import shell.CliShell;
import shell.Command;
import shell.Shell;
import util.Config;

public class Client implements IClientCli {

	private final Shell shell;
	private final Socket controllerSocket;
	private final Scanner controllerScanner;
	private final PrintWriter controllerWriter;

	/**
	 * @param componentName
	 *            the name of the component - represented in the prompt
	 * @param config
	 *            the configuration to use
	 * @param userRequestStream
	 *            the input stream to read user input from
	 * @param userResponseStream
	 *            the output stream to write the console output to
	 * @throws IOException
	 * @throws UnknownHostException
	 */
	public Client(String componentName, Config config, InputStream userRequestStream, PrintStream userResponseStream)
	                throws UnknownHostException, IOException {
		String controllerHost = config.getString("controller.host");
		int controllerPort = config.getInt("controller.tcp.port");
		controllerSocket = new Socket(controllerHost, controllerPort);

		controllerScanner = new Scanner(controllerSocket.getInputStream());
		controllerWriter = new PrintWriter(controllerSocket.getOutputStream(), true);

		this.shell = new CliShell(componentName, userRequestStream, userResponseStream);
		this.shell.register(this);
		shell.run();
	}

	private String parseError(String type) {
		switch (type) {
			case "login_first" :
				return "Please login first!";

			default :
				return String.format("Error (%s)", type);
		}
	}

	private String parseError(String type, String msg) {
		switch (type) {
			case "exception" :
				if (msg.equals("wrong_credentials")) {
					return "Your username or password is wrong.";
				}
				return "Error: " + msg;

			case "calculation_error" :
				return "Error in Calculation: " + msg;

			default :
				return String.format("Strange error (%s): %s", type, msg);
		}
	}

	private String parseResult(String result) {
		String[] parts = result.split("[:]");

		switch (parts[0]) {
			case "error" :
				if (parts.length == 1) {
					return "Internal error. ";
				} else if (parts.length == 2) {
					return parseError(parts[1]);
				} else {
					return parseError(parts[1], parts[2]);
				}

			case "result" :
				return String.format("Result for %s: %s", parts[1], parts[2]);

			case "success" :
				if (parts.length == 1) {
					return "Success.";
				} else if (parts.length == 2) {
					return parseSuccess(parts[1]);
				} else {
					return parseSuccess(parts[1], parts[2]);
				}

			case "credits" :
				if (parts.length == 1) {
					return "Credits: invalid state";
				} else {
					return String.format("You have %s credits left", parts[1]);
				}

			case "list" :
				if (parts.length == 1) {
					return "List: received illegal state from server";
				} else {
					return String.format("Allowed operations are %s", parts[1]);
				}

			default :
				return "Strange state: " + result;
		}

	}

	private String parseSuccess(String type, String msg) {
		switch (type) {
			case "login_worked" :
				return String.format("Willkommen %s!", msg);

			case "credits" :
				return String.format("Cool. You now have %s credits", msg);

			default :
				return "Success: " + msg;
		}
	}

	private String parseSuccess(String type) {
		switch (type) {
			case "logged_out" :
				return "Auf Wiedersehen!";

			default :
				return "Strange State: " + type;
		}
	}

	private String receiveLines() {
		try {
			if (controllerScanner.hasNextLine()) {
				String s = controllerScanner.nextLine();
				s = parseResult(s);

				while (controllerSocket.getInputStream().available() > 0) {
					String anotherLine = parseResult(s);
					s += "\n" + anotherLine;
				}

				return s;
			}
		} catch (IllegalStateException | IOException e) {
			System.err.println("The server has closed the connection");
			exit();
		}

		return null;
	}

	private void sendLine(String msg) {
		controllerWriter.println(msg);
	}

	@Override
	@Command
	public String login(final String username, final String password) throws IOException {
		String msg = String.format("@LOGIN %s %s", username, password);
		sendLine(msg);
		return receiveLines();
	}

	@Command
	@Override
	public String logout() throws IOException {
		sendLine("@LOGOUT");
		return receiveLines();
	}

	@Command
	@Override
	public String credits() throws IOException {
		sendLine("@CREDITS");
		return receiveLines();
	}

	@Command
	@Override
	public String buy(long credits) throws IOException {
		sendLine("@BUY " + credits);
		return receiveLines();
	}

	@Command
	@Override
	public String list() throws IOException {
		sendLine("@LIST");
		return receiveLines();
	}

	@Command
	@Override
	public String compute(String calculation) throws IOException {
		sendLine(String.format("@COMPUTE %s", calculation));
		return receiveLines();
	}

	@Override
	@Command
	public String exit() {
		System.out.println("Shutting down");
		try {
			controllerSocket.close();
		} catch (IOException e) {
			// we can't do anything here
			e.printStackTrace();
		}
		shell.close();
		return "See you later!";
	}

	/**
	 * @param args
	 *            the first argument is the name of the {@link Client} component
	 */
	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("usage: java Client <client-name>");
			return;
		}

		try {
			new Client(args[0], new Config("client"), System.in, System.out);
		} catch (UnknownHostException e) {
			System.err.println("Cloud-Controller host known");
		} catch (IOException e) {
			System.err.println("Not possible to connect the client");
			e.printStackTrace();
		}
	}

}
