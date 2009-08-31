package br.com.bit.ideias.reflection.test.criteria;

import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.com.bit.ideias.reflection.core.Introspector;
import br.com.bit.ideias.reflection.criteria.Criterion;
import br.com.bit.ideias.reflection.criteria.CriterionResult;
import br.com.bit.ideias.reflection.criteria.Restriction;
import br.com.bit.ideias.reflection.criteria.expression.ComplexExpression;
import br.com.bit.ideias.reflection.enums.LikeType;
import br.com.bit.ideias.reflection.enums.TargetType;
import br.com.bit.ideias.reflection.exceptions.NoResultException;
import br.com.bit.ideias.reflection.exceptions.TooManyResultException;
import br.com.bit.ideias.reflection.test.artefacts.ClasseDominio;
import br.com.bit.ideias.reflection.test.artefacts.ClasseDominioFilha;
import br.com.bit.ideias.reflection.test.artefacts.MyAnnotation;

/**
 * 
 * @author Nadilson Oliveira da Silva
 * @since 28/07/2009
 */
public class RestrictionsMethodTest {

	private final Introspector introspector = Introspector.forClass(ClasseDominio.class);

	private final Introspector introspectorClasseFilha = Introspector.forClass(ClasseDominioFilha.class);

	private Criterion criterion;

	private Criterion criterionClasseFilha;

	@Before
	public void prepare() {
		criterion = introspector.createCriterion();
		criterion.add(Restriction.setTargetType(TargetType.METHOD));

		criterionClasseFilha = introspectorClasseFilha.createCriterion();
		criterionClasseFilha.add(Restriction.setTargetType(TargetType.METHOD));
	}

	@Test
	public void testRestrictionEq() throws Exception {
		final Method method = ClasseDominio.class.getDeclaredMethod("getAtributoPrivadoInt");
		criterion.add(Restriction.eq("getAtributoPrivadoInt"));
		final CriterionResult result = criterion.list();

		Assert.assertTrue(result.getFields().isEmpty());
		Assert.assertEquals(result.getMethods().size(), 1);
		Assert.assertEquals(result.getMethods().get(0), method);
	}

	@Test
	public void testRestrictionEqEncadeado() throws Exception {
		criterion.add(Restriction.eq("getAtributoPrivadoInt"));
		criterion.add(Restriction.eq("isAlive"));
		final CriterionResult result = criterion.list();

		Assert.assertTrue(result.getFields().isEmpty());
		Assert.assertTrue(result.getFields().isEmpty());
	}

	@Test
	public void testRestrictionNe() throws Exception {
		criterion.add(Restriction.ne("getAtributoPrivadoInt"));
		criterion.add(Restriction.like("get"));
		final CriterionResult result = criterion.list();

		Assert.assertTrue(result.getFields().isEmpty());
		Assert.assertEquals(result.getMethods().size(), 5);
	}

	@Test
	public void testRestrictionNeEncadead() throws Exception {
		criterion.add(Restriction.ne("getAtributoPrivadoInt"));
		criterion.add(Restriction.ne("getClass"));
		criterion.add(Restriction.like("get"));
		final CriterionResult result = criterion.list();

		Assert.assertTrue(result.getFields().isEmpty());
		Assert.assertEquals(result.getMethods().size(), 4);
	}

	@Test
	public void testRestrictionLike() throws Exception {
		criterion.add(Restriction.like("getA"));
		final CriterionResult result = criterion.list();

		Assert.assertTrue(result.getFields().isEmpty());
		Assert.assertEquals(result.getMethods().size(), 3);
	}

	@Test
	public void testRestrictionLikeEncadeado() throws Exception {
		criterion.add(Restriction.like("getA"));
		criterion.add(Restriction.ne("getAtributoPrivadoInteiro"));
		final CriterionResult result = criterion.list();

		Assert.assertTrue(result.getFields().isEmpty());
		Assert.assertEquals(result.getMethods().size(), 2);
	}

	@Test
	public void testRestrictionLikeComLikeTypeStart() throws Exception {
		criterion.add(Restriction.like("is", LikeType.START));
		final CriterionResult result = criterion.list();

		Assert.assertTrue(result.getFields().isEmpty());
		Assert.assertEquals(result.getMethods().size(), 1);
	}

	@Test
	public void testRestrictionLikeComLikeTypeEnd() throws Exception {
		criterion.add(Restriction.like("Inteiro", LikeType.END));
		final CriterionResult result = criterion.list();

		Assert.assertTrue(result.getFields().isEmpty());
		Assert.assertEquals(result.getMethods().size(), 3);
	}

	@Test
	public void testRestrictionLikeComLikeTypeAnywhere() throws Exception {
		criterion.add(Restriction.like("Priva", LikeType.ANYWHERE));
		final CriterionResult result = criterion.list();

		Assert.assertTrue(result.getFields().isEmpty());
		Assert.assertEquals(result.getMethods().size(), 8);
	}

	@Test
	public void testRestrictionRegex() throws Exception {
		criterion.add(Restriction.regex("getAtributoPrivado[I|S]"));
		final CriterionResult result = criterion.list();

		Assert.assertTrue(result.getFields().isEmpty());
		Assert.assertEquals(result.getMethods().size(), 3);
	}

	@Test
	public void testRestrictionIn() throws Exception {
		criterion.add(Restriction.in("getAtributoPrivadoInt", "setAlive"));
		final CriterionResult result = criterion.list();

		Assert.assertTrue(result.getFields().isEmpty());
		Assert.assertEquals(result.getMethods().size(), 2);
	}

