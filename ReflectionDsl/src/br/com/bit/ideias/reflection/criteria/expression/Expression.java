package br.com.bit.ideias.reflection.criteria.expression;

import java.lang.reflect.Member;

/**
 * @author Leonardo Campos
 * @date 31/07/2009
 */
public interface Expression {
    public boolean accept(Member member);
}
