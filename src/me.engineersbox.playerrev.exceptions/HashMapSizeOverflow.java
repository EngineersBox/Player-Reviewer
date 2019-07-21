package me.engineersbox.playerrev.exceptions;

public class HashMapSizeOverflow extends Exception {

	private static final long serialVersionUID = 4562129596161142802L;

	public HashMapSizeOverflow(String field) {
		super(field);
	}

}
