package com.esempio.primoprogetto.admin;

public class AdminTodoCreateRequest {
    private String titolo;
    private boolean completato;
    private Long userId; // L'ID dell'utente a cui assegnare il To-Do

    // Getters e Setters
    public String getTitolo() { return titolo; }
    public void setTitolo(String titolo) { this.titolo = titolo; }
    public boolean isCompletato() { return completato; }
    public void setCompletato(boolean completato) { this.completato = completato; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}