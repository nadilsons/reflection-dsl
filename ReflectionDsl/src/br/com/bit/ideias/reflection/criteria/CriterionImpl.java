package br.com.bit.ideias.reflection.criteria;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.bit.ideias.reflection.core.Introspector;
import br.com.bit.ideias.reflection.criteria.expression.ConjunctionExpression;
import br.com.bit.ideias.reflection.criteria.expression.Expression;

/**
 * 
 * @author Nadilson Oliveira da Silva
 * @since 28/07/2009
 */
public class CriterionImpl implements Criterion {
	private final ConjunctionExpression expressionHolder = new ConjunctionExpression();
	private final Introspector introspector;

	public CriterionImpl(final Introspector introspector) {
		this.introspector = introspector;
	}

	public void add(final Expression expression) {
		expressionHolder.add(expression);
	}

	public CriterionResult search() {
		List<? extends Member> methods = obtainAllMembers(false);
		List<? extends Member> fields = obtainAllMembers(true);

		fields = executeSearch(fields);
		methods = executeSearch(methods);

		return new CriterionResult(fields, new ArrayList());
	}

	private List<Member> executeSearch(final List<? extends Member> members) {
		final List<Member> filtred = new ArrayList<Member>();
		for (final Member member : members) {
			if (expressionHolder.accept(member))
				filtred.add(member);
		}
		return filtred;
	}

	@SuppressWarnings("unchecked")
	private List<? extends Member> obtainAllMembers(final boolean isField) {
		Class<?> classe = introspector.getTargetClass();
		final List<? extends Member> fields = obtainAllMemberInClass(classe, isField);

		while (classe.getSuperclass() != null) {
			classe = classe.getSuperclass();
			fields.addAll((List) obtainAllMemberInClass(classe, isField));
		}

		return fields;
	}

	private ArrayList<Member> obtainAllMemberInClass(final Class<?> classe, final boolean isField) {
		return new ArrayList<Member>(Arrays.asList(classe.getDeclaredFields()));
	}
}