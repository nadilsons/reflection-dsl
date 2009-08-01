package br.com.bit.ideias.reflection.criteria.expression;

import br.com.bit.ideias.reflection.enums.SearchType;
import br.com.bit.ideias.reflection.enums.TargetType;

/**
 * 
 * @author nadilson
 * @since 28/07/2009
 */
public abstract class Expression {

	public static final String NAME_SEPARATOR = ",";

	protected String value;

	protected SearchType searchType;

	protected TargetType targetType;

	public Expression(final String value, final SearchType searchType, final TargetType targetType) {
		this.value = value;
		this.searchType = searchType;
		this.targetType = targetType;
	}

	public String getValue() {
		return value;
	}

	public TargetType getTargetType() {
		return targetType;
	}

	public SearchType getSearchType() {
		return searchType;
	}
}
