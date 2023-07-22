package com.advertise.exception;

public class UpdateAccountException extends Exception {
	
	/**
	 * Exception levée lors d'une erreur de la modification de compte dans keycloak
	 */
	private static final long serialVersionUID = 1L;
	
	private String codeErreur;
	
	public String getCodeErreur() {
		return codeErreur;
	}

	public UpdateAccountException(String message, String codeErreur)  {
		super(message);
		this.codeErreur = codeErreur;
	}
	
	
	public UpdateAccountException(String message)  {
		super(message);
	}

	public UpdateAccountException(String message, Exception e) {
		super(message,e);
	}
	
	public UpdateAccountException(String message, String codeErreur, Exception e) {
		super(message,e);
		this.codeErreur = codeErreur;
	}
	
	
}
