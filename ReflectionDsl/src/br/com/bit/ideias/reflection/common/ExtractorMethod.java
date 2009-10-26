package br.com.bit.ideias.reflection.common;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import br.com.bit.ideias.reflection.exceptions.MethodAccessException;
import br.com.bit.ideias.reflection.exceptions.MethodNotExistsException;
import br.com.bit.ideias.reflection.exceptions.MethodPrivateException;

/**
 * @author Nadilson Oliveira da Silva
 * @date 19/02/2009
 * 
 */
public class ExtractorMethod extends BaseExtractor {

	private final Extractor extractor;

	private final String methodName;

	private final Method method;

	ExtractorMethod(final Extractor extractor, final String methodName) {
		this.extractor = extractor;
		this.methodName = methodName;
		this.method = null;
	}
	
	ExtractorMethod(final Extractor extractor, final Method method) {
		this.extractor = extractor;
		this.methodName = null;
		this.method = method;
	}

	public Object invoke(final boolean accessPrivateMembers, final Object... params) {
		return invoke(accessPrivateMembers, false, params);
	}
	
	public Method get(Class<?>... parametersTypes) {
		return getMethod(parametersTypes);
	}

	public Object invoke(final boolean accessPrivateMembers, final boolean primitiveParam, final Object... params) {
		final Object targetInstance = extractor.getTargetInstance();
		try {
			final Class<?>[] parametersTypes = getParametersTypes(primitiveParam, params);
			final Method method = getMethod(parametersTypes);
			method.setAccessible(accessPrivateMembers);
			return method.invoke(targetInstance, params);
		} catch (final IllegalAccessException e) {
			throw new MethodPrivateException(e);
		} catch (final InvocationTargetException e) {
			throw new MethodAccessException(e);
		}
	}

	private Method getMethod(final Class<?>[] parametersTypes) {
		final Class<?> targetClass = extractor.getTargetClass();
		try {
			return (this.method == null) ? targetClass.getDeclaredMethod(methodName, parametersTypes) : this.method;
		} catch (final NoSuchMethodException e) {
			throw new MethodNotExistsException(e);
		}
	}
}
