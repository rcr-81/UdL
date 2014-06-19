package es.cesca.ws.client.exception;

public class ParameterException extends Exception {

	private static final long serialVersionUID = 4258534273378459583L;

	public ParameterException() {
		super();
		
	}

	public ParameterException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public ParameterException(String message, String method) {
		super(message + "["+ method +"]");
		
	}

	public ParameterException(Throwable cause) {
		super(cause);
		
	}

}
