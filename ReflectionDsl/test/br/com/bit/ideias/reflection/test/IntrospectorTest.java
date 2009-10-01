package br.com.bit.ideias.reflection.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;

import br.com.bit.ideias.reflection.core.Introspector;
import br.com.bit.ideias.reflection.exceptions.ApplyInterceptorException;
import br.com.bit.ideias.reflection.exceptions.ConstructorNotExistsException;
import br.com.bit.ideias.reflection.exceptions.FieldNotExistsException;
import br.com.bit.ideias.reflection.exceptions.FieldPrivateException;
import br.com.bit.ideias.reflection.exceptions.InvalidParameterException;
import br.com.bit.ideias.reflection.exceptions.InvalidStateException;
import br.com.bit.ideias.reflection.exceptions.MethodAccessException;
import br.com.bit.ideias.reflection.exceptions.MethodNotExistsException;
import br.com.bit.ideias.reflection.exceptions.MethodPrivateException;
import br.com.bit.ideias.reflection.test.artefacts.ClasseDominio;
import br.com.bit.ideias.reflection.test.artefacts.MyInterceptorTest;
import br.com.bit.ideias.reflection.type.TreatmentExceptionType;

/**
 * @author Nadilson Oliveira da Silva
 * @date 18/02/2009
 * 
 */
public class IntrospectorTest {

	private static final Integer INTEIRO = 10;

	private static final String STRING = "valor_default";

	private static final Class<ClasseDominio> TARGET_CLASS = ClasseDominio.class;

	private static final ClasseDominio classeDominio = new ClasseDominio(INTEIRO, STRING);

	private Introspector introspectorForClass;

	private Introspector introspectorInObject;

	private MyInterceptorTest interceptor;

	@Before
	public void prepare() {
		introspectorForClass = Introspector.forClass(TARGET_CLASS);
		introspectorForClass.create(INTEIRO, STRING);
		introspectorInObject = Introspector.inObject(classeDominio);
		interceptor = new MyInterceptorTest();

		classeDominio.setAtributoPrivadoInteiro(INTEIRO);
		classeDominio.setAtributoPrivadoString(STRING);
	}

	// /////////////////////////////////////////////////////////////////////////
	// classForName
	// /////////////////////////////////////////////////////////////////////////
	@Test
	public void testClassForName() throws Exception {
		introspectorForClass = Introspector.forClass(TARGET_CLASS);
		assertEquals(TARGET_CLASS, introspectorForClass.getTargetClass());
	}

	@Test(expected = InvalidParameterException.class)
	public void testClassForNameComParametroClassNulo() throws Exception {
		final Class<?> classe = null;
		Introspector.forClass(classe);
	}

	@Test
	public void testClassForNameComParametroString() throws Exception {
		final String string = TARGET_CLASS.getName();
		Introspector.forClass(string);
	}

	@Test(expected = InvalidParameterException.class)
	public void testClassForNameComParametroStringNulo() throws Exception {
		final String string = null;
		Introspector.forClass(string);
	}

	// /////////////////////////////////////////////////////////////////////////
	// inObject
	// /////////////////////////////////////////////////////////////////////////
	@Test
	public void testInObject() throws Exception {
		Introspector.inObject(classeDominio);
	}

	@Test(expected = InvalidParameterException.class)
	public void testInObjectComParametroNulo() throws Exception {
		Introspector.inObject(null);
	}

	@Test(expected = ApplyInterceptorException.class)
	public void testInObjectTentarAplicarInterceptor() throws Exception {
		introspectorInObject.applyInterceptor(interceptor);
	}

	// /////////////////////////////////////////////////////////////////////////
	// create
	// /////////////////////////////////////////////////////////////////////////
	@Test
	public void testCreate() throws Exception {
		introspectorForClass.create();
		assertNotNull(introspectorForClass.getTargetInstance());

		introspectorForClass = Introspector.forClass(TARGET_CLASS);
		introspectorForClass.create(10);
		assertNotNull(introspectorForClass.getTargetInstance());
		assertEquals(((ClasseDominio) introspectorForClass.getTargetInstance()).getAtributoPrivadoInteiro().intValue(), 10);

		introspectorForClass = Introspector.forClass(TARGET_CLASS);
		introspectorForClass.create(15, "String_ok");
		assertNotNull(introspectorForClass.getTargetInstance());
		assertEquals(((ClasseDominio) introspectorForClass.getTargetInstance()).getAtributoPrivadoInteiro().intValue(), 15);
		assertEquals(((ClasseDominio) introspectorForClass.getTargetInstance()).getAtributoPrivadoString(), "String_ok");
	}

