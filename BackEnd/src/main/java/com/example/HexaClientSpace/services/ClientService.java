package com.example.HexaClientSpace.services;

import com.example.HexaClientSpace.dto.AuthResponse;
import com.example.HexaClientSpace.dto.LoginRequest;
import com.example.HexaClientSpace.dto.RegisterRequest;
import com.example.HexaClientSpace.entity.Client;
import com.example.HexaClientSpace.entity.Role;
import com.example.HexaClientSpace.entity.User;
import com.example.HexaClientSpace.repository.ClientRepository;
import com.example.HexaClientSpace.repository.UserRepository;
import com.example.HexaClientSpace.security.JwtService;

import com.example.HexaClientSpace.services.AuthService;
import com.example.HexaClientSpace.dto.*;

import com.example.HexaClientSpace.repository.ClientRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Transactional(readOnly = true)
    public ClientProfileResponse getProfil(Long userId) {
        Client client = clientRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException(
                        "Aucun profil client rattache a cet utilisateur."));
        return ClientProfileResponse.from(client);
    }

    @Transactional
    public ClientProfileResponse mettreAJourCoordonnees(Long userId, String telephone,
                                                        String adresse, String gouvernorat) {
        Client client = clientRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException(
                        "Aucun profil client rattache a cet utilisateur."));

        if (telephone != null)   client.setTelephone(telephone);
        if (adresse != null)     client.setAdresseInstallation(adresse);
        if (gouvernorat != null) client.setGouvernorat(gouvernorat);

        return ClientProfileResponse.from(client);
    }
}
