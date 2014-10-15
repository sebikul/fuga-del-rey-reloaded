package exceptions;

public class InvalidFormatException extends Exception {

	private final String lineAt;

	public InvalidFormatException(String lineAt) {
		this.lineAt = lineAt;
	}

	public InvalidFormatException() {
		this(null);
	}

	public String getLineAt() {
		return lineAt;
	}

}
