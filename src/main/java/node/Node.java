package node;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import util.Config;

public class Node implements INodeCli, Runnable {

	private final String componentName;
	private final InputStream userRequestStream;
	private final PrintStream userResponseStream;

	private final long intervall;
	private final int tcpPort;

	private volatile boolean isRunning = false;

	private final ScheduledExecutorService isAlivePool = Executors.newSingleThreadScheduledExecutor();
	private final ExecutorService pool = Executors.newCachedThreadPool();
	private final String operators;

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
		this.userRequestStream = userRequestStream;
		this.userResponseStream = userResponseStream;

		intervall = config.getInt("node.alive");
		tcpPort = config.getInt("tcp.port");
		operators = config.getString("node.operators");

		isAlivePool.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				sendAlive();
			}
		}, 0, intervall, TimeUnit.MILLISECONDS);
	}

	@Override
	public void run() {
		try (ServerSocket serverSocket = new ServerSocket(tcpPort)) {

			while (true) {
				try {
					Socket socket = serverSocket.accept();
					Calc calc = new Calc(socket, operators);
					pool.execute(calc);

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
			e.printStackTrace();
			// who cares, if anything fails here, we can't do nothing anyways :)
		}

	}

	protected void sendAlive() {
		// System.err.println("Send alive");
	}

	@Override
	public String exit() throws IOException {
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

}
