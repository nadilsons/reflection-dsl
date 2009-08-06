package br.com.bit.ideias.reflection.criteria.target;

import java.lang.annotation.Annotation;

import br.com.bit.ideias.reflection.criteria.Restrictions;
import br.com.bit.ideias.reflection.criteria.expression.ComplexExpression;
import br.com.bit.ideias.reflection.criteria.expression.DisjunctionExpression;
import br.com.bit.ideias.reflection.criteria.expression.Expression;
import br.com.bit.ideias.reflection.criteria.expression.ExpressionImpl;
import br.com.bit.ideias.reflection.criteria.expression.SimpleExpression;
import br.com.bit.ideias.reflection.enums.LikeType;
import br.com.bit.ideias.reflection.enums.SearchType;
import br.com.bit.ideias.reflection.enums.TargetType;

/**
 * 
 * @author Nadilson Oliveira da Silva
 * @since 28/07/2009
 */
public abstract class Target {
	// /////////////////////////////////////////////////////////////////////////
	// SimpleExpression ///////////////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////////////////
	public Expression eq(final String value) {
		return new SimpleExpression(value, SearchType.EQ);
	}
	
	public Expression type(TargetType type) {
        return new SimpleExpression(type.name(), SearchType.TYPE);
    }

	public Expression ne(final String value) {
		return new SimpleExpression(value, SearchType.NE);
	}

	public Expression like(final String value) {
		return new SimpleExpression(value, SearchType.LIKE_START);
	}

	public Expression like(final String value, final LikeType likeType) {
		switch (likeType) {
		case START:
			return like(value);
		case END:
			return new SimpleExpression(value, SearchType.LIKE_END);
		case ANYWHERE:
			return regex(value);
		default:
			throw new RuntimeException("Not implemented");
		}
	}

	public Expression regex(final String value) {
		return new SimpleExpression(value, SearchType.REGEX);
	}

	public Expression in(final String... values) {
		final StringBuilder concat = new StringBuilder();
		for (final String value : values)
			concat.append(value).append(ExpressionImpl.NAME_SEPARATOR);

		return new SimpleExpression(concat.toString(), SearchType.IN);
	}

	// /////////////////////////////////////////////////////////////////////////
	// ConfigExpression ////////////////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////////////////
	public Expression showOnlyPublic(final boolean flag) {
		return new SimpleExpression(Boolean.toString(flag), SearchType.ONLY_PUBLIC);
	}

	// /////////////////////////////////////////////////////////////////////////
	// ClassExpression /////////////////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////////////////
	public Expression annotatedWith(final Class<? extends Annotation> clazzAnnotation) {
		return new SimpleExpression(clazzAnnotation.getName(), SearchType.ANNOTATION);
	}

	public Restrictions typeEq__soParaFields(final Class<?> classType) {
		return null;
	}

	public Restrictions typeReturn__soParaMetodos(final Class<?> classType) {
		return null;
	}

	public Restrictions typesParams__soParaMetodos(final Class<?>... classTypes) {
		return null;
	}

	// /////////////////////////////////////////////////////////////////////////
	// ComplexExpression //////////////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////////////////
	public ComplexExpression disjunction() {
		return new DisjunctionExpression();
	}

	public ComplexExpression disjunction(final Expression... expressions) {
		final ComplexExpression disjunctionExpression = disjunction();
		if (expressions == null)
			return disjunctionExpression;

		for (final Expression expression : expressions) {
			disjunctionExpression.add(expression);
		}

		return disjunctionExpression;
	}
}
