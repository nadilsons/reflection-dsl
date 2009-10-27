package br.com.bit.ideias.reflection.core;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

import br.com.bit.ideias.reflection.common.Extractor;
import br.com.bit.ideias.reflection.criteria.Criterion;
import br.com.bit.ideias.reflection.criteria.CriterionImpl;
import br.com.bit.ideias.reflection.exceptions.ApplyInterceptorException;
import br.com.bit.ideias.reflection.exceptions.ClassNotExistsException;
import br.com.bit.ideias.reflection.exceptions.InvalidParameterException;
import br.com.bit.ideias.reflection.exceptions.InvalidStateException;
import br.com.bit.ideias.reflection.interfaces.Interceptor;
import br.com.bit.ideias.reflection.type.TargetType;

/**
 * This is the main class of the project</br> It works as a façade.
 * 
 * @author Nadilson Oliveira da Silva
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
	 * 
	 * @param instance
	 *            represents the instance object of introspection
	 */
	public static Introspector inObject(final Object instance) {
		if (instance == null)
			throw new InvalidParameterException("The instance for instrospection can't be null");

		return new Introspector(instance);
	}

	public static Introspector forClass(final String className) {
		validateParam(className, "ClassName for instrospector can't be null");
		Class<?> clazz = null;

		try {
			clazz = Class.forName(className);
		} catch (final ClassNotFoundException e) {
			throw new ClassNotExistsException(e);
		}

		return forClass(clazz);
	}

	public static Introspector forClass(final Class<?> targetClass) {
		validateParam(targetClass, "Class for instrospector can't be null");

		return new Introspector(targetClass);
	}

	// /////////////////////////////////////////////////////////////////////////

	public Introspector create() {
		return create(new Object[] {});
	}

	/**
	 * Creates an instance of the class under instrospection.<br/>
	 * call getTargetInstance()<br/>
	 * if you want this instance right away<br/>
	 */
	public Introspector create(final Object... params) {
		this.extractor.constructor().newInstance(params);
		return this;
	}

	public Introspector member(final Member member) {
		validateParam(member, "Member can't be null");
		if (member instanceof Field)
			return field((Field) member);
		else if (member instanceof Method)
			return method((Method) member);
		else
			throw new InvalidParameterException(String.format("Parameter []%s is invalid", member.getClass().getName()));
	}

	public Introspector field(final String fieldName) {
		this.extractor.setField(fieldName);
		reset(TargetType.FIELD);

		return this;
	}

	public Introspector field(final Field field) {
		this.extractor.setField(field);
		reset(TargetType.FIELD);

		return this;
	}

	public Introspector method(final String methodName) {
		this.extractor.setMethod(methodName);
		reset(TargetType.METHOD);

		return this;
	}

	public Introspector method(final Method method) {
		this.extractor.setMethod(method);
		reset(TargetType.METHOD);

		return this;
	}
	
	public  <T extends Member> T get(final Class<?>... parametersTypes) {
		return (T) (isMethod ? extractor.method().get(parametersTypes) : extractor.field().get(parametersTypes));
	}
	
	 

	public Object invoke() {
		return invoke(new Object[] {});
	}

	public Object invoke(final Object... params) {
		return isMethod ? invokeMethod(params) : invokeField(params);
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

	public Criterion createCriterion() {
		criterion = new CriterionImpl(this);
		return criterion;
	}

	public static Criterion createCriterion(final String className) {
		return forClass(className).createCriterion();
	}

	public static Criterion createCriterion(final Class<?> targetClass) {
		return forClass(targetClass).createCriterion();
	}

	// /////////////////////////////////////////////////////////////////////////
	/**
	 * @return Class<?> the class held by this Instrospector, which is object of
	 *         instrospection.
	 */
	public Class<?> getTargetClass() {
		return extractor.getTargetClass();
	}

	@SuppressWarnings("unchecked")
	public <T> T getTargetInstance() {
		return (T) extractor.getTargetInstance();
	}

	// /////////////////////////////////////////////////////////////////////////

	private static void validateParam(final Object param, final String message) {
		if (param == null)
			throw new InvalidParameterException(message);
	}

	private void reset(final TargetType targetType) {
		this.isMethod = TargetType.METHOD.equals(targetType);
		this.accessPrivateMembers = false;
	}

	private Object invokeField(final Object... params) {
		return extractor.field().invoke(accessPrivateMembers, params);
	}

	private Object invokeMethod(final Object... params) {
		return extractor.method().invoke(accessPrivateMembers, params);
	}
	
}
