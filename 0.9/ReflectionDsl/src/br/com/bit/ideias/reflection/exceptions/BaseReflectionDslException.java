package br.com.bit.ideias.reflection.exceptions;

/**
 * @author Nadilson
 * @date 18/02/2009
 * 
 */
public class BaseReflectionDslException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public BaseReflectionDslException() {
		super();
	}

	public BaseReflectionDslException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public BaseReflectionDslException(final String message) {
		super(message);
	}

	public BaseReflectionDslException(final Throwable cause) {
		super(cause);
	}
}
