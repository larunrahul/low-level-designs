package org.lld.ratelimiter.impl;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

import org.lld.ratelimiter.RateLimiter;
import org.lld.ratelimiter.model.Config;
import org.lld.ratelimiter.model.Response;

public class TokenBucketRateLimiter implements RateLimiter {

	private AtomicLong availableTokens;
	private long tokensPerPeriod;
	private long period;
	private Instant lastRefillTime;
	private long capacity;

	public TokenBucketRateLimiter(Config config){
		this.availableTokens = new AtomicLong(config.getTokensPerPeriod());
		this.tokensPerPeriod = config.getTokensPerPeriod();
		this.period = config.getPeriod().toMillis();
		this.lastRefillTime = Instant.now();
		this.capacity = config.getCapacity();
	}
	@Override
	public synchronized Response isAllowed() {
		refillTokens();
		if(availableTokens.get() > 0){
			availableTokens.getAndDecrement();
			return new Response(true, 0);
		}
		//try after next refill
		return new Response(false, Duration.between(lastRefillTime.plusMillis(period), Instant.now()).toMillis());
	}

	private void refillTokens(){
		Instant currentTime = Instant.now();
		long elapsedTime = Duration.between(lastRefillTime, currentTime).toMillis();
		//last refilled is before bucket period, refill again
		if(elapsedTime >= period) {
			//figure out how many periods happened since we last filled the bucket
			//fill tokens worth of those many periods
			//never update more than capacity
			availableTokens.getAndUpdate(a -> Math.min(capacity, a + (elapsedTime / period) * tokensPerPeriod));
			lastRefillTime = currentTime;
		}
	}
}
