package controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ClientConnection implements Runnable, AutoCloseable {

	private final Socket socket;
	private final CloudController controller;
	private final InputStream inputStream;
	private final OutputStream outputStream;

	public ClientConnection(Socket socket, CloudController controller) throws IOException {
		this.socket = socket;
		this.controller = controller;
		inputStream = new BufferedInputStream(socket.getInputStream());
		outputStream = new BufferedOutputStream(socket.getOutputStream());
	}

	@Override
	public void run() {

	}

	@Override
	public void close() throws Exception {
		inputStream.close();
		outputStream.close();
	}

	public void login() {

	}
}