	@Test(expected = ConstructorNotExistsException.class)
	public void testCreateParaConstructorNaoExistente() throws Exception {
		introspectorForClass.create(true, "String_erro1");
		introspectorForClass.create();
	}

	@Test
	public void testCreateComInterceptor() throws Exception {
		introspectorForClass = Introspector.forClass(TARGET_CLASS);
		// introspectorForClass.applyInterceptor(interceptor).create(params);
	}

	@Test(expected = ApplyInterceptorException.class)
	public void testApplyInterceptorParaObjetoJaCriado() throws Exception {
		introspectorForClass.applyInterceptor(interceptor);
	}

	@Test
	public void testChamadasAoInterceptador() throws Exception {
		introspectorForClass = Introspector.forClass(TARGET_CLASS);
		introspectorForClass.applyInterceptor(interceptor).create(INTEIRO, STRING);

		introspectorForClass.field("atributoPrivadoInteiro").invoke();
		assertTrue(interceptor.isBeforedMethodCalled());
		assertTrue(interceptor.isAfterMethodCalled());
		assertFalse(interceptor.isAfterExceptionMethodCalled());
	}

	@Test(expected = MethodAccessException.class)
	public void testChamadasAoInterceptadorAposExcecaoQueDeveSerRelancada() throws Exception {
		introspectorForClass = Introspector.forClass(TARGET_CLASS);
		introspectorForClass.applyInterceptor(interceptor).create(INTEIRO, STRING);

		introspectorForClass.method("metodoQueVaiLancarException").invoke();
	}

	@Test
	public void testChamadasAoInterceptadorAposExcecaoQueNaoDeveSerRelancada() throws Exception {
		interceptor = new MyInterceptorTest(TreatmentExceptionType.STOP_EXCEPTION);
		introspectorForClass = Introspector.forClass(TARGET_CLASS);
		introspectorForClass.applyInterceptor(interceptor).create(INTEIRO, STRING);

		introspectorForClass.method("metodoQueVaiLancarException").invoke();
		assertTrue(interceptor.isBeforedMethodCalled());
		assertTrue(interceptor.isAfterMethodCalled());
		assertTrue(interceptor.isAfterExceptionMethodCalled());
	}

	// /////////////////////////////////////////////////////////////////////////
	// invokeField
	// /////////////////////////////////////////////////////////////////////////
	@Test
	public void testInvokeMemberSemParametroParaField() throws Exception {
		final String valorTeste = "Valor para o teste";
		final Field field = TARGET_CLASS.getDeclaredField("atributoPrivadoString");

		introspectorForClass.field(field).invoke(valorTeste);
		final Object invokeValue = introspectorForClass.member(field).invoke();
		assertEquals(invokeValue, valorTeste);

		introspectorInObject.field(field).invoke(valorTeste);
		final Object invokeValue2 = introspectorForClass.member(field).invoke();
		assertEquals(invokeValue2, valorTeste);
	}
	
	@Test
	public void testInvokeMemberComParametroParaField() throws Exception {
		final String valorTeste = "200180";
		final Field field = TARGET_CLASS.getDeclaredField("atributoIsolado");

		introspectorForClass.field(field).directAccess().accessPrivateMembers().invoke(valorTeste);
		final Object invokeValue = introspectorForClass.field(field).accessPrivateMembers().directAccess().invoke();
		assertEquals(invokeValue, valorTeste);

		introspectorInObject.field(field).accessPrivateMembers().directAccess().invoke(valorTeste);
		final Object invokeValue2 = introspectorForClass.field(field).accessPrivateMembers().directAccess().invoke();
		assertEquals(invokeValue2, valorTeste);
	}
	
