package controller.computation;

import java.util.Scanner;

public abstract class Computation {

	public abstract int getResult() throws CalculationException;

	public abstract int getPrice();

	public static Computation compute(String calculation, Calculator calc) throws CalculationException {
		String[] parts = calculation.split("\\s");

		if (parts.length < 3) {
			throw new CalculationException("Not a valid calculation (to few operands/operations)");
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

		System.out.println(lastComputation);
		System.out.println(lastComputation.getResult());
		System.out.println(lastComputation.getPrice());

		return lastComputation;
	}

	public static void main(String[] args) {
		Calculator calc = new SimpleCalculator();

		Scanner sc = new Scanner(System.in);

		while (sc.hasNextLine()) {
			String line = sc.nextLine();
			try {
				Computation computation = compute(line, calc);
				int res = computation.getResult();
				System.out.println("result: " + res);
			} catch (CalculationException e) {
				e.printStackTrace();
			}
		}
	}

}
