package org.lld.ratelimiter;

import org.lld.ratelimiter.model.Response;
public interface RateLimiter {
	Response isAllowed();
}
