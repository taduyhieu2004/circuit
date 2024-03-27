package com.example.userdemo.circuit_breaker;

import lombok.Data;

@Data
public class CircuitBreakerConfig {
  private final int failureThreshold;
  private final long openToHalfOpenWaitTime;
  private final int successThresholdInHalfOpen;
  private final int failureThresholdInHalfOpen;
  private final long timeoutDuration;

  public CircuitBreakerConfig(
        int failureThreshold, long openToHalfOpenWaitTime,
        int successThresholdInHalfOpen, int failureThresholdInHalfOpen,
        long timeoutDuration
  ) {
    this.failureThreshold = failureThreshold;
    this.openToHalfOpenWaitTime = openToHalfOpenWaitTime;
    this.successThresholdInHalfOpen = successThresholdInHalfOpen;
    this.failureThresholdInHalfOpen = failureThresholdInHalfOpen;
    this.timeoutDuration = timeoutDuration;
  }
}
