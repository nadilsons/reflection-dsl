package br.com.bit.ideias.reflection.test.rql;

import java.lang.reflect.Field;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.com.bit.ideias.reflection.core.Introspector;
import br.com.bit.ideias.reflection.criteria.Criterion;
import br.com.bit.ideias.reflection.criteria.CriterionResult;
import br.com.bit.ideias.reflection.criteria.Restriction;
import br.com.bit.ideias.reflection.exceptions.NoResultException;
import br.com.bit.ideias.reflection.exceptions.TooManyResultException;
import br.com.bit.ideias.reflection.rql.Parser;
import br.com.bit.ideias.reflection.test.artefacts.ClasseDominio;
import br.com.bit.ideias.reflection.test.artefacts.ClasseDominioFilha;
import br.com.bit.ideias.reflection.type.TargetType;

/**
 * 
 * @author Nadilson Oliveira da Silva
 * @since 28/07/2009
 */
public class ParserTest {
    private final String QUERY_DOMINIO = "FROM br.com.bit.ideias.reflection.test.artefacts.ClasseDominio WHERE target eq 'field' and ";
    private final String QUERY_FILHA = "FROM br.com.bit.ideias.reflection.test.artefacts.ClasseDominioFilha WHERE target eq 'method' AND ";
    
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
        criterion = Parser.getInstance().parse("FROM br.com.bit.ideias.reflection.test.artefacts.ClasseDominio WHERE name like '%tributoPrivadoString'");
        final CriterionResult result = criterion.list();

