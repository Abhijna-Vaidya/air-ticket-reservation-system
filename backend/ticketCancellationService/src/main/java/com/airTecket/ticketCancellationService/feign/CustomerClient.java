package com.airTecket.ticketCancellationService.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import com.airTecket.ticketCancellationService.dto.CustomerAuthResponseDTO;


@FeignClient(name = "user-registration-service")
public interface CustomerClient {
    @GetMapping("/api/auth/customers/validate")
    CustomerAuthResponseDTO validateToken(@RequestHeader("Authorization") String token);
    
}
