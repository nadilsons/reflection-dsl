package br.com.bit.ideias.reflection.criteria;

import java.lang.annotation.Annotation;

import br.com.bit.ideias.reflection.criteria.expression.ComplexExpression;
import br.com.bit.ideias.reflection.criteria.expression.Expression;
import br.com.bit.ideias.reflection.criteria.target.MemberTarget;
import br.com.bit.ideias.reflection.enums.LikeType;
import br.com.bit.ideias.reflection.enums.TargetType;

/**
 * 
 * @author Nadilson Oliveira da Silva
 * @since 27/07/2009
 */
public class Restrictions {
    private static MemberTarget target = MemberTarget.getInstance();
    
	private Restrictions() {
	}

    public static Expression eq(String value) {
        return target.eq(value);
    }
    
    public static Expression type(TargetType type) {
        return target.type(type);
    }

    public static Expression in(String... values) {
        return target.in(values);
    }

    public static Expression like(String value, LikeType likeType) {
        return target.like(value, likeType);
    }

    public static Expression like(String value) {
        return target.like(value);
    }

    public static Expression ne(String value) {
        return target.ne(value);
    }

    public static Expression regex(String value) {
        return target.regex(value);
    }

    public static Expression showOnlyPublic(boolean flag) {
        return target.showOnlyPublic(flag);
    }

    public static Expression annotatedWith(Class<? extends Annotation> clazzAnnotation) {
        return target.annotatedWith(clazzAnnotation);
    }

    public static ComplexExpression disjunction() {
        return target.disjunction();
    }

    public static ComplexExpression disjunction(Expression... expressions) {
        return target.disjunction(expressions);
    }
}
