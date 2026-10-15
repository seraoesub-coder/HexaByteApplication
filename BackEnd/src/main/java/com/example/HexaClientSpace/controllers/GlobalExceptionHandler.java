package com.example.HexaClientSpace.controllers;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> erreurs = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
          .forEach(e -> erreurs.put(e.getField(), e.getDefaultMessage()));

        Map<String, Object> body = corps(HttpStatus.BAD_REQUEST, "Donnees invalides");
        body.put("champs", erreurs);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex) {
        // Message volontairement generique : ne pas reveler si l'email existe.
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(corps(HttpStatus.UNAUTHORIZED, "Email ou mot de passe incorrect."));
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<Map<String, Object>> handleDisabled(DisabledException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(corps(HttpStatus.FORBIDDEN, "Ce compte est desactive."));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalState(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(corps(HttpStatus.CONFLICT, ex.getMessage()));
    }

    private Map<String, Object> corps(HttpStatus statut, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", statut.value());
        body.put("message", message);
        return body;
    }
}
