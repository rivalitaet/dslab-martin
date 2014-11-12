package controller.computation;

public class Operation extends Computation {

	protected final Computation a;
	protected final Computation b;
	protected final Calculator calc;
	protected final String operator;

	public Operation(Computation a, Computation b, String operator, Calculator calc) {
		this.a = a;
		this.b = b;
		this.calc = calc;
		this.operator = operator;
	}

	@Override
	public int getResult() throws CalculationException {
		switch (operator) {
			case "+" :
				return calc.add(a.getResult(), b.getResult());
			case "-" :
				return calc.substract(a.getResult(), b.getResult());
			case "*" :
				return calc.multiply(a.getResult(), b.getResult());
			case "/" :
				return calc.divide(a.getResult(), b.getResult());
			default :
				throw new CalculationException("Operator '" + operator + "' is not supported");
		}
	}

	@Override
	public int getPrice() {
		return a.getPrice() + b.getPrice() + 50;
	}

	public String toString() {
		return "(" + a + " " + operator + " " + b + ")";
	}
}
