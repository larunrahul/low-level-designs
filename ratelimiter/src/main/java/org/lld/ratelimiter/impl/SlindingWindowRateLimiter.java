package org.lld.ratelimiter.impl;

import java.time.Duration;
import java.time.Instant;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicLong;

import org.lld.ratelimiter.RateLimiter;
import org.lld.ratelimiter.model.Config;
import org.lld.ratelimiter.model.Response;

public class SlindingWindowRateLimiter implements RateLimiter {

	class Bucket {
		private long id;
		private AtomicLong count;

		private Bucket(long id){
			this.id = id;
			this.count = new AtomicLong();
		}

		private Bucket(long id, long count){
			this.id = id;
			this.count = new AtomicLong(count);
		}
		public void increment(){
			count.addAndGet(1);
		}

		public long getId() {
			return id;
		}

		public AtomicLong getCount() {
			return count;
		}
	}

	//number of tokens consumed
	private AtomicLong consumedTokens;
	//number of tokens per window
	private long tokensPerPeriod;
	//window size in millis, must be a multiple of bucketCount
	private long period;
	//epoch used for calculating window
	private Instant epoch;
	//size of each bucket in millis
	private long bucketSize;
	//number of buckets
	private long bucketCount;

	Deque<Bucket> slidingWindow = new ConcurrentLinkedDeque<>();

	public SlindingWindowRateLimiter(Config config){
		this.consumedTokens = new AtomicLong();
		this.tokensPerPeriod = config.getTokensPerPeriod();
		this.period = config.getPeriod().toMillis();
		this.epoch = Instant.now();
		this.bucketCount = config.getBucketCount();
		assert period % bucketCount == 0;
		this.bucketSize = period / bucketCount;
	}
	@Override
	public synchronized Response isAllowed() {
		Instant currentTime = Instant.now();
		//0 based
		long currentBucket = Duration.between(epoch, currentTime).toMillis() / bucketSize;
		long nearestInvalidBucket = currentBucket - bucketCount;
		//remove all requests which are before our window
		while(!slidingWindow.isEmpty() && slidingWindow.getFirst().getId() <= nearestInvalidBucket){
			consumedTokens.updateAndGet(count -> Math.max(0, count - slidingWindow.removeFirst().getCount().get()));
		}
		// not allowed if we consumed all tokens
		if(consumedTokens.get() >= tokensPerPeriod) {
			return new Response(false, 0);
		}
		if(slidingWindow.isEmpty() || slidingWindow.getLast().getId() < currentBucket) {
			slidingWindow.addLast(new Bucket(0, 1)); //fresh start of window
		}else {
			slidingWindow.getLast().increment();
		}
		consumedTokens.incrementAndGet();
		return new Response(true, 0);
	}

}
