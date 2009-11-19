package br.com.bit.ideias.reflection.rql;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.bit.ideias.reflection.core.Introspector;
import br.com.bit.ideias.reflection.criteria.Criterion;
import br.com.bit.ideias.reflection.criteria.Restriction;
import br.com.bit.ideias.reflection.criteria.expression.Expression;
import br.com.bit.ideias.reflection.rql.exception.SyntaxException;

/**
 * @author Leonardo Campos
 * @date 16/11/2009
 */
public class Parser {
    private static final Pattern PATTERN = Pattern.compile("from[ ]+[a-zA-Z]{1}[a-zA-Z0-9.]+([ ]+where[ ]+.+)?");
    private static final Pattern CLASS_PATTERN = Pattern.compile("[^ ]*");
    private static final Pattern SIMPLE_EXPRESSION_PATTERN = Pattern.compile("^[^ ]+[ ]+(eq|ne)+[ ]+[^ ]+");
    private static Parser instance = new Parser();
    
    private static enum Clauses {
        NAME {
            public Expression getRestriction(Operations op, String rightHand) {
                if(op == Operations.EQ) {
                    return Restriction.eq(rightHand);
                } else if (op == Operations.NE) {
                    return Restriction.ne(rightHand);
                }  else if (op == Operations.LIKE) {
                    return Restriction.like(rightHand);
                } else {
                    throw new SyntaxException("Name accepts EQ/NE/LIKE only");
                }
            }
        };
        
        public abstract Expression getRestriction(Operations op, String rightHand);
    }
    
    private static enum Operations {
        EQ,NE,LIKE
    }

    private Parser() {
    }

    public static Parser getInstance() {
        return instance;
    }

    public Criterion parse(String rql) {
        if(rql == null || rql.trim().length() == 0) throw new SyntaxException("Empty query");
        rql = rql.trim();
        
        String toTest = rql.toLowerCase();
        
        if(!PATTERN.matcher(toTest).matches()) throw new SyntaxException("Query must follow the pattern: FROM [class_name] followed by optional WHERE clause");
        
        //now that we know it is ok with from, lets get rid of it
        rql = rql.substring(4).trim();
        
        //lets retrieve the class name and get rid of it too
        Matcher matcher = CLASS_PATTERN.matcher(rql);
        if(!matcher.find()) throw new SyntaxException("Classe inválida");
        String className = matcher.group();
        rql = rql.substring(className.length()).trim();
        
        Criterion retorno = Introspector.createCriterion(className);
        
        //is there anything else to parse?
        if(rql.length() == 0) return retorno;
        
        //well, now we must get rid of the where clause
        rql = rql.substring(5).trim();
        
        //ok, now we have to deal with the restrictions
        List<Expression> expressions = parseRestrictions(rql);
        if(expressions == null) return retorno;
        
        for (Expression expression : expressions) {
            retorno.add(expression);
        }
            
        return retorno;
    }
    
    private List<Expression> parseRestrictions(String rql) {
        rql = rql.trim();
        
        validateRestrictions(rql);
        
        return null;
    }
//
//    private Expression simpleExpressionParser(String expressionQuery) {
//        if(!SIMPLE_EXPRESSION_PATTERN.matcher(expressionQuery).matches()) throw new SyntaxException("A simple expression should be composed of left hand operator right hand");
//        
//        String[] parts = expressionQuery.split("[ ]+");
//        String leftHand = parts[0];
//        String rightHand = parts[2];
//        if(!(rightHand.startsWith("'") && rightHand.endsWith("'"))) throw new SyntaxException("Right hand must be surrounded by '");
//        rightHand = rightHand.substring(1, rightHand.length() - 1);
//        
//        String operation = parts[1].toUpperCase();
//        
//        Clauses clause = Clauses.valueOf(leftHand.toUpperCase());
//        
//        return clause.getRestriction(Operations.valueOf(operation), rightHand);
//    }

    private void validateRestrictions(String rql) {
        char chr = 'a';
        Stack<Integer> stack = new Stack<Integer>();
        Integer integer = Integer.valueOf(0);
        boolean isString = false;
        
        for (int i = 0; i < rql.length(); i++) {
            chr = rql.charAt(i);
            
            switch(chr) {
                case '(':
                    if(!isString)
                        stack.add(integer);
                break;
                case ')':
                    try {
                        if(!isString)
                            stack.pop();
                    } catch (EmptyStackException e) {
                        throw new SyntaxException("Erro nas restrições");
                    }
                break;
                case '\'':
                    isString = !isString;
                break;
            }
        }
        
        if(isString)
            throw new SyntaxException("String não fechada");
        
        if(!stack.isEmpty())
            throw new SyntaxException("Erro nas restrições");
    }
}
