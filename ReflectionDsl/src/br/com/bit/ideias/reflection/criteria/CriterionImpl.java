package br.com.bit.ideias.reflection.criteria;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.bit.ideias.reflection.core.Introspector;
import br.com.bit.ideias.reflection.criteria.expression.ConjunctionExpression;
import br.com.bit.ideias.reflection.criteria.expression.Expression;
import br.com.bit.ideias.reflection.enums.TargetType;

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

	public Criterion add(final Expression expression) {
		expressionHolder.add(expression);
		
		return this;
	}

	public CriterionResult search() {
		List<? extends Member> methods = obtainAllMembers(TargetType.METHOD);
		List<? extends Member> fields = obtainAllMembers(TargetType.FIELD);

		fields = executeSearch(fields);
		methods = executeSearch(methods);

		return new CriterionResult(fields, methods);
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
	private List<? extends Member> obtainAllMembers(final TargetType targetType) {
		Class<?> classe = introspector.getTargetClass();

		final List<? extends Member> fields = obtainMembersInClass(classe, targetType);
		while (classe.getSuperclass() != null) {
			classe = classe.getSuperclass();
			fields.addAll((List) obtainMembersInClass(classe, targetType));
		}

		return fields;
	}

	private ArrayList<Member> obtainMembersInClass(final Class<?> classe, final TargetType targetType) {
		final boolean isField = TargetType.FIELD.equals(targetType);
		final Member[] members = isField ? classe.getDeclaredFields() : classe.getDeclaredMethods();
		return new ArrayList<Member>(Arrays.asList(members));
	}
}