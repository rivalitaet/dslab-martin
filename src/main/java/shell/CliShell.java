package shell;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Date;

public class CliShell extends AbstractShell {

	protected String getCommandSign() {
		return "!";
	}

	public CliShell(String name, InputStream in, OutputStream out) {
		super(name, in, out);
	}

	@Override
	public void run() {
		try {
			for (String line; !Thread.currentThread().isInterrupted() && (line = readLine()) != null;) {
				write(String.format("%s\t\t%s> %s%n", DATE_FORMAT.get().format(new Date()), name, line).getBytes());
				Object result;
				try {
					result = invoke(line.trim());
				} catch (IllegalArgumentException e) {
					result = e.getMessage();
					// e.printStackTrace();
				} catch (Throwable throwable) {
					ByteArrayOutputStream str = new ByteArrayOutputStream(1024);
					throwable.printStackTrace(new PrintStream(str, true));
					result = str.toString();
				}
				if (result != null) {
					print(result);
				}
			}
		} catch (IOException e) {
			try {
				writeLine("Shell closed");
			} catch (IOException ex) {
				System.out.println(ex.getClass().getName() + ": " + ex.getMessage());
			}
		}
	}

	@Override
	public void writeLine(String line) throws IOException {
		String now = DATE_FORMAT.get().format(new Date());
		if (line.indexOf('\n') >= 0 && line.indexOf('\n') < line.length() - 1) {
			write((String.format("%s\t\t%s:\n", now, name)).getBytes());
			for (String l : line.split("[\\r\\n]+")) {
				write((String.format("%s\t\t%s\n", now, l)).getBytes());
			}
		} else {
			write((String.format("%s\t\t%s: %s%s", now, name, line, line.endsWith("\n") ? "" : "\n")).getBytes());
		}
	}

}
