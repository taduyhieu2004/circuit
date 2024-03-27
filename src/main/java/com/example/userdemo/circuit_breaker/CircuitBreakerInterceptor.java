//package com.example.userdemo.circuit_breaker;
//
//import lombok.extern.slf4j.Slf4j;
//
//import java.lang.reflect.InvocationHandler;
//import java.lang.reflect.Method;
//import java.util.concurrent.TimeoutException;
//@Slf4j
//public class CircuitBreakerInterceptor implements InvocationHandler {
//  private final Object target;
//  private final CircuitBreakerHandle circuitBreakerHandle;
//
//  public CircuitBreakerInterceptor(Object target, CircuitBreakerHandle circuitBreakerHandle) {
//    this.target = target;
//    this.circuitBreakerHandle = circuitBreakerHandle;
//  }
//
//  @Override
//  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//    log.info("test annotation");
//    if (!method.isAnnotationPresent(CircuitBreakerV1.class)) {
//      return method.invoke(target, args);
//    }
//
//
//    CircuitBreakerV1 circuitBreakerV1 = method.getAnnotation(CircuitBreakerV1.class);
//
//    if (circuitBreakerHandle.getState() == CircuitBreakerState.OPEN) {
//      return circuitBreakerV1.fallBack();
////    }
//
//    try {
//      Object result = method.invoke(target, args);
//      circuitBreakerHandle.handleSuccess(circuitBreakerV1.successThresholdInHalfOpen());
//      return result;
//    } catch (Exception e) {
//      // Xử lý các ngoại lệ khác
//      circuitBreakerHandle.handleFailure(circuitBreakerV1.failureThreshold(), circuitBreakerV1.failureThresholdInHalfOpen(), circuitBreakerV1.openToHalfOpenWaitTime());
//      // Ghi log hoặc xử lý cụ thể cho các trường hợp ngoại lệ không mong muốn
//      e.printStackTrace();
//      return circuitBreakerV1.fallBack();
//    }
//  }
//
//}
