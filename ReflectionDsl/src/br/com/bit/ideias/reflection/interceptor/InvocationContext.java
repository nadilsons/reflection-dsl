package br.com.bit.ideias.reflection.interceptor;

import java.lang.reflect.Method;

/**
 * @author Nadilson Oliveira da Silva
 * @date 19/02/2009
 * 
 */
public class InvocationContext {

	private final Object proxy;

	private final Method method;

	private final Object[] args;

	InvocationContext(final Object proxy, final Method method, final Object[] args) {
		this.proxy = proxy;
		this.method = method;
		this.args = args;
	}

	public Object getProxy() {
		return proxy;
	}

	public Method getMethod() {
		return method;
	}

	public Object[] getArgs() {
		return args;
	}

}
