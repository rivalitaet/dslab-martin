package controller;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;

import util.StringUtils;

public class Node {

	private final InetSocketAddress address;
	private final int usage = 0;

	final Set<String> operations = new HashSet<>();

	public Set<String> getOperations() {
		return operations;
	}

	public Node(InetSocketAddress address, String operations) {
		this.address = address;

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
		String ops = StringUtils.join("", operations);
		String addr = address.getAddress().getHostAddress();
		int port = address.getPort();
		return String.format("IP: %s Port: %d %7s Usage: %3d %s", addr, port, onOff, usage, ops);
	}

	public InetSocketAddress getAddress() {
		return address;
	}
}