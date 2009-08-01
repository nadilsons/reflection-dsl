package br.com.bit.ideias.reflection.criteria.expression;

import java.lang.reflect.Member;

import br.com.bit.ideias.reflection.enums.SearchType;
import br.com.bit.ideias.reflection.enums.TargetType;

/**
 * @author Leonardo Campos
 * @date 31/07/2009
 */
public interface Expression {

	public static final String NAME_SEPARATOR = ",";

	public boolean accept(Member member);

	public String getValue();

	public TargetType getTargetType();

	public SearchType getSearchType();
}
