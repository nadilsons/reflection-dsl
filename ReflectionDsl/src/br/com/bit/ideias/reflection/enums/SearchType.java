package br.com.bit.ideias.reflection.enums;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;

import br.com.bit.ideias.reflection.criteria.expression.Expression;

/**
 * 
 * @author nadilson
 * @since 28/07/2009
 */
public enum SearchType {
	EQ {
		@Override
		public List<Member> filter(List<? extends Member> members, Expression expression) {
			List<Member> filtred = getFilteredList();
			for (Member member : members)
				if (member.getName().equals(expression.getValue()))
					filtred.add(member);

			return filtred;
		}
	}, 
	
	NE  {
		@Override
		public List<Member> filter(List<? extends Member> members, Expression expression) {
			List<Member> filtred = getFilteredList();
			for (Member member : members)
				if (!member.getName().equals(expression.getValue()))
					filtred.add(member);

			return filtred;
		}
	}, 
	
	LIKE {
		@Override
		public List<Member> filter(List<? extends Member> members, Expression expression) {
			List<Member> filtred = getFilteredList();
			for (Member member : members)
				if (member.getName().startsWith(expression.getValue()))
					filtred.add(member);

			return filtred;
		}
	};

	public abstract List<Member> filter(List<? extends Member> members, Expression expression);

	private static List<Member> getFilteredList() {
		List<Member> filtred = new ArrayList<Member>();
		return filtred;
	}
}
