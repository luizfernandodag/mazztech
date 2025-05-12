package com.mazztech.estapar.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.mazztech.estapar.dto.PlateStatusRequestDTO;
import com.mazztech.estapar.dto.PlateStatusResponseDTO;
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
public class PlateStatusService {

    private final VehicleRepository vehicleRepository;
    private final ParkingEventRepository parkingEventRepository;
    private final SpotRepository spotRepository;
    private final SectorRepository sectorRepository;

    public PlateStatusResponseDTO getPlateStatus(PlateStatusRequestDTO request) {
        Optional<Vehicle> vehicleOpt = vehicleRepository.findById(request.getLicense_plate());
        if (vehicleOpt.isEmpty()) {
            return PlateStatusResponseDTO.builder()
                    .license_plate(request.getLicense_plate())
                    .price_until_now(0.0)
                    .entry_time(null)
                    .time_parked(null)
                    .lat(null)
                    .lng(null)
                    .build();
        }
        Vehicle vehicle = vehicleOpt.get();
        List<ParkingEvent> events = parkingEventRepository.findByVehicle_LicensePlate(request.getLicense_plate());
        if (events.isEmpty()) {
            return PlateStatusResponseDTO.builder()
                    .license_plate(request.getLicense_plate())
                    .price_until_now(0.0)
                    .entry_time(null)
                    .time_parked(null)
                    .lat(null)
                    .lng(null)
                    .build();
        }
        // Último ENTRY
        Optional<ParkingEvent> lastEntry = events.stream()
                .filter(e -> e.getEventType() == ParkingEvent.EventType.ENTRY)
                .max(Comparator.comparing(ParkingEvent::getEntryTime));
        // Último PARKED
        Optional<ParkingEvent> lastParked = events.stream()
                .filter(e -> e.getEventType() == ParkingEvent.EventType.PARKED)
                .max(Comparator.comparing(ParkingEvent::getParkedTime));
        // Último EXIT
        Optional<ParkingEvent> lastExit = events.stream()
                .filter(e -> e.getEventType() == ParkingEvent.EventType.EXIT)
                .max(Comparator.comparing(ParkingEvent::getExitTime));

        LocalDateTime entryTime = lastEntry.map(ParkingEvent::getEntryTime).orElse(null);
        LocalDateTime parkedTime = lastParked.map(ParkingEvent::getParkedTime).orElse(entryTime);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime exitTime = lastExit.map(ParkingEvent::getExitTime).orElse(null);

        // Se já saiu, usar exitTime, senão usar now
        LocalDateTime endTime = exitTime != null ? exitTime : now;
        Duration duration = (parkedTime != null && endTime != null) ? Duration.between(parkedTime, endTime)
                : Duration.ZERO;

        // Buscar vaga e setor
        Double lat = null;
        Double lng = null;
        Double price = 0.0;
        if (lastParked.isPresent() && lastParked.get().getSpot() != null) {
            Spot spot = lastParked.get().getSpot();
            lat = spot.getLat();
            lng = spot.getLng();
            Sector sector = spot.getSector();
            if (sector != null) {
                // Calcular preço base
                double basePrice = sector.getBasePrice();
                long minutes = duration.toMinutes();
                double hours = Math.ceil(minutes / 60.0);
                // Calcular lotação do setor
                long ocupadas = spotRepository.findAll().stream()
                        .filter(s -> s.getSector() != null && s.getSector().getName().equals(sector.getName()))
                        .filter(s -> {
                            // Vaga está ocupada se houver um PARKED sem EXIT para ela
                            return parkingEventRepository.findByVehicle_LicensePlate(request.getLicense_plate())
                                    .stream()
                                    .anyMatch(e -> e.getSpot() != null && e.getSpot().getId().equals(s.getId())
                                            && e.getEventType() == ParkingEvent.EventType.PARKED);
                        })
                        .count();
                double lotacao = (double) ocupadas / sector.getMaxCapacity();
                double fator = 1.0;
                if (lotacao < 0.25) {
                    fator = 0.9;
                } else if (lotacao < 0.5) {
                    fator = 1.0;
                } else if (lotacao < 0.75) {
                    fator = 1.1;
                } else {
                    fator = 1.25;
                }
                price = basePrice * hours * fator;
            }
        }
        return PlateStatusResponseDTO.builder()
                .license_plate(request.getLicense_plate())
                .price_until_now(price)
                .entry_time(entryTime != null ? entryTime.toString() : null)
                .time_parked(parkedTime != null ? parkedTime.toString() : null)
                .lat(lat)
                .lng(lng)
                .build();
    }

}
