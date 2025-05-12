package com.mazztech.estapar.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.mazztech.estapar.dto.WebhookEventDTO;
import com.mazztech.estapar.model.ParkingEvent;
import com.mazztech.estapar.model.Spot;
import com.mazztech.estapar.model.Vehicle;
import com.mazztech.estapar.repository.ParkingEventRepository;
import com.mazztech.estapar.repository.SpotRepository;
import com.mazztech.estapar.repository.VehicleRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class WebHookService {

     private final VehicleRepository vehicleRepository;
    private final SpotRepository spotRepository;
    private final ParkingEventRepository parkingEventRepository;

    private static final DateTimeFormatter ISO_FORMAT = DateTimeFormatter.ISO_DATE_TIME;

    
    @Transactional
    public void processEvent(WebhookEventDTO event) {
        if(event.getEvent_type()!=null)
        {
        switch (event.getEvent_type()) {
            case "ENTRY":
                processEntry(event);
                break;
            case "PARKED":
                processParked(event);
                break;
            case "EXIT":
                processExit(event);
                break;
            default:
                throw new IllegalArgumentException("Tipo de evento desconhecido: " + event.getEvent_type());
        }
            }
    }

    
    private void processEntry(WebhookEventDTO event) {

        System.out.println("AAAAAAAAAAAAAAA");
        Vehicle vehicle = vehicleRepository.findById(event.getLicense_plate())
                .orElse(Vehicle.builder().licensePlate(event.getLicense_plate()).build());
        vehicle = vehicleRepository.save(vehicle);
        System.out.println(vehicle);

        ParkingEvent parkingEvent = ParkingEvent.builder()
                .vehicle(vehicle)
                .entryTime(LocalDateTime.parse(event.getEntry_time(), ISO_FORMAT))
                .eventType(ParkingEvent.EventType.ENTRY)
                .build();
        parkingEventRepository.save(parkingEvent);
        System.out.println(parkingEvent);

    }

    private void processParked(WebhookEventDTO event) {
        Optional<Vehicle> vehicleOpt = vehicleRepository.findById(event.getLicense_plate());
        Optional<Spot> spotOpt = spotRepository.findByLatAndLng(event.getLat(), event.getLng());
        if (vehicleOpt.isPresent() && spotOpt.isPresent()) {
            Vehicle vehicle = vehicleOpt.get();
            Spot spot = spotOpt.get();
            ParkingEvent parkingEvent = ParkingEvent.builder()
                    .vehicle(vehicle)
                    .spot(spot)
                    .parkedTime(LocalDateTime.now())
                    .eventType(ParkingEvent.EventType.PARKED)
                    .build();
            parkingEventRepository.save(parkingEvent);
        }
    }

    private void processExit(WebhookEventDTO event) {
        Optional<Vehicle> vehicleOpt = vehicleRepository.findById(event.getLicense_plate());
        if (vehicleOpt.isPresent()) {
            Vehicle vehicle = vehicleOpt.get();
            ParkingEvent parkingEvent = ParkingEvent.builder()
                    .vehicle(vehicle)
                    .exitTime(LocalDateTime.parse(event.getExit_time(), ISO_FORMAT))
                    .eventType(ParkingEvent.EventType.EXIT)
                    .build();
            parkingEventRepository.save(parkingEvent);
        }
    }

}
