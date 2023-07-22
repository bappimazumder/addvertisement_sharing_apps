package com.advertise.exception;

public class CreateAccountException extends Exception {
	
	/**
	 * Exception levée lors d'une erreur de creation de compte dans keycloak
	 */
	private static final long serialVersionUID = 1L;
	
	private final String codeErreur;
	
	public String getCodeErreur() {
		return codeErreur;
	}

	public CreateAccountException(String message, String codeErreur)  {
		super(message);
		this.codeErreur = codeErreur;
	}
	
	
	public CreateAccountException(String message)  {
		super(message);
		this.codeErreur =null;
	}

	public CreateAccountException(String message, Exception e) {
		super(message,e);
		this.codeErreur =null;
	}
	
	public CreateAccountException(String message, String codeErreur, Exception e) {
		super(message,e);
		this.codeErreur = codeErreur;
	}
	
	
}
