package br.com.bit.ideias.reflection.criteria.expression;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Leonardo Campos
 * @date 31/07/2009
 * 
 *       For a ConjunctionExpression to be evaluated as true, all its
 *       subexpressions should evaluate to true
 */
public class ConjunctionExpression extends ExpressionImpl implements ComplexExpression {
	//private final List<Expression> methodExpressions = new ArrayList<Expression>();
	protected final List<Expression> expressions = new ArrayList<Expression>();

	public ConjunctionExpression() {
		super(null, null);
	}

	public ComplexExpression add(final Expression expression) {
		final List<Expression> list = getList(expression);
		list.add(expression);
		
		return this;
	}

	@Override
	public boolean accept(final Member member) {
		final List<Expression> list = getList(member);
		for (final Expression expression : list) {
			if (!expression.accept(member))
				return false;
		}

		return true && !list.isEmpty();
	}

	private List<Expression> getList(final Member member) {
		return expressions;//(member.getClass().isAssignableFrom(Field.class)) ? fieldExpressions : methodExpressions;
	}

	private List<Expression> getList(final Expression expression) {
		return expressions;//(TargetType.FIELD.equals(expression.getTargetType())) ? fieldExpressions : methodExpressions;
	}
}