	@Test
	public void testRestrictionAnnotatedWith() throws Exception {
		criterion.add(Restriction.annotatedWith(MyAnnotation.class));
		final CriterionResult result = criterion.list();

		Assert.assertTrue(result.getFields().isEmpty());
		Assert.assertEquals(result.getMethods().size(), 2);
	}

	@Test
	public void testRestrictionShowOnlyPublicTrue() throws Exception {
		criterion.add(Restriction.like("metodo"));
		criterion.add(Restriction.showOnlyPublic(true));
		final CriterionResult result = criterion.list();

		Assert.assertTrue(result.getFields().isEmpty());
		Assert.assertEquals(3, result.getMethods().size());
	}

	@Test
	public void testRestrictionShowOnlyPublicFalse() throws Exception {
		criterion.add(Restriction.like("metodo"));
		criterion.add(Restriction.showOnlyPublic(false));
		final CriterionResult result = criterion.list();

		Assert.assertTrue(result.getFields().isEmpty());
		Assert.assertEquals(4, result.getMethods().size());
	}

	@Test
	public void testRestrictionEqComPropriedadeNaClassePai() throws Exception {
		criterion = introspectorClasseFilha.createCriterion();
		final Method method = ClasseDominio.class.getDeclaredMethod("getAtributoPrivadoInt");
		criterion.add(Restriction.eq("getAtributoPrivadoInt"));
		final CriterionResult result = criterion.list();

		Assert.assertTrue(result.getFields().isEmpty());
		Assert.assertEquals(result.getMethods().size(), 1);
		Assert.assertEquals(method, result.getMethods().get(0));
	}

	@Test
	public void testRestrictionAnnotatedWithNaClassePai() throws Exception {
		criterionClasseFilha.add(Restriction.annotatedWith(MyAnnotation.class));
		final CriterionResult result = criterionClasseFilha.list();

		Assert.assertTrue(result.getFields().isEmpty());
		Assert.assertEquals(result.getMethods().size(), 2);
	}

	@Test(expected = TooManyResultException.class)
	public void testRestrictionUniqueShouldThrowAnExceptionIfThereAreMoreThanOneResult() throws Exception {
		criterion.add(Restriction.annotatedWith(MyAnnotation.class));
		criterion.uniqueResult();
	}

	@Test(expected = NoResultException.class)
	public void testRestrictionUniqueShouldThrowAnExceptionIfThereAreNoResults() throws Exception {
		criterion.add(Restriction.eq("xyzu"));
		criterion.uniqueResult();
	}

	@Test
	public void testRestrictionUniqueShouldReturnOnlyOneMember() throws Exception {
		criterion.add(Restriction.like("metodoQueVaiLanc")).add(Restriction.showOnlyPublic(true));

		final Method methodActual = criterion.uniqueResult();
		final Method methodExpected = ClasseDominio.class.getDeclaredMethod("metodoQueVaiLancarException");

		Assert.assertEquals(methodExpected, methodActual);
	}

	@Test
	public void testRestrictionDisjunction() throws Exception {
		final Method method = ClasseDominio.class.getDeclaredMethod("getAtributoPrivadoInt");

		final ComplexExpression disjunction = Restriction.disjunction();
		disjunction.add(Restriction.eq("AAa"));
		disjunction.add(Restriction.eq("BBa"));
		disjunction.add(Restriction.eq("getAtributoPrivadoInt"));
		// criterion.add(disjunction.add(Restrictions.eq("AAa")));
		// criterion.add(disjunction.add(Restrictions.eq("BBa")));
		criterion.add(disjunction);

		final CriterionResult result = criterion.list();

		Assert.assertTrue(result.getFields().isEmpty());
		Assert.assertEquals(result.getMethods().size(), 1);
		Assert.assertEquals(result.getMethods().get(0), method);
	}

	@Test
	public void testTypeEq() {
		criterion.add(Restriction.typeEq(String.class));
		final CriterionResult result = criterion.list();
		
		Assert.assertTrue(result.getFields().isEmpty());
		Assert.assertTrue(result.getMethods().isEmpty());
	}
	
	@Test
	public void testTypeReturn() {
		criterion.add(Restriction.typeReturn(Integer.class));
		final CriterionResult result = criterion.list();
		
		Assert.assertTrue(result.getFields().isEmpty());
		Assert.assertEquals(3, result.getMethods().size());
	}
	
	@Test
	public void testTypeParams() {
		criterion.add(Restriction.typesParams(String.class));
		final CriterionResult result = criterion.list();
		
		Assert.assertTrue(result.getFields().isEmpty());
		Assert.assertEquals(2, result.getMethods().size());
	}
	
	@Test
	public void testTypeParamsComMaisDeUmParametro() {
		criterion.add(Restriction.typesParams(String.class, Integer.class, Boolean.class));
		final CriterionResult result = criterion.list();
		
		Assert.assertTrue(result.getFields().isEmpty());
		Assert.assertEquals(1, result.getMethods().size());
	}
	
	@Test
	public void testTypeParamsComMaisDeUmParametroPrimitivo() {
		criterion.add(Restriction.typesParams(String.class, Integer.class, boolean.class));
		final CriterionResult result = criterion.list();
		
		Assert.assertTrue(result.getFields().isEmpty());
		Assert.assertEquals(1, result.getMethods().size());
	}
}
