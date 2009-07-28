package br.com.bit.ideias.reflection.criteria;

import br.com.bit.ideias.reflection.criteria.target.MethodsTarget;
import br.com.bit.ideias.reflection.criteria.target.PropertiesTarget;

/**
 * 
 * @author Nadilson
 * @since 27/07/2009
 */
public class Restrictions {

	private Restrictions() {

	}
	public static PropertiesTarget properties() {

		return PropertiesTarget.getInstance();
	}

	public static MethodsTarget methods() {
		return MethodsTarget.getInstance();
	}

}
