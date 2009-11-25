package br.com.bit.ideias.reflection.criteria;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;

import br.com.bit.ideias.reflection.cache.Cache;
import br.com.bit.ideias.reflection.cache.CacheProvider;
import br.com.bit.ideias.reflection.cache.CompositeKey;
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
	
	public Introspector getIntrospector() {
        return introspector;
    }

    @SuppressWarnings("unchecked")
	public <T extends Member> T uniqueResult() {
		final List result = list();
		if (result.isEmpty())
			throw new NoResultException();

		if (result.size() > 1)
			throw new TooManyResultException();

		return (T) result.get(0);
	}

    @SuppressWarnings("unchecked")
    public <T extends Member> List<T> list() {
		List<Member> all = new ArrayList<Member>();
		List<? extends Member> methods = obtainAllMembers(TargetType.METHOD);
		List<? extends Member> fields = obtainAllMembers(TargetType.FIELD);

		fields = executeSearch(fields);
		methods = executeSearch(methods);

		all.addAll(fields);
		all.addAll(methods);
		return (List<T>) all;
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
		
		CompositeKey key = new CompositeKey(classe, targetType);
		
		Cache cache = CacheProvider.getCache();
		
        List<? extends Member> fields = (List<? extends Member>) cache.get(key);
        if(fields != null) return fields;

		fields = targetType.obtainMembersInClass(classe);
		while (classe.getSuperclass() != null) {
			classe = classe.getSuperclass();
			fields.addAll((List) targetType.obtainMembersInClass(classe));
		}

		cache.add(key, fields);
		return fields;
	}
}