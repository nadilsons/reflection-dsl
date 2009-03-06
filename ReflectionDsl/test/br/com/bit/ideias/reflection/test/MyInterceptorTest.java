/**
 * 
 */
package br.com.bit.ideias.reflection.test;

import br.com.bit.ideias.reflection.interceptor.InvocationContext;
import br.com.bit.ideias.reflection.interfaces.Interceptor;

/**
 * @author Nadilson
 * @date 19/02/2009
 * 
 */
public class MyInterceptorTest implements Interceptor {

	public boolean after;

	public boolean afterException;

	public boolean before;

	public void doAfter(final InvocationContext invocationContext) {
		final String s = String.format("[After] - method: %s, na classe: %s, com os params: %s", invocationContext.getMethod().getName(),
				invocationContext.getProxy().getClass().getSimpleName(), invocationContext.getArgs());
		System.out.println(s);
		after = true;
	}

	public void doAfterException(final InvocationContext invocationContext, final Exception e) {
		final String s = String.format("[After Exception] - method: %s, na classe: %s, com os params: %s", invocationContext.getMethod().getName(),
				invocationContext.getProxy().getClass().getSimpleName(), invocationContext.getArgs());
		System.out.println(s);
		afterException = true;
	}

	public void doBefore(final InvocationContext invocationContext) {
		final String s = String.format("[Before] - method: %s, na classe: %s, com os params: %s", invocationContext.getMethod().getName(),
				invocationContext.getProxy().getClass().getSimpleName(), invocationContext.getArgs());
		System.out.println(s);
		before = true;
	}

}
