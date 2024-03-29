package admin;

import java.io.InputStream;
import java.io.PrintStream;
import java.rmi.RemoteException;
import java.security.Key;
import java.util.LinkedHashMap;
import java.util.List;

import model.ComputationRequestInfo;
import util.Config;
import controller.IAdminConsole;

/**
 * Please note that this class is not needed for Lab 1, but will later be used in Lab 2. Hence, you
 * do not have to implement it for the first submission.
 */
public class AdminConsole implements IAdminConsole, Runnable {

	private String componentName;
	private Config config;
	private InputStream userRequestStream;
	private PrintStream userResponseStream;

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
	public AdminConsole(String componentName, Config config, InputStream userRequestStream,
	                PrintStream userResponseStream) {
		this.componentName = componentName;
		this.config = config;
		this.userRequestStream = userRequestStream;
		this.userResponseStream = userResponseStream;

		// TODO in lab 2
	}

	@Override
	public void run() {
		// TODO in lab 2
	}

	@Override
	public boolean subscribe(String username, int credits, INotificationCallback callback) throws RemoteException {
		// TODO in lab 2 Auto-generated method stub
		return false;
	}

	@Override
	public List<ComputationRequestInfo> getLogs() throws RemoteException {
		// TODO in lab 2 Auto-generated method stub
		return null;
	}

	@Override
	public LinkedHashMap<Character, Long> statistics() throws RemoteException {
		// TODO in lab 2 Auto-generated method stub
		return null;
	}

	@Override
	public Key getControllerPublicKey() throws RemoteException {
		// TODO in lab 2 Auto-generated method stub
		return null;
	}

	@Override
	public void setUserPublicKey(String username, byte[] key) throws RemoteException {
		// TODO in lab 2 Auto-generated method stub
	}

	/**
	 * @param args
	 *            the first argument is the name of the {@link AdminConsole} component
	 */
	public static void main(String[] args) {
		AdminConsole adminConsole = new AdminConsole(args[0], new Config("admin"), System.in, System.out);
		// TODO in lab 2: start the admin console
	}
}
