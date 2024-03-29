package node;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import model.computation.CalculationException;
import model.computation.Computation;
import model.computation.SimpleCalculator;

public class Calc extends SimpleCalculator implements Runnable, Closeable {

	private final Socket socket;
	private final Scanner inputScanner;
	private final PrintWriter outputWriter;
	private final Logger logger;
	private final String operators;

	public Calc(Socket socket, Logger logger, String operators) throws IOException {
		this.socket = socket;
		inputScanner = new Scanner(socket.getInputStream());
		outputWriter = new PrintWriter(socket.getOutputStream(), true);
		this.logger = logger;
		this.operators = operators;
	}

	@Override
	public void run() {
		if (inputScanner.hasNextLine()) {
			String line = inputScanner.nextLine();
			String output;

			try {
				if (line.split("\\s").length == 3) {
					Computation c = Computation.getComputation(line, this, operators);
					output = Integer.toString(c.getResult());
				} else {
					throw new CalculationException("Only 'a <op> b' is allowed");
				}
			} catch (CalculationException e) {
				output = "Error: " + e.getMessage();
			}

			logger.log(line, output);
			outputWriter.println(output);

		} else {
			String msg = "No input received :/ ";
			logger.log(msg, "");
			outputWriter.write(msg);
		}

		try {
			close();
		} catch (IOException e) {
			// I'll be gone :)
			e.printStackTrace();
		}
	}

	@Override
	public void close() throws IOException {
		try {
			socket.close();
		} catch (IOException e) {
			// We can't do much about it
			e.printStackTrace();
		}

		inputScanner.close();
		outputWriter.close();
	}

}
