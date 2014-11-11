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

	private final String componentName;
	private final InputStream userRequestStream;
	private final PrintStream userResponseStream;
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
		this.componentName = componentName;
		this.userRequestStream = userRequestStream;
		this.userResponseStream = userResponseStream;

		String controllerHost = config.getString("controller.host");
		int controllerPort = config.getInt("controller.tcp.port");
		controllerSocket = new Socket(controllerHost, controllerPort);

		controllerScanner = new Scanner(controllerSocket.getInputStream());
		controllerWriter = new PrintWriter(controllerSocket.getOutputStream(), true);

		this.shell = new CliShell(componentName, userRequestStream, userResponseStream);
		this.shell.register(this);
		shell.run();

	}
	@Override
	@Command
	public String login(final String username, final String password) throws IOException {
		String msg = String.format("!LOGIN %s %s", username, password);

		controllerWriter.println(msg);

		try {
			while (controllerScanner.hasNextLine()) {
				String line = controllerScanner.nextLine();
				System.out.println(line);

				if (line == null) {
					break;
				}
			}

		} catch (IllegalStateException e) {
			System.err.println("Socket to server is closed :/ ");
		}

		System.out.println("DONE ... :)");

		return "_NOTHING_";
	}
	@Override
	public String logout() throws IOException {
		// TODO Auto-generated method stub
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
	public String exit() throws IOException {
		controllerSocket.close();
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

		Client client;
		try {
			client = new Client(args[0], new Config("client"), System.in, System.out);
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
