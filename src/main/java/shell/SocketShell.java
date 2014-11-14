package shell;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

public class SocketShell extends AbstractShell {

	protected String getCommandSign() {
		return "@";
	}

	public SocketShell(String name, InputStream in, OutputStream out) {
		super(name, in, out);
		this.name = name;
	}

	@Override
	public void run() {
		try {
			for (String line; !Thread.currentThread().isInterrupted() && (line = readLine()) != null;) {
				Object result;

				try {
					result = invoke(line.trim());
				} catch (IllegalArgumentException e) {
					result = "error:illegal_command:" + e.getMessage();
					// e.printStackTrace();
				} catch (Throwable throwable) {
					ByteArrayOutputStream str = new ByteArrayOutputStream(1024);
					throwable.printStackTrace(new PrintStream(str, true));
					result = "error:exception " + str.toString();
				}
				if (result != null) {
					print(result);
				}
			}
		} catch (IOException e) {
			try {
				writeLine("Shell closed");
			} catch (IOException ex) {
				// System.out.println(ex.getClass().getName() + ": " + ex.getMessage());
			}
		}
	}

	public void writeLine(String line) throws IOException {
		if (line.indexOf('\n') >= 0 && line.indexOf('\n') < line.length() - 1) {
			for (String l : line.split("[\\r\\n]+")) {
				write((l + "\n").getBytes());
			}
		} else {
			String msg = line += line.endsWith("\n") ? "" : "\n";
			write(msg.getBytes());
		}
	}
}
