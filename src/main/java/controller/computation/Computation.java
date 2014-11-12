package controller.computation;

import java.util.Scanner;

public abstract class Computation {

	public abstract int getResult() throws CalculationException;

	public abstract int getPrice();

	public static Computation getComputation(String calculation, Calculator calc) throws CalculationException {
		String[] parts = calculation.split("\\s");

		try {
			if (parts.length == 0) {
				throw new CalculationException("Not a valid calculation (to few operands/operations)");
			} else if (parts.length == 1) {
				return new Number(Integer.parseInt(parts[0]));
			}

			if (parts.length % 2 == 0) {
				throw new CalculationException("Not a valid calculation (Wrong number of operands/operations)");
			}

			Computation lastComputation = new Number(Integer.parseInt(parts[0]));

			for (int i = 1; i < parts.length; i++) {
				if (i % 2 == 0) {
					Number current = new Number(Integer.parseInt(parts[i]));
					String op = parts[i - 1];
					lastComputation = new Operation(lastComputation, current, op, calc);
				}
			}

			return lastComputation;
		} catch (NumberFormatException e) {
			throw new CalculationException("Not a valid number: " + e.getMessage());
		}
	}

	public static void main(String[] args) {
		Calculator calc = new SimpleCalculator();

		Scanner sc = new Scanner(System.in);

		while (sc.hasNextLine()) {
			String line = sc.nextLine();
			try {
				Computation computation = getComputation(line, calc);
				int res = computation.getResult();
				System.out.println("result: " + res);
			} catch (CalculationException e) {
				e.printStackTrace();
			}
		}
	}

}
