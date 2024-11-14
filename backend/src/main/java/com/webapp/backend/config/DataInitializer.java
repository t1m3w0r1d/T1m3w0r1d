package com.webapp.backend.config;

import com.webapp.backend.model.Role;
import com.webapp.backend.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initRoles(RoleRepository roleRepository) {
        return args -> {
            // Créer le rôle 'USER' si il n'existe pas déjà
            if (!roleRepository.existsById("USER")) {
                Role userRole = new Role();
                userRole.setName("USER");
                roleRepository.save(userRole);
            }

            // Créer le rôle 'ADMIN' si il n'existe pas déjà
            if (!roleRepository.existsById("ADMIN")) {
                Role adminRole = new Role();
                adminRole.setName("ADMIN");
                roleRepository.save(adminRole);
            }
        };
    }
}
