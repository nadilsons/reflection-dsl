package br.com.bit.ideias.reflection.criteria.target;



/**
 * 
 * @author Nadilson Oliveira da Silva
 * @since 28/07/2009
 */
public class MemberTarget extends Target {

	private static final MemberTarget instance = new MemberTarget();

	private MemberTarget() {

	}

	public static MemberTarget getInstance() {
		return instance;
	}
}