        Assert.assertEquals(2, result.getMethods().size());
        Assert.assertEquals(1, result.getFields().size());
    }

    @Test
    public void testRestrictionEqDirectCriterion() throws Exception {
        final Field field = ClasseDominio.class.getDeclaredField("atributoPrivadoInt");
        final Criterion localCriterion = criterion = Parser.getInstance().parse(QUERY_DOMINIO + "name eq 'atributoPrivadoInt'");
        final CriterionResult result = localCriterion.list();

        Assert.assertTrue(result.getMethods().isEmpty());
        Assert.assertEquals(1, result.getFields().size());
        Assert.assertEquals(field, result.getFields().get(0));
    }

    @Test
    public void testRestrictionEq() throws Exception {
        final Field field = ClasseDominio.class.getDeclaredField("atributoPrivadoInt");
        criterion = Parser.getInstance().parse(QUERY_DOMINIO + "name eq 'atributoPrivadoInt'");
        final CriterionResult result = criterion.list();

        Assert.assertTrue(result.getMethods().isEmpty());
        Assert.assertEquals(1, result.getFields().size());
        Assert.assertEquals(field, result.getFields().get(0));
    }

    @Test
    public void shouldReturnEitherWhenUsingDisjunction() throws Exception {
        final Field field = ClasseDominio.class.getDeclaredField("atributoPrivadoInt");
        
        criterion = Parser.getInstance().parse(QUERY_DOMINIO + "name eq 'atributoPrivadoInt' and (name eq 'yzxabc' or name eq 'atributoPrivadoInt')");
        final CriterionResult result = criterion.list();

        Assert.assertTrue(result.getMethods().isEmpty());
        Assert.assertEquals(1, result.getFields().size());
        Assert.assertEquals(field, result.getFields().get(0));
    }

    @Test
    public void testRestrictionEqEncadeado() throws Exception {
        criterion = Parser.getInstance().parse(QUERY_DOMINIO + "name eq 'atributoPrivadoInt' and name eq 'isAlive'");
        final CriterionResult result = criterion.list();

        Assert.assertTrue(result.getMethods().isEmpty());
        Assert.assertTrue(result.getFields().isEmpty());
    }

    @Test
    public void testRestrictionNe() throws Exception {
        criterion = Parser.getInstance().parse(QUERY_DOMINIO + "name ne 'atributoPrivadoInt' and name ne 'constante'");
        final CriterionResult result = criterion.list();

        Assert.assertTrue(result.getMethods().isEmpty());
        Assert.assertEquals(7, result.getFields().size());
    }

    @Test
    public void testRestrictionNeEncadead() throws Exception {
        criterion = Parser.getInstance().parse(QUERY_DOMINIO + "name ne 'atributoPrivadoInt' and name ne 'isAlive' and name ne 'Privative' and name ne 'constante'");
        final CriterionResult result = criterion.list();

        Assert.assertTrue(result.getMethods().isEmpty());
        Assert.assertEquals(5, result.getFields().size());
    }

    @Test
    public void testRestrictionLike() throws Exception {
        criterion = Parser.getInstance().parse(QUERY_DOMINIO + "name like '%atributo%'");
        final CriterionResult result = criterion.list();

        Assert.assertTrue(result.getMethods().isEmpty());
        Assert.assertEquals(4, result.getFields().size());
    }

    @Test
    public void testRestrictionLikeEncadeado() throws Exception {
        criterion = Parser.getInstance().parse(QUERY_DOMINIO + "name like '%atributo%' and name ne 'atributoPrivadoInt'");
        final CriterionResult result = criterion.list();

        Assert.assertTrue(result.getMethods().isEmpty());
        Assert.assertEquals(3, result.getFields().size());
    }

    @Test
    public void testRestrictionLikeComLikeTypeStart() throws Exception {
        criterion = Parser.getInstance().parse(QUERY_DOMINIO + "name like 'atributo%'");
        final CriterionResult result = criterion.list();

        Assert.assertTrue(result.getMethods().isEmpty());
        Assert.assertEquals(4, result.getFields().size());
    }

    @Test
    public void testRestrictionLikeComLikeTypeEnd() throws Exception {
        criterion = Parser.getInstance().parse(QUERY_DOMINIO + "name like '%ive'");
        final CriterionResult result = criterion.list();

        Assert.assertTrue("There should be no result", result.getMethods().isEmpty());
        Assert.assertEquals(2, result.getFields().size());
    }

    @Test
    public void testRestrictionLikeComLikeTypeAnywhere() throws Exception {
        criterion = Parser.getInstance().parse(QUERY_DOMINIO + "name like '%Priva%'");
        final CriterionResult result = criterion.list();

        Assert.assertTrue(result.getMethods().isEmpty());
        Assert.assertEquals(5, result.getFields().size());
    }

    @Test
    public void testRestrictionRegex() throws Exception {
        criterion = Parser.getInstance().parse(QUERY_DOMINIO + "name like '/comeca[P|p]riva/'");
        final CriterionResult result = criterion.list();

        Assert.assertTrue(result.getMethods().isEmpty());
        Assert.assertEquals(2, result.getFields().size());
    }

    @Test
    public void testRestrictionIn() throws Exception {
        criterion = Parser.getInstance().parse(QUERY_DOMINIO + "name in ('atributoPrivadoInt','comecaPriva')");
        final CriterionResult result = criterion.list();

        Assert.assertTrue(result.getMethods().isEmpty());
        Assert.assertEquals(2, result.getFields().size());
    }

    @Test
    public void testRestrictionAnnotatedWith() throws Exception {
        criterion = Parser.getInstance().parse(QUERY_DOMINIO + "annotated with 'br.com.bit.ideias.reflection.test.artefacts.MyAnnotation'");
        final CriterionResult result = criterion.list();

        Assert.assertTrue(result.getMethods().isEmpty());
        Assert.assertEquals(3, result.getFields().size());
    }
    
    @Test
    public void testRestrictionAnnotatedWithComDoisAdd() throws Exception {
        criterion = Parser.getInstance().parse(QUERY_DOMINIO + "annotated with 'br.com.bit.ideias.reflection.test.artefacts.MyAnnotation' and annotated with 'br.com.bit.ideias.reflection.test.artefacts.MyAnnotation2'");
        final CriterionResult result = criterion.list();

        Assert.assertTrue(result.getMethods().isEmpty());
        Assert.assertEquals(0, result.getFields().size());
    }

    @Test
    public void testRestrictionShowOnlyPublicTrue() throws Exception {
        criterion = Parser.getInstance().parse(QUERY_DOMINIO + "name like '/comeca[P|p]riva/' and modifier eq 'public'");
        final CriterionResult result = criterion.list();

        Assert.assertTrue(result.getMethods().isEmpty());
        Assert.assertEquals(1, result.getFields().size());
    }

    @Test(expected = TooManyResultException.class)
    public void testRestrictionUniqueShouldThrowAnExceptionIfThereAreMoreThanOneResult() throws Exception {
        criterion = Parser.getInstance().parse(QUERY_DOMINIO + "annotated with 'br.com.bit.ideias.reflection.test.artefacts.MyAnnotation'");
        criterion.uniqueResult();
    }

    @Test(expected = NoResultException.class)
    public void testRestrictionUniqueShouldThrowAnExceptionIfThereAreNoResults() throws Exception {
        criterion = Parser.getInstance().parse(QUERY_DOMINIO + "name eq 'xyzu'");
        criterion.uniqueResult();
    }

    @Test
    public void testRestrictionUniqueShouldReturnOnlyOneMember() throws Exception {
        criterion = Parser.getInstance().parse(QUERY_DOMINIO + "name like '/comeca[P|p]riva/' and modifier eq 'public'");
        final Field fieldActual = criterion.uniqueResult();

        final Field fieldExpected = ClasseDominio.class.getDeclaredField("comecaPriva");
        Assert.assertEquals(fieldExpected, fieldActual);
    }

    @Test
    public void testRestrictionShowOnlyPublicFalse() throws Exception {
        criterion = Parser.getInstance().parse(QUERY_DOMINIO + "name like '/comeca[P|p]riva/' and modifier eq 'PRIVATE' and modifier eq 'PROTECTED'");
        final CriterionResult result = criterion.list();

        Assert.assertTrue(result.getMethods().isEmpty());
        Assert.assertEquals(1, result.getFields().size());
    }

    @Test
    public void testRestrictionShowOnlyPublicTrueComAtributosNaClassePai() throws Exception {
        criterion = Parser.getInstance().parse(QUERY_FILHA + "name like '%atributo%' and modifier eq 'PRIVATE' and modifier eq 'PROTECTED'");
        final CriterionResult result = criterion.list();

        Assert.assertTrue(result.getMethods().isEmpty());
        Assert.assertEquals(5, result.getFields().size());
    }

    @Test
    public void testRestrictionEqComPropriedadeNaClassePai() throws Exception {
        criterion = Parser.getInstance().parse(QUERY_FILHA + "name like '/comeca[P|p]riva/'");
        final CriterionResult result = criterion.list();

        Assert.assertTrue(result.getMethods().isEmpty());
        Assert.assertEquals(2, result.getFields().size());
    }

    @Test
    public void testRestrictionAnnotatedWithNaClassePai() throws Exception {
        criterion = Parser.getInstance().parse(QUERY_DOMINIO + "annotated with 'br.com.bit.ideias.reflection.test.artefacts.MyAnnotation'");
        final CriterionResult result = criterion.list();

        Assert.assertTrue(result.getMethods().isEmpty());
        Assert.assertEquals(3, result.getFields().size());
    }

    @Test
    public void testTypeEq() {
        criterion = Parser.getInstance().parse(QUERY_DOMINIO + "fieldclass eq 'java.lang.String'");
        final CriterionResult result = criterion.list();

        Assert.assertEquals(2, result.getFields().size());
        Assert.assertTrue(result.getMethods().isEmpty());
    }

    @Test
    public void testMethodReturnClass() {
        criterion = Parser.getInstance().parse(QUERY_DOMINIO + "methodreturnclass eq 'java.lang.Integer'");
        final CriterionResult result = criterion.list();

        Assert.assertTrue(result.getFields().isEmpty());
        Assert.assertTrue(result.getMethods().isEmpty());
    }

    @Test
    public void testWithParams() {
        criterion = Parser.getInstance().parse(QUERY_DOMINIO + "method with ('java.lang.String')");
        final CriterionResult result = criterion.list();

        Assert.assertTrue(result.getFields().isEmpty());
        Assert.assertTrue(result.getMethods().isEmpty());
    }

    @Test
    public void testWithParamsComMaisDeUmParametro() {
        criterion = Parser.getInstance().parse(QUERY_DOMINIO + "method with ('java.lang.String', 'java.lang.Integer', 'java.lang.Boolean')");
        final CriterionResult result = criterion.list();

        Assert.assertTrue(result.getFields().isEmpty());
        Assert.assertTrue(result.getMethods().isEmpty());
    }

    @Test
    public void testCriteriaProcuraPorFieldsConstantes() {
        criterion = Parser.getInstance().parse(QUERY_DOMINIO + "modifier eq 'final' and modifier eq 'static'");
        final CriterionResult result = criterion.list();

        Assert.assertTrue(result.getMethods().isEmpty());
        Assert.assertEquals(1, result.getFields().size());
    }

    @Test
    public void testCriteriaProcuraPorFieldsEstaticos() {
        criterion = Parser.getInstance().parse(QUERY_DOMINIO + "modifier eq 'static'");
        final CriterionResult result = criterion.list();

        Assert.assertTrue(result.getMethods().isEmpty());
        Assert.assertEquals(1, result.getFields().size());
    }

    @Test
    public void testCriteriaProcuraPorFieldsSynchronized() {
        criterion = Parser.getInstance().parse(QUERY_DOMINIO + "modifier eq 'SYNCHRONIZED'");
        final CriterionResult result = criterion.list();

        Assert.assertTrue(result.getMethods().isEmpty());
        Assert.assertEquals(0, result.getFields().size());
    }
}