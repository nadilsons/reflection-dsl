package br.com.bit.ideias.reflection.criteria;

import static br.com.bit.ideias.reflection.util.CollectionUtil.isEmpty;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import br.com.bit.ideias.reflection.exceptions.NoResultException;
import br.com.bit.ideias.reflection.exceptions.TooManyResultException;

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

	public <T extends AccessibleObject> T unique() {
		if (!(hasFields() || hasMethods()))
			throw new NoResultException();
		if (hasFields() && hasMethods())
			throw new TooManyResultException();

		if (hasFields()) {
			if (fields.size() > 1)
				throw new TooManyResultException();

			return (T) fields.get(0);
		}

		if (methods.size() > 1)
			throw new TooManyResultException();

		return (T) methods.get(0);
	}

	private boolean hasFields() {
		return !isEmpty(fields);
	}

	private boolean hasMethods() {
		return !isEmpty(methods);
	}
}
