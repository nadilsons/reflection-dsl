package br.com.bit.ideias.reflection.criteria;

import static br.com.bit.ideias.reflection.util.CollectionUtil.isEmpty;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;

import br.com.bit.ideias.reflection.core.Introspector;
import br.com.bit.ideias.reflection.criteria.expression.ConjunctionExpression;
import br.com.bit.ideias.reflection.criteria.expression.Expression;
import br.com.bit.ideias.reflection.enums.TargetType;
import br.com.bit.ideias.reflection.exceptions.NoResultException;
import br.com.bit.ideias.reflection.exceptions.TooManyResultException;
import br.com.bit.ideias.reflection.util.CollectionUtil;

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

	@SuppressWarnings("unchecked")
    public <T extends AccessibleObject> T uniqueResult() {
		final CriterionResult result = list();
		if (isEmpty(result.getMembers()))
			throw new NoResultException();


		if (result.getMembers().size() > 1)
			throw new TooManyResultException();

		return (T) result.getMembers().get(0);
	}

	public CriterionResult list() {
		return new CriterionResult(executeSearch(obtainAllMembers()));
	}

	private List<Member> executeSearch(final List<? extends Member> members) {
		final List<Member> filtred = new ArrayList<Member>();
		for (final Member member : members) {
			if (expressionHolder.accept(member))
				filtred.add(member);
		}
		return filtred;
	}

    private List<Member> obtainAllMembers() {
	    List<Member> members = new ArrayList<Member>();
	    List<? extends Member> fields = obtainAllMembers(TargetType.FIELD);
	    List<? extends Member> methods = obtainAllMembers(TargetType.METHOD);
	    
	    if(!CollectionUtil.isEmpty(fields)) members.addAll(fields);
	    if(!CollectionUtil.isEmpty(methods)) members.addAll(methods);
	    
	    return members;
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