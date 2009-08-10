package br.com.bit.ideias.reflection.test.criteria;

import java.lang.reflect.Field;

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
public class RestrictionsTest {

	private final Introspector introspector = Introspector.forClass(ClasseDominio.class);

	private final Introspector introspectorClasseFilha = Introspector.forClass(ClasseDominioFilha.class);

	private Criterion criterion;

	private Criterion criterionClasseFilha;

	@Before
	public void prepare() {
		criterion = introspector.createCriterion();
		criterion.add(Restrictions.setTargetType(TargetType.FIELD));

		criterionClasseFilha = introspectorClasseFilha.createCriterion();
		criterionClasseFilha.add(Restrictions.setTargetType(TargetType.METHOD));
	}

	@Test
	public void testRestrictionEqComTargetTypeNaoEspecificado() throws Exception {
		criterion = introspector.createCriterion();
		criterion.add(Restrictions.like("tributoPrivadoString", LikeType.END));
		final CriterionResult result = criterion.list();

		Assert.assertEquals(result.getMethods().size(), 2);
		Assert.assertEquals(result.getFields().size(), 1);
	}

	@Test
	public void testRestrictionEqDirectCriterion() throws Exception {
		final Field field = ClasseDominio.class.getDeclaredField("atributoPrivadoInt");
		final Criterion localCriterion = Introspector.createCriterion(ClasseDominio.class).add(Restrictions.eq("atributoPrivadoInt"));
		final CriterionResult result = localCriterion.list();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 1);
		Assert.assertEquals(result.getFields().get(0), field);
	}

	@Test
	public void testRestrictionEq() throws Exception {
		final Field field = ClasseDominio.class.getDeclaredField("atributoPrivadoInt");
		criterion.add(Restrictions.eq("atributoPrivadoInt"));
		final CriterionResult result = criterion.list();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 1);
		Assert.assertEquals(result.getFields().get(0), field);
	}

	@Test
	public void shouldReturnEitherWhenUsingDisjunction() throws Exception {
		final Field field = ClasseDominio.class.getDeclaredField("atributoPrivadoInt");
		criterion.add(Restrictions.eq("atributoPrivadoInt"));
		final ComplexExpression disjunction = Restrictions.disjunction(Restrictions.eq("yzxabc"));
		disjunction.add(Restrictions.eq("atributoPrivadoInt"));
		criterion.add(disjunction);

		final CriterionResult result = criterion.list();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 1);
		Assert.assertEquals(result.getFields().get(0), field);
	}

	@Test
	public void testRestrictionEqEncadeado() throws Exception {
		criterion.add(Restrictions.eq("atributoPrivadoInt"));
		criterion.add(Restrictions.eq("isAlive"));
		final CriterionResult result = criterion.list();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertTrue(result.getFields().isEmpty());
	}

	@Test
	public void testRestrictionNe() throws Exception {
		criterion.add(Restrictions.ne("atributoPrivadoInt"));
		final CriterionResult result = criterion.list();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 7);
	}

	@Test
	public void testRestrictionNeEncadead() throws Exception {
		criterion.add(Restrictions.ne("atributoPrivadoInt"));
		criterion.add(Restrictions.ne("isAlive"));
		criterion.add(Restrictions.ne("Privative"));
		final CriterionResult result = criterion.list();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 5);
	}

	@Test
	public void testRestrictionLike() throws Exception {
		criterion.add(Restrictions.like("atributo"));
		final CriterionResult result = criterion.list();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 4);
	}

	@Test
	public void testRestrictionLikeEncadeado() throws Exception {
		criterion.add(Restrictions.like("atributo"));
		criterion.add(Restrictions.ne("atributoPrivadoInt"));
		final CriterionResult result = criterion.list();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 3);
	}

	@Test
	public void testRestrictionLikeComLikeTypeStart() throws Exception {
		criterion.add(Restrictions.like("atributo", LikeType.START));
		final CriterionResult result = criterion.list();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 4);
	}

	@Test
	public void testRestrictionLikeComLikeTypeEnd() throws Exception {
		criterion.add(Restrictions.like("ive", LikeType.END));
		final CriterionResult result = criterion.list();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 2);
	}

	@Test
	public void testRestrictionLikeComLikeTypeAnywhere() throws Exception {
		criterion.add(Restrictions.like("Priva", LikeType.ANYWHERE));
		final CriterionResult result = criterion.list();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 5);
	}

	@Test
	public void testRestrictionRegex() throws Exception {
		criterion.add(Restrictions.regex("comeca[P|p]riva"));
		final CriterionResult result = criterion.list();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 2);
	}

	@Test
	public void testRestrictionIn() throws Exception {
		criterion.add(Restrictions.in("atributoPrivadoInt", "comecaPriva"));
		final CriterionResult result = criterion.list();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 2);
	}

	@Test
	public void testRestrictionAnnotatedWith() throws Exception {
		criterion.add(Restrictions.annotatedWith(MyAnnotation.class));
		final CriterionResult result = criterion.list();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 3);
	}

	@Test
	public void testRestrictionShowOnlyPublicTrue() throws Exception {
		criterion.add(Restrictions.regex("comeca[P|p]riva"));
		criterion.add(Restrictions.showOnlyPublic(true));
		final CriterionResult result = criterion.list();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 1);
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
		criterion.add(Restrictions.regex("comeca[P|p]riva")).add(Restrictions.showOnlyPublic(true));
		final Field fieldActual = criterion.uniqueResult();

		final Field fieldExpected = ClasseDominio.class.getDeclaredField("comecaPriva");
		Assert.assertEquals(fieldExpected, fieldActual);
	}

	@Test
	public void testRestrictionShowOnlyPublicFalse() throws Exception {
		criterion.add(Restrictions.regex("comeca[P|p]riva"));
		criterion.add(Restrictions.showOnlyPublic(false));
		final CriterionResult result = criterion.list();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 2);
	}

	@Test
	public void testRestrictionEqComPropriedadeNaClassePai() throws Exception {
		criterion = introspectorClasseFilha.createCriterion();
		criterion.add(Restrictions.regex("comeca[P|p]riva"));
		final CriterionResult result = criterion.list();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 2);
	}

	@Test
	public void testRestrictionAnnotatedWithNaClassePai() throws Exception {
		criterion.add(Restrictions.annotatedWith(MyAnnotation.class));
		final CriterionResult result = criterion.list();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 3);
	}

}
