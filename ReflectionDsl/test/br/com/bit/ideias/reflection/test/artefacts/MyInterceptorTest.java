/**
 * 
 */
package br.com.bit.ideias.reflection.test.artefacts;

import br.com.bit.ideias.reflection.enums.TreatmentExceptionType;
import br.com.bit.ideias.reflection.interceptor.InvocationContext;
import br.com.bit.ideias.reflection.interfaces.Interceptor;

/**
 * @author Nadilson Oliveira da Silva
 * @date 19/02/2009
 * 
 */
public class MyInterceptorTest implements Interceptor {

	private boolean after;

	private boolean afterException;

	private boolean before;

	private InvocationContext invocationContext;

	private final TreatmentExceptionType exceptionType;

	public MyInterceptorTest(final TreatmentExceptionType stopException) {
		this.exceptionType = stopException;
	}

	public MyInterceptorTest() {
		this(TreatmentExceptionType.CONTINUE_EXCEPTION);
	}

	public void doAfter(final InvocationContext invocationContext) {
		final String s = String.format("[After] - method: %s, na classe: %s, com os params: %s", invocationContext.getMethod().getName(), invocationContext.getProxy().getClass().getSimpleName(), invocationContext.getArgs());
		System.out.println(s);
		after = true;
		this.invocationContext = invocationContext;
	}

	public TreatmentExceptionType doAfterException(final InvocationContext invocationContext, final Exception e) {
		final String s = String.format("[After Exception] - method: %s, na classe: %s, com os params: %s", invocationContext.getMethod().getName(), invocationContext.getProxy().getClass().getSimpleName(), invocationContext.getArgs());
		System.out.println(s);
		afterException = true;
		this.invocationContext = invocationContext;

		return exceptionType;
	}

	public void doBefore(final InvocationContext invocationContext) {
		final String s = String.format("[Before] - method: %s, na classe: %s, com os params: %s", invocationContext.getMethod().getName(), invocationContext.getProxy().getClass().getSimpleName(), invocationContext.getArgs());
		System.out.println(s);
		before = true;
		this.invocationContext = invocationContext;
	}

	public boolean isAfterMethodCalled() {
		return after;
	}

	public boolean isAfterExceptionMethodCalled() {
		return afterException;
	}

	public boolean isBeforedMethodCalled() {
		return before;
	}

	public InvocationContext getInvocationContext() {
		return invocationContext;
	}

}
