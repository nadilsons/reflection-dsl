package br.com.bit.ideias.reflection.interceptor;

import br.com.bit.ideias.reflection.core.extrator.Extractor;
import br.com.bit.ideias.reflection.type.TreatmentExceptionType;

/**
 * 
 * @see Extractor#applyInterceptor(Interceptor)
 * @author Nadilson Oliveira da Silva
 * @date 19/02/2009
 */
public interface Interceptor {

	public void doBefore(InvocationContext invocationContext);

	public void doAfter(InvocationContext invocationContext);

	public TreatmentExceptionType doAfterException(final InvocationContext invocationContext, final Exception e);

}
