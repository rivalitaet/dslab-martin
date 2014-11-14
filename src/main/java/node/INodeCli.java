package node;

import java.io.IOException;

public interface INodeCli {

	/**
	 * Performs a shutdown of the node and release all resources.<br/>
	 * Shutting down an already terminated node has no effect.
	 * <p/>
	 * E.g.:
	 * 
	 * <pre>
	 * &gt; !exit
	 * Shutting down node now
	 * </pre>
	 * 
	 * @return any message indicating that the node is going to terminate
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	String exit() throws IOException;

}
