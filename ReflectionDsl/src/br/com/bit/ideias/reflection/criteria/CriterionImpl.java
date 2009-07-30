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
		final List<? extends Member> fields = searchFields();

		return new CriterionResult(fields, methods);
	}

	private List<? extends Member> searchFields() {
		final Class<?> classe = introspector.getTargetClass();
		List<? extends Member> fields = Arrays.asList(classe.getDeclaredFields());

		// while (classe.getSuperclass() != null) {
		// final List<? extends Member> lista =
		// Arrays.asList(classe.getDeclaredFields());
		// fields.addAll((List) lista);
		// classe = classe.getSuperclass();
		// }

		for (final Expression expression : fieldExpressions) {
			final SearchType searchType = expression.getSearchType();
			fields = searchType.filter(fields, expression);
		}
		return fields;
	}

}
