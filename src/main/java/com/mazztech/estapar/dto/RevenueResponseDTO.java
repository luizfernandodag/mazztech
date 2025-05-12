package com.mazztech.estapar.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RevenueResponseDTO {
     @NotNull
    private Double amount;
     @NotNull
     @NotNull
     @NotNull
     private String currency;
     @NotNull
    private String timestamp;



}
