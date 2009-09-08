package br.com.bit.ideias.reflection.type;

import java.lang.reflect.Modifier;

/**
 * @since 08/09/2009
 * @author Nadilson Oliveira da Silva
 */
public enum ModifierType {
	
	ABSTRACT(Modifier.ABSTRACT),	
	FINAL(Modifier.FINAL),
	INTERFACE(Modifier.INTERFACE),
	NATIVE(Modifier.NATIVE),
	PRIVATE(Modifier.PRIVATE),	
	PROTECTED(Modifier.PROTECTED),	
	PUBLIC(Modifier.PUBLIC),	
	STATIC(Modifier.STATIC),
	STRICT(Modifier.STRICT),
	SYNCHRONIZED(Modifier.SYNCHRONIZED),
	TRANSIENT(Modifier.TRANSIENT),	
	VOLATILE(Modifier.VOLATILE);
	
	private final int modifier;

	private ModifierType(int modifier) {
		this.modifier = modifier;
	}

	public int getModifier() {
		return modifier;
	}
	
	public static void main(String[] args) {
		int zero = 0;
		zero = Modifier.ABSTRACT;
		zero = zero | Modifier.PRIVATE;
		zero = zero | Modifier.FINAL;
		zero = zero | Modifier.PROTECTED;
		
		System.out.println(Modifier.isAbstract(zero));
		System.out.println(Modifier.isPrivate(zero));
		System.out.println(Modifier.isProtected(zero));
		System.out.println(Modifier.isFinal(zero));
	}
}
