package com.example.userdemo.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "address-service", url = "http://localhost:8081/api/v1/addresses")
public interface AddressFeignClient {
  @GetMapping()
  String detail();
}
