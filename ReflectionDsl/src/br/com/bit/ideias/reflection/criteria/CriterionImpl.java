package br.com.bit.ideias.reflection.criteria;

import static br.com.bit.ideias.reflection.util.CollectionUtil.isEmpty;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;

import br.com.bit.ideias.reflection.core.Introspector;
import br.com.bit.ideias.reflection.criteria.expression.ConjunctionExpression;
import br.com.bit.ideias.reflection.criteria.expression.Expression;
import br.com.bit.ideias.reflection.exceptions.NoResultException;
import br.com.bit.ideias.reflection.exceptions.TooManyResultException;
import br.com.bit.ideias.reflection.type.TargetType;

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

	public <T extends AccessibleObject> T uniqueResult() {
		final CriterionResult result = list();
		if (isEmpty(result.getFields()) && isEmpty(result.getMethods()))
			throw new NoResultException();

		if (!isEmpty(result.getFields()) && !isEmpty(result.getMethods()))
			throw new TooManyResultException();

		if (!isEmpty(result.getFields())) {
			if (result.getFields().size() > 1)
				throw new TooManyResultException();

			return (T) result.getFields().get(0);
		}

		if (result.getMethods().size() > 1)
			throw new TooManyResultException();

		return (T) result.getMethods().get(0);
	}

	public CriterionResult list() {
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

		final List<? extends Member> fields = targetType.obtainMembersInClass(classe);
		while (classe.getSuperclass() != null) {
			classe = classe.getSuperclass();
			fields.addAll((List) targetType.obtainMembersInClass(classe));
		}

		return fields;
	}
}