package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
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

	private List<String> parseResult(String result) {
		String[] parts = result.split("[:]");
		List<String> parsed = new LinkedList<>();

		if (parts.length == 3 && parts[0].equals("result")) {
			parsed.add(String.format("Result for %s: %s", parts[1], parts[2]));
			return parsed;
		}

		for (int i = 0; i < parts.length; i++) {
			String prev = i > 0 ? parts[i - 1] : "";
			String current = parts[i];

			if (current == "error") {
				// nothing
			} else if (prev.equals("error") && current.equals("illegal_command")) {
				parsed.add("Internal error, this command does not exist on the server: ");
			} else if (prev.equals("error") && current.equals("exception")) {
				parsed.add("An error occured on the server:");
			} else if (prev.equals("error")) {
				parsed.add("An undefined error occured on the server");
			} else {
				parsed.add("  " + current);
			}
		}

		return null;
	}

	private String receiveLines() {
		try {
			if (controllerScanner.hasNextLine()) {
				String s = controllerScanner.nextLine();

				while (controllerSocket.getInputStream().available() > 0) {
					s += "\n" + controllerScanner.nextLine();
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

		return null;
	}

	@Override
	public String credits() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String buy(long credits) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String list() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String compute(String term) throws IOException {
		// TODO Auto-generated method stub
		return null;
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

	// --- Commands needed for Lab 2. Please note that you do not have to
	// implement them for the first submission. ---
	@Override
	public String authenticate(String username) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	protected class CriticalException extends Exception {

		private static final long serialVersionUID = 6958580904739111049L;
	}

}
