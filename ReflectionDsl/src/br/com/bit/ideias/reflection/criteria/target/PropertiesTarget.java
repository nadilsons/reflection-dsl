package br.com.bit.ideias.reflection.criteria.target;

import br.com.bit.ideias.reflection.enums.TargetType;

/**
 * 
 * @author Nadilson
 * @since 28/07/2009
 */
public class PropertiesTarget extends Target {

	private static final PropertiesTarget instance = new PropertiesTarget();

	private PropertiesTarget() {

	}

	public static PropertiesTarget getInstance() {
		return instance;
	}

	@Override
	public TargetType getTargetType() {
		return TargetType.FIELD;
	}

}
