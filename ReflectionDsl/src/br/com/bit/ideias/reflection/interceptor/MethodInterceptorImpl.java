package br.com.bit.ideias.reflection.interceptor;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import br.com.bit.ideias.reflection.interfaces.Interceptor;
import br.com.bit.ideias.reflection.type.TreatmentExceptionType;

/**
 * @author Nadilson Oliveira da Silva
 * @date 19/02/2009
 * 
 */
public class MethodInterceptorImpl implements MethodInterceptor {

	private final Interceptor interceptor;

	public MethodInterceptorImpl(final Interceptor interceptor) {
		this.interceptor = interceptor;
	}

	public Object intercept(final Object proxy, final Method method, final Object[] args, final MethodProxy methodProxy) throws Throwable {
		Object retorno = null;
		final InvocationContext invocationContext = new InvocationContext(proxy, method, args);
		interceptor.doBefore(invocationContext);

		try {
			retorno = methodProxy.invokeSuper(proxy, args);
		} catch (final Exception e) {
			if (!TreatmentExceptionType.STOP_EXCEPTION.equals(interceptor.doAfterException(invocationContext, e)))
				throw e;
		} finally {
			interceptor.doAfter(invocationContext);
		}

		return retorno;
	}
}