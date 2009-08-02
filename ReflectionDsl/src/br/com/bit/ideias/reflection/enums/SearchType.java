package br.com.bit.ideias.reflection.enums;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.regex.Pattern;

import br.com.bit.ideias.reflection.criteria.expression.Expression;
import br.com.bit.ideias.reflection.exceptions.ClassNotExistsException;

/**
 * 
 * @author Nadilson Oliveira da Silva
 * @since 28/07/2009
 */
public enum SearchType {

	EQ {
		@Override
		public boolean matches(final Member member, final Expression expression) {
			return member.getName().equals(expression.getValue());
		}
	},
	NE {
		@Override
		public boolean matches(final Member member, final Expression expression) {
			return !SearchType.EQ.matches(member, expression);
		}
	},
	LIKE_START {
		@Override
		public boolean matches(final Member member, final Expression expression) {
			return member.getName().startsWith(expression.getValue());
		}
	},
	LIKE_END {
		@Override
		public boolean matches(final Member member, final Expression expression) {
			return member.getName().endsWith(expression.getValue());
		}
	},
	REGEX {
		@Override
		public boolean matches(final Member member, final Expression expression) {
			final String regex = String.format(".*%s.*", expression.getValue());
			return Pattern.matches(regex, member.getName());
		}
	},
	IN {
		@Override
		public boolean matches(final Member member, final Expression expression) {
			final String[] values = expression.getValue().split(Expression.NAME_SEPARATOR);
			for (final String value : values)
				if (member.getName().equals(value))
					return true;

			return false;
		}
	},
	ANNOTATION {
		@Override
		public boolean matches(final Member member, final Expression expression) {
			final Class<? extends Annotation> annotationClass = getAnnotatedClass(expression);
			final AnnotatedElement annotatedElement = (AnnotatedElement) member;

			return annotatedElement.isAnnotationPresent(annotationClass);
		}

		@SuppressWarnings("unchecked")
		private Class<? extends Annotation> getAnnotatedClass(final Expression expression) {
			Class<? extends Annotation> classe = null;
			try {
				classe = (Class<? extends Annotation>) Class.forName(expression.getValue());
			} catch (final ClassNotFoundException e) {
				throw new ClassNotExistsException(e);
			}
			return classe;
		}
	},
	ONLY_PUBLIC {
		@Override
		public boolean matches(final Member member, final Expression expression) {
			final boolean onlyPublic = Boolean.parseBoolean(expression.getValue());
			return !onlyPublic || Modifier.isPublic(member.getModifiers());
		}
	};

	public abstract boolean matches(Member member, Expression expression);

}
