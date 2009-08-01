package br.com.bit.ideias.reflection.criteria.target;

import br.com.bit.ideias.reflection.enums.TargetType;

/**
 * 
 * @author Nadilson Oliveira da Silva
 * @since 28/07/2009
 */
public class FieldsTarget extends Target {

	private static final FieldsTarget instance = new FieldsTarget();

	private FieldsTarget() {

	}

	public static FieldsTarget getInstance() {
		return instance;
	}

	@Override
	public TargetType getTargetType() {
		return TargetType.FIELD;
	}

}
