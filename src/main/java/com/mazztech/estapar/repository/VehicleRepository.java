package com.mazztech.estapar.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mazztech.estapar.model.Vehicle;

public interface VehicleRepository extends JpaRepository<Vehicle, String> {
}
