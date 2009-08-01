package br.com.bit.ideias.reflection.common;

import java.lang.reflect.Field;

import br.com.bit.ideias.reflection.exceptions.FieldNotExistsException;
import br.com.bit.ideias.reflection.exceptions.FieldPrivateException;
import br.com.bit.ideias.reflection.exceptions.InvalidParameterException;
import br.com.bit.ideias.reflection.exceptions.MethodNotExistsException;

/**
 * @author Nadilson
 * @date 19/02/2009
 * 
 */
public class ExtractorField {

	private final Extractor extractor;

	private final Field field;

	private boolean directAccess;

	ExtractorField(final Extractor extractor, final String fieldName) {
		this.extractor = extractor;

		try {
			final Class<?> targetClass = extractor.getTargetClass();
			this.field = targetClass.getDeclaredField(fieldName);
		} catch (final NoSuchFieldException e) {
			throw new FieldNotExistsException(e);
		}
	}

	public Object invoke(final boolean accessPrivateMembers, final Object... params) {
		if (params.length > 1)
			throw new InvalidParameterException(String.format("Número excessivo de parametros [%s] para o metodo setter", params.length));

		try {
			return (directAccess) ? invokeField(accessPrivateMembers, params) : invokeMethod(accessPrivateMembers, params);
		} catch (final IllegalAccessException e) {
			throw new FieldPrivateException(e);
		}
	}

	public void directAccess() {
		this.directAccess = true;
	}

	public void directAccess(final boolean directAccess) {
		this.directAccess = directAccess;
	}

	// /////////////////////////////////////////////////////////////////////

	private Object invokeField(final boolean accessPrivateMembers, final Object... params) throws IllegalAccessException {
		Object retorno = null;
		final boolean getter = params.length == 0;
		field.setAccessible(accessPrivateMembers);
		final Object targetInstance = extractor.getTargetInstance();

		if (getter)
			retorno = field.get(targetInstance);
		else
			field.set(targetInstance, params[0]);

		return retorno;
	}

	private Object invokeMethod(final boolean accessPrivateMembers, final Object... params) {
		final boolean getter = params.length == 0;
		final ExtractorMethod em = new ExtractorMethod(extractor, getMethodForField(field, getter));

		try {
			return em.invoke(accessPrivateMembers, params);
		} catch (MethodNotExistsException e) {
			if (params.length == 1)
				return em.invoke(accessPrivateMembers, true, params);
			else
				throw e;
		}
	}

	private String getMethodForField(final Field field, final boolean getter) {
		final StringBuilder retorno = new StringBuilder(getter ? "get" : "set");

		final char[] letras = field.getName().toCharArray();
		letras[0] = Character.toUpperCase(letras[0]);

		return retorno.append(letras).toString();
	}

}
