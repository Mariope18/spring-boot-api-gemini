package com.esempio.primoprogetto.todo;

import com.esempio.primoprogetto.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Dice a JUnit di attivare Mockito
class TodoServiceImplTest {

    @Mock // Dice a Mockito: "Crea una versione finta di questa dipendenza"
    private TodoRepository todoRepository;

    @InjectMocks // Dice a Mockito: "Crea un'istanza REALE di questa classe e inietta i @Mock al suo interno"
    private TodoServiceImpl todoService;

    // I nostri metodi di test andranno qui...

    @Test // (JUnit) Indica che questo è un metodo di test
    void quandoChiamoFindAllByUser_restituisceLaListaDellUtente() {
        // --- 1. GIVEN (Dato che...) ---
        // Prepariamo i dati finti e programmiamo il comportamento del nostro mock.
        User utente = new User(); // Un finto utente

        // Diciamo a Mockito: "Quando il metodo 'findByUser' del finto repository viene chiamato
        // con questo specifico utente, ALLORA fai finta di restituire una lista con 1 solo Todo".
        when(todoRepository.findByUser(utente)).thenReturn(List.of(new Todo()));


        // --- 2. WHEN (Quando...) ---
        // Eseguiamo il metodo REALE del service che vogliamo testare.
        List<Todo> risultato = todoService.findAllByUser(utente);


        // --- 3. THEN (Allora...) ---
        // Verifichiamo con JUnit che il risultato sia quello che ci aspettiamo.
        assertNotNull(risultato); // Il risultato non deve essere nullo
        assertEquals(1, risultato.size()); // La lista deve contenere esattamente 1 elemento
    }
    @Test
    void quandoUtenteAggiornaProprioTodo_alloraUpdateHaSuccesso() {
        // --- GIVEN (Dato che...) ---
        // 1. Creiamo un utente e un To-Do esistente di sua proprietà.
        User proprietario = new User();
        proprietario.setId(1L);

        Todo todoEsistente = new Todo();
        todoEsistente.setId(10L);
        todoEsistente.setTitolo("Titolo Originale");
        todoEsistente.setUser(proprietario); // Fondamentale: associamo il todo al suo proprietario

        // 2. Creiamo l'oggetto con i nuovi dati per l'aggiornamento.
        Todo todoDettagliNuovi = new Todo();
        todoDettagliNuovi.setTitolo("Titolo Aggiornato");
        todoDettagliNuovi.setCompletato(true);

        // 3. Programmiamo i nostri mock.
        // Quando il repository cerca il todo con id 10, deve trovarlo.
        when(todoRepository.findById(10L)).thenReturn(Optional.of(todoEsistente));
        // Quando il repository salva un qualsiasi oggetto Todo, deve restituire l'oggetto stesso.
        when(todoRepository.save(any(Todo.class))).thenAnswer(invocation -> invocation.getArgument(0));


        // --- WHEN (Quando...) ---
        // 4. Eseguiamo il metodo di update reale.
        Optional<Todo> risultatoOptional = todoService.update(10L, todoDettagliNuovi, proprietario);


        // --- THEN (Allora...) ---
        // 5. Verifichiamo i risultati.
        assertTrue(risultatoOptional.isPresent(), "L'Optional non dovrebbe essere vuoto");

        Todo todoAggiornato = risultatoOptional.get();
        assertEquals("Titolo Aggiornato", todoAggiornato.getTitolo(), "Il titolo dovrebbe essere stato aggiornato");
        assertTrue(todoAggiornato.isCompletato(), "Lo stato 'completato' dovrebbe essere true");

        // 6. Verifichiamo che i metodi del repository siano stati chiamati.
        verify(todoRepository, times(1)).findById(10L); // Verifica che findById sia stato chiamato 1 volta.
        verify(todoRepository, times(1)).save(any(Todo.class)); // Verifica che save sia stato chiamato 1 volta.
    }
    @Test
    void quandoUtenteAggiornaTodoDiUnAltro_alloraRestituisceOptionalVuoto() {
        // --- GIVEN (Dato che...) ---
        // 1. Creiamo due utenti diversi.
        User proprietario = new User();
        proprietario.setId(1L);

        User altroUtente = new User();
        altroUtente.setId(2L); // Un utente diverso

        // 2. Creiamo un To-Do che appartiene al PRIMO utente.
        Todo todoEsistente = new Todo();
        todoEsistente.setId(10L);
        todoEsistente.setUser(proprietario);

        Todo todoDettagliNuovi = new Todo();
        todoDettagliNuovi.setTitolo("Tentativo di modifica");

        // 3. Programmiamo il mock: quando cerca il todo, lo trova.
        when(todoRepository.findById(10L)).thenReturn(Optional.of(todoEsistente));

        // NOTA: Non serve programmare il mock per 'save()', perché non ci aspettiamo che venga mai chiamato!

        // --- WHEN (Quando...) ---
        // 4. Il SECONDO utente prova ad aggiornare il todo del primo utente.
        Optional<Todo> risultatoOptional = todoService.update(10L, todoDettagliNuovi, altroUtente);


        // --- THEN (Allora...) ---
        // 5. Verifichiamo che l'operazione sia fallita come previsto.
        assertTrue(risultatoOptional.isEmpty(), "L'Optional dovrebbe essere vuoto perché l'utente non è il proprietario");

        // 6. Verifichiamo che il metodo 'save' non sia MAI stato chiamato.
        verify(todoRepository, never()).save(any(Todo.class));
    }
    @Test
    void quandoSalvoUnNuovoTodo_vieneAssegnatoLUtenteCorretto() {

        Todo todoDaSalvare = new Todo();
        User user = new User();

        when(todoRepository.save(any(Todo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Todo todoSalvato = todoService.save(todoDaSalvare,user);

        assertNotNull(todoSalvato);
        assertEquals(todoSalvato.getUser(),user);

        verify(todoRepository, times(1)).save(any(Todo.class));
    }
    @Test
    void findByIdAndUser_successo(){

        Todo todo = new Todo();
        User user = new User();

        when(todoRepository.findByIdAndUser(todo.getId(),user)).thenReturn(Optional.of(new Todo()));

        Optional<Todo> todoEsistente = todoService.findByIdAndUser(todo.getId(),user);

        assertNotNull(todoEsistente);
    }

    @Test
    void findByIdAndUser_fallito(){

        Todo todo = new Todo();
        User user = new User();

        Todo todo2 = new Todo();

        when(todoRepository.findByIdAndUser(todo.getId(),user)).thenReturn(Optional.of(new Todo()));

        Optional<Todo> todoEsistente = todoService.findByIdAndUser(todo2.getId(),user);

        assertNotNull(todoEsistente);
    }

    @Test
    void deleteById_successo() {

        Todo todo = new Todo();
        User user = new User();


    }
}