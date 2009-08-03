package br.com.bit.ideias.reflection.enums;

import java.lang.reflect.Member;
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
	}, 
	METHOD {
		@Override
		protected Member[] getMembers(final Class<?> classe) {
			return  classe.getDeclaredMethods();
		}
	};
	
	public List<Member> obtainMembersInClass(final Class<?> classe) {
		final Member[] members = getMembers(classe);
		return new ArrayList<Member>(Arrays.asList(members));
	}

	protected abstract Member[] getMembers(final Class<?> classe); 

}
