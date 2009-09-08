package br.com.bit.ideias.reflection.test.criteria;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.com.bit.ideias.reflection.core.Introspector;
import br.com.bit.ideias.reflection.criteria.Criterion;
import br.com.bit.ideias.reflection.criteria.CriterionResult;
import br.com.bit.ideias.reflection.criteria.Restriction;
import br.com.bit.ideias.reflection.criteria.expression.ComplexExpression;
import br.com.bit.ideias.reflection.exceptions.NoResultException;
import br.com.bit.ideias.reflection.exceptions.TooManyResultException;
import br.com.bit.ideias.reflection.test.artefacts.ClasseDominio;
import br.com.bit.ideias.reflection.test.artefacts.ClasseDominioFilha;
import br.com.bit.ideias.reflection.test.artefacts.MyAnnotation;
import br.com.bit.ideias.reflection.type.LikeType;
import br.com.bit.ideias.reflection.type.ModifierType;
import br.com.bit.ideias.reflection.type.TargetType;

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
		criterion.add(Restriction.targetType(TargetType.FIELD));

		criterionClasseFilha = introspectorClasseFilha.createCriterion();
		criterionClasseFilha.add(Restriction.targetType(TargetType.METHOD));
	}

	@Test
	public void testRestrictionEqComTargetTypeNaoEspecificado() throws Exception {
		criterion = introspector.createCriterion();
		criterion.add(Restriction.like("tributoPrivadoString", LikeType.END));
		final CriterionResult result = criterion.list();

		Assert.assertEquals(result.getMethods().size(), 2);
		Assert.assertEquals(result.getFields().size(), 1);
	}

	@Test
	public void testRestrictionEqDirectCriterion() throws Exception {
		final Field field = ClasseDominio.class.getDeclaredField("atributoPrivadoInt");
		final Criterion localCriterion = Introspector.createCriterion(ClasseDominio.class).add(Restriction.eq("atributoPrivadoInt"));
		final CriterionResult result = localCriterion.list();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 1);
		Assert.assertEquals(result.getFields().get(0), field);
	}

	@Test
	public void testRestrictionEq() throws Exception {
		final Field field = ClasseDominio.class.getDeclaredField("atributoPrivadoInt");
		criterion.add(Restriction.eq("atributoPrivadoInt"));
		final CriterionResult result = criterion.list();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 1);
		Assert.assertEquals(result.getFields().get(0), field);
	}

	@Test
	public void shouldReturnEitherWhenUsingDisjunction() throws Exception {
		final Field field = ClasseDominio.class.getDeclaredField("atributoPrivadoInt");
		criterion.add(Restriction.eq("atributoPrivadoInt"));
		final ComplexExpression disjunction = Restriction.disjunction(Restriction.eq("yzxabc"));
		disjunction.add(Restriction.eq("atributoPrivadoInt"));
		criterion.add(disjunction);

		final CriterionResult result = criterion.list();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 1);
		Assert.assertEquals(result.getFields().get(0), field);
	}

	@Test
	public void testRestrictionEqEncadeado() throws Exception {
		criterion.add(Restriction.eq("atributoPrivadoInt"));
		criterion.add(Restriction.eq("isAlive"));
		final CriterionResult result = criterion.list();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertTrue(result.getFields().isEmpty());
	}

	@Test
	public void testRestrictionNe() throws Exception {
		criterion.add(Restriction.ne("atributoPrivadoInt"));
		final CriterionResult result = criterion.list();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 7);
	}

	@Test
	public void testRestrictionNeEncadead() throws Exception {
		criterion.add(Restriction.ne("atributoPrivadoInt"));
		criterion.add(Restriction.ne("isAlive"));
		criterion.add(Restriction.ne("Privative"));
		final CriterionResult result = criterion.list();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 5);
	}

	@Test
	public void testRestrictionLike() throws Exception {
		criterion.add(Restriction.like("atributo"));
		final CriterionResult result = criterion.list();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 4);
	}

	@Test
	public void testRestrictionLikeEncadeado() throws Exception {
		criterion.add(Restriction.like("atributo"));
		criterion.add(Restriction.ne("atributoPrivadoInt"));
		final CriterionResult result = criterion.list();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 3);
	}

	@Test
	public void testRestrictionLikeComLikeTypeStart() throws Exception {
		criterion.add(Restriction.like("atributo", LikeType.START));
		final CriterionResult result = criterion.list();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 4);
	}

	@Test
	public void testRestrictionLikeComLikeTypeEnd() throws Exception {
		criterion.add(Restriction.like("ive", LikeType.END));
		final CriterionResult result = criterion.list();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 2);
	}

	@Test
	public void testRestrictionLikeComLikeTypeAnywhere() throws Exception {
		criterion.add(Restriction.like("Priva", LikeType.ANYWHERE));
		final CriterionResult result = criterion.list();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 5);
	}

	@Test
	public void testRestrictionRegex() throws Exception {
		criterion.add(Restriction.regex("comeca[P|p]riva"));
		final CriterionResult result = criterion.list();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 2);
	}

	@Test
	public void testRestrictionIn() throws Exception {
		criterion.add(Restriction.in("atributoPrivadoInt", "comecaPriva"));
		final CriterionResult result = criterion.list();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 2);
	}

	@Test
	public void testRestrictionAnnotatedWith() throws Exception {
		criterion.add(Restriction.annotatedWith(MyAnnotation.class));
		final CriterionResult result = criterion.list();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 3);
	}

	@Test
	public void testRestrictionShowOnlyPublicTrue() throws Exception {
		criterion.add(Restriction.regex("comeca[P|p]riva"));
		criterion.add(Restriction.showOnlyPublic(true));
		final CriterionResult result = criterion.list();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 1);
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
		criterion.add(Restriction.regex("comeca[P|p]riva")).add(Restriction.showOnlyPublic(true));
		final Field fieldActual = criterion.uniqueResult();

		final Field fieldExpected = ClasseDominio.class.getDeclaredField("comecaPriva");
		Assert.assertEquals(fieldExpected, fieldActual);
	}

	@Test
	public void testRestrictionShowOnlyPublicFalse() throws Exception {
		criterion.add(Restriction.regex("comeca[P|p]riva"));
		criterion.add(Restriction.showOnlyPublic(false));
		final CriterionResult result = criterion.list();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 2);
	}

	@Test
	public void testRestrictionEqComPropriedadeNaClassePai() throws Exception {
		criterion = introspectorClasseFilha.createCriterion();
		criterion.add(Restriction.regex("comeca[P|p]riva"));
		final CriterionResult result = criterion.list();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 2);
	}

	@Test
	public void testRestrictionAnnotatedWithNaClassePai() throws Exception {
		criterion.add(Restriction.annotatedWith(MyAnnotation.class));
		final CriterionResult result = criterion.list();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 3);
	}
	
	@Test
	public void testTypeEq() {
		criterion.add(Restriction.typeEq(String.class));
		final CriterionResult result = criterion.list();
		
		Assert.assertEquals(2, result.getFields().size());
		Assert.assertTrue(result.getMethods().isEmpty());
	}
	
	@Test
	public void testTypeReturn() {
		criterion.add(Restriction.typeReturn(Integer.class));
		final CriterionResult result = criterion.list();
		
		Assert.assertTrue(result.getFields().isEmpty());
		Assert.assertTrue(result.getMethods().isEmpty());
	}
	
	@Test
	public void testTypeParams() {
		criterion.add(Restriction.typesParams(String.class));
		final CriterionResult result = criterion.list();
		
		Assert.assertTrue(result.getFields().isEmpty());
		Assert.assertTrue(result.getMethods().isEmpty());
	}
	
	@Test
	public void testTypeParamsComMaisDeUmParametro() {
		criterion.add(Restriction.typesParams(String.class, Integer.class, boolean.class));
		final CriterionResult result = criterion.list();
		
		Assert.assertTrue(result.getFields().isEmpty());
		Assert.assertTrue(result.getMethods().isEmpty());
	}
	
	@Test
	public void testCriteriaProcuraPorFieldsConstantes() {
		criterion.add(Restriction.typeModifiers(ModifierType.FINAL, ModifierType.STATIC));
		final CriterionResult result = criterion.list();
	}
	
	@Test
	public void testCriteriaProcuraPorFieldsEstaticos() {
		criterion.add(Restriction.typeModifiers(ModifierType.STATIC));
		final CriterionResult result = criterion.list();
	}
	
	@Test
	public void testCriteriaProcuraPorFieldsModificador() {
		criterion.add(Restriction.typeModifiers(ModifierType.SYNCHRONIZED));
		final CriterionResult result = criterion.list();
	}

}
