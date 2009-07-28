package br.com.bit.ideias.reflection.test.criteria;

import java.lang.reflect.Field;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import br.com.bit.ideias.reflection.core.Introspector;
import br.com.bit.ideias.reflection.criteria.Criterion;
import br.com.bit.ideias.reflection.criteria.Restrictions;
import br.com.bit.ideias.reflection.criteria.CriterionResult;
import br.com.bit.ideias.reflection.criteria.target.MethodsTarget;
import br.com.bit.ideias.reflection.criteria.target.PropertiesTarget;
import br.com.bit.ideias.reflection.enums.LikeType;
import br.com.bit.ideias.reflection.test.artefacts.ClasseDominio;

/**
 * 
 * @author Nadilson
 * @since 28/07/2009
 */
public class RestristionsTest {
	
	private final Introspector introspector = Introspector.forClass(ClasseDominio.class);
	
	private Criterion criterion;
	
	@BeforeMethod
	public void prepare() {
		criterion = introspector.makeCriterion();	
	}

	@Test
	public void testRestrictionEq() throws Exception {
		Field field = ClasseDominio.class.getDeclaredField("atributoPrivadoInt");
		
		criterion.add(Restrictions.properties().eq("atributoPrivadoInt"));
		CriterionResult result = criterion.search();
		
		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 1);
		Assert.assertEquals(result.getFields().get(0), field);
	}
	
	@Test
	public void testRestrictionEqEncadeado() throws Exception {
		criterion.add(Restrictions.properties().eq("atributoPrivadoInt"));
		criterion.add(Restrictions.properties().eq("isAlive"));
		CriterionResult result = criterion.search();
		
		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertTrue(result.getFields().isEmpty());
	}
	
	@Test
	public void testRestrictionNe() throws Exception {
		criterion.add(Restrictions.properties().ne("atributoPrivadoInt"));
		CriterionResult result = criterion.search();
		
		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 4);
	}
	
	@Test
	public void testRestrictionNeEncadead() throws Exception {
		criterion.add(Restrictions.properties().ne("atributoPrivadoInt"));
		criterion.add(Restrictions.properties().ne("isAlive"));
		CriterionResult result = criterion.search();
		
		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 3);
	}
	
	@Test
	public void testRestrictionLike() throws Exception {
		criterion.add(Restrictions.properties().like("atributo"));
		CriterionResult result = criterion.search();
		
		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 4);
	}
	
	@Test
	public void testRestrictionLikeEncadeado() throws Exception {
		criterion.add(Restrictions.properties().like("atributo"));
		criterion.add(Restrictions.properties().ne("atributoPrivadoInt"));
		CriterionResult result = criterion.search();
		
		Assert.assertTrue(result.getMethods().isEmpty());
		Assert.assertEquals(result.getFields().size(), 3);
	}

	/////////////////////////////////////////////////////////////////////////////////////////
	
	public static void main(String[] args) {
		Introspector i = Introspector.forClass(ClasseDominio.class);
		Criterion criterion = i.makeCriterion();

		PropertiesTarget properties = Restrictions.properties();
		criterion.add( properties.eq(""));
		criterion.add( properties.ne(""));
		criterion.add( properties.like(""));
		criterion.add( properties.like("", LikeType.ANYWHERE));
		criterion.add( properties.regex(""));
		criterion.add( properties.in("", ""));
		criterion.add( properties.annotatedWith(SuppressWarnings.class));
		criterion.add( properties.showOnlyPublic(true));
		
		MethodsTarget methods = Restrictions.methods();
		criterion.add( methods.eq(""));
		criterion.add( methods.ne(""));
		criterion.add( methods.like(""));
		criterion.add( methods.like("", LikeType.ANYWHERE));
		criterion.add( methods.regex(""));
		criterion.add( methods.in("", ""));
		criterion.add( methods.annotatedWith(SuppressWarnings.class));
		criterion.add( methods.showOnlyPublic(true));

		CriterionResult lista = criterion.search();
		System.out.println(lista);
	}

}
