package com.mazztech.estapar.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mazztech.estapar.model.Revenue;

public interface RevenueRepository extends JpaRepository<Revenue, Long> {
Optional<Revenue> findBySectorAndDate(String sector, LocalDate date);


}
