package com.example.userdemo.configuration;

import com.example.userdemo.circuit_breaker.CircuitBreakerConfig;
import com.example.userdemo.circuit_breaker.CircuitBreakerHandle;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@org.springframework.context.annotation.Configuration
public class Configuration {

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
  @Bean
  public CircuitBreakerHandle customCircuitBreaker() {
    CircuitBreakerConfig config = new CircuitBreakerConfig(2, 10000, 2, 5, 2000);
    return new CircuitBreakerHandle(config);
  }

}
