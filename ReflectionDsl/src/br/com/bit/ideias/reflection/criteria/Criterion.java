package br.com.bit.ideias.reflection.criteria;

import java.lang.reflect.Member;

import br.com.bit.ideias.reflection.criteria.expression.Expression;

/**
 * 
 * @author Nadilson Oliveira da Silva
 * @since 28/07/2009
 */
public interface Criterion {

	public Criterion add(Expression expression);

	public CriterionResult list();

	public <T extends Member> T uniqueResult();
}
