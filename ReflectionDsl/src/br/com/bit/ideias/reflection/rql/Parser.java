package br.com.bit.ideias.reflection.rql;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.bit.ideias.reflection.core.Introspector;
import br.com.bit.ideias.reflection.criteria.Criterion;
import br.com.bit.ideias.reflection.criteria.Restriction;
import br.com.bit.ideias.reflection.criteria.expression.ComplexExpression;
import br.com.bit.ideias.reflection.criteria.expression.ConjunctionExpression;
import br.com.bit.ideias.reflection.criteria.expression.Expression;
import br.com.bit.ideias.reflection.rql.exception.SyntaxException;
import br.com.bit.ideias.reflection.type.LikeType;
import br.com.bit.ideias.reflection.type.ModifierType;
import br.com.bit.ideias.reflection.type.TargetType;

/**
 * @author Leonardo Campos
 * @date 16/11/2009
 */
public class Parser {
    private static final Pattern PATTERN = Pattern.compile("from[ ]+[a-zA-Z]{1}[a-zA-Z0-9.]+([ ]+where[ ]+.+)?");
    private static final Pattern CLASS_PATTERN = Pattern.compile("[^ ]*");
//    private static final Pattern SIMPLE_EXPRESSION_PATTERN = Pattern.compile("^[^ ]+[ ]+(eq|ne)+[ ]+[^ ]+");
    private static Parser instance = new Parser();
    
    private static enum Connector {
        AND,OR
    }
    
    private abstract class QueryPart {
        protected String rql;
        public abstract Expression parse();
    }
    
    private class ExpressionPart extends QueryPart {
        public ExpressionPart(String rql) {
            this.rql = rql;
        }
        
        @Override
        public Expression parse() {
            return parseSimple(rql);
        }
    }
    
    private class ComplexPart extends QueryPart {
        private List<QueryPart> parts = new ArrayList<QueryPart>();
        private List<Connector> connectors = new ArrayList<Connector>();
        
        public ComplexPart() {
        }
        
        public ComplexPart addPart(QueryPart part) {
            parts.add(part);
            return this;
        }
        
        public ComplexPart addConnector(Connector connector) {
            connectors.add(connector);
            return this;
        }
        
        @Override
        public Expression parse() {
            if ((parts.size() - 1) != connectors.size())
                throw new SyntaxException("The number of connectors AND/OR is invalid");

            if(parts.size() == 1)
                return parts.get(0).parse();
            
            ConjunctionExpression expression = new ConjunctionExpression();
            ComplexExpression disjunction = null;

            int index = 0;
            Iterator<Connector> iterator = connectors.iterator();
            while (iterator.hasNext()) {
                Connector connector = iterator.next();

                if (connector == Connector.OR) {
                    if (disjunction == null) {
                        // transforms previous and next into an disjunction
                        QueryPart previous = parts.get(index);
                        QueryPart actual = parts.get(index + 1);

                        disjunction = Restriction.disjunction(previous.parse(), actual.parse());
                        expression.add(disjunction);
                        // now, add the disjunction to the complex expression
                    } else {
                        // adds next to the existing disjunction
                        disjunction.add(parts.get(index + 1).parse());
                    }
                } else {
                    // Just null the disjunction variable and add the expression
                    disjunction = null;
                    expression.add(parts.get(index).parse());
                }

                index++;
            }
            
            if(disjunction == null) {
                //it means it ended with an AND conjunction
                expression.add(parts.get(index).parse());
            }
            
            return expression;
        }
    }
    
    private static enum Clause {
        NAME {
            @Override
            public Set<Operator> doGetAccepted() {
                return createSet(Operator.EQ,Operator.NE,Operator.LIKE,Operator.IN);
            }
        },ANNOTATION {
            @Override
            public Set<Operator> doGetAccepted() {
                return createSet(Operator.EQ,Operator.NE);
            }
        }, MODIFIER {
        }, FIELDCLASS {
        }, METHODRETURNCLASS{
        }, METHOD {
            @Override
            public Set<Operator> doGetAccepted() {
                return createSet(Operator.WITH);
            }
        }, TARGET {
        };
        
        public boolean acceptsOperator(Operator operator) {
            return doGetAccepted().contains(operator);
        }

        public Set<Operator> doGetAccepted() {
            return createSet(Operator.EQ);
        }
        
        protected Set<Operator> createSet(Operator...operators) {
            Set<Operator> accepted = new HashSet<Operator>();
            for (Operator operator : operators) {
                accepted.add(operator);
            }
            
            return accepted;
        }
    }
    
