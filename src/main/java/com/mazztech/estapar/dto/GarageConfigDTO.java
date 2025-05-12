package com.mazztech.estapar.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GarageConfigDTO {

private List<SectorDTO> garage;

@Data
public static class SectorDTO{
    @NotNull
    private String sector;
    @NotNull
    private Double basePrice;
    @NotNull
    private Integer max_capacity;
    @NotNull
    private String open_hour;
    @NotNull
    private String close_hour;
    @NotNull   
     private Integer duration_limit_minutes;
    private List<SpotDTO> spots;


    
}

@Data 
public static class SpotDTO{
    @NotNull
    private Long id;
    @NotNull
    private Double lat;
    @NotNull
    private Double lng;
}


}
