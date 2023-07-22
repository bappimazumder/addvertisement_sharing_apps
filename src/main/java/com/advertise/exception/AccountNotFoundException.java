package com.advertise.exception;

public class AccountNotFoundException extends Exception {
	
	/**
	 * Exception levée lorsque le compte modifié est introuvable dans keycloak
	 */
	private static final long serialVersionUID = 1L;
	
	private final String codeErreur;
	
	public String getCodeErreur() {
		return codeErreur;
	}

	public AccountNotFoundException(String message, String codeErreur)  {
		super(message);
		this.codeErreur = codeErreur;
	}
	
	
	public AccountNotFoundException(String message)  {
		super(message);
		this.codeErreur="";
	}

	public AccountNotFoundException(String message, Exception e) {
		super(message,e);
		this.codeErreur="";
	}
	
	public AccountNotFoundException(String message, String codeErreur, Exception e) {
		super(message,e);
		this.codeErreur = codeErreur;
	}
	
	
}
