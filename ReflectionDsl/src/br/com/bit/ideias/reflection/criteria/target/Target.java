package br.com.bit.ideias.reflection.criteria.target;

import java.lang.annotation.Annotation;

import br.com.bit.ideias.reflection.criteria.expression.ComplexExpression;
import br.com.bit.ideias.reflection.criteria.expression.DisjunctionExpression;
import br.com.bit.ideias.reflection.criteria.expression.Expression;
import br.com.bit.ideias.reflection.criteria.expression.SimpleExpression;
import br.com.bit.ideias.reflection.enums.LikeType;
import br.com.bit.ideias.reflection.enums.SearchType;
import br.com.bit.ideias.reflection.enums.TargetType;

/**
 * 
 * @author Nadilson Oliveira da Silva
 * @since 28/07/2009
 */
public class Target {

	// /////////////////////////////////////////////////////////////////////////
	// SimpleExpression ///////////////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////////////////
	public SimpleExpression eq(final String value) {
		return new SimpleExpression(value, SearchType.EQ);
	}

	public SimpleExpression ne(final String value) {
		return new SimpleExpression(value, SearchType.NE);
	}

	public SimpleExpression like(final String value) {
		return new SimpleExpression(value, SearchType.LIKE_START);
	}

	public SimpleExpression like(final String value, final LikeType likeType) {
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

	public SimpleExpression regex(final String value) {
		return new SimpleExpression(value, SearchType.REGEX);
	}

	public SimpleExpression in(final String... values) {
		final StringBuilder concat = new StringBuilder();
		for (final String value : values)
			concat.append(value).append(Expression.NAME_SEPARATOR);

		return new SimpleExpression(concat.toString(), SearchType.IN);
	}

	// /////////////////////////////////////////////////////////////////////////
	// ConfigExpression ////////////////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////////////////
	public SimpleExpression showOnlyPublic(final boolean flag) {
		return new SimpleExpression(Boolean.toString(flag), SearchType.ONLY_PUBLIC);
	}

	// /////////////////////////////////////////////////////////////////////////
	// ClassExpression /////////////////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////////////////
	public SimpleExpression setTargetType(final TargetType targetType) {
		return new SimpleExpression(targetType.name(), SearchType.TYPE);
	}

	public SimpleExpression annotatedWith(final Class<? extends Annotation> clazzAnnotation) {
		return new SimpleExpression(clazzAnnotation.getName(), SearchType.ANNOTATION);
	}

	public SimpleExpression typeEq(final Class<?> classType) {
		return new SimpleExpression(classType.getName(), SearchType.TYPE_EQ);
	}

	public SimpleExpression typeReturn(final Class<?> classType) {
		return new SimpleExpression(classType.getName(), SearchType.TYPE_RETURN);
	}

	public SimpleExpression typesParams(final Class<?>... classTypes) {
		final StringBuilder concat = new StringBuilder();
		for (final Class<?> value : classTypes)
			concat.append(value.getName()).append(Expression.NAME_SEPARATOR);
		
		return new SimpleExpression(concat.toString(), SearchType.TYPES_PARAMS);
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
