package me.engineersbox.playerrev.exceptions;

public class ChunkyParameterException extends Exception {

	private static final long serialVersionUID = 6947614154123269176L;
	
	public ChunkyParameterException(String field) {
		super(field);
	}

}
