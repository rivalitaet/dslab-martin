package controller;

import model.computation.CalculationException;

public class NoNodeAvailableException extends CalculationException {

	private static final long serialVersionUID = -8921833577714373384L;

	public NoNodeAvailableException(String msg) {
		super(msg);
	}
}
