package br.com.bit.ideias.reflection.interfaces;

import br.com.bit.ideias.reflection.interceptor.InvocationContext;

/**
 * @author Nadilson
 * @date 19/02/2009
 * 
 */
public interface Interceptor {

	public void doBefore(InvocationContext invocationContext);

	public void doAfter(InvocationContext invocationContext);

	public void doAfterException(InvocationContext invocationContext, Exception e);

}