package br.com.bit.ideias.reflection.criteria;

import br.com.bit.ideias.reflection.criteria.expression.Expression;

/**
 * 
 * @author Nadilson
 * @since 28/07/2009
 */
public interface Criterion {
	
	public void add(Expression expression);

	public CriterionResult search();

}
