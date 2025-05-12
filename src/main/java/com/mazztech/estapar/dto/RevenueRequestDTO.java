package com.mazztech.estapar.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RevenueRequestDTO {
     @NotNull
    private String date;
     @NotNull
    private String sector;


}
