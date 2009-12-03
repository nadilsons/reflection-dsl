package br.com.bit.ideias.reflection.core.extrator;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import net.sf.cglib.proxy.Enhancer;
import br.com.bit.ideias.reflection.exceptions.ConstructorNotExistsException;
import br.com.bit.ideias.reflection.exceptions.ObjectCreateException;
import br.com.bit.ideias.reflection.interceptor.Interceptor;
import br.com.bit.ideias.reflection.interceptor.MethodInterceptorImpl;

/**
 * @author Nadilson Oliveira da Silva
 * @date 19/02/2009
 * 
 */
public class ExtractorConstructor extends BaseExtractor {

	private final Extractor extractor;

	private Object targetInstance;

	private final Interceptor interceptor;

	ExtractorConstructor(final Extractor extractor, final Object instance) {
		this(extractor, null);
		this.targetInstance = instance;
	}

	ExtractorConstructor(final Extractor extractor, final Interceptor interceptor) {
		this.extractor = extractor;
		this.interceptor = interceptor;
	}

	public Object newInstance(final Object... params) {
		return (interceptor == null) ? constructorStandard(params) : constructorWithInterceptor(params);
	}

	public Object getTargetInstance() {
		return targetInstance;
	}

	// /////////////////////////////////////////////////////////////////////////

	private Object constructorStandard(final Object... params) {
		final Class<?> targetClass = extractor.getTargetClass();
		try {
			final Constructor<?> constructor = targetClass.getConstructor(getParametersTypes(params));
			targetInstance = constructor.newInstance(params);
			return targetInstance;
		} catch (final NoSuchMethodException e) {
			throw new ConstructorNotExistsException(e);
		} catch (final InstantiationException e) {
			throw new ObjectCreateException(e);
		} catch (final IllegalAccessException e) {
			throw new ConstructorNotExistsException(e);
		} catch (final InvocationTargetException e) {
			throw new ObjectCreateException(e);
		}
	}

	private Object constructorWithInterceptor(final Object... params) {
		final Enhancer e = new Enhancer();

		e.setSuperclass(extractor.getTargetClass());
		e.setCallback(new MethodInterceptorImpl(interceptor));
		targetInstance = e.create(getParametersTypes(params), params);

		return targetInstance;
	}

}
