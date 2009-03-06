package br.com.bit.ideias.reflection.common;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import br.com.bit.ideias.reflection.exceptions.MethodAccessException;
import br.com.bit.ideias.reflection.exceptions.MethodNotExistsException;
import br.com.bit.ideias.reflection.exceptions.MethodPrivateException;

/**
 * @author Nadilson
 * @date 19/02/2009
 * 
 */
public class ExtractorMethod extends BaseExtractor {

	private final Extractor extractor;

	private final String methodName;

	ExtractorMethod(final Extractor extractor, final String methodName) {
		this.extractor = extractor;
		this.methodName = methodName;
	}

	public Object invoke(final boolean accessPrivateMembers, final Object... params) {
		final Class<?> targetClass = extractor.getTargetClass();
		final Object targetInstance = extractor.getTargetInstance();
		try {
			final Class<?>[] parametersTypes = getParametersTypes(params);
			final Method method = targetClass.getDeclaredMethod(methodName, parametersTypes);
			method.setAccessible(accessPrivateMembers);
			return method.invoke(targetInstance, params);

		} catch (final NoSuchMethodException e) {
			throw new MethodNotExistsException(e);
		} catch (final IllegalAccessException e) {
			throw new MethodPrivateException(e);
		} catch (final InvocationTargetException e) {
			throw new MethodAccessException(e);
		}
	}

}
