package com.mazztech.estapar.service;

import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.mazztech.estapar.dto.GarageConfigDTO;
import com.mazztech.estapar.model.Garage;
import com.mazztech.estapar.model.Sector;
import com.mazztech.estapar.model.Spot;
import com.mazztech.estapar.repository.GarageRepository;
import com.mazztech.estapar.repository.SectorRepository;
import com.mazztech.estapar.repository.SpotRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GarageService {
    private final GarageRepository garageRepository;
    private final SectorRepository sectorRepository;
    private final SpotRepository spotRepository;
    private final RestTemplate restTemplate;

    @Transactional
    public Garage importGarageData(GarageConfigDTO config) {
        final Garage garage = garageRepository.save(new Garage());

        // Setores e Vagas
        List<Sector> sectors = config.getGarage().stream().map(s -> {
            Sector sector = new Sector();
            sector.setName(s.getSector());
            sector.setBasePrice(s.getBasePrice());
            sector.setMaxCapacity(s.getMax_capacity());
            sector.setOpenHour(LocalTime.parse(s.getOpen_hour()));
            sector.setCloseHour(LocalTime.parse(s.getClose_hour()));
            sector.setDurationLimitMinutes(s.getDuration_limit_minutes());
            sector.setGarage(garage);
            sector = sectorRepository.save(sector);
            // Spots deste setor
            if (s.getSpots() != null) {
                for (GarageConfigDTO.SpotDTO spotDTO : s.getSpots()) {
                    Spot spot = new Spot();
                    spot.setId(spotDTO.getId());
                    spot.setSector(sector);
                    spot.setLat(spotDTO.getLat());
                    spot.setLng(spotDTO.getLng());
                    spotRepository.save(spot);
                }
            }
            return sector;
        }).toList();

        garage.setSectors(new java.util.ArrayList<>(sectors));
        return garageRepository.save(garage);
    }
}