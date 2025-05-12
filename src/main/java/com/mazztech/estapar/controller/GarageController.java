package com.mazztech.estapar.controller;


import com.mazztech.estapar.model.Garage;
import com.mazztech.estapar.service.GarageService;
import com.mazztech.estapar.dto.GarageConfigDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/garage")
@RequiredArgsConstructor
public class GarageController {
    private final GarageService garageService;

    @PostMapping
    public ResponseEntity<Garage> importGarage(@RequestBody GarageConfigDTO config) {
        Garage garage = garageService.importGarageData(config);
        return ResponseEntity.ok(garage);
    }
}