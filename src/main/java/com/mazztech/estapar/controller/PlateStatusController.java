package com.mazztech.estapar.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import com.mazztech.estapar.dto.PlateStatusRequestDTO;
import com.mazztech.estapar.dto.PlateStatusResponseDTO;
import com.mazztech.estapar.service.PlateStatusService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/plate-status")
@RequiredArgsConstructor
public class PlateStatusController {
    private final PlateStatusService plateStatusService;

    @Operation(summary = "Consulta status de uma placa", description = "Retorna status atual, preço parcial, horários e localização da placa informada.", requestBody = @RequestBody(required = true), responses = {
            @ApiResponse(responseCode = "200", description = "Status da placa", content = @Content(schema = @Schema(implementation = com.mazztech.estapar.dto.PlateStatusResponseDTO.class), examples = @ExampleObject(value = "{\n  \"license_plate\": \"ZUL0001\",\n  \"price_until_now\": 10.0,\n  \"entry_time\": \"2025-01-01T12:00:00.000Z\",\n  \"time_parked\": \"2025-01-01T12:00:00.000Z\",\n  \"lat\": -23.561684,\n  \"lng\": -46.655981\n}")))
    })
    @PostMapping
    public ResponseEntity<PlateStatusResponseDTO> getPlateStatus(
            @org.springframework.web.bind.annotation.RequestBody PlateStatusRequestDTO request) {
        PlateStatusResponseDTO response = plateStatusService.getPlateStatus(request);
        return ResponseEntity.ok(response);
    }
}