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
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Component
@RequiredArgsConstructor
public class CircuitBreakerAspectConfig {
  private final ConcurrentHashMap<String, CircuitBreakerState> states = new ConcurrentHashMap<>();

  private final CircuitBreakerHandle customCircuitBreaker;

  @Around("@annotation(com.example.userdemo.circuit_breaker.CircuitBreakerV1)")
  public Object checkCircuitBreaker(ProceedingJoinPoint joinPoint) throws Throwable {


    if (customCircuitBreaker.getState() == CircuitBreakerState.OPEN) {
      // Nếu có tên hàm fallback được chỉ định, thì gọi hàm fallback
      // Nếu không có hàm fallback, có thể ném ra một ngoại lệ hoặc thực hiện hành động khác
      return new User("1","duy hieu","00231","none");

    }

    try {
      Object result = joinPoint.proceed();
      customCircuitBreaker.handleSuccess();
      return result;
    } catch (Throwable throwable) {
      customCircuitBreaker.handleFailure();

      throw throwable;
    }
  }


  private Object invokeFallbackMethod(ProceedingJoinPoint joinPoint, String methodName) throws Throwable {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();

    try {
      Method fallbackMethod = joinPoint.getTarget().getClass().getMethod(methodName, method.getParameterTypes());
      return fallbackMethod.invoke(joinPoint.getTarget(), joinPoint.getArgs());
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException("Could not invoke fallback method: " + methodName, e);
    }
  }
}

