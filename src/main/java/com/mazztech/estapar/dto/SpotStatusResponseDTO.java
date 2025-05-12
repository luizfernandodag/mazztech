package com.mazztech.estapar.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SpotStatusResponseDTO {
     @NotNull
    private boolean occupied;
     @NotNull
    private String license_plate;
     @NotNull
    private String entry_time;
     @NotNull @NotNull
    private String time_parked;
     @NotNull
     @NotNull
    private Double price_until_now;


}
