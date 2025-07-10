package com.esempio.primoprogetto.todo;

import com.esempio.primoprogetto.admin.AdminTodoCreateRequest;
import com.esempio.primoprogetto.user.User;
import com.esempio.primoprogetto.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service // Annotazione fondamentale! Dichiara a Spring che questa è una classe di servizio.
public class TodoServiceImpl implements TodoService {

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<Todo> findAllByUser(User user) {
        return todoRepository.findByUser(user);
    }

    @Override
    public Optional<Todo> findByIdAndUser(Long id, User user) {
        return todoRepository.findByIdAndUser(id,user);
    }

    @Override
    @Transactional
    public Todo save(Todo todo, User user) {
        // Associa il Todo all'utente proprietario prima di salvarlo
        todo.setUser(user);
        return todoRepository.save(todo);
    }
    @Override
    @Transactional
    public boolean deleteById(Long id, User user) {
        // Delega tutto al nuovo, intelligentissimo metodo del repository.
        // Restituisce true solo se una riga è stata effettivamente cancellata.
        return todoRepository.deleteByIdAndUser(id, user) > 0;
    }

    @Override
    @Transactional
    public Optional<Todo> update(Long id, Todo todoDetails, User user) {
        // Controlla che il Todo esista E che appartenga all'utente
        return todoRepository.findById(id)
                .filter(todo -> todo.getUser().equals(user)) // Restituisce l'Optional solo se il proprietario corrisponde
                .map(todoEsistente -> {
                    if(todoDetails.getTitolo() != null){
                        todoEsistente.setTitolo(todoDetails.getTitolo());
                    }
                    todoEsistente.setCompletato(todoDetails.isCompletato());
                    return todoRepository.save(todoEsistente);
                });
    }

    @Override
    public List<Todo> findAllAsAdmin() {
        return todoRepository.findAll();
    }

    @Override
    @Transactional
    public Optional<Todo> updateTodoAsAdmin(Long id, Todo todoDetails) {
        return todoRepository.findById(id)
                .map(todoEsistente -> {
                    if(todoDetails.getTitolo() != null){
                        todoEsistente.setTitolo(todoDetails.getTitolo());
                    }
                    todoEsistente.setCompletato(todoDetails.isCompletato());
                    return todoRepository.save(todoEsistente);
                });
    }

    @Override
    @Transactional
    public boolean deleteTodoAsAdmin(Long id) {
        if(todoRepository.existsById(id)){
            todoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public Todo createTodoAsAdmin(AdminTodoCreateRequest request) {
        // 1. Cerca l'utente proprietario tramite l'ID fornito nella richiesta
        User proprietario = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Utente non trovato con id: " + request.getUserId()));

        // 2. Crea il nuovo oggetto Todo
        Todo nuovoTodo = new Todo();
        nuovoTodo.setTitolo(request.getTitolo());
        nuovoTodo.setCompletato(request.isCompletato());

        // 3. Associa il Todo all'utente proprietario trovato
        nuovoTodo.setUser(proprietario);

        // 4. Salva e restituisci il nuovo Todo
        return todoRepository.save(nuovoTodo);
    }


}