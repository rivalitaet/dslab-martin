package controller;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import util.StringUtils;

public class Node {

	private final InetSocketAddress address;
	private final Set<String> operations = new HashSet<>();

	private volatile int usage = 0;
	private volatile Date lastActive = new Date();
	private final long maxTimeout;

	public Set<String> getOperations() {
		return operations;
	}

	public synchronized void increaseUsage(String result) {
		int digits = result.trim().length();
		usage += 50 * digits;
	}

	public void updateActive() {
		lastActive = new Date();
	}

	public boolean isOnline() {
		Date now = new Date();
		long diff = now.getTime() - lastActive.getTime();

		System.out.println("diff. " + diff);

		return diff < maxTimeout;
	}

	public Node(InetSocketAddress address, String operations, long maxTimeout) {
		this.address = address;
		this.maxTimeout = maxTimeout;

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
		String onOff = isOnline() ? "online" : "offline";
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