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

	@Test
	public void testRestrictionEqEncadeado() throws Exception {
		criterion.add(Restrictions.methods().eq("getAtributoPrivadoInt"));
		criterion.add(Restrictions.methods().eq("isAlive"));
		final CriterionResult result = criterion.search();

		Assert.assertTrue(result.getFields().isEmpty());
		Assert.assertTrue(result.getFields().isEmpty());
	}

	@Test
	public void testRestrictionNe() throws Exception {
		criterion.add(Restrictions.methods().ne("getAtributoPrivadoInt"));
		criterion.add(Restrictions.methods().like("get"));
		final CriterionResult result = criterion.search();

		Assert.assertTrue(result.getFields().isEmpty());
		Assert.assertEquals(result.getMethods().size(), 5);
	}

	@Test
	public void testRestrictionNeEncadead() throws Exception {
		criterion.add(Restrictions.methods().ne("getAtributoPrivadoInt"));
		criterion.add(Restrictions.methods().ne("getClass"));
		criterion.add(Restrictions.methods().like("get"));
		final CriterionResult result = criterion.search();

		Assert.assertTrue(result.getFields().isEmpty());
		Assert.assertEquals(result.getMethods().size(), 4);
	}

	@Test
	public void testRestrictionLike() throws Exception {
		criterion.add(Restrictions.methods().like("getA"));
		final CriterionResult result = criterion.search();

		Assert.assertTrue(result.getFields().isEmpty());
		Assert.assertEquals(result.getMethods().size(), 3);
	}

	@Test
	public void testRestrictionLikeEncadeado() throws Exception {
		criterion.add(Restrictions.methods().like("getA"));
		criterion.add(Restrictions.methods().ne("getAtributoPrivadoInteiro"));
		final CriterionResult result = criterion.search();

		Assert.assertTrue(result.getFields().isEmpty());
		Assert.assertEquals(result.getMethods().size(), 2);
	}

	@Test
	public void testRestrictionLikeComLikeTypeStart() throws Exception {
		criterion.add(Restrictions.methods().like("is", LikeType.START));
		final CriterionResult result = criterion.search();

		Assert.assertTrue(result.getFields().isEmpty());
		Assert.assertEquals(result.getMethods().size(), 1);
	}

	@Test
	public void testRestrictionLikeComLikeTypeEnd() throws Exception {
		criterion.add(Restrictions.methods().like("Inteiro", LikeType.END));
		final CriterionResult result = criterion.search();

		Assert.assertTrue(result.getFields().isEmpty());
		Assert.assertEquals(result.getMethods().size(), 3);
	}

	@Test
	public void testRestrictionLikeComLikeTypeAnywhere() throws Exception {
		criterion.add(Restrictions.methods().like("Priva", LikeType.ANYWHERE));
		final CriterionResult result = criterion.search();

		Assert.assertTrue(result.getFields().isEmpty());
		Assert.assertEquals(result.getMethods().size(), 8);
	}

	@Test
	public void testRestrictionRegex() throws Exception {
		criterion.add(Restrictions.methods().regex("getAtributoPrivado[I|S]"));
		final CriterionResult result = criterion.search();

		Assert.assertTrue(result.getFields().isEmpty());
		Assert.assertEquals(result.getMethods().size(), 3);
	}

	@Test
	public void testRestrictionIn() throws Exception {
		criterion.add(Restrictions.methods().in("getAtributoPrivadoInt", "setAlive"));
		final CriterionResult result = criterion.search();

		Assert.assertTrue(result.getFields().isEmpty());
		Assert.assertEquals(result.getMethods().size(), 2);
	}

	@Test
	public void testRestrictionAnnotatedWith() throws Exception {
		criterion.add(Restrictions.methods().annotatedWith(MyAnnotation.class));
		final CriterionResult result = criterion.search();

		Assert.assertTrue(result.getFields().isEmpty());
		Assert.assertEquals(result.getMethods().size(), 2);
	}

	@Test
	public void testRestrictionShowOnlyPublicTrue() throws Exception {
		criterion.add(Restrictions.methods().like("metodo"));
		criterion.add(Restrictions.methods().showOnlyPublic(true));
		final CriterionResult result = criterion.search();

		Assert.assertTrue(result.getFields().isEmpty());
		Assert.assertEquals(result.getMethods().size(), 1);
	}

	@Test
	public void testRestrictionShowOnlyPublicFalse() throws Exception {
		criterion.add(Restrictions.methods().like("metodo"));
		criterion.add(Restrictions.methods().showOnlyPublic(false));
		final CriterionResult result = criterion.search();

		Assert.assertTrue(result.getFields().isEmpty());
		Assert.assertEquals(result.getMethods().size(), 2);
	}

	@Test
	public void testRestrictionEqComPropriedadeNaClassePai() throws Exception {
		criterion = introspectorClasseFilha.createCriterion();
		final Method method = ClasseDominio.class.getDeclaredMethod("getAtributoPrivadoInt");
		criterion.add(Restrictions.methods().eq("getAtributoPrivadoInt"));
		final CriterionResult result = criterion.search();

		Assert.assertTrue(result.getFields().isEmpty());
		Assert.assertEquals(result.getMethods().size(), 1);
		Assert.assertEquals(method, result.getMethods().get(0));
	}

	@Test
	public void testRestrictionAnnotatedWithNaClassePai() throws Exception {
		criterion = introspectorClasseFilha.createCriterion();
		criterion.add(Restrictions.methods().annotatedWith(MyAnnotation.class));
		final CriterionResult result = criterion.search();

		Assert.assertTrue(result.getFields().isEmpty());
		Assert.assertEquals(result.getMethods().size(), 2);
	}

}
