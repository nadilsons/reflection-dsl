package br.com.bit.ideias.reflection.rql;

import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import br.com.bit.ideias.reflection.criteria.Criterion;
import br.com.bit.ideias.reflection.exceptions.NoResultException;
import br.com.bit.ideias.reflection.exceptions.SyntaxException;
import br.com.bit.ideias.reflection.exceptions.TooManyResultException;
import br.com.bit.ideias.reflection.rql.Rql;
import br.com.bit.ideias.reflection.test.artefacts.ClasseDominio;

/**
 * 
 * @author Leonarod Augusto de Souza Campos
 * @since 21/11/2009 - Dia da consciência negra!
 */
public class RqlTest {
	private final String QUERY_DOMINIO = "FROM br.com.bit.ideias.reflection.test.artefacts.ClasseDominio WHERE target eq 'field' and ";
	private final String QUERY_FILHA = "FROM br.com.bit.ideias.reflection.test.artefacts.ClasseDominioFilha WHERE ";

	private Criterion criterion;

	@Test
	public void testInvalidNumberOfParenthesisShouldThrowSyntaxException() throws Exception {
		String message = "Invalid number of parenthesis should have thrown SyntaxException";
		assertThrowsSyntaxException(message, "(");
		assertThrowsSyntaxException(message, ")");
		assertThrowsSyntaxException(message, "(()");
		assertThrowsSyntaxException(message, "(())()(");
	}

	@Test
	public void testInvalidNumberOfConjunctionsShouldThrowSyntaxException() throws Exception {
		String message = "Invalid number of AND/OR should have thrown exception";
		assertThrowsSyntaxException(message, "name eq '' name eq ''");
		assertThrowsSyntaxException(message, "name eq '' and name eq '' or");
	}

	@Test
	public void testUnknownClausesShouldThrowSyntaxException() throws Exception {
		String message = "Invalid number of AND/OR should have thrown exception";
		assertThrowsSyntaxException(message, "yyy eq ''");
		assertThrowsSyntaxException(message, "name eq 'abc' and yyy eq ''");
	}

	@Test
	public void testUnknownOperatorsShouldThrowSyntaxException() throws Exception {
		String message = "Invalid number of AND/OR should have thrown exception";
		assertThrowsSyntaxException(message, "yyy eq ''");
		assertThrowsSyntaxException(message, "name ab 'abc' and method ii ''");
	}

	@Test
	public void testRestrictionEqComTargetTypeNaoEspecificado() throws Exception {
		criterion = Rql.getInstance().parse("FROM br.com.bit.ideias.reflection.test.artefacts.ClasseDominio WHERE name like '%tributoPrivadoString'");
		final List<Member> result = criterion.list();

		Assert.assertEquals(3, result.size());
	}

	@Test
	public void testRestrictionEqComTargetTypeNaoEspecificadoUsingForClass() throws Exception {
		criterion = Rql.forClass(ClasseDominio.class).parse("name like '%tributoPrivadoString'");
		final List<Member> result = criterion.list();

		Assert.assertEquals(3, result.size());
	}

	@Test
	public void testRestrictionEqDirectCriterion() throws Exception {
		final Field field = ClasseDominio.class.getDeclaredField("atributoPrivadoInt");
		final Criterion localCriterion = criterion = Rql.getInstance().parse(QUERY_DOMINIO + "name eq 'atributoPrivadoInt'");
		final List<Field> result = localCriterion.list();

		Assert.assertEquals(1, result.size());
		Assert.assertEquals(field, result.get(0));
	}

	@Test
	public void testRestrictionEq() throws Exception {
		final Field field = ClasseDominio.class.getDeclaredField("atributoPrivadoInt");
		criterion = Rql.getInstance().parse(QUERY_DOMINIO + "name = 'atributoPrivadoInt'");
		final List<Member> result = criterion.list();

		Assert.assertEquals(1, result.size());
		Assert.assertEquals(field, result.get(0));
	}

	@Test
	public void shouldReturnEitherWhenUsingDisjunction() throws Exception {
		final Field field = ClasseDominio.class.getDeclaredField("atributoPrivadoInt");

		criterion = Rql.getInstance().parse(QUERY_DOMINIO + "name eq 'atributoPrivadoInt' and (name eq 'yzxabc' or name eq 'atributoPrivadoInt')");
		final List<Member> result = criterion.list();

		Assert.assertEquals(1, result.size());
		Assert.assertEquals(field, result.get(0));
	}

