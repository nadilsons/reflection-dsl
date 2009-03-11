package br.com.bit.ideias.reflection.core;

import br.com.bit.ideias.reflection.common.Extractor;
import br.com.bit.ideias.reflection.exceptions.ApplyInterceptorException;
import br.com.bit.ideias.reflection.exceptions.InvalidParameterException;
import br.com.bit.ideias.reflection.exceptions.InvalidStateException;
import br.com.bit.ideias.reflection.interfaces.Interceptor;

/**
 * 
 * @author Nadilson
 * @date 18/02/2009
 * 
 */
public class Introspector {

	private final Extractor extractor;

	private boolean isMethod;

	private boolean accessPrivateMembers;

	private Introspector(final Object instance) {
		this.extractor = Extractor.inObject(instance);
	}

	private Introspector(final Class<?> classe) {
		this.extractor = Extractor.forClass(classe);
	}

	public static Introspector inObject(final Object instance) {
		if (instance == null)
			throw new InvalidParameterException("A instância para introspecção nao pode ser nula");

		return new Introspector(instance);
	}

	public static Introspector forClass(final Class<?> targetClass) {
		if (targetClass == null)
			throw new InvalidParameterException("A classe para introspecção nao pode ser nula");

		return new Introspector(targetClass);
	}

	// /////////////////////////////////////////////////////////////////////////

	public Introspector create() {
		return create(new Object[] {});
	}

	public Introspector create(final Object... params) {
		this.extractor.constructor().newInstance(params);
		return this;
	}

	public Introspector field(final String fieldName) {
		this.extractor.setField(fieldName);
		reset(false);

		return this;
	}

	public Introspector method(final String methodName) {
		this.extractor.setMethod(methodName);
		reset(true);

		return this;
	}

	public Object invoke() {
		return invoke(new Object[] {});
	}

	public Object invoke(final Object... params) {
		final boolean flag = accessPrivateMembers;
		return isMethod ? extractor.method().invoke(flag, params) : extractor.field().invoke(flag, params);
	}

	// /////////////////////////////////////////////////////////////////////////

	public Introspector accessPrivateMembers() {
		return accessPrivateMembers(true);
	}

	public Introspector accessPrivateMembers(final boolean flag) {
		if (extractor.isEmpty())
			throw new InvalidStateException("A classe nao foi instanciada");

		this.accessPrivateMembers = flag;
		return this;
	}

	public Introspector directAccess() {
		return directAccess(true);
	}

	public Introspector directAccess(final boolean flag) {
		this.extractor.field().directAccess(flag);
		return this;
	}

	public Introspector applyInterceptor(final Interceptor interceptor) {
		if (!extractor.isEmpty())
			throw new ApplyInterceptorException("Interceptadores não podem ser aplicados para objetos já instanciados");

		extractor.applyInterceptor(interceptor);
		return this;
	}

	// /////////////////////////////////////////////////////////////////////////

	public Class<?> getTargetClass() {
		return extractor.getTargetClass();
	}

	public Object getTargetInstance() {
		return extractor.getTargetInstance();
	}

	// /////////////////////////////////////////////////////////////////////////

	private void reset(final boolean isMethod) {
		this.isMethod = isMethod;
		this.accessPrivateMembers = false;
	}

}
