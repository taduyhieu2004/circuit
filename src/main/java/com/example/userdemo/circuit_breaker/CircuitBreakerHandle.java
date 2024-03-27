package com.example.userdemo.circuit_breaker;

import jakarta.annotation.PreDestroy;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;
import java.util.function.Supplier;

@Data
@Slf4j

public class CircuitBreakerHandle {
  private CircuitBreakerState state;
  private static int failureCount =0 ;
  private CircuitBreakerConfig config;
  private static int successCount =0;
  private static int failureCountInHalfOpen =0;
  private static long lastFailureTime =0;
  private ScheduledExecutorService executor = Executors.newScheduledThreadPool(3);


  public CircuitBreakerHandle(CircuitBreakerConfig config) {
    this.config = config;
    this.state = CircuitBreakerState.CLOSED;

  }
  public synchronized <T> T call(Supplier<T> supplier, FallbackFunction<T> fallback) {
    if (state == CircuitBreakerState.OPEN) {
      return fallback.apply();
    }

    Future<T> future = executor.submit(supplier::get);

    try {
      T result = future.get(config.getTimeoutDuration(), TimeUnit.MILLISECONDS);
      handleSuccess();
      return result;
    } catch (TimeoutException e) {
      future.cancel(true);
      handleFailure();
      return fallback.apply();
    } catch (Exception e) {
      handleFailure();
      return fallback.apply();
    }
  }

  public  void handleSuccess() {
    if (state == CircuitBreakerState.HALF_OPEN &&
          successCount >= config.getSuccessThresholdInHalfOpen()) {
      this.reset();
    }

    successCount++;
  }

  public  void handleFailure() {
    if (state == CircuitBreakerState.HALF_OPEN) {
      failureCountInHalfOpen++;
      if (failureCountInHalfOpen >= config.getFailureThresholdInHalfOpen()) {
        this.transitionToOpen();
        lastFailureTime = System.currentTimeMillis();
      }
    } else {
      failureCount++;
      if (failureCount >= config.getFailureThreshold()) {
        lastFailureTime = System.currentTimeMillis();
        this.transitionToOpen();
      }
    }
  }

  public  void transitionToHalfOpen() {
    if (state == CircuitBreakerState.OPEN) {
      state = CircuitBreakerState.HALF_OPEN;
      this.resetHalfOpenCounters();
    }
  }

  public  void transitionToOpen() {
    state = CircuitBreakerState.OPEN;
    this.resetHalfOpenCounters();
    executor.schedule(this::transitionToHalfOpen,
          config.getOpenToHalfOpenWaitTime(),
          TimeUnit.MILLISECONDS);
  }

  public  void shutdown() {
    executor.shutdown();
  }

  private  void reset() {
    state = CircuitBreakerState.CLOSED;
    failureCount = 0;
    resetHalfOpenCounters();
  }

  private  void resetHalfOpenCounters() {
    successCount = 0;
    failureCountInHalfOpen = 0;
  }

  @PreDestroy
  public void destroy() {
    this.shutdown();
  }
}

