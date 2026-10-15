package com.example.HexaClientSpace.services;

import com.example.HexaClientSpace.dto.AuthResponse;
import com.example.HexaClientSpace.dto.LoginRequest;
import com.example.HexaClientSpace.dto.RegisterRequest;


import com.example.HexaClientSpace.entity.*;

import com.example.HexaClientSpace.repository.ClientRepository;
import com.example.HexaClientSpace.repository.UserRepository;
import com.example.HexaClientSpace.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Year;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       ClientRepository clientRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    /**
     * Inscription : User + Client crees dans la meme transaction.
     * Si la creation du Client echoue, le User est annule aussi.
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalStateException("Un compte existe deja avec cet email.");
        }
        if (request.telephone() != null && clientRepository.existsByTelephone(request.telephone())) {
            throw new IllegalStateException("Ce numero de telephone est deja utilise.");
        }

        User user = new User();
        user.setEmail(request.email().toLowerCase().trim());
        user.setMotDePasse(passwordEncoder.encode(request.motDePasse()));
        user.setRole(Role.CLIENT);
        user.setActif(true);

        Client client = new Client();
        client.setNom(request.nom().trim());
        client.setPrenom(request.prenom().trim());
        client.setTelephone(request.telephone());
        client.setAdresseInstallation(request.adresseInstallation());
        client.setGouvernorat(request.gouvernorat());
        client.setNumeroClient(genererNumeroClient());
        client.setUser(user);

        user.setClient(client);          // cascade ALL depuis User
        User saved = userRepository.save(user);

        return construireReponse(saved);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        // Leve BadCredentialsException / DisabledException si echec.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email().toLowerCase().trim(), request.motDePasse()));

        User user = userRepository.findByEmail(request.email().toLowerCase().trim())
                .orElseThrow(() -> new IllegalStateException("Utilisateur introuvable."));

        user.setDerniereConnexion(LocalDateTime.now());

        return construireReponse(user);
    }

    private AuthResponse construireReponse(User user) {
        Client client = user.getClient();
        return AuthResponse.of(
                jwtService.generateToken(user),
                jwtService.getExpirationMs() / 1000,
                user.getId(),
                user.getEmail(),
                user.getRole().name(),
                client != null ? client.getNumeroClient() : null,
                client != null ? client.getPrenom() + " " + client.getNom() : null);
    }

    /**
     * Reference abonne lisible : HXB-2026-000001.
     * Pour de la production, preferer une sequence dediee en base
     * plutot qu'un comptage, qui n'est pas sur en concurrence.
     */
    private String genererNumeroClient() {
        long suivant = clientRepository.count() + 1;
        String numero;
        do {
            numero = String.format("HXB-%d-%06d", Year.now().getValue(), suivant++);
        } while (clientRepository.existsByNumeroClient(numero));
        return numero;
    }
}
