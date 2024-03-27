package com.example.userdemo.service;

import com.example.userdemo.circuit_breaker.CircuitBreakerV1;
import com.example.userdemo.entity.User;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class UserServiceImpl implements UserService {

  @Autowired
  AddressFeignClient addressFeignClient;

  private static final String SERVICE_NAME = "user-service";


  private User buildUser() {
    return new User("1", "duy hieu", "012391283", null);
  }


  @Override
  @CircuitBreakerV1(name = SERVICE_NAME, fallBack = "fallbackMethod")
  public User detail() {
    User user = buildUser();
    String address = addressFeignClient.detail();

    user.setAddress(address);
    return user;
  }

  private String fallbackMethod() {
    return "Address service is not responding properly";
  }

}
