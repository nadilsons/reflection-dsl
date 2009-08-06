package br.com.bit.ideias.reflection.criteria;

import java.lang.reflect.Member;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @author Nadilson Oliveira da Silva
 * @since 28/07/2009
 */
public class CriterionResult {
	private final List<Member> members;

	public CriterionResult(List<Member> members) {
		this.members = members;
	}

	public List<Member> getMembers() {
		return Collections.unmodifiableList(members);
	}
}
