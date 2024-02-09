package org.lld.ratelimiter.model;

public class Response {
	boolean allowed;
	long retryAfterInMillis;
	public Response(boolean allowed, long retryAfterInMillis){
		this.allowed = allowed;
		this.retryAfterInMillis = retryAfterInMillis;
	}
}
