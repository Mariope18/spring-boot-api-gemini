package com.esempio.primoprogetto.todo;

import com.esempio.primoprogetto.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo,Long> {

    @Modifying
    @Query("DELETE FROM Todo t WHERE t.id = :id AND t.user = :user")
    int deleteByIdAndUser(Long id, User user);

    // Trova tutti i Todo associati a un specifico oggetto User
    // Spring Data JPA capisce dal nome del metodo come costruire la query
    List<Todo> findByUser(User user);

    Optional<Todo> findByIdAndUser(Long id, User user);
}
