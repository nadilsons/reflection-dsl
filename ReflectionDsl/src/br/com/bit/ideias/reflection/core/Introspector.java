package br.com.bit.ideias.reflection.core;

import br.com.bit.ideias.reflection.common.Extractor;
import br.com.bit.ideias.reflection.criteria.Criterion;
import br.com.bit.ideias.reflection.criteria.CriterionImpl;
import br.com.bit.ideias.reflection.exceptions.ApplyInterceptorException;
import br.com.bit.ideias.reflection.exceptions.ClassNotExistsException;
import br.com.bit.ideias.reflection.exceptions.InvalidParameterException;
import br.com.bit.ideias.reflection.exceptions.InvalidStateException;
import br.com.bit.ideias.reflection.interfaces.Interceptor;

/**
 * This is the main class of the project</br>
 * It works as a façade.
 * 
 * @author Nadilson
 * @date 18/02/2009
 */
public class Introspector {

	private final Extractor extractor;

	private boolean isMethod;

	private boolean accessPrivateMembers;

	private Criterion criterion;

	private Introspector(final Object instance) {
		this.extractor = Extractor.inObject(instance);
	}

	private Introspector(final Class<?> classe) {
		this.extractor = Extractor.forClass(classe);
	}

	/**
	 * Creates an instrospector for the instance.
	 * @param instance represents the instance object of introspection
	 */
	public static Introspector inObject(final Object instance) {
		if (instance == null)
			throw new InvalidParameterException("The instance for instrospection can't be null");

		return new Introspector(instance);
	}

	public static Introspector forClass(final String className) {
		validateParam(className);
		Class<?> clazz = null;
		
		try {
			clazz = Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new ClassNotExistsException(e);
		}
		
		return forClass(clazz);
	}

	public static Introspector forClass(final Class<?> targetClass) {
		validateParam(targetClass);

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
			throw new ApplyInterceptorException("Interceptors can't be applyied to already instanced objects");

		extractor.applyInterceptor(interceptor);
		return this;
	}

	public Criterion makeCriterion() {
		criterion = new CriterionImpl(this);
		return criterion;
	}
	// /////////////////////////////////////////////////////////////////////////
	/**
	 * @return Class<?> the class held by this Instrospector, which is object of instrospection.
	 */
	public Class<?> getTargetClass() {
		return extractor.getTargetClass();
	}

	public Object getTargetInstance() {
		return extractor.getTargetInstance();
	}

	// /////////////////////////////////////////////////////////////////////////

	private static void validateParam(final Object param) {
		if (param == null)
			throw new InvalidParameterException("Class for instrospector can't be null");
	}
	
	private void reset(final boolean isMethod) {
		this.isMethod = isMethod;
		this.accessPrivateMembers = false;
	}


}
