package com.advertise.exception;

/**
 * Exception levée lors d'une erreur de compte dans keycloak
 * 
 * @author A153768
 */
public class ConflictAccountException extends CreateAccountException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ConflictAccountException(String message) {
		super(message);
	}
	
	public ConflictAccountException(String message, String codeErreur)  {
		super(message, codeErreur);
	}
	
	public ConflictAccountException(String message, Exception e) {
		super(message,e);
	}
	
	public ConflictAccountException(String message, String codeErreur, Exception e) {
		super(message,codeErreur,e);
	}

}
