package com.mazztech.estapar.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class PlateStatusResponseDTO {
    @NotNull
    private String license_plate;
    @NotNull
    private String entry_time;
    @NotNull
    private String time_parked;
    @NotNull
    private Double price_until_now;
    @NotNull
    private Double lat;
    @NotNull
    private Double lng;


}
