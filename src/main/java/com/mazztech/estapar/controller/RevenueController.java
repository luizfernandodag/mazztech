package com.mazztech.estapar.controller;


import com.mazztech.estapar.dto.RevenueRequestDTO;
import com.mazztech.estapar.dto.RevenueResponseDTO;
import com.mazztech.estapar.service.RevenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/revenue")
@RequiredArgsConstructor
public class RevenueController {

     private final RevenueService revenueService;

  
    @PostMapping
    public ResponseEntity<RevenueResponseDTO> getRevenue(@RequestBody RevenueRequestDTO request) {
        RevenueResponseDTO response = revenueService.getRevenue(request);
        return ResponseEntity.ok(response);
    }

}
