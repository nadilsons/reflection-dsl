package br.com.bit.ideias.reflection.test.criteria;

import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.com.bit.ideias.reflection.core.Introspector;
import br.com.bit.ideias.reflection.criteria.Criterion;
import br.com.bit.ideias.reflection.criteria.CriterionResult;
import br.com.bit.ideias.reflection.criteria.Restrictions;
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
		criterion.add(Restrictions.setTargetType(TargetType.METHOD));

		criterionClasseFilha = introspectorClasseFilha.createCriterion();
		criterionClasseFilha.add(Restrictions.setTargetType(TargetType.METHOD));
	}

	@Test
	public void testRestrictionEq() throws Exception {
		final Method method = ClasseDominio.class.getDeclaredMethod("getAtributoPrivadoInt");
		criterion.add(Restrictions.eq("getAtributoPrivadoInt"));
		final CriterionResult result = criterion.list();

		Assert.assertTrue(result.getFields().isEmpty());
		Assert.assertEquals(result.getMethods().size(), 1);
		Assert.assertEquals(result.getMethods().get(0), method);
	}

	@Test
	public void testRestrictionEqEncadeado() throws Exception {
		criterion.add(Restrictions.eq("getAtributoPrivadoInt"));
		criterion.add(Restrictions.eq("isAlive"));
		final CriterionResult result = criterion.list();

		Assert.assertTrue(result.getFields().isEmpty());
		Assert.assertTrue(result.getFields().isEmpty());
	}

	@Test
	public void testRestrictionNe() throws Exception {
		criterion.add(Restrictions.ne("getAtributoPrivadoInt"));
		criterion.add(Restrictions.like("get"));
		final CriterionResult result = criterion.list();

		Assert.assertTrue(result.getFields().isEmpty());
		Assert.assertEquals(result.getMethods().size(), 5);
	}

	@Test
	public void testRestrictionNeEncadead() throws Exception {
		criterion.add(Restrictions.ne("getAtributoPrivadoInt"));
		criterion.add(Restrictions.ne("getClass"));
		criterion.add(Restrictions.like("get"));
		final CriterionResult result = criterion.list();

		Assert.assertTrue(result.getFields().isEmpty());
		Assert.assertEquals(result.getMethods().size(), 4);
	}

	@Test
	public void testRestrictionLike() throws Exception {
		criterion.add(Restrictions.like("getA"));
		final CriterionResult result = criterion.list();

		Assert.assertTrue(result.getFields().isEmpty());
		Assert.assertEquals(result.getMethods().size(), 3);
	}

	@Test
	public void testRestrictionLikeEncadeado() throws Exception {
		criterion.add(Restrictions.like("getA"));
		criterion.add(Restrictions.ne("getAtributoPrivadoInteiro"));
		final CriterionResult result = criterion.list();

		Assert.assertTrue(result.getFields().isEmpty());
		Assert.assertEquals(result.getMethods().size(), 2);
	}

	@Test
	public void testRestrictionLikeComLikeTypeStart() throws Exception {
		criterion.add(Restrictions.like("is", LikeType.START));
		final CriterionResult result = criterion.list();

		Assert.assertTrue(result.getFields().isEmpty());
		Assert.assertEquals(result.getMethods().size(), 1);
	}

	@Test
	public void testRestrictionLikeComLikeTypeEnd() throws Exception {
		criterion.add(Restrictions.like("Inteiro", LikeType.END));
		final CriterionResult result = criterion.list();

		Assert.assertTrue(result.getFields().isEmpty());
		Assert.assertEquals(result.getMethods().size(), 3);
	}

	@Test
	public void testRestrictionLikeComLikeTypeAnywhere() throws Exception {
		criterion.add(Restrictions.like("Priva", LikeType.ANYWHERE));
		final CriterionResult result = criterion.list();

		Assert.assertTrue(result.getFields().isEmpty());
		Assert.assertEquals(result.getMethods().size(), 8);
	}

	@Test
	public void testRestrictionRegex() throws Exception {
		criterion.add(Restrictions.regex("getAtributoPrivado[I|S]"));
		final CriterionResult result = criterion.list();

		Assert.assertTrue(result.getFields().isEmpty());
		Assert.assertEquals(result.getMethods().size(), 3);
	}

	@Test
	public void testRestrictionIn() throws Exception {
		criterion.add(Restrictions.in("getAtributoPrivadoInt", "setAlive"));
		final CriterionResult result = criterion.list();

		Assert.assertTrue(result.getFields().isEmpty());
		Assert.assertEquals(result.getMethods().size(), 2);
	}

	@Test
	public void testRestrictionAnnotatedWith() throws Exception {
		criterion.add(Restrictions.annotatedWith(MyAnnotation.class));
		final CriterionResult result = criterion.list();

		Assert.assertTrue(result.getFields().isEmpty());
		Assert.assertEquals(result.getMethods().size(), 2);
	}

	@Test
	public void testRestrictionShowOnlyPublicTrue() throws Exception {
		criterion.add(Restrictions.like("metodo"));
		criterion.add(Restrictions.showOnlyPublic(true));
		final CriterionResult result = criterion.list();

		Assert.assertTrue(result.getFields().isEmpty());
		Assert.assertEquals(result.getMethods().size(), 1);
	}

	@Test
	public void testRestrictionShowOnlyPublicFalse() throws Exception {
		criterion.add(Restrictions.like("metodo"));
		criterion.add(Restrictions.showOnlyPublic(false));
		final CriterionResult result = criterion.list();

		Assert.assertTrue(result.getFields().isEmpty());
		Assert.assertEquals(result.getMethods().size(), 2);
	}

	@Test
	public void testRestrictionEqComPropriedadeNaClassePai() throws Exception {
		criterion = introspectorClasseFilha.createCriterion();
		final Method method = ClasseDominio.class.getDeclaredMethod("getAtributoPrivadoInt");
		criterion.add(Restrictions.eq("getAtributoPrivadoInt"));
		final CriterionResult result = criterion.list();

		Assert.assertTrue(result.getFields().isEmpty());
		Assert.assertEquals(result.getMethods().size(), 1);
		Assert.assertEquals(method, result.getMethods().get(0));
	}

	@Test
	public void testRestrictionAnnotatedWithNaClassePai() throws Exception {
		criterionClasseFilha.add(Restrictions.annotatedWith(MyAnnotation.class));
		final CriterionResult result = criterionClasseFilha.list();

		Assert.assertTrue(result.getFields().isEmpty());
		Assert.assertEquals(result.getMethods().size(), 2);
	}

	@Test(expected = TooManyResultException.class)
	public void testRestrictionUniqueShouldThrowAnExceptionIfThereAreMoreThanOneResult() throws Exception {
		criterion.add(Restrictions.annotatedWith(MyAnnotation.class));
		criterion.uniqueResult();
	}

	@Test(expected = NoResultException.class)
	public void testRestrictionUniqueShouldThrowAnExceptionIfThereAreNoResults() throws Exception {
		criterion.add(Restrictions.eq("xyzu"));
		criterion.uniqueResult();
	}

	@Test
	public void testRestrictionUniqueShouldReturnOnlyOneMember() throws Exception {
		criterion.add(Restrictions.like("metodo")).add(Restrictions.showOnlyPublic(true));

		final Method methodActual = criterion.uniqueResult();
		final Method methodExpected = ClasseDominio.class.getDeclaredMethod("metodoQueVaiLancarException");

		Assert.assertEquals(methodExpected, methodActual);
	}

	@Test
	public void testRestrictionDisjunction() throws Exception {
		final Method method = ClasseDominio.class.getDeclaredMethod("getAtributoPrivadoInt");

		final ComplexExpression disjunction = Restrictions.disjunction();
		disjunction.add(Restrictions.eq("AAa"));
		disjunction.add(Restrictions.eq("BBa"));
		disjunction.add(Restrictions.eq("getAtributoPrivadoInt"));
		// criterion.add(disjunction.add(Restrictions.eq("AAa")));
		// criterion.add(disjunction.add(Restrictions.eq("BBa")));
		criterion.add(disjunction);

		final CriterionResult result = criterion.list();

		Assert.assertTrue(result.getFields().isEmpty());
		Assert.assertEquals(result.getMethods().size(), 1);
		Assert.assertEquals(result.getMethods().get(0), method);
	}

}
