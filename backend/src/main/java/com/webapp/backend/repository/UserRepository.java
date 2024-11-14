package com.webapp.backend.repository;

import com.webapp.backend.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);  // Méthode pour trouver un utilisateur par son nom
    boolean existsByUsername(String username);  // Vérifie si un utilisateur avec ce nom existe
}
