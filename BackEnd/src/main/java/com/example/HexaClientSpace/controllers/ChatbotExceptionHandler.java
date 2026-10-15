package com.example.HexaClientSpace.controllers;

import com.example.HexaClientSpace.exception.ChatbotIndisponibleException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ChatbotExceptionHandler {

    @ExceptionHandler(ChatbotIndisponibleException.class)
    public ResponseEntity<Map<String, Object>> handle(ChatbotIndisponibleException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        // Message volontairement générique : aucun détail du fournisseur n'est exposé au client.
        body.put("message", "L'assistant est momentanément indisponible. Veuillez réessayer dans un instant.");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body);
    }
}