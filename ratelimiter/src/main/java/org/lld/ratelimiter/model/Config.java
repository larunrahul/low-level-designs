package org.lld.ratelimiter.model;

import java.time.Duration;

public class Config {
	private Duration period;
	private long tokensPerPeriod;
	private long capacity;
	private long bucketCount;

	public Duration getPeriod() {
		return period;
	}

	public void setPeriod(Duration period) {
		this.period = period;
	}

	public long getTokensPerPeriod() {
		return tokensPerPeriod;
	}

	public void setTokensPerPeriod(long tokensPerPeriod) {
		this.tokensPerPeriod = tokensPerPeriod;
	}

	public long getCapacity() {
		return capacity;
	}

	public void setCapacity(long capacity) {
		this.capacity = capacity;
	}

	public long getBucketCount() {
		return bucketCount;
	}

	public void setBucketCount(long bucketCount) {
		this.bucketCount = bucketCount;
	}
}
