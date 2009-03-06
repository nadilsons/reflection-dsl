/**
 * 
 */
package br.com.bit.ideias.reflection.test;

/**
 * @author Nadilson
 * @date 18/02/2009
 * 
 */
@SuppressWarnings("unused")
public class ClasseDominio {

	private Integer atributoPrivadoInteiro;

	private String atributoPrivadoString;

	private String atributoIsolado;

	private int atributoPrivadoInt;

	public ClasseDominio() {
		_evitaFinal();
	}

	public ClasseDominio(final Integer atributoPrivadoInteiro) {
		this(atributoPrivadoInteiro, "NAO_SETADO");
	}

	public ClasseDominio(final Integer atributoPrivadoInteiro, final String atributoPrivadoString) {
		this();
		this.atributoPrivadoInteiro = atributoPrivadoInteiro;
		this.atributoPrivadoString = atributoPrivadoString;
	}

	public Integer getAtributoPrivadoInteiro() {
		return atributoPrivadoInteiro;
	}

	public void setAtributoPrivadoInteiro(final Integer atributoPrivadoInteiro) {
		this.atributoPrivadoInteiro = atributoPrivadoInteiro;
	}

	public String getAtributoPrivadoString() {
		return atributoPrivadoString;
	}

	public void setAtributoPrivadoString(final String atributoPrivadoString) {
		this.atributoPrivadoString = atributoPrivadoString;
	}

	public Integer getDobroAtributoPrivadoInteiro() {
		return getDobro(atributoPrivadoInteiro);
	}

	public Integer getDobro(final Integer value) {
		return value * 2;
	}

	private String metodoPrivado(final String nome) {
		return String.format("%s, %1$s", nome);
	}

	public void metodoQueVaiLancarException() {
		throw new RuntimeException("Excecao de teste");
	}

	public int getAtributoPrivadoInt() {
		return atributoPrivadoInt;
	}

	public void setAtributoPrivadoInt(int atributoPrivadoInt) {
		this.atributoPrivadoInt = atributoPrivadoInt;
	}

	private void _evitaFinal() {
		this.atributoIsolado = "only";
	}
}
