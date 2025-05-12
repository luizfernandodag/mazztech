package com.mazztech.estapar.dto;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
public class PlateStatusRequestDTO {
    @NotNull
    private String license_plate;

}
