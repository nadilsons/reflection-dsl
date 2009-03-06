package br.com.bit.ideias.reflection.interceptor;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import br.com.bit.ideias.reflection.interfaces.Interceptor;

/**
 * @author Nadilson
 * @date 19/02/2009
 * 
 */
public class MethodInterceptorImpl implements MethodInterceptor {

	private final Interceptor interceptor;

	public MethodInterceptorImpl(final Interceptor interceptor) {
		this.interceptor = interceptor;
	}

	public Object intercept(final Object proxy, final Method method, final Object[] args, final MethodProxy methodProxy) throws Throwable {
		final InvocationContext ic = new InvocationContext(proxy, method, args);
		interceptor.doBefore(ic);

		try {
			final Object retorno = methodProxy.invokeSuper(proxy, args);
			return retorno;
		} catch (final Exception e) {
			interceptor.doAfterException(ic, e);
			throw e;
		} finally {
			interceptor.doAfter(ic);
		}
	}
}