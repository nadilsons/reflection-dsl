package br.com.bit.ideias.reflection.criteria.expression;

import br.com.bit.ideias.reflection.enums.SearchType;
import br.com.bit.ideias.reflection.enums.TargetType;

/**
 * 
 * @author nadilson
 * @since 28/07/2009
 */
public abstract class Expression {

	protected String value;

	protected SearchType searchType;
	
	protected TargetType targetType;
	
	public Expression(String value, SearchType searchType, TargetType targetType) {
		this.value = value;
		this.searchType = searchType;
		this.targetType = targetType;
	}

	public String getValue() {
		return value;
	}

	public TargetType getTargetType() {
		return targetType;
	}
	
	public SearchType getSearchType() {
		return searchType;
	}

//	public abstract List<?> filter(Class<?> classe);
//	
//	protected Field[] getFields(Class<?> classe) {
//		classe.getDeclaredFields()[0].getName();
//		return classe.getDeclaredFields(); 
//	}
//	
//	protected Method[] getMethods(Class<?> classe) {
//		return classe.getDeclaredMethods();
//	}
}
