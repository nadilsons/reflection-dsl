package br.com.bit.ideias.reflection.criteria;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.bit.ideias.reflection.core.Introspector;
import br.com.bit.ideias.reflection.criteria.expression.ConjunctionExpression;
import br.com.bit.ideias.reflection.criteria.expression.Expression;

/**
 * 
 * @author nadilson
 * @since 28/07/2009
 */
public class CriterionImpl implements Criterion {
    private ConjunctionExpression expressionHolder = new ConjunctionExpression();
	private final Introspector introspector;

	public CriterionImpl(final Introspector introspector) {
		this.introspector = introspector;
	}

	public void add(final Expression expression) {
	    expressionHolder.add(expression);
	}

	public CriterionResult search() {
		final List<? extends Member> methods = new ArrayList<Method>();
		
		Class<?> classe = introspector.getTargetClass();
        List<? extends Member> fields = obtainFields(classe);
        
        final List<Member> filtred = new ArrayList<Member>();
        for (Member member : fields) {
            if(expressionHolder.accept(member))
                filtred.add(member);
        }

		return new CriterionResult(filtred, methods);
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