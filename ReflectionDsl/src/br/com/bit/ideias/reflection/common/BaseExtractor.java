package br.com.bit.ideias.reflection.common;

/**
 * @author Nadilson
 * @date 19/02/2009
 * 
 */
public class BaseExtractor {

	protected Class<?>[] getParametersTypes(final Object... params) {
		final Class<?>[] retorno = new Class<?>[params.length];

		for (int i = 0; i < params.length; i++)
			retorno[i] = params[i].getClass();

		return retorno;
	}

}
