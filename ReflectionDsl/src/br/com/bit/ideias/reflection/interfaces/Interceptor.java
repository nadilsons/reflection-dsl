package br.com.bit.ideias.reflection.interfaces;

import br.com.bit.ideias.reflection.common.Extractor;
import br.com.bit.ideias.reflection.enums.TreatmentExceptionType;
import br.com.bit.ideias.reflection.interceptor.InvocationContext;

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
