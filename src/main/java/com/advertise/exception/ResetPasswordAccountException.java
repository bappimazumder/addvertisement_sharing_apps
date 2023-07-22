package com.advertise.exception;

public class ResetPasswordAccountException extends Exception {
	
	/**
	 * Exception levée lors d'une erreur de la reinitialisation du mot de passe d'un compte dans keycloak
	 */
	private static final long serialVersionUID = 1L;
	
	private final String codeErreur;
	
	public String getCodeErreur() {
		return codeErreur;
	}

	public ResetPasswordAccountException(String message, String codeErreur)  {
		super(message);
		this.codeErreur = codeErreur;
	}
	
	
	public ResetPasswordAccountException(String message)  {
		super(message);
		this.codeErreur =null;
	}

	public ResetPasswordAccountException(String message, Exception e) {
		super(message,e);
		this.codeErreur =null;
	}
	
	public ResetPasswordAccountException(String message, String codeErreur, Exception e) {
		super(message,e);
		this.codeErreur = codeErreur;
	}
	
	
}
