package org.lld.ratelimiter.impl;

import java.time.Instant;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.lld.ratelimiter.RateLimiter;
import org.lld.ratelimiter.model.Config;
import org.lld.ratelimiter.model.Response;

public class SlidingWindowLogRateLimiter implements RateLimiter {

	private long tokensPerPeriod;
	private long period;

	Deque<Instant> slidingWindow = new ConcurrentLinkedDeque<>();

	public SlidingWindowLogRateLimiter(Config config){
		this.tokensPerPeriod = config.getTokensPerPeriod();
		this.period = config.getPeriod().toMillis();
	}
	@Override
	public synchronized Response isAllowed() {
		Instant currentTime = Instant.now();
		Instant windowStart = currentTime.minusMillis(period - 1);
		//remove all requests which are before our window
		while(!slidingWindow.isEmpty() && slidingWindow.getFirst().isBefore(windowStart)){
			slidingWindow.removeFirst();
		}
		//if current window can still accommodate tokens
		if(slidingWindow.size() < tokensPerPeriod){
			slidingWindow.addLast(currentTime);
			return new Response(true, 0);
		}
		return new Response(false, 0);
	}

}
