package controller;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import model.computation.CalculationException;
import model.computation.Calculator;

public class RemoteCalculator implements Calculator, Runnable, Closeable {

	private final int bufferLength = 1024;
	private final PrintStream userResponseStream;
	private final DatagramSocket socket;
	private final long nodeTimeout;

	private final ConcurrentMap<InetSocketAddress, Node> nodes = new ConcurrentHashMap<>();

	private volatile boolean isRunning = false;

	public RemoteCalculator(int udpPort, PrintStream userResponseStream, long maxNodeTimeout) throws SocketException {
		this.userResponseStream = userResponseStream;
		socket = new DatagramSocket(udpPort);
		this.nodeTimeout = maxNodeTimeout;
	}

	public Iterator<Node> getNodes() {
		return nodes.values().iterator();
	}

	protected Node getNode(String operator) throws CalculationException {
		Iterator<Node> it = getNodes();

		List<Node> possibleNodes = new LinkedList<>();
		while (it.hasNext()) {
			Node node = it.next();
			if (node.isOnline() && node.hasOperation(operator)) {
				possibleNodes.add(node);
			}
		}

		if (possibleNodes.isEmpty()) {
			throw new NoNodeAvailableException(String.format("No calculation-node available for '%s'", operator));
		}

		Collections.sort(possibleNodes);

		return possibleNodes.get(0);
	}

	@Override
	public Set<String> getOperations() {
		Iterator<Node> it = getNodes();
		Set<String> operations = new HashSet<>();

		while (it.hasNext()) {
			Node node = it.next();
			if (node.isOnline()) {
				operations.addAll(node.getOperations());
			}
		}

		return operations;
	}

	protected Socket connect(String operator) throws CalculationException {
		Node node = getNode(operator);

		try {
			Socket socket = new Socket();
			socket.connect(node.getAddress());

			return socket;
		} catch (IOException e) {
			e.printStackTrace();
			throw new CalculationException("Connection to node failed");
		}
	}

	private int send(String msg, Node node) throws CalculationException {
		String line = null;

		Socket socket = null;
		PrintWriter writer = null;
		Scanner sc = null;

		try {
			socket = new Socket();
			socket.connect(node.getAddress());

			writer = new PrintWriter(socket.getOutputStream());
			writer.println(msg);
			writer.flush();
			sc = new Scanner(socket.getInputStream());

			if (sc.hasNextLine()) {
				line = sc.nextLine();

				System.err.println("RECEIVED: " + line);

				if (line.startsWith("Error: ")) {
					throw new CalculationException(line.substring(7));
				}

				return Integer.parseInt(line);
			} else {
				throw new CalculationException("No result from node");
			}

		} catch (IOException e) {
			e.printStackTrace();
			throw new CalculationException("Connection to node failed");
		} catch (NumberFormatException e) {
			throw new CalculationException(line);
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (writer != null) {
				writer.close();
			}
			if (sc != null) {
				sc.close();
			}
		}
	}

	@Override
	public int add(int a, int b) throws CalculationException {
		Node node = getNode("+");
		int result = send(a + " + " + b, node);
		node.increaseUsage(Integer.toString(result));
		return result;
	}

	@Override
	public int substract(int a, int b) throws CalculationException {
		Node node = getNode("-");
		int result = send(a + " - " + b, node);
		node.increaseUsage(Integer.toString(result));
		return result;
	}

	@Override
	public int multiply(int a, int b) throws CalculationException {
		Node node = getNode("*");
		int result = send(a + " * " + b, node);
		node.increaseUsage(Integer.toString(result));
		return result;
	}

	@Override
	public int divide(int a, int b) throws CalculationException {
		Node node = getNode("/");
		int result = send(a + " / " + b, node);
		node.increaseUsage(Integer.toString(result));
		return result;
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
		Node newNode = new Node(address, parts[2], nodeTimeout);

		nodes.putIfAbsent(address, newNode);

		Node realNode = nodes.get(address);
		realNode.updateActive();
	}

	@Override
	public void close() throws IOException {
		isRunning = false;
	}

}