	@Test
	public void testInvokeFieldUsandoApiReflection() throws Exception {
		final String valorTeste = "Valor para o teste";
		final Field field = TARGET_CLASS.getDeclaredField("atributoPrivadoString");

		introspectorForClass.field(field).invoke(valorTeste);
		final Object invokeValue = introspectorForClass.field(field).invoke();
		assertEquals(invokeValue, valorTeste);

		introspectorInObject.field(field).invoke(valorTeste);
		final Object invokeValue2 = introspectorForClass.field(field).invoke();
		assertEquals(invokeValue2, valorTeste);
	}

	@Test
	public void testInvokeFieldUsandoApiReflectionDiretoComAcesso() throws Exception {
		final String valorTeste = "200180";
		final Field field = TARGET_CLASS.getDeclaredField("atributoIsolado");

		introspectorForClass.field(field).directAccess().accessPrivateMembers().invoke(valorTeste);
		final Object invokeValue = introspectorForClass.field(field).accessPrivateMembers().directAccess().invoke();
		assertEquals(invokeValue, valorTeste);

		introspectorInObject.field(field).accessPrivateMembers().directAccess().invoke(valorTeste);
		final Object invokeValue2 = introspectorForClass.field(field).accessPrivateMembers().directAccess().invoke();
		assertEquals(invokeValue2, valorTeste);
	}

	@Test
	public void testInvokeFieldSemParametro() throws Exception {
		final Object invokeValue = introspectorForClass.field("atributoPrivadoInteiro").invoke();
		assertEquals(invokeValue, INTEIRO);

		final Object invokeValue2 = introspectorInObject.field("atributoPrivadoInteiro").invoke();
		assertEquals(invokeValue2, INTEIRO);
	}

	@Test
	public void testInvokeFieldComParametro() throws Exception {
		final Integer valorTeste = 200180;

		introspectorForClass.field("atributoPrivadoInteiro").invoke(valorTeste);
		final Object invokeValue = introspectorForClass.field("atributoPrivadoInteiro").invoke();
		assertEquals(invokeValue, valorTeste);

		introspectorInObject.field("atributoPrivadoInteiro").invoke(valorTeste);
		final Object invokeValue2 = introspectorForClass.field("atributoPrivadoInteiro").invoke();
		assertEquals(invokeValue2, valorTeste);
	}

	@Test(expected = FieldNotExistsException.class)
	public void testInvokeFieldInexistenteSemParametro() throws Exception {
		final Object invoke = introspectorForClass.field("atributoInexistente").invoke();
		assertEquals(invoke, INTEIRO);
	}

	@Test(expected = FieldNotExistsException.class)
	public void testInvokeFieldInexistenteComParametro() throws Exception {
		final Integer valorTeste = 200180;
		introspectorForClass.field("atributoInexistente").invoke(valorTeste);
	}

	@Test
	public void testInvokeGetFieldPrivadoDiretoComAcesso() throws Exception {
		introspectorForClass.field("atributoIsolado").directAccess().accessPrivateMembers().invoke();
		introspectorInObject.field("atributoIsolado").directAccess().accessPrivateMembers().invoke();
	}

	@Test
	public void testInvokeSetFieldPrivadoDiretoComAcesso() throws Exception {
		introspectorForClass.field("atributoIsolado").directAccess().accessPrivateMembers().invoke("Olá");
		introspectorInObject.field("atributoIsolado").directAccess().accessPrivateMembers().invoke("Olá");
	}

	@Test(expected = InvalidParameterException.class)
	public void testInvokeSetFieldPrivadoDiretoComAcessoComMaisDeUmParametro() throws Exception {
		introspectorForClass.field("atributoIsolado").directAccess().accessPrivateMembers().invoke("Olá", "errado");
	}

	@Test(expected = InvalidParameterException.class)
	public void testInvokeSetFieldPrivadoComMaisDeUmParametro() throws Exception {
		introspectorForClass.field("atributoIsolado").invoke("Olá", "errado");
	}

	@Test(expected = FieldPrivateException.class)
	public void testInvokeFieldPrivadoDiretoSemAcesso() throws Exception {
		introspectorForClass.field("atributoIsolado").directAccess().invoke();
	}

	@Test(expected = InvalidStateException.class)
	public void testInvokeFieldParaClasseNaoInstanciada() throws Exception {
		Introspector.forClass(TARGET_CLASS).field("atributoIsolado").invoke(20);
	}

