package br.com.bit.ideias.reflection.criteria.expression;

import br.com.bit.ideias.reflection.enums.SearchType;
import br.com.bit.ideias.reflection.enums.TargetType;

/**
 * 
 * @author Nadilson Oliveira da Silva
 * @since 28/07/2009
 */
public class SimpleExpression extends ExpressionImpl {
	public SimpleExpression(String value, SearchType searchType, TargetType targetType) {
		super(value, searchType, targetType);
	}
}
