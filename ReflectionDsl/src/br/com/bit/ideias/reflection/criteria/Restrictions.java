package br.com.bit.ideias.reflection.criteria;

import br.com.bit.ideias.reflection.criteria.expression.Expression;
import br.com.bit.ideias.reflection.criteria.target.FieldsTarget;
import br.com.bit.ideias.reflection.criteria.target.MethodsTarget;
import br.com.bit.ideias.reflection.enums.TargetType;

/**
 * 
 * @author Nadilson Oliveira da Silva
 * @since 27/07/2009
 */
public class Restrictions {

	private Restrictions() {

	}

	public static FieldsTarget fields() {
		return FieldsTarget.getInstance();
	}

	public static MethodsTarget methods() {
		return MethodsTarget.getInstance();
	}

	public static Expression setTargetType(TargetType method) {
		return null;
	}

}
