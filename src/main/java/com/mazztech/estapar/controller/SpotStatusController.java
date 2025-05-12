package com.mazztech.estapar.controller;



import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mazztech.estapar.dto.SpotStatusRequestDTO;
import com.mazztech.estapar.dto.SpotStatusResponseDTO;
import com.mazztech.estapar.service.SpotStatusService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/spot-status")
@RequiredArgsConstructor
public class SpotStatusController {
     private final SpotStatusService spotStatusService;


    @PostMapping
    public ResponseEntity<SpotStatusResponseDTO> getSpotStatus(@RequestBody SpotStatusRequestDTO request) {
        System.out.println(request);
        SpotStatusResponseDTO response = spotStatusService.getSpotStatus(request);
        return ResponseEntity.ok(response);
    }

}
