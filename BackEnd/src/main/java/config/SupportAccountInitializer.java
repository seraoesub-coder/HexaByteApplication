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
 * Crée au démarrage un compte AGENT_SUPPORT et un compte ADMIN si leurs identifiants sont définis
 * dans le fichier .env (SUPPORT_EMAIL / SUPPORT_PASSWORD, ADMIN_EMAIL / ADMIN_PASSWORD).
 * L'inscription publique ne peut JAMAIS produire ces rôles : elle crée toujours un CLIENT.
 * Aucun mot de passe n'est écrit dans le code ni dans le SQL.
 */
@Component
public class SupportAccountInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SupportAccountInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.support.email:}")
    private String supportEmail;

    @Value("${app.support.password:}")
    private String supportPassword;

    @Value("${app.admin.email:}")
    private String adminEmail;

    @Value("${app.admin.password:}")
    private String adminPassword;

    public SupportAccountInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        creerSiAbsent(supportEmail, supportPassword, Role.AGENT_SUPPORT, "support", "SUPPORT");
        creerSiAbsent(adminEmail, adminPassword, Role.ADMIN, "administrateur", "ADMIN");
    }

    private void creerSiAbsent(String email, String motDePasse, Role role, String libelle, String prefixeVariable) {
        if (email.isBlank() || motDePasse.isBlank()) {
            log.info("Aucun compte {} à créer ({}_EMAIL / {}_PASSWORD non définis).",
                    libelle, prefixeVariable, prefixeVariable);
            return;
        }
        if (motDePasse.length() < 8) {
            log.warn("{}_PASSWORD trop court (8 caractères minimum) : compte {} non créé.", prefixeVariable, libelle);
            return;
        }

        String mail = email.trim().toLowerCase();
        if (userRepository.existsByEmail(mail)) {
            log.info("Un compte existe déjà pour {} : aucun compte {} créé.", mail, libelle);
            return;
        }

        User compte = new User();
        compte.setEmail(mail);
        compte.setMotDePasse(passwordEncoder.encode(motDePasse));
        compte.setRole(role);
        compte.setActif(true);
        userRepository.save(compte);
        log.info("Compte {} créé : {}", libelle, mail);
    }
}