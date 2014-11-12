package controller.computation;

public interface Calculator {
	public abstract int add(int a, int b);
	public abstract int substract(int a, int b);
	public abstract int multiply(int a, int b);
	public abstract int divide(int a, int b) throws CalculationException;
}
