package com.esempio.primoprogetto.user;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // Metodo per trovare un utente dal suo username.
    // Spring Data JPA creerà l'implementazione per noi.
    Optional<User> findByUsername(String username);
}