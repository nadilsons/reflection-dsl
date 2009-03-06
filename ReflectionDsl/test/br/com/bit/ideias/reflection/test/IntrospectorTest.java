package br.com.bit.ideias.reflection.test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import br.com.bit.ideias.reflection.core.Introspector;
import br.com.bit.ideias.reflection.exceptions.ConstructorNotExistsException;
import br.com.bit.ideias.reflection.exceptions.FieldNotExistsException;
import br.com.bit.ideias.reflection.exceptions.FieldPrivateException;
import br.com.bit.ideias.reflection.exceptions.InvalidParameterException;
import br.com.bit.ideias.reflection.exceptions.InvalidStateException;
import br.com.bit.ideias.reflection.exceptions.MethodNotExistsException;
import br.com.bit.ideias.reflection.exceptions.MethodPrivateException;

/**
 * @author Nadilson
 * @date 18/02/2009
 * 
 */
public class IntrospectorTest {

	private static final Integer INTEIRO = 10;

	private static final String STRING = "valor_default";

	private static final Class<ClasseDominio> TARGET_CLASS = ClasseDominio.class;

	private static final ClasseDominio classeDominio = new ClasseDominio(INTEIRO, STRING);

	private static final MyInterceptorTest interceptor = new MyInterceptorTest();

	private Introspector introspectorInObject;

	private Introspector introspectorForClass;

	@BeforeMethod
	public void prepare() {
		introspectorForClass = Introspector.forClass(TARGET_CLASS);
		introspectorForClass.create(INTEIRO, STRING);
		introspectorInObject = Introspector.inObject(classeDominio);
	}

	// /////////////////////////////////////////////////////////////////////////
	// forClass
	// /////////////////////////////////////////////////////////////////////////
	@Test
	public void testClassForName() throws Exception {
		introspectorForClass = Introspector.forClass(TARGET_CLASS);
		assertEquals(TARGET_CLASS, introspectorForClass.getTargetClass());
	}

	@Test(expectedExceptions = InvalidParameterException.class)
	public void testClassForNameComParametroNulo() throws Exception {
		Introspector.forClass(null);
	}

	// /////////////////////////////////////////////////////////////////////////
	// inObject
	// /////////////////////////////////////////////////////////////////////////
	@Test
	public void testInObject() throws Exception {
		Introspector.inObject(classeDominio);
	}

	@Test(expectedExceptions = InvalidParameterException.class)
	public void testInObjectComParametroNulo() throws Exception {
		Introspector.inObject(null);
	}

	@Test(enabled = false)
	public void testInObjectTentarAplicarInterceptador() throws Exception {
		introspectorInObject.applyInterceptor(interceptor);
	}

	// /////////////////////////////////////////////////////////////////////////
	// create
	// /////////////////////////////////////////////////////////////////////////
	@Test(dataProvider = "testCreateComSucessoDataProvider")
	public void testCreate(final Object[] params) throws Exception {
		introspectorForClass.create(params);
		final ClasseDominio instance = (ClasseDominio) introspectorForClass.getTargetInstance();
		assertNotNull(instance);

		// Verifica se os atributos receberam o valor do constructor
		if (params.length > 0) {
			assertEquals(instance.getAtributoPrivadoInteiro(), params[0]);
			if (params.length == 2)
				assertEquals(instance.getAtributoPrivadoString(), params[1]);
		}
	}

	@Test(dataProvider = "testCreateComSucessoDataProvider")
	public void testCreateComInterceptor(final Object[] params) throws Exception {
		introspectorForClass = Introspector.forClass(TARGET_CLASS);
		introspectorForClass.applyInterceptor(new MyInterceptorTest());
		introspectorForClass.create(INTEIRO, STRING);

		introspectorForClass.field("atributoPrivadoInteiro").invoke();
	}

	@Test(expectedExceptions = ConstructorNotExistsException.class, dataProvider = "testCreateParaConstructorNaoExistenteDataProvider")
	public void testCreateParaConstructorNaoExistente(final Object[] params) throws Exception {
		introspectorForClass.create(params);
	}

	// /////////////////////////////////////////////////////////////////////////
	// invokeField
	// /////////////////////////////////////////////////////////////////////////
	@Test
	public void testInvokeFieldSemParametro() throws Exception {
		final Object invokeValue = introspectorForClass.field("atributoPrivadoInteiro").invoke();
		assertEquals(invokeValue, INTEIRO);

		final Object invokeValue2 = introspectorInObject.field("atributoPrivadoInteiro").invoke();
		assertEquals(invokeValue2, INTEIRO);
	}

	@Test
	public void testInvokeFieldComParametro() throws Exception {
		final int valorTeste = 200180;

		introspectorForClass.field("atributoPrivadoInteiro").invoke(valorTeste);
		final Object invokeValue = introspectorForClass.field("atributoPrivadoInteiro").invoke();
		assertEquals(invokeValue, valorTeste);

		introspectorInObject.field("atributoPrivadoInteiro").invoke(valorTeste);
		final Object invokeValue2 = introspectorForClass.field("atributoPrivadoInteiro").invoke();
		assertEquals(invokeValue2, valorTeste);
	}

