package controller.computation;

public class SimpleCalculator implements Calculator {

	@Override
	public int add(int a, int b) {
		return a + b;
	}

	@Override
	public int substract(int a, int b) {
		return a - b;
	}

	@Override
	public int multiply(int a, int b) {
		return a * b;
	}

	@Override
	public int divide(int a, int b) throws CalculationException {
		if (b == 0) {
			throw new CalculationException("Division by 0");
		}

		int x = a / b;
		int rest = a % b;

		if (rest * 2 >= a) {
			return x + 1;
		} else {
			return x;
		}
	}

}
