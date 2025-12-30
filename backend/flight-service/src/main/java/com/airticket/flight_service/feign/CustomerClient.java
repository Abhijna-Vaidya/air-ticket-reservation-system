package com.airticket.flight_service.feign;

import com.airticket.flight_service.dto.CustomerAuthResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "user-registration-service")
public interface CustomerClient {
    @GetMapping("/api/auth/customers/validate")
    ResponseEntity<CustomerAuthResponseDTO> validateToken(@RequestHeader("Authorization") String token);
}
