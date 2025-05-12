package com.mazztech.estapar.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.mazztech.estapar.dto.RevenueRequestDTO;
import com.mazztech.estapar.dto.RevenueResponseDTO;
import com.mazztech.estapar.model.ParkingEvent;
import com.mazztech.estapar.model.Sector;
import com.mazztech.estapar.model.Spot;
import com.mazztech.estapar.repository.ParkingEventRepository;
import com.mazztech.estapar.repository.SectorRepository;
import com.mazztech.estapar.repository.SpotRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RevenueService {

    private final ParkingEventRepository parkingEventRepository;
    private final SpotRepository spotRepository;
    private final SectorRepository sectorRepository;

    public RevenueResponseDTO getRevenue(RevenueRequestDTO request) {
        LocalDate date = LocalDate.parse(request.getDate(), DateTimeFormatter.ISO_DATE);
        String sectorName = request.getSector();
        List<Spot> spots = spotRepository.findAll().stream()
                .filter(s -> s.getSector() != null && s.getSector().getName().equals(sectorName))
                .toList();
        List<Long> spotIds = spots.stream().map(Spot::getId).toList();
        List<ParkingEvent> exits = parkingEventRepository.findAll().stream()
                .filter(e -> e.getEventType() == ParkingEvent.EventType.EXIT)
                .filter(e -> e.getExitTime() != null && e.getExitTime().toLocalDate().equals(date))
                .filter(e -> e.getSpot() != null && spotIds.contains(e.getSpot().getId()))
                .toList();
        double total = 0.0;
        Optional<Sector> sectorOpt = sectorRepository.findAll().stream()
                .filter(s -> s.getName().equals(sectorName))
                .findFirst();
        double basePrice = sectorOpt.map(Sector::getBasePrice).orElse(0.0);
        for (ParkingEvent exit : exits) {
            LocalDateTime parkedTime = exit.getParkedTime();
            LocalDateTime exitTime = exit.getExitTime();
            if (parkedTime != null && exitTime != null) {
                long minutes = java.time.Duration.between(parkedTime, exitTime).toMinutes();
                double hours = Math.ceil(minutes / 60.0);
                total += basePrice * hours; // Regra dinâmica pode ser aplicada aqui se necessário
            }
        }
        return RevenueResponseDTO.builder()
                .amount(total)
                .currency("BRL")
                .timestamp(date.atStartOfDay().toString())
                .build();
    }

}
