package com.mazztech.estapar.controller;



import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mazztech.estapar.dto.WebhookEventDTO;
import com.mazztech.estapar.service.WebHookService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
public class WebHookController {
    private final WebHookService webhookService;

   
    @PostMapping
    public ResponseEntity<Void> receiveEvent(@RequestBody WebhookEventDTO event) {
        System.out.println(event);
        webhookService.processEvent(event);
        return ResponseEntity.ok().build();
        
    }

}
