package br.com.bit.ideias.reflection.test.criteria;

import java.lang.reflect.Field;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import br.com.bit.ideias.reflection.core.Introspector;
import br.com.bit.ideias.reflection.criteria.Criterion;
import br.com.bit.ideias.reflection.criteria.CriterionResult;
import br.com.bit.ideias.reflection.criteria.Restrictions;
import br.com.bit.ideias.reflection.criteria.expression.ComplexExpression;
import br.com.bit.ideias.reflection.enums.LikeType;
import br.com.bit.ideias.reflection.test.artefacts.ClasseDominio;
import br.com.bit.ideias.reflection.test.artefacts.ClasseDominioFilha;
import br.com.bit.ideias.reflection.test.artefacts.MyAnnotation;

/**
 * 
 * @author Nadilson
 * @since 28/07/2009
 */
public class RestrictionsTest {

	private final Introspector introspector = Introspector.forClass(ClasseDominio.class);

	private final Introspector introspectorClasseFilha = Introspector.forClass(ClasseDominioFilha.class);

	private Criterion criterion;

	@BeforeMethod
	public void prepare() {
		criterion = introspector.createCriterion();
	}
	
	@Test
    public void testRestrictionEq() throws Exception {
        final Field field = ClasseDominio.class.getDeclaredField("atributoPrivadoInt");
        criterion.add(Restrictions.properties().eq("atributoPrivadoInt"));
        final CriterionResult result = criterion.search();

        Assert.assertTrue(result.getMethods().isEmpty());
        Assert.assertEquals(result.getFields().size(), 1);
        Assert.assertEquals(result.getFields().get(0), field);
    }

	@Test
	public void shouldReturnEitherWhenUsingDisjunction() throws Exception {
		final Field field = ClasseDominio.class.getDeclaredField("atributoPrivadoInt");
		ComplexExpression disjunction = Restrictions.properties().disjunction(Restrictions.properties().eq("yzxabc"));
		disjunction.add(Restrictions.properties().eq("atributoPrivadoInt"));
		criterion.add(disjunction);
		
		final CriterionResult result = criterion.search();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 1);
		Assert.assertEquals(result.getFields().get(0), field);
	}

	@Test
	public void testRestrictionEqEncadeado() throws Exception {
		criterion.add(Restrictions.properties().eq("atributoPrivadoInt"));
		criterion.add(Restrictions.properties().eq("isAlive"));
		final CriterionResult result = criterion.search();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertTrue(result.getFields().isEmpty());
	}

	@Test
	public void testRestrictionNe() throws Exception {
		criterion.add(Restrictions.properties().ne("atributoPrivadoInt"));
		final CriterionResult result = criterion.search();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 7);
	}

	@Test
	public void testRestrictionNeEncadead() throws Exception {
		criterion.add(Restrictions.properties().ne("atributoPrivadoInt"));
		criterion.add(Restrictions.properties().ne("isAlive"));
		criterion.add(Restrictions.properties().ne("Privative"));
		final CriterionResult result = criterion.search();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 5);
	}

	@Test
	public void testRestrictionLike() throws Exception {
		criterion.add(Restrictions.properties().like("atributo"));
		final CriterionResult result = criterion.search();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 4);
	}

	@Test
	public void testRestrictionLikeEncadeado() throws Exception {
		criterion.add(Restrictions.properties().like("atributo"));
		criterion.add(Restrictions.properties().ne("atributoPrivadoInt"));
		final CriterionResult result = criterion.search();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 3);
	}

	@Test
	public void testRestrictionLikeComLikeTypeStart() throws Exception {
		criterion.add(Restrictions.properties().like("atributo", LikeType.START));
		final CriterionResult result = criterion.search();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 4);
	}

	@Test
	public void testRestrictionLikeComLikeTypeEnd() throws Exception {
		criterion.add(Restrictions.properties().like("ive", LikeType.END));
		final CriterionResult result = criterion.search();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 2);
	}

	@Test
	public void testRestrictionLikeComLikeTypeAnywhere() throws Exception {
		criterion.add(Restrictions.properties().like("Priva", LikeType.ANYWHERE));
		final CriterionResult result = criterion.search();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 5);
	}

	@Test
	public void testRestrictionRegex() throws Exception {
		criterion.add(Restrictions.properties().regex("comeca[P|p]riva"));
		final CriterionResult result = criterion.search();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 2);
	}

	@Test
	public void testRestrictionIn() throws Exception {
		criterion.add(Restrictions.properties().in("atributoPrivadoInt", "comecaPriva"));
		final CriterionResult result = criterion.search();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 2);
	}

	@Test
	public void testRestrictionAnnotatedWith() throws Exception {
		criterion.add(Restrictions.properties().annotatedWith(MyAnnotation.class));
		final CriterionResult result = criterion.search();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 3);
	}

	@Test
	public void testRestrictionShowOnlyPublicTrue() throws Exception {
		criterion.add(Restrictions.properties().regex("comeca[P|p]riva"));
		criterion.add(Restrictions.properties().showOnlyPublic(true));
		final CriterionResult result = criterion.search();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 1);
	}

	@Test
	public void testRestrictionShowOnlyPublicFalse() throws Exception {
		criterion.add(Restrictions.properties().regex("comeca[P|p]riva"));
		criterion.add(Restrictions.properties().showOnlyPublic(false));
		final CriterionResult result = criterion.search();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 2);
	}

	@Test
	public void testRestrictionEqComPropriedadeNaClassePai() throws Exception {
		criterion = introspectorClasseFilha.createCriterion();
		criterion.add(Restrictions.properties().regex("comeca[P|p]riva"));
		final CriterionResult result = criterion.search();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 2);
	}
	
	@Test
	public void testRestrictionAnnotatedWithNaClassePai() throws Exception {
		criterion = introspectorClasseFilha.createCriterion();
		criterion.add(Restrictions.properties().annotatedWith(MyAnnotation.class));
		final CriterionResult result = criterion.search();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 3);
	}

}