    private static enum Operator {
        EQ {
            @SuppressWarnings("unchecked")
            @Override
            public Expression getExpression(Clause clause, String value) {
                switch (clause) {
                    case NAME:
                        return Restriction.eq(value);
                    case ANNOTATION:
                        Class<? extends Annotation> classFromValue = null;
                        try {
                            classFromValue = (Class<? extends Annotation>) getClassFromValue(value);
                        } catch (ClassCastException e) {
                            throw new SyntaxException(String.format("Right hand must be an Annotation => %s", value));
                        } 
                        return Restriction.annotatedWith(classFromValue);
                    case MODIFIER:
                        ModifierType modifier = ModifierType.valueOf(value);
                        return Restriction.withModifiers(modifier);
                    case TARGET:
                        return Restriction.targetType(TargetType.valueOf(value.toUpperCase()));
                }

                throw new SyntaxException("");
            }
        },NE {
            @SuppressWarnings("unchecked")
            @Override
            public Expression getExpression(Clause clause, String value) {
                if(clause == Clause.NAME) {
                    return Restriction.ne(value);
                } else if (clause == Clause.ANNOTATION) {
                    Class<?> classFromValue = getClassFromValue(value);
                    try {
                        return Restriction.notAnnotatedWith((Class<? extends Annotation>) classFromValue);
                    } catch (ClassCastException e) {
                        throw new SyntaxException("Class must be an Annotation");
                    }
                }
                throw new SyntaxException("NE");
            }
        },LIKE {
            @Override
            public Expression getExpression(Clause clause, String value) {
                if(value.startsWith("/")) {
                    //it is a Regex, so it should and with another "/"
                    
                    if(!value.endsWith("/")) throw new SyntaxException("Unclosed regex expression");
                    return Restriction.regex(removeEdges(value));
                }
                
                if(value.startsWith("%") && value.endsWith("%"))
                    return Restriction.like(removeEdges(value), LikeType.ANYWHERE);
                
                if(value.startsWith("%"))
                    return Restriction.like(value.substring(1), LikeType.END);
                
                if(value.endsWith("%"))
                    return Restriction.like(value.substring(0, value.length() - 1), LikeType.START);
                
                return Restriction.eq(value);
            }
        }, IN {
            @Override
            public Expression getExpression(Clause clause, String value) {
                String[] parts = value.split("[ ]{0,},[ ]{0,}");
                for (int i = 0; i < parts.length; i++) {
                    parts[i] = removeEdges(parts[i]);
                }
                
                return Restriction.in(parts);
            }
        }, WITH {
            @Override
            public Expression getExpression(Clause clause, String value) {
                switch (clause) {
                    case METHOD:
                        Class<?>[] params = null;
                        if(value.indexOf(",") == -1) {
                            params = new Class<?>[]{getClassFromValue(value.trim())};
                        } else {
                            String[] classes = value.split(",");
                            params = new Class<?>[classes.length];
                            
                            for (int i = 0; i < classes.length; i++) {
                                params[i] = getClassFromValue(classes[i].trim());
                            }
                        }
                        
                        return Restriction.methodWithParams(params);
                }

                throw new SyntaxException("");
            }
        };

        public Expression getExpression(Clause clause, String value) {
            return null;
        }

        protected Class<?> getClassFromValue(String value) {
            Class<?> klass = null;
            try {
                klass = (Class<?>) Class.forName(value);
            } catch (Throwable e) {
                throw new SyntaxException(String.format("Right hand must be a Class => %s", value));
            }
            return klass;
        }
        
        protected String removeEdges(String value) {
            return value.substring(1, value.length() - 1);
        }
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
        validateRestrictions(rql);
        QueryPart part = parseRestrictions(rql, new AtomicInteger(), 0);
        retorno.add(part.parse());
        return retorno;
    }
    
