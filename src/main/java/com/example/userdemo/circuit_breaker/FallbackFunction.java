package com.example.userdemo.circuit_breaker;

@FunctionalInterface
public interface FallbackFunction<T> {
  T apply();
}
