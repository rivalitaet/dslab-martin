package controller;

import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import model.computation.CalculationException;
import model.computation.Calculator;

public class RemoteCalculator implements Calculator, Runnable {

	private final int bufferLength = 1024;
	private final int udpPort;
	private final PrintStream userResponseStream;

	private final SortedSet<Node> nodes = new TreeSet<>();

	public RemoteCalculator(int udpPort, PrintStream userResponseStream) {
		this.udpPort = udpPort;
		this.userResponseStream = userResponseStream;
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

		DatagramSocket socket;
		try {
			socket = new DatagramSocket(udpPort);
		} catch (SocketException e) {
			String msg = String.format("Not possible to create a the Datagram socket on port %s", udpPort);
			userResponseStream.print(msg);
			e.printStackTrace(userResponseStream);
			return;
		}

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

	private void handlePacket(DatagramPacket packet) {
		InetAddress nodeAddress = packet.getAddress();
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

		Node node = new Node(packet.getAddress(), port, parts[2]);
		System.out.println(node);
	}
	public class Node {

		final InetAddress address;
		final int port;

		final Set<String> operations = new HashSet<>();

		public Set<String> getOperations() {
			return operations;
		}

		public Node(InetAddress address, int port, String operations) {
			this.address = address;
			this.port = port;

			String[] ops = operations.split("");
			for (String op : ops) {
				this.operations.add(op);
			}
		}

		/**
		 * e.g.: IP: 127.0.0.1 Port: 10001 offline Usage: 750
		 */
		@Override
		public String toString() {
			String onOff = "on/off";
			long usage = 123;
			return String.format("IP: %s Port: %d %7s Usage: %3d", address.getHostAddress(), port, onOff, usage);
		}
	}
}
