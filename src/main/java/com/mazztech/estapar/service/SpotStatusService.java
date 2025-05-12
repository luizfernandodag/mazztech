package com.mazztech.estapar.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.mazztech.estapar.dto.SpotStatusRequestDTO;
import com.mazztech.estapar.dto.SpotStatusResponseDTO;
import com.mazztech.estapar.model.ParkingEvent;
import com.mazztech.estapar.model.Sector;
import com.mazztech.estapar.model.Spot;
import com.mazztech.estapar.model.Vehicle;
import com.mazztech.estapar.repository.ParkingEventRepository;
import com.mazztech.estapar.repository.SectorRepository;
import com.mazztech.estapar.repository.SpotRepository;
import com.mazztech.estapar.repository.VehicleRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class SpotStatusService {

    private final SpotRepository spotRepository;
    private final ParkingEventRepository parkingEventRepository;
    private final VehicleRepository vehicleRepository;
    private final SectorRepository sectorRepository;

    public SpotStatusResponseDTO getSpotStatus(SpotStatusRequestDTO request) {
        Optional<Spot> spotOpt = spotRepository.findById(request.getSpotID());
        if (spotOpt.isEmpty()) {
            return SpotStatusResponseDTO.builder()
                    .occupied(false)
                    .license_plate("")
                    .price_until_now(0.0)
                    .entry_time(null)
                    .time_parked(null)
                    .build();
        }
        Spot spot = spotOpt.get();
        // Buscar eventos PARKED para a vaga
        List<ParkingEvent> events = parkingEventRepository.findAll().stream()
                .filter(e -> e.getSpot() != null && e.getSpot().getId().equals(spot.getId()))
                .toList();
        // Último PARKED
        Optional<ParkingEvent> lastParked = events.stream()
                .filter(e -> e.getEventType() == ParkingEvent.EventType.PARKED)
                .max(Comparator.comparing(ParkingEvent::getParkedTime));
        // Último EXIT
        Optional<ParkingEvent> lastExit = events.stream()
                .filter(e -> e.getEventType() == ParkingEvent.EventType.EXIT)
                .max(Comparator.comparing(ParkingEvent::getExitTime));
        boolean occupied = false;
        String licensePlate = "";
        Double price = 0.0;
        String entryTime = null;
        String timeParked = null;
        if (lastParked.isPresent()) {
            LocalDateTime parkedTime = lastParked.get().getParkedTime();
            LocalDateTime exitTime = lastExit.map(ParkingEvent::getExitTime).orElse(null);
            occupied = exitTime == null || parkedTime.isAfter(exitTime);
            Vehicle vehicle = lastParked.get().getVehicle();
            licensePlate = vehicle != null ? vehicle.getLicensePlate() : "";
            entryTime = lastParked.get().getEntryTime() != null ? lastParked.get().getEntryTime().toString() : null;
            timeParked = parkedTime != null ? parkedTime.toString() : null;
            // Calcular preço parcial
            Sector sector = spot.getSector();
            if (sector != null) {
                double basePrice = sector.getBasePrice();
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime endTime = occupied ? now : exitTime;
                Duration duration = (parkedTime != null && endTime != null) ? Duration.between(parkedTime, endTime)
                        : Duration.ZERO;
                long minutes = duration.toMinutes();
                double hours = Math.ceil(minutes / 60.0);
                // Regra de preço dinâmico (simplificada: lotação não é relevante para consulta
                // de uma vaga)
                price = basePrice * hours;
            }
        }

        return SpotStatusResponseDTO.builder()
                .occupied(occupied)
                .license_plate(licensePlate)
                .price_until_now(price)
                .entry_time(entryTime)
                .time_parked(timeParked)
                .build();
    }

}
