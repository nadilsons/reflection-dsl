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
 * @author nadilson
 * @since 28/07/2009
 */
public abstract class Target {

	public abstract TargetType getTargetType();

	// /////////////////////////////////////////////////////////////////////////
	// SimpleExpression ///////////////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////////////////
	public Expression eq(final String value) {
		return new SimpleExpression(value, SearchType.EQ, getTargetType());
	}
	
	public ComplexExpression disjunction() {
        return new DisjunctionExpression();
    }
	
	public ComplexExpression disjunction(Expression...expressions) {
        DisjunctionExpression disjunctionExpression = new DisjunctionExpression();
        if(expressions == null) return disjunctionExpression;
        
        for (Expression expression : expressions) {
            disjunctionExpression.add(expression);
        }
        
        return disjunctionExpression;
    }

	public ExpressionImpl ne(final String value) {
		return new SimpleExpression(value, SearchType.NE, getTargetType());
	}

	public ExpressionImpl like(final String value) {
		return new SimpleExpression(value, SearchType.LIKE_START, getTargetType());
	}

	public ExpressionImpl like(final String value, final LikeType likeType) {
		switch (likeType) {
		case START:
			return like(value);
		case END:
			return new SimpleExpression(value, SearchType.LIKE_END, getTargetType());
		case ANYWHERE:
			return regex(value);
		default:
			throw new RuntimeException("Not implemented");
		}
	}

	public ExpressionImpl regex(final String value) {
		return new SimpleExpression(value, SearchType.REGEX, getTargetType());
	}

	public ExpressionImpl in(final String... values) {
		final StringBuilder concat = new StringBuilder();
		for (final String value : values)
			concat.append(value).append(ExpressionImpl.NAME_SEPARATOR);

		return new SimpleExpression(concat.toString(), SearchType.IN, getTargetType());
	}

	// /////////////////////////////////////////////////////////////////////////
	// ConfigExpression ////////////////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////////////////
	public ExpressionImpl showOnlyPublic(final boolean flag) {
		return new SimpleExpression(Boolean.toString(flag), SearchType.ONLY_PUBLIC, getTargetType());
	}

	// /////////////////////////////////////////////////////////////////////////
	// ClassExpression /////////////////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////////////////
	public ExpressionImpl annotatedWith(final Class<? extends Annotation> clazzAnnotation) {
		return new SimpleExpression(clazzAnnotation.getName(), SearchType.ANNOTATION, getTargetType());
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
	// LogicalExpression //////////////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////////////////
	// public Restrictions conjuction() {
	// return null;
	// }
	//	
	// public Restrictions disjuction() {
	// return null;
	// }
}