	@Test
	public void testInvokeFieldParaAtributoIntPrimitivo() {
		introspectorForClass.field("atributoPrivadoInt").directAccess().accessPrivateMembers().invoke(10);
		introspectorInObject.field("atributoPrivadoInt").directAccess().accessPrivateMembers().invoke(10);
	}

	// /////////////////////////////////////////////////////////////////////////
	// invokeMethod
	// /////////////////////////////////////////////////////////////////////////
	@Test
	public void testInvokeMemberSemParametroParaMethod() throws Exception {
		final Method method = TARGET_CLASS.getDeclaredMethod("getDobroAtributoPrivadoInteiro");
		final Object invoke = introspectorForClass.member(method).invoke();

		assertEquals(invoke, INTEIRO * 2);
	}
	
	@Test
	public void testInvokeMemberComParametroParaMethod() throws Exception {
		final Integer valorTeste = 200180;
		final Method method = TARGET_CLASS.getDeclaredMethod("getDobro", Integer.class);

		final Object invoke = introspectorForClass.member(method).invoke(valorTeste);
		assertEquals(invoke, valorTeste * 2);
	}
	
	@Test
	public void testInvokeMethodSemParametroUsandoApiReflection() throws Exception {
		final Method method = TARGET_CLASS.getDeclaredMethod("getDobroAtributoPrivadoInteiro");
		final Object invoke = introspectorForClass.method(method).invoke();

		assertEquals(invoke, INTEIRO * 2);
	}

	@Test
	public void testInvokeMethodComParametroUsandoApiReflection() throws Exception {
		final Integer valorTeste = 200180;
		final Method method = TARGET_CLASS.getDeclaredMethod("getDobro", Integer.class);

		final Object invoke = introspectorForClass.method(method).invoke(valorTeste);
		assertEquals(invoke, valorTeste * 2);
	}

	@Test
	public void testInvokeMethodSemParametros() throws Exception {
		final Object invoke = introspectorForClass.method("getDobroAtributoPrivadoInteiro").invoke();
		assertEquals(invoke, INTEIRO * 2);

		final Object invoke2 = introspectorInObject.method("getDobroAtributoPrivadoInteiro").invoke();
		assertEquals(invoke2, INTEIRO * 2);
	}

	@Test
	public void testInvokeMethodComParametros() throws Exception {
		final Integer valorTeste = 200180;

		final Object invoke = introspectorForClass.method("getDobro").invoke(valorTeste);
		assertEquals(invoke, valorTeste * 2);

		final Object invoke2 = introspectorInObject.method("getDobro").invoke(valorTeste);
		assertEquals(invoke2, valorTeste * 2);
	}

	@Test(expected = MethodNotExistsException.class)
	public void testInvokeMethodInexistenteSemParametros() throws Exception {
		introspectorForClass.method("metodoInexistente").invoke(true);
	}

	@Test(expected = MethodNotExistsException.class)
	public void testInvokeMethodInexistenteComParametros() throws Exception {
		introspectorForClass.method("metodoInexistente").invoke(200180);
	}

	@Test(expected = InvalidStateException.class)
	public void testInvokeMethodParaClasseNaoInstanciada() throws Exception {
		Introspector.forClass(TARGET_CLASS).method("getDobroAtributoPrivadoInteiro").invoke(20);
	}

	@Test(expected = MethodPrivateException.class)
	public void testInvokeMethodPrivadoSemAcesso() throws Exception {
		introspectorForClass.method("metodoPrivado").invoke("testando");
	}

	@Test
	public void testInvokeMethodPrivadoComAcesso() throws Exception {
		introspectorForClass.method("metodoPrivado").accessPrivateMembers().invoke("testando");
		introspectorInObject.method("metodoPrivado").accessPrivateMembers().invoke("testando");
	}

	// /////////////////////////////////////////////////////////////////////////
	// Checks
	// /////////////////////////////////////////////////////////////////////////
	@Test(expected = InvalidStateException.class)
	public void testSetarDirectAccesSemInstancia() throws Exception {
		introspectorForClass = Introspector.forClass(ClasseDominio.class);
		introspectorForClass.directAccess();
	}

	@Test(expected = InvalidStateException.class)
	public void testSetarAccessPrivateMembersSemInstancia() throws Exception {
		introspectorForClass = Introspector.forClass(ClasseDominio.class);
		introspectorForClass.accessPrivateMembers();
	}

}
