package br.com.bit.ideias.reflection.rql.query;

import java.lang.annotation.Annotation;
import java.util.regex.Pattern;

import br.com.bit.ideias.reflection.criteria.Restriction;
import br.com.bit.ideias.reflection.criteria.expression.Expression;
import br.com.bit.ideias.reflection.exceptions.SyntaxException;
import br.com.bit.ideias.reflection.type.LikeType;
import br.com.bit.ideias.reflection.type.ModifierType;
import br.com.bit.ideias.reflection.type.TargetType;

/**
 * @author Leonardo Campos
 * @date 20/11/2009
 */
public enum QueryOperator {
    EQ {
        @SuppressWarnings("unchecked")
        @Override
        public Expression getExpression(QueryClause clause, String value) {
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
                    ModifierType modifier = ModifierType.valueOf(value.toUpperCase());
                    return Restriction.withModifiers(modifier);
                case TARGET:
                    return Restriction.targetType(TargetType.valueOf(value.toUpperCase()));
                case FIELDCLASS:
                    return Restriction.fieldClassEq(getClassFromValue(value));
                case METHODRETURNCLASS:
                    return Restriction.methodReturnClassEq(getClassFromValue(value));
                
            }

            throw new SyntaxException("");
        }
    },NE {
        @SuppressWarnings("unchecked")
        @Override
        public Expression getExpression(QueryClause clause, String value) {
            if(clause == QueryClause.NAME) {
                return Restriction.ne(value);
            } else if (clause == QueryClause.ANNOTATION) {
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
        public Expression getExpression(QueryClause clause, String value) {
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
        public Expression getExpression(QueryClause clause, String value) {
            String[] parts = ARRAY_SEPARATOR_PATTERN.split(value);
            for (int i = 0; i < parts.length; i++) {
                parts[i] = removeEdges(parts[i]);
            }
            
            return Restriction.in(parts);
        }
    }, WITH {
        @Override
        public Expression getExpression(QueryClause clause, String value) {
            switch (clause) {
                case METHOD:
                    String[] parts = ARRAY_SEPARATOR_PATTERN.split(value);
                    Class<?>[] params = new Class<?>[parts.length];
                    for (int i = 0; i < parts.length; i++) {
                        params[i] = getClassFromValue(removeEdges(parts[i]));
                    }
                    
                    return Restriction.methodWithParams(params);
            }

            throw new SyntaxException("");
        }
    };

    public static final Pattern ARRAY_SEPARATOR_PATTERN = Pattern.compile("[ ]{0,},[ ]{0,}");
    
    public Expression getExpression(QueryClause clause, String value) {
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
