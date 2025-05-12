package com.mazztech.estapar.service;


import com.mazztech.estapar.model.ParkingEvent;
import com.mazztech.estapar.model.Vehicle;
import com.mazztech.estapar.dto.PlateStatusRequestDTO;
import com.mazztech.estapar.dto.PlateStatusResponseDTO;
import com.mazztech.estapar.repository.ParkingEventRepository;
import com.mazztech.estapar.repository.VehicleRepository;
import com.mazztech.estapar.service.PlateStatusService;
import com.mazztech.estapar.repository.SectorRepository;
import com.mazztech.estapar.repository.SpotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PlateStatusServiceTest {

     @Mock
    private VehicleRepository vehicleRepository;
    @Mock
    private ParkingEventRepository parkingEventRepository;
    @Mock
    private SpotRepository spotRepository;
    @Mock
    private SectorRepository sectorRepository;

    @InjectMocks
    private PlateStatusService plateStatusService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetPlateStatus_VehicleNotFound() {
        PlateStatusRequestDTO request = new PlateStatusRequestDTO();
        request.setLicense_plate("ABC1234");
        when(vehicleRepository.findById("ABC1234")).thenReturn(Optional.empty());
        PlateStatusResponseDTO response = plateStatusService.getPlateStatus(request);
        assertEquals("ABC1234", response.getLicense_plate());
        assertEquals(0.0, response.getPrice_until_now());
        assertNull(response.getEntry_time());
        assertNull(response.getTime_parked());
        assertNull(response.getLat());
        assertNull(response.getLng());
    }

    @Test
    void testGetPlateStatus_VehicleExistsNoEvents() {
        PlateStatusRequestDTO request = new PlateStatusRequestDTO();
        request.setLicense_plate("DEF5678");
        Vehicle vehicle = Vehicle.builder().licensePlate("DEF5678").build();
        when(vehicleRepository.findById("DEF5678")).thenReturn(Optional.of(vehicle));
        when(parkingEventRepository.findByVehicle_LicensePlate("DEF5678")).thenReturn(Collections.emptyList());
        PlateStatusResponseDTO response = plateStatusService.getPlateStatus(request);
        assertEquals("DEF5678", response.getLicense_plate());
        assertEquals(0.0, response.getPrice_until_now());
        assertNull(response.getEntry_time());
        assertNull(response.getTime_parked());
        assertNull(response.getLat());
        assertNull(response.getLng());
    }

     @Test
    void testGetPlateStatus_WithEntryAndParkedEvents() {
        PlateStatusRequestDTO request = new PlateStatusRequestDTO();
        request.setLicense_plate("ZUL0001");
        Vehicle vehicle = Vehicle.builder().licensePlate("ZUL0001").build();
        when(vehicleRepository.findById("ZUL0001")).thenReturn(Optional.of(vehicle));

        // Simula setor e vaga
        com.mazztech.estapar.model.Sector sector = com.mazztech.estapar.model.Sector.builder().id(1L).name("A").build();
        com.mazztech.estapar.model.Spot spot = com.mazztech.estapar.model.Spot.builder()
                .id(1L)
                .sector(sector)
                .lat(-23.561684)
                .lng(-46.655981)
                .build();

        // Simula eventos ENTRY e PARKED
        ParkingEvent entryEvent = ParkingEvent.builder()
                .vehicle(vehicle)
                .entryTime(java.time.LocalDateTime.of(2025, 1, 1, 12, 0))
                .eventType(ParkingEvent.EventType.ENTRY)
                .build();
        ParkingEvent parkedEvent = ParkingEvent.builder()
                .vehicle(vehicle)
                .spot(spot)
                .parkedTime(java.time.LocalDateTime.of(2025, 1, 1, 12, 5))
                .eventType(ParkingEvent.EventType.PARKED)
                .build();
        when(parkingEventRepository.findByVehicle_LicensePlate("ZUL0001"))
                .thenReturn(java.util.List.of(entryEvent, parkedEvent));

        PlateStatusResponseDTO response = plateStatusService.getPlateStatus(request);
        assertEquals("ZUL0001", response.getLicense_plate());
        assertNotNull(response.getEntry_time());
        assertNotNull(response.getTime_parked());
        // O preço parcial será 0.0 porque não há basePrice no setor
        assertEquals(0.0, response.getPrice_until_now());
    }

     @Test
    void testGetPlateStatus_WithEntryParkedAndExitEventsAndSpotAndSector() {
        PlateStatusRequestDTO request = new PlateStatusRequestDTO();
        request.setLicense_plate("ZUL0002");
        Vehicle vehicle = Vehicle.builder().licensePlate("ZUL0002").build();
        when(vehicleRepository.findById("ZUL0002")).thenReturn(Optional.of(vehicle));

        // Simula setor e vaga
        com.mazztech.estapar.model.Sector sector = com.mazztech.estapar.model.Sector.builder()
                .id(1L)
                .name("A")
                .basePrice(10.0)
                .maxCapacity(100)
                .build();
        com.mazztech.estapar.model.Spot spot = com.mazztech.estapar.model.Spot.builder()
                .id(1L)
                .sector(sector)
                .lat(-23.561684)
                .lng(-46.655981)
                .build();
        when(sectorRepository.findAll()).thenReturn(java.util.List.of(sector));
        when(spotRepository.findAll()).thenReturn(java.util.List.of(spot));

        // Simula eventos ENTRY, PARKED e EXIT
        java.time.LocalDateTime entryTime = java.time.LocalDateTime.of(2025, 1, 1, 12, 0);
        java.time.LocalDateTime parkedTime = java.time.LocalDateTime.of(2025, 1, 1, 12, 5);
        java.time.LocalDateTime exitTime = java.time.LocalDateTime.of(2025, 1, 1, 14, 0);
        ParkingEvent entryEvent = ParkingEvent.builder()
                .vehicle(vehicle)
                .entryTime(entryTime)
                .eventType(ParkingEvent.EventType.ENTRY)
                .build();
        ParkingEvent parkedEvent = ParkingEvent.builder()
                .vehicle(vehicle)
                .spot(spot)
                .parkedTime(parkedTime)
                .eventType(ParkingEvent.EventType.PARKED)
                .build();
        ParkingEvent exitEvent = ParkingEvent.builder()
                .vehicle(vehicle)
                .spot(spot)
                .exitTime(exitTime)
                .eventType(ParkingEvent.EventType.EXIT)
                .build();
        when(parkingEventRepository.findByVehicle_LicensePlate("ZUL0002"))
                .thenReturn(java.util.List.of(entryEvent, parkedEvent, exitEvent));

        PlateStatusResponseDTO response = plateStatusService.getPlateStatus(request);
        assertEquals("ZUL0002", response.getLicense_plate());
        assertEquals(-23.561684, response.getLat());
        assertEquals(-46.655981, response.getLng());
        assertEquals(entryTime.toString(), response.getEntry_time());
        assertEquals(parkedTime.toString(), response.getTime_parked());
        // Duração: 2h (12:05 -> 14:00), preço base 10.0, preço = 20.0
        assertEquals(20.0, response.getPrice_until_now());
    }



}
