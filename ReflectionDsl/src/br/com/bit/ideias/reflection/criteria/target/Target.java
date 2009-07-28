package br.com.bit.ideias.reflection.criteria.target;

import java.lang.annotation.Annotation;

import br.com.bit.ideias.reflection.criteria.Restrictions;
import br.com.bit.ideias.reflection.criteria.expression.Expression;
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
	public Expression eq(String value) {
		return new SimpleExpression(value, SearchType.EQ, getTargetType());
	}
	
	public Expression ne(String value) {
		return new SimpleExpression(value, SearchType.NE, getTargetType());
	}
	
	public Expression like(String value) {
		return new SimpleExpression(value, SearchType.LIKE, getTargetType());
	}

	public Restrictions like(String string, LikeType anywhere) {
		return null;
	}

	public Restrictions regex(String value) {
		return null;
	}

	public Restrictions in(String... values) {
		return null;
	}
	
	public Restrictions showOnlyPublic(boolean flag) {
		return null;
	}

	// /////////////////////////////////////////////////////////////////////////
	// AnnotatedExpression ////////////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////////////////
	public Restrictions annotatedWith(Class<? extends Annotation> clazzAnnotation) {
		return null;
	}

	// /////////////////////////////////////////////////////////////////////////
	// LogicalExpression //////////////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////////////////
//	public Restrictions conjuction() {
//		return null;
//	}
//	
//	public Restrictions disjuction() {
//		return null;
//	}
}
