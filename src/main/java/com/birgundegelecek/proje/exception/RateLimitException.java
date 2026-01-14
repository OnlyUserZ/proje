package com.birgundegelecek.proje.exception;

public class RateLimitException extends RuntimeException { 
	
	public RateLimitException(String message) {
		super(message);
	}

}
