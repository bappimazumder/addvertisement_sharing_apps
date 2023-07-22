package com.advertise.exception;

public class DeleteAccountException extends Exception {
	
	/**
	 * Exception levée lors d'une erreur de suppression d'un compte dans keycloak
	 */
	private static final long serialVersionUID = 1L;
	
	private final String codeErreur;
	
	public String getCodeErreur() {
		return codeErreur;
	}

	public DeleteAccountException(String message, String codeErreur)  {
		super(message);
		this.codeErreur = codeErreur;
	}
	
	
	public DeleteAccountException(String message)  {
		super(message);
		this.codeErreur =null;
	}

	public DeleteAccountException(String message, Exception e) {
		super(message,e);
		this.codeErreur =null;
	}
	
	public DeleteAccountException(String message, String codeErreur, Exception e) {
		super(message,e);
		this.codeErreur = codeErreur;
	}
	
	
}
