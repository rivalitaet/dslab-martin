package controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

import shell.CliShell;
import shell.Shell;
import util.Config;

public class CloudController implements ICloudControllerCli, Runnable {

	private final String componentName;
	private final Config config;
	private final InputStream userRequestStream;
	private final PrintStream userResponseStream;
	private final Shell shell;

	private final int tcpPort;
	private final int udpPort;

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
	public CloudController(String componentName, Config config, InputStream userRequestStream,
	                PrintStream userResponseStream) {
		this.componentName = componentName;
		this.config = config;
		this.userRequestStream = userRequestStream;
		this.userResponseStream = userResponseStream;
		this.shell = new CliShell(componentName, userRequestStream, userResponseStream);
		this.tcpPort = config.getInt("tcp.port");
		this.udpPort = config.getInt("udp.port");
	}

	private final void handleClient(Socket socket) throws IOException {
		System.out.println("I will handle client");
		socket.close();
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
	public String users() throws IOException {
		// TODO Auto-generated method stub
		return null;
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
		CloudController cloudController = new CloudController(args[0], new Config("controller"), System.in, System.out);
		Thread thread = new Thread(cloudController);
		thread.start();
	}

}
