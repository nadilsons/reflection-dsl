package br.com.bit.ideias.reflection.enums;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 
 * @author Nadilson Oliveira da Silva
 * @since 28/07/2009
 */
public enum TargetType {
	FIELD {
		@Override
		protected Member[] getMembers(final Class<?> classe) {
			return classe.getDeclaredFields();
		}

		@Override
		public boolean isValidMember(final Member member) {
			return member instanceof Field;
		}
	},
	METHOD {
		@Override
		protected Member[] getMembers(final Class<?> classe) {
			return classe.getDeclaredMethods();
		}

		@Override
		public boolean isValidMember(final Member member) {
			return member instanceof Method;
		}
	};

	public List<Member> obtainMembersInClass(final Class<?> classe) {
		final Member[] members = getMembers(classe);
		return new ArrayList<Member>(Arrays.asList(members));
	}

	public abstract boolean isValidMember(Member member);

	protected abstract Member[] getMembers(final Class<?> classe);

}
