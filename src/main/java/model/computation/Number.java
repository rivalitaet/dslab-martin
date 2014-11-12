package model.computation;

public class Number extends Computation {

	private final int number;

	public Number(int number) {
		this.number = number;
	}

	@Override
	public int getResult() {
		return number;
	}

	@Override
	public int getPrice() {
		return 0;
	}

	@Override
	public String toString() {
		return Integer.toString(number);
	}

}
