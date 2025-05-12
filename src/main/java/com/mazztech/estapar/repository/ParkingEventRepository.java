package com.mazztech.estapar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mazztech.estapar.model.ParkingEvent;

public interface ParkingEventRepository extends JpaRepository<ParkingEvent, Long> {

List<ParkingEvent> findByVehicle_LicensePlate(String licensePlate);



}
