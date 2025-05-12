package com.mazztech.estapar.integration;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.mazztech.estapar.dto.GarageConfigDTO;
import com.mazztech.estapar.dto.PlateStatusRequestDTO;
import com.mazztech.estapar.dto.PlateStatusResponseDTO;
import com.mazztech.estapar.dto.RevenueRequestDTO;
import com.mazztech.estapar.dto.RevenueResponseDTO;
import com.mazztech.estapar.dto.SpotStatusRequestDTO;
import com.mazztech.estapar.dto.SpotStatusResponseDTO;
import com.mazztech.estapar.model.Garage;
import com.mazztech.estapar.repository.GarageRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ApplicationFullIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private GarageRepository garageRepository;

    private static Long spotId;

    @Test
    @Order(1)
    void testImportGarage() {
        // Monta o JSON de importação
        GarageConfigDTO.SpotDTO spotDTO = new GarageConfigDTO.SpotDTO();
        spotDTO.setId(1L);
        spotDTO.setLat(-23.561684);
        spotDTO.setLng(-46.655981);

        GarageConfigDTO.SectorDTO sectorDTO = new GarageConfigDTO.SectorDTO();
        sectorDTO.setSector("A");
        sectorDTO.setBasePrice(10.0);
        sectorDTO.setMax_capacity(100);
        sectorDTO.setOpen_hour("08:00");
        sectorDTO.setClose_hour("22:00");
        sectorDTO.setDuration_limit_minutes(240);
        sectorDTO.setSpots(List.of(spotDTO));

        GarageConfigDTO garageConfig = new GarageConfigDTO();
        garageConfig.setGarage(List.of(sectorDTO));

        // Simula a importação (direto no banco, pois o endpoint /garage consome de um
        // serviço externo)
        Garage garage = new Garage();
        garage = garageRepository.save(garage);

        // O spotId será usado nos próximos testes
        spotId = spotDTO.getId();

        assertNotNull(garage.getId());
    }

    @Test
    @Order(2)
    void testWebhookEntryParkedExit() {
        // ENTRY
        String webhookEntry = "{" +
                "  \"license_plate\": \"ZUL0001\"," +
                "  \"entry_time\": \"2025-01-01T12:00:00.000Z\"," +
                "  \"event_type\": \"ENTRY\"" +
                "}";
        restTemplate.postForEntity("/webhook", webhookEntry, Void.class);

        // PARKED
        String webhookParked = "{" +
                "  \"license_plate\": \"ZUL0001\"," +
                "  \"lat\": -23.561684," +
                "  \"lng\": -46.655981," +
                "  \"event_type\": \"PARKED\"" +
                "}";
        restTemplate.postForEntity("/webhook", webhookParked, Void.class);

        // EXIT
        String webhookExit = "{" +
                "  \"license_plate\": \"ZUL0001\"," +
                "  \"exit_time\": \"2025-01-01T14:00:00.000Z\"," +
                "  \"event_type\": \"EXIT\"" +
                "}";
        restTemplate.postForEntity("/webhook", webhookExit, Void.class);
    }

    @Test
    @Order(3)
    void testPlateStatus() {
        PlateStatusRequestDTO plateRequest = new PlateStatusRequestDTO();
        plateRequest.setLicense_plate("ZUL0001");
        ResponseEntity<PlateStatusResponseDTO> plateResponse = restTemplate.postForEntity(
                "/plate-status", plateRequest, PlateStatusResponseDTO.class);
        assertEquals(HttpStatus.OK, plateResponse.getStatusCode());
        assertEquals("ZUL0001", plateResponse.getBody().getLicense_plate());
        assertNotNull(plateResponse.getBody().getEntry_time());
    }

    @Test
    @Order(4)
    void testSpotStatus() {
        SpotStatusRequestDTO spotRequest = new SpotStatusRequestDTO();
        spotRequest.setSpotID(1L);
        ResponseEntity<SpotStatusResponseDTO> spotResponse = restTemplate.postForEntity(
                "/spot-status", spotRequest, SpotStatusResponseDTO.class);
        assertEquals(HttpStatus.OK, spotResponse.getStatusCode());
        assertEquals("ZUL0001", spotResponse.getBody().getLicense_plate());
    }

    @Test
    @Order(5)
    void testRevenue() {
        RevenueRequestDTO revenueRequest = new RevenueRequestDTO();
        revenueRequest.setDate("2025-01-01");
        revenueRequest.setSector("A");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RevenueRequestDTO> entity = new HttpEntity<>(revenueRequest, headers);

        ResponseEntity<RevenueResponseDTO> revenueResponse = restTemplate.exchange(
                "/revenue", HttpMethod.GET, entity, RevenueResponseDTO.class);
        assertEquals(HttpStatus.OK, revenueResponse.getStatusCode());
        assertTrue(revenueResponse.getBody().getAmount() >= 0.0);
    }
}