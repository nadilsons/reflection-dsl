package br.com.bit.ideias.reflection.common;

/**
 * @author Nadilson
 * @date 19/02/2009
 * 
 */
public abstract class BaseExtractor {

	protected Class<?>[] getParametersTypes(final Object... params) {
		return getParametersTypes(false, params);
	}

	protected Class<?>[] getParametersTypes(boolean primitiveParam, final Object... params) {
		final Class<?>[] retorno = new Class<?>[params.length];

		for (int i = 0; i < params.length; i++) {
			Object param = params[i];
			retorno[i] = (primitiveParam) ? transformInPrimitive(param) : param.getClass();
		}

		return retorno;
	}

	private Class<?> transformInPrimitive(Object param) {
		Class<?> retorno = null;

		if (param instanceof Byte) {
			retorno = byte.class;
		} else if (param instanceof Short) {
			retorno = short.class;
		} else if (param instanceof Integer) {
			retorno = int.class;
		} else if (param instanceof Long) {
			retorno = long.class;
		} else if (param instanceof Float) {
			retorno = float.class;
		} else if (param instanceof Double) {
			retorno = double.class;
		} else if (param instanceof Boolean) {
			retorno = boolean.class;
		} else
			retorno = param.getClass();

		return retorno;
	}

}
