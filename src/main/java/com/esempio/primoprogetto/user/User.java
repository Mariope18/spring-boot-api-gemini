package com.esempio.primoprogetto.user;

import com.esempio.primoprogetto.todo.Todo;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "utenti") // Meglio non chiamare la tabella "user", può essere una parola chiave in alcuni DB
public class User implements UserDetails {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING) // Salva il ruolo come stringa ("USER", "ADMIN") nel DB
    private Role role;

    // Un utente può avere tanti To-Do
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Todo> todos;

    // Metodi richiesti dall'interfaccia UserDetails
    // Per ora, li lasciamo semplici. Non gestiremo ruoli o account scaduti.

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Questo è un passo FONDAMENTALE.
        // Spring Security usa questa collezione per verificare le autorizzazioni.
        // Restituiamo una lista contenente un solo ruolo.
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // L'account non scade mai
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // L'account non è mai bloccato
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Le credenziali non scadono mai
    }

    @Override
    public boolean isEnabled() {
        return true; // L'account è sempre abilitato
    }

    // Getters e Setters standard per id, username, password
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return java.util.Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id);
    }
}