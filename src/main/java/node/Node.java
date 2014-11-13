package node;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import shell.AbstractShell;
import shell.CliShell;
import shell.Command;
import util.Config;

public class Node implements INodeCli, Logger, Runnable {

	private final String componentName;
	private final long intervall;
	private final int tcpPort;
	private final String operators;
	private final String logDir;

	private final AbstractShell shell;
	private final PrintStream userResponseStream;

	private final ScheduledExecutorService isAlivePool = Executors.newSingleThreadScheduledExecutor();
	private final ExecutorService pool = Executors.newCachedThreadPool();

	private volatile boolean isRunning = false;

	private static final ThreadLocal<DateFormat> DATE_FORMAT = new ThreadLocal<DateFormat>() {

		@Override
		protected DateFormat initialValue() {
			return new SimpleDateFormat("yyyyMMdd_HHmmss.SSS");
		}
	};

	/**
	 * Needed for shutdown.
	 */
	ServerSocket serverSocket = null;

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
	public Node(String componentName, Config config, InputStream userRequestStream, PrintStream userResponseStream) {
		this.componentName = componentName;
		this.userResponseStream = userResponseStream;

		intervall = config.getInt("node.alive");
		tcpPort = config.getInt("tcp.port");
		operators = config.getString("node.operators");
		logDir = config.getString("log.dir");

		this.shell = new CliShell(componentName, userRequestStream, userResponseStream);
		this.shell.register(this);
		new Thread(this.shell).start();

		isAlivePool.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				sendAlive();
			}
		}, 0, intervall, TimeUnit.MILLISECONDS);
	}

	@Override
	public void run() {
		isRunning = true;

		try (ServerSocket serverSocket = new ServerSocket(tcpPort)) {
			this.serverSocket = serverSocket;

			while (true) {
				try {
					Socket socket = serverSocket.accept();
					Calc calc = new Calc(socket, this, operators);
					pool.execute(calc);

				} catch (IOException e) {
					if (isRunning) {
						userResponseStream.println("IO Error with ServerSocket :/ ");
					} else {
						return;
					}
				} catch (Exception e) {
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
			e.printStackTrace();
			// who cares, if anything fails here, we can't do nothing anyways :)
		}

	}

	protected void sendAlive() {
		// System.err.println("Send alive");
	}

	@Command
	@Override
	public String exit() throws IOException {
		isRunning = false;

		isAlivePool.shutdown();
		pool.shutdown();
		shell.close();

		if (serverSocket != null) {
			serverSocket.close();
		}

		return "Tschau mit au!";
	}

	@Override
	public String history(int numberOfRequests) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param args
	 *            the first argument is the name of the {@link Node} component, which also
	 *            represents the name of the configuration
	 */
	public static void main(String[] args) {
		Node node = new Node(args[0], new Config(args[0]), System.in, System.out);
		new Thread(node).start();
	}

	private void createDirectoryRecursive(Path directoryPath) {
		directoryPath = directoryPath.toAbsolutePath();
		System.out.println("createParent: " + directoryPath);

		if (!Files.exists(directoryPath.getParent())) {
			createDirectoryRecursive(directoryPath.getParent());
		}

		if (!Files.exists(directoryPath)) {
			try {
				Files.createDirectory(directoryPath);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void log(String input, String output) {
		String time = DATE_FORMAT.get().format(new Date());
		String filename = String.format("%s_%s.log", time, componentName);
		Path path = Paths.get(logDir, filename).toAbsolutePath();

		createDirectoryRecursive(path.getParent());

		String msg = input + "\n" + output;

		try {
			Files.write(path, msg.getBytes());
		} catch (IOException e) {
			userResponseStream.println(String.format("Not possible to create logfile '%s'", path));
			e.printStackTrace(userResponseStream);
		}

		// TODO remove
		System.out.println("LOG: " + input + " >==> " + output);
	}
}
