package model.computation;

public class Operation extends Computation {

	protected final Computation a;
	protected final Computation b;
	protected final Calculator calc;
	protected final String operator;

	/**
	 * Should be incremented to 50, when getResult() is called, and both parameters are already
	 * executed
	 */
	protected volatile long price = 0;

	public Operation(Computation a, Computation b, String operator, Calculator calc) {
		this.a = a;
		this.b = b;
		this.calc = calc;
		this.operator = operator;
	}

	@Override
	public int getResult() throws CalculationException {
		int aRes = a.getResult();
		int bRes = b.getResult();

		this.price = 50;

		switch (operator) {
			case "+" :
				return calc.add(aRes, bRes);
			case "-" :
				return calc.substract(aRes, bRes);
			case "*" :
				return calc.multiply(aRes, bRes);
			case "/" :
				return calc.divide(aRes, bRes);
			default :
				// getComputation should only create valid computations
				throw new CalculationException("Operator '" + operator + "' is not supported");
		}
	}

	@Override
	public long getPrice() {
		return a.getPrice() + b.getPrice() + price;
	}

	@Override
	public long getMaxPrice() {
		return a.getMaxPrice() + b.getMaxPrice() + 50;
	}

	@Override
	public String toString() {
		return "(" + a + " " + operator + " " + b + ")";
	}
}
