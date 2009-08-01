package br.com.bit.ideias.reflection.enums;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import br.com.bit.ideias.reflection.criteria.expression.ExpressionImpl;
import br.com.bit.ideias.reflection.exceptions.ClassNotExistsException;

/**
 * 
 * @author nadilson
 * @since 28/07/2009
 */
public enum SearchType {

	EQ {
		@Override
		public boolean matches(final Member member, final ExpressionImpl expression) {
			return member.getName().equals(expression.getValue());
		}
	},

	NE {
		@Override
		public boolean matches(final Member member, final ExpressionImpl expression) {
			return !SearchType.EQ.matches(member, expression);
		}
	},

	LIKE_START {
		@Override
		public boolean matches(final Member member, final ExpressionImpl expression) {
			return member.getName().startsWith(expression.getValue());
		}
	},

	LIKE_END {
		@Override
		public boolean matches(final Member member, final ExpressionImpl expression) {
			return member.getName().endsWith(expression.getValue());
		}
	},

	REGEX {
		@Override
		public boolean matches(final Member member, final ExpressionImpl expression) {
			final String regex = String.format(".*%s.*", expression.getValue());
			return Pattern.matches(regex, member.getName());
		}
	},

	IN {
		@Override
		public boolean matches(final Member member, final ExpressionImpl expression) {
			final String[] values = expression.getValue().split(ExpressionImpl.NAME_SEPARATOR);
			for (final String value : values)
				if (member.getName().equals(value))
					return true;

			return false;
		}
	},

	ANNOTATION {
		@Override
		public boolean matches(final Member member, final ExpressionImpl expression) {
			final Class<? extends Annotation> annotationClass = getAnnotatedClass(expression);
			final AnnotatedElement annotatedElement = (AnnotatedElement) member;

			return annotatedElement.isAnnotationPresent(annotationClass);
		}

		@SuppressWarnings("unchecked")
		private Class<? extends Annotation> getAnnotatedClass(final ExpressionImpl expression) {
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
		public boolean matches(final Member member, final ExpressionImpl expression) {
			final boolean onlyPublic = Boolean.parseBoolean(expression.getValue());
			return !onlyPublic || Modifier.isPublic(member.getModifiers());
		}
	};

	public final List<Member> filter(final List<? extends Member> members, final ExpressionImpl expression) {
		final List<Member> filtred = new ArrayList<Member>();
		for (final Member member : members)
			if (matches(member, expression))
				filtred.add(member);

		return filtred;
	}

	public abstract boolean matches(Member member, ExpressionImpl expression);

}
