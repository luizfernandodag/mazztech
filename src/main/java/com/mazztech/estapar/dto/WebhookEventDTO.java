package com.mazztech.estapar.dto;

import lombok.Data;

@Data
public class WebhookEventDTO {
  private String license_plate;
    private String entry_time;
    private String exit_time;
    private String event_type;
    private Double lat;
    private Double lng;

    
    

}
