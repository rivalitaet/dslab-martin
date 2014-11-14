package test;

import node.INodeCli;
import node.Node;
import util.Config;
import util.TestInputStream;
import util.TestOutputStream;
import client.Client;
import client.IClientCli;
import controller.CloudController;
import controller.ICloudControllerCli;

/**
 * Provides methods for starting an arbitrary amount of various components.
 */
public class ComponentFactory {

	/**
	 * Creates and starts a new client instance using the provided {@link Config} and I/O streams.
	 * 
	 * @param componentName
	 *            the name of the component to create
	 * @return the created component after starting it successfully
	 * @throws Exception
	 *             if an exception occurs
	 */
	public IClientCli createClient(String componentName, TestInputStream in, TestOutputStream out) throws Exception {
		Config config = new Config("client");
		Client client = new Client(componentName, config, in, out);
		return client;
	}

	/**
	 * Creates and starts a new cloud controller instance using the provided {@link Config} and I/O
	 * streams.
	 * 
	 * @param componentName
	 *            the name of the component to create
	 * @return the created component after starting it successfully
	 * @throws Exception
	 *             if an exception occurs
	 */
	public ICloudControllerCli createCloudController(String componentName, TestInputStream in, TestOutputStream out)
	                throws Exception {
		Config config = new Config("controller");
		Config userConfig = new Config("user");
		CloudController controller = new CloudController(componentName, config, userConfig, in, out);
		return controller;
	}

	/**
	 * Creates and starts a new node instance using the provided {@link Config} and I/O streams.
	 * 
	 * @param componentName
	 *            the name of the component to create
	 * @return the created component after starting it successfully
	 * @throws Exception
	 *             if an exception occurs
	 */
	public INodeCli createNode(String componentName, TestInputStream in, TestOutputStream out) throws Exception {
		Config config = new Config(componentName);
		Node node = new Node(componentName, config, in, out);
		return node;
	}
}
