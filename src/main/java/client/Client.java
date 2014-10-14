package client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

import util.Config;
import cli.Command;
import cli.Shell;

public class Client implements IClientCli, Runnable {

	private final String componentName;
	private final Config config;
	private final InputStream userRequestStream;
	private final PrintStream userResponseStream;
	private final Shell shell;
	private final Socket controllerSocket;
	private final InputStream controllerOutput;
	private final OutputStream controllerInput;

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
		this.config = config;
		this.userRequestStream = userRequestStream;
		this.userResponseStream = userResponseStream;

		String controllerHost = config.getString("controller.host");
		int controllerPort = config.getInt("controller.tcp.port");
		controllerSocket = new Socket(controllerHost, controllerPort);

		controllerOutput = new BufferedInputStream(controllerSocket.getInputStream());
		controllerInput = new BufferedOutputStream(controllerSocket.getOutputStream());

		this.shell = new Shell(componentName, userRequestStream, userResponseStream);
		this.shell.register(this);
	}

	@Override
	public void run() {
		shell.run();
	}

	@Override
	@Command
	public String login(final String username, final String password) throws IOException {
		String msg = String.format("LOGIN %s %s", username, password);

		return msg;
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
		shell.close();
		controllerSocket.close();
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
			client.run();
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
	}

}
