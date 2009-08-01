package br.com.bit.ideias.reflection.test.criteria;

import java.lang.reflect.Method;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import br.com.bit.ideias.reflection.core.Introspector;
import br.com.bit.ideias.reflection.criteria.Criterion;
import br.com.bit.ideias.reflection.criteria.CriterionResult;
import br.com.bit.ideias.reflection.criteria.Restrictions;
import br.com.bit.ideias.reflection.enums.LikeType;
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

	@BeforeMethod
	public void prepare() {
		criterion = introspector.createCriterion();
	}

	@Test
	public void testRestrictionEq() throws Exception {
		final Method method = ClasseDominio.class.getDeclaredMethod("getAtributoPrivadoInt");
		criterion.add(Restrictions.methods().eq("getAtributoPrivadoInt"));
		final CriterionResult result = criterion.search();

		Assert.assertTrue(result.getFields().isEmpty());
		Assert.assertEquals(result.getMethods().size(), 1);
		Assert.assertEquals(result.getMethods().get(0), method);
	}

	@Test(enabled = false)
	public void testRestrictionEqEncadeado() throws Exception {
		criterion.add(Restrictions.methods().eq("atributoPrivadoInt"));
		criterion.add(Restrictions.methods().eq("isAlive"));
		final CriterionResult result = criterion.search();

		Assert.assertTrue(result.getFields().isEmpty());
		Assert.assertTrue(result.getFields().isEmpty());
	}

	@Test(enabled = false)
	public void testRestrictionNe() throws Exception {
		criterion.add(Restrictions.methods().ne("atributoPrivadoInt"));
		final CriterionResult result = criterion.search();

		Assert.assertTrue(result.getFields().isEmpty());
		Assert.assertEquals(result.getFields().size(), 7);
	}

	@Test(enabled = false)
	public void testRestrictionNeEncadead() throws Exception {
		criterion.add(Restrictions.methods().ne("atributoPrivadoInt"));
		criterion.add(Restrictions.methods().ne("isAlive"));
		criterion.add(Restrictions.methods().ne("Privative"));
		final CriterionResult result = criterion.search();

		Assert.assertTrue(result.getFields().isEmpty());
		Assert.assertEquals(result.getFields().size(), 5);
	}

	@Test(enabled = false)
	public void testRestrictionLike() throws Exception {
		criterion.add(Restrictions.methods().like("atributo"));
		final CriterionResult result = criterion.search();

		Assert.assertTrue(result.getFields().isEmpty());
		Assert.assertEquals(result.getFields().size(), 4);
	}

	@Test(enabled = false)
	public void testRestrictionLikeEncadeado() throws Exception {
		criterion.add(Restrictions.methods().like("atributo"));
		criterion.add(Restrictions.methods().ne("atributoPrivadoInt"));
		final CriterionResult result = criterion.search();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 3);
	}

	@Test(enabled = false)
	public void testRestrictionLikeComLikeTypeStart() throws Exception {
		criterion.add(Restrictions.methods().like("atributo", LikeType.START));
		final CriterionResult result = criterion.search();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 4);
	}

	@Test(enabled = false)
	public void testRestrictionLikeComLikeTypeEnd() throws Exception {
		criterion.add(Restrictions.methods().like("ive", LikeType.END));
		final CriterionResult result = criterion.search();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 2);
	}

	@Test(enabled = false)
	public void testRestrictionLikeComLikeTypeAnywhere() throws Exception {
		criterion.add(Restrictions.methods().like("Priva", LikeType.ANYWHERE));
		final CriterionResult result = criterion.search();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 5);
	}

	@Test(enabled = false)
	public void testRestrictionRegex() throws Exception {
		criterion.add(Restrictions.methods().regex("comeca[P|p]riva"));
		final CriterionResult result = criterion.search();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 2);
	}

	@Test(enabled = false)
	public void testRestrictionIn() throws Exception {
		criterion.add(Restrictions.methods().in("atributoPrivadoInt", "comecaPriva"));
		final CriterionResult result = criterion.search();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 2);
	}

	@Test(enabled = false)
	public void testRestrictionAnnotatedWith() throws Exception {
		criterion.add(Restrictions.methods().annotatedWith(MyAnnotation.class));
		final CriterionResult result = criterion.search();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 3);
	}

	@Test(enabled = false)
	public void testRestrictionShowOnlyPublicTrue() throws Exception {
		criterion.add(Restrictions.methods().regex("comeca[P|p]riva"));
		criterion.add(Restrictions.methods().showOnlyPublic(true));
		final CriterionResult result = criterion.search();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 1);
	}

	@Test(enabled = false)
	public void testRestrictionShowOnlyPublicFalse() throws Exception {
		criterion.add(Restrictions.methods().regex("comeca[P|p]riva"));
		criterion.add(Restrictions.methods().showOnlyPublic(false));
		final CriterionResult result = criterion.search();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 2);
	}

	@Test(enabled = false)
	public void testRestrictionEqComPropriedadeNaClassePai() throws Exception {
		criterion = introspectorClasseFilha.createCriterion();
		criterion.add(Restrictions.methods().regex("comeca[P|p]riva"));
		final CriterionResult result = criterion.search();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 2);
	}

	@Test(enabled = false)
	public void testRestrictionAnnotatedWithNaClassePai() throws Exception {
		criterion = introspectorClasseFilha.createCriterion();
		criterion.add(Restrictions.methods().annotatedWith(MyAnnotation.class));
		final CriterionResult result = criterion.search();

		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 3);
	}

}
