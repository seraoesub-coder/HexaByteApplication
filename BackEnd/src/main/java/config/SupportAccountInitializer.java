package com.example.HexaClientSpace.config;

import com.example.HexaClientSpace.entity.Role;
import com.example.HexaClientSpace.entity.User;
import com.example.HexaClientSpace.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Crée un compte AGENT_SUPPORT au démarrage si SUPPORT_EMAIL et SUPPORT_PASSWORD sont définis
 * (fichier .env). L'inscription publique ne peut JAMAIS produire ce rôle : elle crée toujours un CLIENT.
 * Aucun mot de passe n'est écrit dans le code ni dans le SQL.
 */
@Component
public class SupportAccountInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SupportAccountInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.support.email:}")
    private String email;

    @Value("${app.support.password:}")
    private String password;

    public SupportAccountInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (email.isBlank() || password.isBlank()) {
            log.info("Aucun compte support à créer (SUPPORT_EMAIL / SUPPORT_PASSWORD non définis).");
            return;
        }
        if (password.length() < 8) {
            log.warn("SUPPORT_PASSWORD trop court (8 caractères minimum) : compte support non créé.");
            return;
        }

        String mail = email.trim().toLowerCase();
        if (userRepository.existsByEmail(mail)) {
            log.info("Un compte existe déjà pour {} : aucun compte support créé.", mail);
            return;
        }

        User agent = new User();
        agent.setEmail(mail);
        agent.setMotDePasse(passwordEncoder.encode(password));
        agent.setRole(Role.AGENT_SUPPORT);
        agent.setActif(true);
        userRepository.save(agent);
        log.info("Compte support créé : {}", mail);
    }
}