	@Test
	public void testRestrictionEqEncadeado() throws Exception {
		criterion = Rql.getInstance().parse(QUERY_DOMINIO + "name eq 'atributoPrivadoInt' and name eq 'isAlive'");
		final List<Member> result = criterion.list();

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void testRestrictionNe() throws Exception {
		criterion = Rql.getInstance().parse(QUERY_DOMINIO + "name != 'atributoPrivadoInt' and name ne 'constante'");
		final List<Member> result = criterion.list();

		Assert.assertEquals(7, result.size());
	}

	@Test
	public void testRestrictionNeEncadead() throws Exception {
		criterion = Rql.getInstance().parse(QUERY_DOMINIO + "name ne 'atributoPrivadoInt' and name ne 'isAlive' and name ne 'Privative' and name ne 'constante'");
		final List<Member> result = criterion.list();

		Assert.assertEquals(5, result.size());
	}

	@Test
	public void testRestrictionLike() throws Exception {
		criterion = Rql.getInstance().parse(QUERY_DOMINIO + "name like '%atributo%'");
		final List<Member> result = criterion.list();

		Assert.assertEquals(4, result.size());
	}

	@Test
	public void testRestrictionLikeEncadeado() throws Exception {
		criterion = Rql.getInstance().parse(QUERY_DOMINIO + "name like '%atributo%' and name ne 'atributoPrivadoInt'");
		final List<Member> result = criterion.list();

		Assert.assertEquals(3, result.size());
	}

	@Test
	public void testRestrictionLikeComLikeTypeStart() throws Exception {
		criterion = Rql.getInstance().parse(QUERY_DOMINIO + "name like 'atributo%'");
		final List<Member> result = criterion.list();

		Assert.assertEquals(4, result.size());
	}

	@Test
	public void testRestrictionLikeComLikeTypeEnd() throws Exception {
		criterion = Rql.getInstance().parse(QUERY_DOMINIO + "name like '%ive'");
		final List<Member> result = criterion.list();

		Assert.assertEquals(2, result.size());
	}

	@Test
	public void testRestrictionLikeComLikeTypeAnywhere() throws Exception {
		criterion = Rql.getInstance().parse(QUERY_DOMINIO + "name like '%Priva%'");
		final List<Member> result = criterion.list();

		Assert.assertEquals(5, result.size());
	}

	@Test
	public void testRestrictionRegex() throws Exception {
		criterion = Rql.getInstance().parse(QUERY_DOMINIO + "name like '/comeca[P|p]riva/'");
		final List<Member> result = criterion.list();

		Assert.assertEquals(2, result.size());
	}

	@Test
	public void testRestrictionIn() throws Exception {
		criterion = Rql.getInstance().parse(QUERY_DOMINIO + "name in ('atributoPrivadoInt','comecaPriva')");
		final List<Member> result = criterion.list();

		Assert.assertEquals(2, result.size());
	}

	@Test
	public void testRestrictionAnnotatedWith() throws Exception {
		criterion = Rql.getInstance().parse(QUERY_DOMINIO + "annotation eq 'br.com.bit.ideias.reflection.test.artefacts.MyAnnotation'");
		final List<Member> result = criterion.list();

		Assert.assertEquals(3, result.size());
	}

	@Test
	public void testRestrictionAnnotatedWithComDoisAdd() throws Exception {
		criterion = Rql.getInstance().parse(QUERY_DOMINIO + "annotation eq 'br.com.bit.ideias.reflection.test.artefacts.MyAnnotation' and annotation eq 'br.com.bit.ideias.reflection.test.artefacts.MyAnnotation2'");
		final List<Member> result = criterion.list();

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testRestrictionShowOnlyPublicTrue() throws Exception {
		criterion = Rql.getInstance().parse(QUERY_DOMINIO + "name like '/comeca[P|p]riva/' and modifier eq 'public'");
		final List<Member> result = criterion.list();

		Assert.assertEquals(1, result.size());
	}

	@Test(expected = TooManyResultException.class)
	public void testRestrictionUniqueShouldThrowAnExceptionIfThereAreMoreThanOneResult() throws Exception {
		criterion = Rql.getInstance().parse(QUERY_DOMINIO + "annotation eq 'br.com.bit.ideias.reflection.test.artefacts.MyAnnotation'");
		criterion.uniqueResult();
	}

	@Test(expected = NoResultException.class)
	public void testRestrictionUniqueShouldThrowAnExceptionIfThereAreNoResults() throws Exception {
		criterion = Rql.getInstance().parse(QUERY_DOMINIO + "name eq 'xyzu'");
		criterion.uniqueResult();
	}

	@Test
	public void testRestrictionUniqueShouldReturnOnlyOneMember() throws Exception {
		criterion = Rql.getInstance().parse(QUERY_DOMINIO + "name like '/comeca[P|p]riva/' and modifier eq 'public'");
		final Field fieldActual = criterion.uniqueResult();

		final Field fieldExpected = ClasseDominio.class.getDeclaredField("comecaPriva");
		Assert.assertEquals(fieldExpected, fieldActual);
	}

	@Test
	public void testRestrictionShowOnlyPublicFalse() throws Exception {
		criterion = Rql.getInstance().parse(QUERY_DOMINIO + "name like '/comeca[P|p]riva/' and modifier eq 'PRIVATE' or modifier eq 'PROTECTED'");
		final List<Member> result = criterion.list();

		Assert.assertEquals(1, result.size());
	}

	@Test
	public void testRestrictionShowOnlyPublicTrueComAtributosNaClassePai() throws Exception {
		criterion = Rql.getInstance().parse(QUERY_FILHA + "name like '%atributo%' and modifier eq 'PRIVATE' or modifier eq 'PROTECTED'");
		final List<Member> result = criterion.list();

		Assert.assertEquals(5, result.size());
	}

	@Test
	public void testRestrictionEqComPropriedadeNaClassePai() throws Exception {
		criterion = Rql.getInstance().parse(QUERY_FILHA + "name like '/comeca[P|p]riva/'");
		final List<Member> result = criterion.list();

		Assert.assertEquals(2, result.size());
	}

	@Test
	public void testRestrictionAnnotatedWithNaClassePai() throws Exception {
		criterion = Rql.getInstance().parse(QUERY_DOMINIO + "annotation eq 'br.com.bit.ideias.reflection.test.artefacts.MyAnnotation'");
		final List<Member> result = criterion.list();

		Assert.assertEquals(3, result.size());
	}

	@Test
	public void testTypeEq() {
		criterion = Rql.getInstance().parse(QUERY_DOMINIO + "fieldclass eq 'java.lang.String'");
		final List<Member> result = criterion.list();

		Assert.assertEquals(2, result.size());
	}

	@Test
	public void testMethodReturnClass() {
		criterion = Rql.getInstance().parse(QUERY_DOMINIO + "methodreturnclass eq 'java.lang.Integer'");
		final List<Member> result = criterion.list();

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void testWithParams() {
		criterion = Rql.getInstance().parse(QUERY_DOMINIO + "method with ('java.lang.String')");
		final List<Member> result = criterion.list();

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void testWithParamsComMaisDeUmParametro() {
		criterion = Rql.getInstance().parse(QUERY_DOMINIO + "method with ('java.lang.String', 'java.lang.Integer', 'java.lang.Boolean')");
		final List<Member> result = criterion.list();

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void testCriteriaProcuraPorFieldsConstantes() {
		criterion = Rql.getInstance().parse(QUERY_DOMINIO + "modifier eq 'final' and modifier eq 'static'");
		final List<Member> result = criterion.list();

		Assert.assertEquals(1, result.size());
	}

	@Test
	public void testCriteriaProcuraPorFieldsEstaticos() {
		criterion = Rql.getInstance().parse(QUERY_DOMINIO + "modifier eq 'static'");
		final List<Member> result = criterion.list();

		Assert.assertEquals(1, result.size());
	}

	@Test
	public void testCriteriaProcuraPorFieldsSynchronized() {
		criterion = Rql.getInstance().parse(QUERY_DOMINIO + "modifier eq 'SYNCHRONIZED'");
		final List<Field> result = criterion.list();

		Assert.assertEquals(0, result.size());
	}

	private void assertThrowsSyntaxException(String message, String rql) {
		try {
			Rql.getInstance().parse("FROM br.com.bit.ideias.reflection.test.artefacts.ClasseDominio WHERE " + rql);
			fail(message);
		} catch (SyntaxException e) {
		}
	}
}