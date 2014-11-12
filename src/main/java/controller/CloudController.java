package controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import shell.CliShell;
import shell.Command;
import shell.Shell;
import util.Config;
import util.StringUtils;
import controller.computation.Calculator;
import controller.computation.SimpleCalculator;

public class CloudController implements ICloudControllerCli, Runnable {

	private final InputStream userRequestStream;
	private final PrintStream userResponseStream;
	private final Shell shell;

	private final int tcpPort;
	private final int udpPort;

	private final HashMap<String, User> users = new HashMap<>();
	private final HashMap<User, ClientConnection> logins = new HashMap<>();

	private final Calculator calc = new SimpleCalculator();

	/**
	 * @param componentName
	 *            the name of the component - represented in the prompt
	 * @param config
	 *            the configuration to use
	 * @param userRequestStream
	 *            the input stream to read user input from
	 * @param userResponseStream
	 *            the output stream to write the console output to
	 */
	public CloudController(String componentName, Config config, Config userConfig, InputStream userRequestStream,
	                PrintStream userResponseStream) {
		this.userRequestStream = userRequestStream;
		this.userResponseStream = userResponseStream;
		this.shell = new CliShell(componentName, userRequestStream, userResponseStream);
		this.tcpPort = config.getInt("tcp.port");
		this.udpPort = config.getInt("udp.port");

		initUsers(userConfig);
	}

	public Calculator getCalc() {
		return calc;
	}

	private void initUsers(Config userConfig) {
		Set<String> configKeys = userConfig.getKeys();

		for (String key : configKeys) {
			String[] parts = key.split("[.]");

			if (parts.length == 2 && "password".equals(parts[1])) {
				String username = parts[0];
				String password = userConfig.getString(key);
				int credits = userConfig.getInt(username + ".credits");
				User user = new User(username, password, credits);
				users.put(user.getHash(), user);
			}
		}
	}

	/**
	 * Logs in a user by her credentials.
	 * 
	 * @return the matching user, or {@code null} if none matches
	 */
	public User login(String username, String password, ClientConnection connection) throws CommandException {
		String hash = User.calcHash(username, password);

		synchronized (users) {
			if (!users.containsKey(hash)) {
				throw new CommandException("Wrong username or password");
			}

			User user = users.get(hash);
			user.login(connection);

			return user;
		}
	}

	@Override
	public void run() {
		shell.register(this);
		new Thread(shell).start();

		try (ServerSocket serverSocket = new ServerSocket(tcpPort)) {

			while (true) {
				try {
					Socket socket = serverSocket.accept();
					ClientConnection clientConnection = new ClientConnection(socket, this);
					Thread thread = new Thread(clientConnection);
					thread.start();

				} catch (IOException e) {
					userResponseStream.println("A socket caught an exception");
				} catch (Exception e) {
					// TODO really handle closing problems
					userResponseStream.println("AutoClose failed!");
					e.printStackTrace();
				}
			}

		} catch (IOException e) {
			userResponseStream.println("Opening the socket was not possible");
			e.printStackTrace();
		}

		try {
			exit();
		} catch (IOException e) {
			// who cares, if anything fails here, we can't do nothing anyways :)
		}
	}

	@Override
	public String nodes() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Command
	public String users() throws IOException {
		List<String> parts = new LinkedList<>();

		int i = 1;
		for (User user : users.values()) {
			String s = String.format("%2d. %s", i, user.toString());
			parts.add(s);
			i++;
		}

		return StringUtils.join("\n", parts);
	}
	@Override
	public String exit() throws IOException {
		shell.close();
		return "Now I'm gone :)";
	}

	/**
	 * @param args
	 *            the first argument is the name of the {@link CloudController} component
	 */
	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("usage: java CloudController <cloud-name>");
			return;
		}

		Config config = new Config("controller");
		Config userConfig = new Config("user");

		CloudController controller = new CloudController(args[0], config, userConfig, System.in, System.out);
		new Thread(controller).start();
	}

}
