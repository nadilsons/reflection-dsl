package br.com.bit.ideias.reflection.criteria;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.bit.ideias.reflection.core.Introspector;
import br.com.bit.ideias.reflection.criteria.expression.Expression;
import br.com.bit.ideias.reflection.enums.SearchType;
import br.com.bit.ideias.reflection.enums.TargetType;

/**
 * 
 * @author nadilson
 * @since 28/07/2009
 */
public class CriterionImpl implements Criterion {
	
	private final Introspector introspector;

	private final List<Expression> methodExpressions = new ArrayList<Expression>();
	private final List<Expression> fieldExpressions = new ArrayList<Expression>();
	
	public CriterionImpl(Introspector introspector) {
		this.introspector = introspector;
	}

	public void add(Expression expression) {
		List<Expression> lista = (TargetType.FIELD.equals(expression.getTargetType())) ? fieldExpressions : methodExpressions;
		lista.add(expression);
	}

	public CriterionResult search() {
		List<? extends Member> methods = new ArrayList<Method>();
		List<? extends Member> fields = searchFields();
		
		return new CriterionResult(fields, methods);
	}

	private List<? extends Member> searchFields() {
		Class<?> classe = introspector.getTargetClass();
		//TODO Verificar classespai
		List<? extends Member> fields = Arrays.asList(classe.getDeclaredFields());

		for (Expression expression : fieldExpressions) {
			SearchType searchType = expression.getSearchType();
			fields = searchType.filter(fields, expression);
		}
		return fields;
	}
	
	

}
