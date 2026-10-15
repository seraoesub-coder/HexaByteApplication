package com.example.HexaClientSpace.exception;

/** Le fournisseur d'IA ne répond pas (réseau, quota, clé...). Les détails restent dans les logs. */
public class ChatbotIndisponibleException extends RuntimeException {

    public ChatbotIndisponibleException(String message) {
        super(message);
    }
}