	@Test(expectedExceptions = FieldNotExistsException.class)
	public void testInvokeFieldInexistenteSemParametro() throws Exception {
		final Object invoke = introspectorForClass.field("atributoInexistente").invoke();
		assertEquals(invoke, INTEIRO);
	}

	@Test(expectedExceptions = FieldNotExistsException.class)
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

	@Test(expectedExceptions = InvalidParameterException.class)
	public void testInvokeSetFieldPrivadoDiretoComAcessoComMaisDeUmParametro() throws Exception {
		introspectorForClass.field("atributoIsolado").directAccess().accessPrivateMembers().invoke("Olá", "errado");
	}

	@Test(expectedExceptions = InvalidParameterException.class)
	public void testInvokeSetFieldPrivadoComMaisDeUmParametro() throws Exception {
		introspectorForClass.field("atributoIsolado").invoke("Olá", "errado");
	}

	@Test(expectedExceptions = FieldPrivateException.class)
	public void testInvokeFieldPrivadoDiretoSemAcesso() throws Exception {
		introspectorForClass.field("atributoIsolado").directAccess().invoke();
	}

	@Test(expectedExceptions = InvalidStateException.class)
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

	@Test(expectedExceptions = MethodNotExistsException.class, dataProvider = "testInvokeMethodInexistenteSemParametrosDataProvider")
	public void testInvokeMethodInexistenteSemParametros(final String methodName, final Object params) throws Exception {
		introspectorForClass.method(methodName).invoke(params);
	}

	@Test(expectedExceptions = MethodNotExistsException.class)
	public void testInvokeMethodInexistenteComParametros() throws Exception {
		introspectorForClass.method("metodoInexistente").invoke(200180);
	}

	@Test(expectedExceptions = InvalidStateException.class)
	public void testInvokeMethodParaClasseNaoInstanciada() throws Exception {
		Introspector.forClass(TARGET_CLASS).method("getDobroAtributoPrivadoInteiro").invoke(20);
	}

	@Test(expectedExceptions = MethodPrivateException.class)
	public void testInvokeMethodPrivadoSemAcesso() throws Exception {
		introspectorForClass.method("metodoPrivado").invoke("testando");
	}

	@Test
	public void testInvokeMethodPrivadoComAcesso() throws Exception {
		introspectorForClass.method("metodoPrivado").accessPrivateMembers().invoke("testando");
		introspectorInObject.method("metodoPrivado").accessPrivateMembers().invoke("testando");
	}

	@Test
	public void testInvokeMethodAtributoIntPrimitivo() {
		introspectorForClass.field("atributoPrivadoInt").invoke(10);
		introspectorInObject.field("atributoPrivadoInt").invoke(10);
	}

	// /////////////////////////////////////////////////////////////////////////
	// Checks
	// /////////////////////////////////////////////////////////////////////////
	@Test(expectedExceptions = InvalidStateException.class)
	public void testSetarDirectAccesSemInstancia() throws Exception {
		introspectorForClass = Introspector.forClass(ClasseDominio.class);
		introspectorForClass.directAccess();
	}

	@Test(expectedExceptions = InvalidStateException.class)
	public void testSetarAccessPrivateMembersSemInstancia() throws Exception {
		introspectorForClass = Introspector.forClass(ClasseDominio.class);
		introspectorForClass.accessPrivateMembers();
	}

	// /////////////////////////////////////////////////////////////////////////

	@DataProvider
	protected Object[][] testCreateComSucessoDataProvider() {
		final Object[] params1 = new Object[] {};
		final Object[] params2 = new Object[] { 10 };
		final Object[] params3 = new Object[] { 15, "String_ok" };

		return new Object[][] { { params1 }, { params2 }, { params3 } };
	}

	@DataProvider
	protected Object[][] testCreateParaConstructorNaoExistenteDataProvider() {
		final Object[] params1 = new Object[] { true, "String_erro1" };
		final Object[] params2 = new Object[] { true, true };
		final Object[] params3 = new Object[] { "String_erro2", 8 };

		return new Object[][] { { params1 }, { params2 }, { params3 } };
	}

	@DataProvider
	protected Object[][] testInvokeMethodInexistenteSemParametrosDataProvider() {
		final Object[] params1 = new Object[] {};
		final Object[] params2 = new Object[] { true };
		final Object[] params3 = new Object[] { 8, 8 };
		final Object[] params4 = new Object[] { 8 };

		return new Object[][] { { "metodoInexistente", params1 }, { "metodoInexistente", params2 },
				{ "getDobro", params3 }, { "getDobroAtributoPrivadoInteiro", params4 } };
	}
}
