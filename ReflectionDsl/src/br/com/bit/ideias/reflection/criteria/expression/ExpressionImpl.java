package br.com.bit.ideias.reflection.criteria.expression;

import java.lang.reflect.Member;

import br.com.bit.ideias.reflection.enums.SearchType;
import br.com.bit.ideias.reflection.enums.TargetType;

/**
 * 
 * @author Nadilson Oliveira da Silva
 * @since 28/07/2009
 */
public abstract class ExpressionImpl implements Expression {

	protected String value;

	protected SearchType searchType;

	protected TargetType targetType;

	public ExpressionImpl(final String value, final SearchType searchType, final TargetType targetType) {
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

	public boolean accept(final Member member) {
		return searchType.matches(member, this);
	}
}
