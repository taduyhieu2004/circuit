package com.example.userdemo.circuit_breaker;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CircuitBreakerV1  {

  String name();

  // Số lượng lỗi tối thiểu trước khi circuit breaker mở mạch.
  int failureThreshold() default 2;

  // Time chuyển từ open sang  half open
  int openToHalfOpenWaitTime() default 3000;

  int successThresholdInHalfOpen() default 2;

  int failureThresholdInHalfOpen() default 2;

  long timeoutDuration() default 2000;

  String fallBack() default "";
}
