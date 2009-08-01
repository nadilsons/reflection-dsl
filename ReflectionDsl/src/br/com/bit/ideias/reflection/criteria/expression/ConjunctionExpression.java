package br.com.bit.ideias.reflection.criteria.expression;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Leonardo Campos
 * @date 31/07/2009
 * 
 * For a ConjunctionExpression to be evaluated as true, all its subexpressions should evaluate to true
 */
public class ConjunctionExpression extends ExpressionImpl implements ComplexExpression {
	private final List<Expression> methodExpressions = new ArrayList<Expression>();
	protected final List<Expression> fieldExpressions = new ArrayList<Expression>();

	public ConjunctionExpression() {
		super(null, null, null);
	}

	public void add(final Expression expression) {
		// final List<Expression> lista =
		// (TargetType.FIELD.equals(expression.getTargetType())) ?
		// fieldExpressions : methodExpressions;
		fieldExpressions.add(expression);
	}

	@Override
	public boolean accept(final Member member) {
		for (final Expression expression : fieldExpressions) {
			if (!expression.accept(member))
				return false;
		}

		return true;
	}
}
