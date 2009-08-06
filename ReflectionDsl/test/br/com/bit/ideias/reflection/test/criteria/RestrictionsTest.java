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

	@Before
	public void prepare() {
		criterion = introspector.createCriterion();
	}

	@Test
	public void testRestrictionEq() throws Exception {
		final Field field = ClasseDominio.class.getDeclaredField("atributoPrivadoInt");
		criterion.add(Restrictions.eq("atributoPrivadoInt")).add(Restrictions.type(TargetType.FIELD));
		final CriterionResult result = criterion.list();

		Assert.assertEquals(result.getMembers().size(), 1);
		Assert.assertEquals(result.getMembers().get(0), field);
	}

	@Test
	public void shouldReturnEitherWhenUsingDisjunction() throws Exception {
		final Field field = ClasseDominio.class.getDeclaredField("atributoPrivadoInt");
		criterion.add(Restrictions.eq("atributoPrivadoInt")).add(Restrictions.type(TargetType.FIELD));
		final ComplexExpression disjunction = Restrictions.disjunction(Restrictions.eq("yzxabc"));
		disjunction.add(Restrictions.eq("atributoPrivadoInt"));
		criterion.add(disjunction).add(Restrictions.type(TargetType.FIELD));

		final CriterionResult result = criterion.list();

		Assert.assertEquals(result.getMembers().size(), 1);
		Assert.assertEquals(result.getMembers().get(0), field);
	}

	@Test
	public void testRestrictionEqEncadeado() throws Exception {
		criterion.add(Restrictions.eq("atributoPrivadoInt"));
		criterion.add(Restrictions.eq("isAlive")).add(Restrictions.type(TargetType.FIELD));
		final CriterionResult result = criterion.list();

		Assert.assertTrue(result.getMembers().isEmpty());
	}

	@Test
	public void testRestrictionNe() throws Exception {
		criterion.add(Restrictions.ne("atributoPrivadoInt")).add(Restrictions.type(TargetType.FIELD));
		final CriterionResult result = criterion.list();

		Assert.assertEquals(result.getMembers().size(), 7);
	}

	@Test
	public void testRestrictionNeEncadead() throws Exception {
		criterion.add(Restrictions.ne("atributoPrivadoInt"));
		criterion.add(Restrictions.ne("isAlive"));
		criterion.add(Restrictions.ne("Privative")).add(Restrictions.type(TargetType.FIELD));
		final CriterionResult result = criterion.list();

		Assert.assertEquals(result.getMembers().size(), 5);
	}

	@Test
	public void testRestrictionLike() throws Exception {
		criterion.add(Restrictions.like("atributo")).add(Restrictions.type(TargetType.FIELD));
		final CriterionResult result = criterion.list();

		Assert.assertEquals(result.getMembers().size(), 4);
	}

	@Test
	public void testRestrictionLikeEncadeado() throws Exception {
		criterion.add(Restrictions.like("atributo"));
		criterion.add(Restrictions.ne("atributoPrivadoInt")).add(Restrictions.type(TargetType.FIELD));
		final CriterionResult result = criterion.list();

		Assert.assertEquals(result.getMembers().size(), 3);
	}

	@Test
	public void testRestrictionLikeComLikeTypeStart() throws Exception {
		criterion.add(Restrictions.like("atributo", LikeType.START)).add(Restrictions.type(TargetType.FIELD));
		final CriterionResult result = criterion.list();

		Assert.assertEquals(result.getMembers().size(), 4);
	}

	@Test
	public void testRestrictionLikeComLikeTypeEnd() throws Exception {
		criterion.add(Restrictions.like("ive", LikeType.END)).add(Restrictions.type(TargetType.FIELD));
		final CriterionResult result = criterion.list();

		Assert.assertEquals(result.getMembers().size(), 2);
	}

	@Test
	public void testRestrictionLikeComLikeTypeAnywhere() throws Exception {
		criterion.add(Restrictions.like("Priva", LikeType.ANYWHERE)).add(Restrictions.type(TargetType.FIELD));
		final CriterionResult result = criterion.list();

		Assert.assertEquals(result.getMembers().size(), 5);
	}

	@Test
	public void testRestrictionRegex() throws Exception {
		criterion.add(Restrictions.regex("comeca[P|p]riva")).add(Restrictions.type(TargetType.FIELD));
		final CriterionResult result = criterion.list();

		Assert.assertEquals(result.getMembers().size(), 2);
	}

	@Test
	public void testRestrictionIn() throws Exception {
		criterion.add(Restrictions.in("atributoPrivadoInt", "comecaPriva")).add(Restrictions.type(TargetType.FIELD));
		final CriterionResult result = criterion.list();

		Assert.assertEquals(result.getMembers().size(), 2);
	}

	@Test
	public void testRestrictionAnnotatedWith() throws Exception {
		criterion.add(Restrictions.annotatedWith(MyAnnotation.class)).add(Restrictions.type(TargetType.FIELD));
		final CriterionResult result = criterion.list();

		Assert.assertEquals(result.getMembers().size(), 3);
	}

	@Test
	public void testRestrictionShowOnlyPublicTrue() throws Exception {
		criterion.add(Restrictions.regex("comeca[P|p]riva"));
		criterion.add(Restrictions.showOnlyPublic(true)).add(Restrictions.type(TargetType.FIELD));
		final CriterionResult result = criterion.list();

		Assert.assertEquals(result.getMembers().size(), 1);
	}

	@Test(expected = TooManyResultException.class)
	public void testRestrictionUniqueShouldThrowAnExceptionIfThereAreMoreThanOneResult() throws Exception {
		criterion.add(Restrictions.annotatedWith(MyAnnotation.class)).add(Restrictions.type(TargetType.FIELD));
		criterion.uniqueResult();
	}

	@Test(expected = NoResultException.class)
	public void testRestrictionUniqueShouldThrowAnExceptionIfThereAreNoResults() throws Exception {
		criterion.add(Restrictions.eq("xyzu")).add(Restrictions.type(TargetType.FIELD));
		criterion.uniqueResult();
	}

	@Test
	public void testRestrictionUniqueShouldReturnOnlyOneMember() throws Exception {
		criterion.add(Restrictions.regex("comeca[P|p]riva")).add(Restrictions.showOnlyPublic(true)).add(Restrictions.type(TargetType.FIELD));
		final Field fieldActual = criterion.uniqueResult();

		final Field fieldExpected = ClasseDominio.class.getDeclaredField("comecaPriva");
		Assert.assertEquals(fieldExpected, fieldActual);
	}

	@Test
	public void testRestrictionShowOnlyPublicFalse() throws Exception {
		criterion.add(Restrictions.regex("comeca[P|p]riva"));
		criterion.add(Restrictions.showOnlyPublic(false)).add(Restrictions.type(TargetType.FIELD));
		final CriterionResult result = criterion.list();

		Assert.assertEquals(result.getMembers().size(), 2);
	}

	@Test
	public void testRestrictionEqComPropriedadeNaClassePai() throws Exception {
		criterion = introspectorClasseFilha.createCriterion();
		criterion.add(Restrictions.regex("comeca[P|p]riva")).add(Restrictions.type(TargetType.FIELD));
		final CriterionResult result = criterion.list();

		Assert.assertEquals(result.getMembers().size(), 2);
	}

	@Test
	public void testRestrictionAnnotatedWithNaClassePai() throws Exception {
		criterion = introspectorClasseFilha.createCriterion();
		criterion.add(Restrictions.annotatedWith(MyAnnotation.class)).add(Restrictions.type(TargetType.FIELD));
		final CriterionResult result = criterion.list();

		Assert.assertEquals(result.getMembers().size(), 3);
	}

}
