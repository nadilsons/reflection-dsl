package br.com.bit.ideias.reflection.rql;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.junit.Test;

import br.com.bit.ideias.reflection.core.Introspector;
import br.com.bit.ideias.reflection.criteria.Criterion;
import br.com.bit.ideias.reflection.criteria.CriterionImpl;
import br.com.bit.ideias.reflection.criteria.Restriction;
import br.com.bit.ideias.reflection.criteria.expression.Expression;
import br.com.bit.ideias.reflection.rql.exception.SyntaxException;


/**
 * @author Leonardo Campos
 * @date 16/11/2009
 */
public class ParserTest {
    @Test(expected=SyntaxException.class)
    public void queryVaziaDeveriaLancarErroDeSintaxe() throws Exception {
        Parser.getInstance().parse("  ");
    }
    
    @Test(expected=SyntaxException.class)
    public void queryNulaDeveriaLancarErroDeSintaxe() throws Exception {
        Parser.getInstance().parse(null);
    }
    
    @Test(expected=SyntaxException.class)
    public void querySemFromDeveriaLancarErroDeSintaxe() throws Exception {
        Parser.getInstance().parse("abc");
    }
    
    @Test(expected=SyntaxException.class)
    public void queryComFromSeguidoDeNaoClasseDeveriaLancarErroDeSintaxe() throws Exception {
        Parser.getInstance().parse("FROM 444");
    }
    
    @Test
    public void queryComFromEClasseDeveriaFuncionar() throws Exception {
        Parser.getInstance().parse("FROM br.com.bit.ideias.reflection.rql.Parser");
    }
    
    @Test(expected=SyntaxException.class)
    public void queryComFromEClasseEWhereSemNadaDeveriaLancarErroDeSintaxe() throws Exception {
        Parser.getInstance().parse("FROM br.com.bit.ideias.reflection.rql.Parser WHERE");
    }
    
    @Test
    public void queryComFromEClasseEWhereDeveriaFuncionar() throws Exception {
        Criterion criterion = Parser.getInstance().parse("FROM br.com.bit.ideias.reflection.rql.Parser WHERE name eq 'teste'");
        assertNotNull(criterion);
        Introspector introspector = ((CriterionImpl)criterion).getIntrospector();
        assertEquals(Parser.class, introspector.getTargetClass());
    }
    
    @Test
    public void validaWhere() throws Exception {
        assertTrows("(");
        assertTrows(")");
        assertTrows("())");
        assertTrows("))");
        assertTrows("'vri");
        assertTrows("vri'");
        assertTrows("v'ri");
        
        assertOk("()");
        assertOk("''");
        assertOk("'('");
        assertOk("('(')()");
    }
    
    public void assertOk(String query) throws Exception {
        try {
            call(query);
        } catch (SyntaxException e) {
            fail("Deveria ter lançado exceção");
        } catch (InvocationTargetException e) {
            if(e.getCause() instanceof SyntaxException)
                fail("Deveria ter lançado exceção");
        }
    }
    
    public void assertTrows(String query) throws Exception {
        try {
            call(query);
            fail("Deveria ter lançado exceção");
        } catch (SyntaxException e) {
        } catch (InvocationTargetException e) {
            if(!(e.getCause() instanceof SyntaxException))
                fail("Deveria ter lançado exceção");
        }
    }
    
//    @Test
//    public void testParseRestrictionsShouldReturnSimpleExpression() throws Exception {
//        String query = "name eq 'teste'";
//        
//        List<Expression> expressions = call(query);
//        
//        assertNotNull("Deveria ter retornado algo", expressions);
//        assertEquals(1, expressions.size());
//    }
//    
//    @Test
//    public void testParseRestrictionsShouldAddSimpleExpression() throws Exception {
//        String query = "name eq 'teste' and name eq 'teste'";
//        
//        List<Expression> expressions = call(query);
//        
//        assertNotNull("Deveria ter retornado algo", expressions);
//        assertEquals(2, expressions.size());
//    }
//    
//    @Test
//    public void testParseRestrictionsShouldAddComplexExpressionWhenParentesisPresent() throws Exception {
//        
//    }

    private List<Expression> call(String query) throws IllegalAccessException, InvocationTargetException {
        Criterion criterion = Introspector.createCriterion(Parser.class);
        criterion.add(Restriction.eq("parseRestrictions"));
        Method result = criterion.uniqueResult();
        result.setAccessible(true);
        
        List<Expression> expressions = (List)result.invoke(Parser.getInstance(), query);
        return expressions;
    }
}
