package com.example.userdemo.circuit_breaker;

import java.lang.reflect.Proxy;

public class CircuitBreakerProxy {
  public static <T> T createProxy(T target, CircuitBreakerHandle circuitBreakerHandler) {
    return (T) Proxy.newProxyInstance(
          target.getClass().getClassLoader(),
          target.getClass().getInterfaces(),
          new CircuitBreakerInterceptor(target, circuitBreakerHandler));
  }
}
