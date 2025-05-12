package com.mazztech.estapar.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mazztech.estapar.model.Spot;

public interface SpotRepository extends JpaRepository<Spot, Long> {
    Optional<Spot> findByLatAndLng(Double lat, Double lng);
}
