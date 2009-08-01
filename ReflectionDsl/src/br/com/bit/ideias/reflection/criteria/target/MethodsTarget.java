package br.com.bit.ideias.reflection.criteria.target;

import br.com.bit.ideias.reflection.enums.TargetType;

/**
 * 
 * @author Nadilson Oliveira da Silva
 * @since 28/07/2009
 */
public class MethodsTarget extends Target {

	private static final MethodsTarget instance = new MethodsTarget();

	private MethodsTarget() {

	}

	public static MethodsTarget getInstance() {
		return instance;
	}

	@Override
	public TargetType getTargetType() {
		return TargetType.METHOD;
	}
}
