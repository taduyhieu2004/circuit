package com.example.userdemo.circuit_breaker;

import jakarta.annotation.PreDestroy;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Data
@Slf4j
public class CircuitBreakerHandle {
  private CircuitBreakerState state;
  private int failureCount =0 ;
  private int successCount =0;
  private int failureCountInHalfOpen =0;
  private long lastFailureTime =0;
  private ScheduledExecutorService executor = Executors.newScheduledThreadPool(3);


  public synchronized void handleSuccess(int successThresholdInHalfOpen) {
    if (state == CircuitBreakerState.HALF_OPEN &&
          successCount >= successThresholdInHalfOpen) {
      this.reset();
    }

    successCount++;
  }

  public synchronized  void handleFailure(int failureThreshold, int failureThresholdInHalfOpen, int openToHalfOpenWaitTime) {
    if (state == CircuitBreakerState.HALF_OPEN) {
      failureCountInHalfOpen++;
      if (failureCountInHalfOpen >= failureThresholdInHalfOpen) {
        this.transitionToOpen(openToHalfOpenWaitTime);
        lastFailureTime = System.currentTimeMillis();
      }
    } else {
      failureCount++;
      if (failureCount >= failureThreshold) {
        lastFailureTime = System.currentTimeMillis();
        this.transitionToOpen(openToHalfOpenWaitTime);
      }
    }
  }

  public synchronized void transitionToHalfOpen() {
    if (state == CircuitBreakerState.OPEN) {
      state = CircuitBreakerState.HALF_OPEN;
      this.resetHalfOpenCounters();
    }
  }

  public synchronized void transitionToOpen(int openToHalfOpenWaitTime) {
    state = CircuitBreakerState.OPEN;
    this.resetHalfOpenCounters();
    executor.schedule(this::transitionToHalfOpen,
          openToHalfOpenWaitTime,
          TimeUnit.MILLISECONDS);
  }

  public synchronized void shutdown() {
    executor.shutdown();
  }

  private synchronized void reset() {
    state = CircuitBreakerState.CLOSED;
    failureCount = 0;
    resetHalfOpenCounters();
  }

  private synchronized void resetHalfOpenCounters() {
    successCount = 0;
    failureCountInHalfOpen = 0;
  }

  @PreDestroy
  public void destroy() {
    this.shutdown();
  }
}

