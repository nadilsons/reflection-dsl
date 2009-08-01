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

	public CriterionImpl(final Introspector introspector) {
		this.introspector = introspector;
	}

	public void add(final Expression expression) {
		final List<Expression> lista = (TargetType.FIELD.equals(expression.getTargetType())) ? fieldExpressions : methodExpressions;
		lista.add(expression);
	}

	public CriterionResult search() {
		final List<? extends Member> methods = new ArrayList<Method>();
		final List<? extends Member> fields = searchMember(true);

		return new CriterionResult(fields, methods);
	}

	private List<? extends Member> searchMember(boolean isField) {
		Class<?> classe = introspector.getTargetClass();
		List<? extends Member> fields = obtainFields(classe);

		for (final Expression expression : fieldExpressions) {
			final SearchType searchType = expression.getSearchType();
			fields = searchType.filter(fields, expression);
		}
		return fields;
	}

	@SuppressWarnings("unchecked")
	private List<? extends Member> obtainFields(Class<?> classe) {
		List<? extends Member> fields = new ArrayList<Member>(Arrays.asList(classe.getDeclaredFields()));
		
		 while (classe.getSuperclass() != null) {
			 classe = classe.getSuperclass();
			 
			 final List<? extends Member> lista = Arrays.asList(classe.getDeclaredFields());
			 fields.addAll((List) lista);
		 }
		return fields;
	}

}
