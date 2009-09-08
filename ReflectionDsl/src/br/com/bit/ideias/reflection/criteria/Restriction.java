package br.com.bit.ideias.reflection.criteria;

import java.lang.annotation.Annotation;

import br.com.bit.ideias.reflection.criteria.expression.ComplexExpression;
import br.com.bit.ideias.reflection.criteria.expression.Expression;
import br.com.bit.ideias.reflection.criteria.expression.SimpleExpression;
import br.com.bit.ideias.reflection.criteria.target.Target;
import br.com.bit.ideias.reflection.enums.LikeType;
import br.com.bit.ideias.reflection.enums.TargetType;

/**
 * 
 * @author Nadilson Oliveira da Silva
 * @since 27/07/2009
 */
public class Restriction { 

	private static final Target TARGET = new Target();

	private Restriction() {

	}

	public static SimpleExpression annotatedWith(Class<? extends Annotation> clazzAnnotation) {
		return TARGET.annotatedWith(clazzAnnotation);
	}

	public static ComplexExpression disjunction() {
		return TARGET.disjunction();
	}

	public static ComplexExpression disjunction(Expression... expressions) {
		return TARGET.disjunction(expressions);
	}

	public static SimpleExpression eq(String value) {
		return TARGET.eq(value);
	}

	public static SimpleExpression in(String... values) {
		return TARGET.in(values);
	}

	public static SimpleExpression like(String value, LikeType likeType) {
		return TARGET.like(value, likeType);
	}

	public static SimpleExpression like(String value) {
		return TARGET.like(value);
	}

	public static SimpleExpression ne(String value) {
		return TARGET.ne(value);
	}

	public static SimpleExpression regex(String value) {
		return TARGET.regex(value);
	}

	public static SimpleExpression targetType(TargetType targetType) {
		return TARGET.targetType(targetType);
	}

	public static SimpleExpression showOnlyPublic(boolean flag) {
		return TARGET.showOnlyPublic(flag);
	}

	public static SimpleExpression typeEq(Class<?> classType) {		
		return TARGET.typeEq(classType);
	}

	public static SimpleExpression typeReturn(Class<?> classType) {
		return TARGET.typeReturn(classType);
	}

	public static SimpleExpression typesParams(Class<?>... classTypes) {
		return TARGET.typesParams(classTypes);
	}

	// public static static static FieldsTarget fields() {
	// return FieldsTarget.getInstance();
	// }
	//
	// public static static static MethodsTarget methods() {
	// return MethodsTarget.getInstance();
	// }

}
