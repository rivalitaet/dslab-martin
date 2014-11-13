package controller;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import model.computation.CalculationException;
import model.computation.Calculator;

public class RemoteCalculator implements Calculator, Runnable, Closeable {

	private final int bufferLength = 1024;
	private final PrintStream userResponseStream;
	private final DatagramSocket socket;

	private final ConcurrentMap<InetSocketAddress, Node> nodes = new ConcurrentHashMap<>();

	private volatile boolean isRunning = false;

	public RemoteCalculator(int udpPort, PrintStream userResponseStream) throws SocketException {
		this.userResponseStream = userResponseStream;
		socket = new DatagramSocket(udpPort);
	}

	public Iterator<Node> getNodes() {
		return nodes.values().iterator();
	}

	@Override
	public int add(int a, int b) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int substract(int a, int b) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int multiply(int a, int b) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int divide(int a, int b) throws CalculationException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void run() {
		isRunning = true;

		while (isRunning) {
			try {
				byte[] buf = new byte[bufferLength];
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				socket.receive(packet);
				handlePacket(packet);
			} catch (IOException e) {
				String msg = "Error while receiving datagram packages: ";
				userResponseStream.print(msg);
				e.printStackTrace(userResponseStream);
			}
		}

	}

	private void handlePacket(DatagramPacket packet) {
		String line = null;

		try {
			line = new String(packet.getData(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// nobody expects this to happen
			e.printStackTrace();
			return;
		}

		// !alive <port> <operation>
		String[] parts = line.split("\\s");

		if (parts.length != 3) {
			String msg = String.format("Received malformed packet: '%s' (not 3 parts long)");
			userResponseStream.print(msg);
		}

		int port = Integer.parseInt(parts[1]);

		InetSocketAddress address = new InetSocketAddress(packet.getAddress(), port);
		Node node = new Node(address, parts[2]);

		nodes.putIfAbsent(node.getAddress(), node);
	}

	@Override
	public void close() throws IOException {
		isRunning = false;
	}
}
