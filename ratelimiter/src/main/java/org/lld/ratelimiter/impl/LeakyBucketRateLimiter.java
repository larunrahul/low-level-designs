package org.lld.ratelimiter.impl;

import org.lld.ratelimiter.RateLimiter;
import org.lld.ratelimiter.model.Response;

public class LeakyBucketRateLimiter implements RateLimiter {
	@Override
	public Response isAllowed() {
		return null;
	}
}
