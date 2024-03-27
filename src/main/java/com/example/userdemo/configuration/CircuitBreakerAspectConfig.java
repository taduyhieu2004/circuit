package com.example.userdemo.configuration;

import com.example.userdemo.circuit_breaker.CircuitBreakerHandle;
import com.example.userdemo.circuit_breaker.CircuitBreakerState;
import com.example.userdemo.entity.User;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.*;

@Aspect
@Component
@RequiredArgsConstructor
public class CircuitBreakerAspectConfig {
  private final ConcurrentHashMap<String, CircuitBreakerState> states = new ConcurrentHashMap<>();

  private final CircuitBreakerHandle customCircuitBreaker;

  private final ExecutorService executor = Executors.newFixedThreadPool(10);

  @Around("@annotation(com.example.userdemo.circuit_breaker.CircuitBreakerV1)")
  public Object checkCircuitBreaker(ProceedingJoinPoint joinPoint) throws Throwable {


    if (customCircuitBreaker.getState() == CircuitBreakerState.OPEN) {
      MethodSignature signature = (MethodSignature) joinPoint.getSignature();
      Method method = signature.getMethod();
      Method fallbackMethod = joinPoint.getTarget().getClass().getMethod("fallbackMethod", String.class );
      return fallbackMethod.invoke(joinPoint.getTarget(), joinPoint.getArgs());
//      return invokeFallbackMethod(joinPoint,"fallbackMethod");

//      return new User("1", "duy hieu", "00231", "none");

    }

    Callable<Object> task = () -> {
      try {
        return joinPoint.proceed();
      } catch (Throwable throwable) {
        throw new RuntimeException(throwable);
      }
    };

    Future<Object> future = executor.submit(task);
    try {
      return future.get(2000, TimeUnit.MILLISECONDS);
    } catch (TimeoutException e) {

      customCircuitBreaker.handleFailure();

      throw e;
    }
  }
  private Object invokeFallbackMethod(ProceedingJoinPoint joinPoint, String methodName) throws Throwable {
    if (methodName.isEmpty()) {
      throw new RuntimeException("No fallback method specified");
    }

    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();

    try {
      Method fallbackMethod = joinPoint.getTarget().getClass().getMethod(methodName, method.getParameterTypes());
      return fallbackMethod.invoke(joinPoint.getTarget(), joinPoint.getArgs());
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException("Could not invoke fallback method: " + methodName, e);
    }
  }

//  private Object invokeFallbackMethod(ProceedingJoinPoint joinPoint, String methodName) throws Throwable {
//    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
//    Method method = signature.getMethod();
//
//    try {
//      Method fallbackMethod = joinPoint.getTarget().getClass().getMethod(methodName, method.getParameterTypes());
//      return fallbackMethod.invoke(joinPoint.getTarget(), joinPoint.getArgs());
//    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
//      throw new RuntimeException("Could not invoke fallback method: " + methodName, e);
//    }
//  }
}

