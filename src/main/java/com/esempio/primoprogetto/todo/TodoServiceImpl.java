package com.esempio.primoprogetto.todo;

import com.esempio.primoprogetto.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service // Annotazione fondamentale! Dichiara a Spring che questa è una classe di servizio.
public class TodoServiceImpl implements TodoService {

    @Autowired
    private TodoRepository todoRepository;

    @Override
    public List<Todo> findAllByUser(User user) {
        return todoRepository.findByUser(user);
    }

    @Override
    public Optional<Todo> findById(Long id, User user) {
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


}