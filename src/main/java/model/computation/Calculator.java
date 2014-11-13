package model.computation;

public interface Calculator {

	public abstract int add(int a, int b) throws CalculationException;

	public abstract int substract(int a, int b) throws CalculationException;

	public abstract int multiply(int a, int b) throws CalculationException;

	public abstract int divide(int a, int b) throws CalculationException;
}