    private QueryPart parseRestrictions(String rql, AtomicInteger pos, int level) {
        ComplexPart part = new ComplexPart(); 
//        restriction.eq(""); //name eq ''
//        restriction.annotatedwith(null); //annotation eq 'class'
//        restriction.in(""); //name in ('','')
//        restriction.like("", liketype.anywhere); //name like '%x%'
//        restriction.ne(""); //name ne ''
//        restriction.regex(""); //name like '/regex/'
//        restriction.withmodifiers(modifiertype.final); //modifier eq ''
//        restriction.fieldclasseq(null); //fieldclass eq 'class'
//        restriction.methodreturnclasseq(null); //methodreturnclass eq 'class'
//        restriction.methodwithparams(null); //method with (class, class)
//        restriction.notannotatedwith(null); //annotation ne 'class'
//        restriction.targettype(targettype.field);//target eq 'field'
        rql = rql.trim();
//        
        //exemplo = (name eq 'teste' and annotation eq 'xxx' or (method with (class, class))) or (modifier eq '' and target eq 'xx')
        
        boolean isString = false;
        char chr = '-';
        StringBuilder builder = new StringBuilder();
        String lowerRQL = null;
        
        boolean isMethodWithParenthesis = false;
        int i = pos.get();
        while(i < rql.length()) {
            i = pos.get();
            chr = rql.charAt(i);
            i = pos.incrementAndGet();
            
            switch(chr) {
                case '(':
                    if(isString) {
                        builder.append(chr);
                        continue;
                    }
                    
                    lowerRQL = builder.toString().toLowerCase();
                    isMethodWithParenthesis = Pattern.matches("(\\b|.*)method[ ]+with[ ]+", lowerRQL) || Pattern.matches("(\\b|.*[ ]+)name[ ]+in[ ]+", lowerRQL);
                    
                    if(isMethodWithParenthesis) {
                        builder.append(chr);
                        i = pos.get();
                        continue;
                    }
                    
                    lowerRQL = lowerRQL.trim();
                    
                    if(lowerRQL.length() == 0) {
                        //This is the start of a subquery with no previous query
                        part.addPart(parseRestrictions(rql, pos, level + 1));
                        i = pos.get();
                        continue;
                    }
                    
                    parsePartial(part, rql, pos, level, builder.toString().trim());
                    part.addPart(parseRestrictions(rql, pos, level + 1));
                    i = pos.get();
                break;
                case ')':
                    if(isString) {
                        builder.append(chr);
                        i = pos.get();
                        continue;
                    }
                    
                    if(isMethodWithParenthesis) {
                        builder.append(chr);
                        isMethodWithParenthesis = false;
                        i = pos.get();
                        continue;
                    }
                    parsePartial(part, rql, pos, level, builder.toString());
                    return part;
                case '\'':
                    isString = !isString;
                    builder.append(chr);
                break;
                default:
                    builder.append(chr);
            }
        }
        
        parsePartial(part, rql, pos, level, builder.toString().trim());
        
        return part;
    }
    
    private void parsePartial(ComplexPart part, String originalRql, AtomicInteger pos, int level, String partial) {
      //Before this subquery, there was query on this stack level
        String operatorsPattern = "([ ]+|\\b)([aA][nN][dD]|[oO][rR])([ ]+|\\b)";
        
        Matcher matcher = Pattern.compile(operatorsPattern).matcher(partial);
        List<Connector> connectors = new ArrayList<Connector>();
        while(matcher.find()) {
            connectors.add(Connector.valueOf(matcher.group().trim().toUpperCase()));
        }
            
        String[] parts = partial.split(operatorsPattern);
        
        for (String string : parts) {
            if(string.trim().length() == 0)
                continue;
            part.addPart(new ExpressionPart(string));
        }
        
        for (Connector connector : connectors) {
            part.addConnector(connector);
        }
    }

    private Expression parseSimple(String rql) {
        String originalRql = rql;
        rql = rql.trim();
        String lowerRql = rql.toLowerCase();
        if(!Pattern.matches("^(name|annotation|modifier|fieldclass|methodreturnclass|method|target)[ ]+(eq|in|like|ne|with)[ ]+('[^\\']*'|\\([^\\)]*\\))$", lowerRql))
            throw new SyntaxException(String.format("There is an error on this part of your rql => %s", rql));
        
        //now we get what it is demmanded
        int pos = rql.indexOf(' ');
        String clauseName = rql.substring(0, pos).toUpperCase().trim();
        
        Clause clause = Clause.valueOf(clauseName);
        
        rql = rql.substring(pos).trim();
        pos = rql.indexOf(' ');
        String op = rql.substring(0, pos).toUpperCase().trim();
        Operator operator = Operator.valueOf(op);
        
        if(!clause.acceptsOperator(operator))
            throw new SyntaxException(String.format("%s does not work with %s => %s", clauseName, op, rql));
        
        String rightHand = rql.substring(op.length()).trim();
        
        if(rightHand.startsWith("'")) {
            return operator.getExpression(clause, rightHand.substring(1, rightHand.length() - 1));
        } else if(rightHand.startsWith("(")) {
            return operator.getExpression(clause, rightHand.substring(1, rightHand.length() - 1));
        } else {
            throw new SyntaxException(String.format("There is an error on the right hand operand of this query => %s", originalRql));
        }
    }

    private void validateRestrictions(String rql) {
        char chr = '-';
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
    
    public static void main(String[] args) {
        String query = "from br.com.bit.ideias.reflection.rql.Parser WHERE (name eq 'abc') aNd name eq 'xyz' or name eq 'aaa'";
        Parser.getInstance().parse(query);
    }
}
