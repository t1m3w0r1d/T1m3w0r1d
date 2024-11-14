package com.webapp.backend.repository;

import com.webapp.backend.model.Role;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface RoleRepository extends MongoRepository<Role, String> {
    Optional<Role> findByName(String name);  // Méthode pour trouver un rôle par son nom
}
