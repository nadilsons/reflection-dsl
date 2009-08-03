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

	@Before
	public void prepare() {
		criterion = introspector.createCriterion();
	}

	@Test
	public void testRestrictionEq() throws Exception {
		final Field field = ClasseDominio.class.getDeclaredField("atributoPrivadoInt");
		criterion.add(Restrictions.fields().eq("atributoPrivadoInt"));
		final CriterionResult result = criterion.search();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 1);
		Assert.assertEquals(result.getFields().get(0), field);
	}

	@Test
	public void shouldReturnEitherWhenUsingDisjunction() throws Exception {
		final Field field = ClasseDominio.class.getDeclaredField("atributoPrivadoInt");
		criterion.add(Restrictions.fields().eq("atributoPrivadoInt"));
		final ComplexExpression disjunction = Restrictions.fields().disjunction(Restrictions.fields().eq("yzxabc"));
		disjunction.add(Restrictions.fields().eq("atributoPrivadoInt"));
		criterion.add(disjunction);

		final CriterionResult result = criterion.search();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 1);
		Assert.assertEquals(result.getFields().get(0), field);
	}

	@Test
	public void testRestrictionEqEncadeado() throws Exception {
		criterion.add(Restrictions.fields().eq("atributoPrivadoInt"));
		criterion.add(Restrictions.fields().eq("isAlive"));
		final CriterionResult result = criterion.search();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertTrue(result.getFields().isEmpty());
	}

	@Test
	public void testRestrictionNe() throws Exception {
		criterion.add(Restrictions.fields().ne("atributoPrivadoInt"));
		final CriterionResult result = criterion.search();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 7);
	}

	@Test
	public void testRestrictionNeEncadead() throws Exception {
		criterion.add(Restrictions.fields().ne("atributoPrivadoInt"));
		criterion.add(Restrictions.fields().ne("isAlive"));
		criterion.add(Restrictions.fields().ne("Privative"));
		final CriterionResult result = criterion.search();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 5);
	}

	@Test
	public void testRestrictionLike() throws Exception {
		criterion.add(Restrictions.fields().like("atributo"));
		final CriterionResult result = criterion.search();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 4);
	}

	@Test
	public void testRestrictionLikeEncadeado() throws Exception {
		criterion.add(Restrictions.fields().like("atributo"));
		criterion.add(Restrictions.fields().ne("atributoPrivadoInt"));
		final CriterionResult result = criterion.search();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 3);
	}

	@Test
	public void testRestrictionLikeComLikeTypeStart() throws Exception {
		criterion.add(Restrictions.fields().like("atributo", LikeType.START));
		final CriterionResult result = criterion.search();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 4);
	}

	@Test
	public void testRestrictionLikeComLikeTypeEnd() throws Exception {
		criterion.add(Restrictions.fields().like("ive", LikeType.END));
		final CriterionResult result = criterion.search();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 2);
	}

	@Test
	public void testRestrictionLikeComLikeTypeAnywhere() throws Exception {
		criterion.add(Restrictions.fields().like("Priva", LikeType.ANYWHERE));
		final CriterionResult result = criterion.search();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 5);
	}

	@Test
	public void testRestrictionRegex() throws Exception {
		criterion.add(Restrictions.fields().regex("comeca[P|p]riva"));
		final CriterionResult result = criterion.search();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 2);
	}

	@Test
	public void testRestrictionIn() throws Exception {
		criterion.add(Restrictions.fields().in("atributoPrivadoInt", "comecaPriva"));
		final CriterionResult result = criterion.search();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 2);
	}

	@Test
	public void testRestrictionAnnotatedWith() throws Exception {
		criterion.add(Restrictions.fields().annotatedWith(MyAnnotation.class));
		final CriterionResult result = criterion.search();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 3);
	}

	@Test
	public void testRestrictionShowOnlyPublicTrue() throws Exception {
		criterion.add(Restrictions.fields().regex("comeca[P|p]riva"));
		criterion.add(Restrictions.fields().showOnlyPublic(true));
		final CriterionResult result = criterion.search();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 1);
	}

	@Test(expected = TooManyResultException.class)
	public void testRestrictionUniqueShouldThrowAnExceptionIfThereAreMoreThanOneResult() throws Exception {
		criterion.add(Restrictions.fields().annotatedWith(MyAnnotation.class));
		final CriterionResult result = criterion.search();

		result.unique();
	}

	@Test(expected = NoResultException.class)
	public void testRestrictionUniqueShouldThrowAnExceptionIfThereAreNoResults() throws Exception {
		criterion.add(Restrictions.fields().eq("xyzu"));
		final CriterionResult result = criterion.search();

		result.unique();
	}

	@Test
	public void testRestrictionUniqueShouldReturnOnlyOneMember() throws Exception {
		criterion.add(Restrictions.fields().regex("comeca[P|p]riva")).add(Restrictions.fields().showOnlyPublic(true));
		final CriterionResult result = criterion.search();

		final Field fieldExpected = ClasseDominio.class.getDeclaredField("comecaPriva");
		final Field fieldActual = result.unique();
		Assert.assertEquals(fieldExpected, fieldActual);
	}

	@Test
	public void testRestrictionShowOnlyPublicFalse() throws Exception {
		criterion.add(Restrictions.fields().regex("comeca[P|p]riva"));
		criterion.add(Restrictions.fields().showOnlyPublic(false));
		final CriterionResult result = criterion.search();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 2);
	}

	@Test
	public void testRestrictionEqComPropriedadeNaClassePai() throws Exception {
		criterion = introspectorClasseFilha.createCriterion();
		criterion.add(Restrictions.fields().regex("comeca[P|p]riva"));
		final CriterionResult result = criterion.search();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 2);
	}

	@Test
	public void testRestrictionAnnotatedWithNaClassePai() throws Exception {
		criterion = introspectorClasseFilha.createCriterion();
		criterion.add(Restrictions.fields().annotatedWith(MyAnnotation.class));
		final CriterionResult result = criterion.search();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 3);
	}

}
