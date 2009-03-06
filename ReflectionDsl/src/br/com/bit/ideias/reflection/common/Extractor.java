package br.com.bit.ideias.reflection.common;

import br.com.bit.ideias.reflection.exceptions.InvalidStateException;
import br.com.bit.ideias.reflection.interfaces.Interceptor;

/**
 * @author Nadilson
 * @date 18/02/2009
 * 
 */
public class Extractor extends BaseExtractor {

	private final Class<?> targetClass;

	private ExtractorConstructor extractorConstructor;

	private ExtractorMethod extractorMethod;

	private ExtractorField extractorField;

	private Interceptor interceptor;

	private Extractor(final Class<?> classe) {
		this.targetClass = classe;
	}

	private Extractor(Object instance) {
		this(instance.getClass());
		this.extractorConstructor = new ExtractorConstructor(this, instance);
	}

	public static Extractor inObject(Object instance) {
		return new Extractor(instance);
	}

	public static Extractor forClass(final Class<?> classe) {
		return new Extractor(classe);
	}

	public ExtractorConstructor constructor() {
		extractorConstructor = new ExtractorConstructor(this, interceptor);
		return extractorConstructor;
	}

	public ExtractorMethod setMethod(final String methodName) {
		extractorMethod = new ExtractorMethod(this, methodName);
		return extractorMethod;
	}

	public ExtractorMethod method() {
		if (extractorMethod == null)
			throw new InvalidStateException("Metodo nao foi especificado");

		return extractorMethod;
	}

	public ExtractorField setField(final String fieldName) {
		extractorField = new ExtractorField(this, fieldName);
		return extractorField;
	}

	public ExtractorField field() {
		if (extractorField == null)
			throw new InvalidStateException("Field nao foi especificado");

		return extractorField;
	}

	public void applyInterceptor(final Interceptor interceptor) {
		this.interceptor = interceptor;
	}

	public Object getTargetInstance() {
		if (extractorConstructor == null)
			throw new InvalidStateException("Objeto não foi instanciado");

		return extractorConstructor.getTargetInstance();
	}

	public Class<?> getTargetClass() {
		return targetClass;
	}

	public boolean isEmpty() {
		return extractorConstructor == null || extractorConstructor.getTargetInstance() == null;
	}

}