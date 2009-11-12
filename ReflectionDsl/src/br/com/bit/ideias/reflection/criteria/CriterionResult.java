package br.com.bit.ideias.reflection.criteria;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @author Nadilson Oliveira da Silva
 * @since 28/07/2009
 */
public class CriterionResult {

	private final List<Field> fields;

	private final List<Method> methods;

	public CriterionResult(final List fields, final List methods) {
		this.fields = fields;
		this.methods = methods;
	}

	public List<Field> getFields() {
		return Collections.unmodifiableList(fields);
	}

	public List<Method> getMethods() {
		return Collections.unmodifiableList(methods);
	}
	
	public List<Member> getMembers() {
		List<Member> retorno = new ArrayList<Member>(fields);
		retorno.addAll(methods);
		
		return Collections.unmodifiableList(retorno);
	}

}
