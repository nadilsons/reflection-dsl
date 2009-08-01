package br.com.bit.ideias.reflection.criteria.expression;

import java.lang.reflect.Member;

/**
 * @author Leonardo Campos
 * @date 31/07/2009
 */
public class DisjunctionExpression extends ConjunctionExpression {
    @Override
    public boolean accept(Member member) {
        for (Expression expression : fieldExpressions) {
            if(expression.accept(member)) return true;
        }
        
        return false;
    }
}
