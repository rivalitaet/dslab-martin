package controller;

import java.io.IOException;
import java.net.Socket;

import shell.Command;
import shell.Shell;
import shell.SocketShell;

public class ClientConnection implements Runnable {

	private final Socket socket;
	private final CloudController controller;
	private final Shell loginShell;

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
	}

	@Command("@LOGIN")
	public String login() {
		System.err.println("I just logged in :)");
		return "Hello World";
	}
}
