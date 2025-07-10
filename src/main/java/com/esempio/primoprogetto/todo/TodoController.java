package com.esempio.primoprogetto.todo;

import com.esempio.primoprogetto.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/todos")
public class TodoController {

    @Autowired
    private TodoService todoService;

    @GetMapping
    public ResponseEntity<List<Todo>> getAllTodos(@AuthenticationPrincipal User user){
        return ResponseEntity.ok(todoService.findAllByUser(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Todo> getTodoById(@PathVariable Long id, @AuthenticationPrincipal User user) {
        //Todo todo = todoService.findById(id,user);
        // Se il todo esiste, restituisci 200 OK con il todo, altrimenti 404 Not Found
        //return todo.map(ResponseEntity::ok).orElseThrow(() -> new EntityNotFoundException("Todo non trovato con id: "+id));
        return todoService.findByIdAndUser(id,user)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Todo> createTodo(@RequestBody Todo todo, @AuthenticationPrincipal User user){
        return new ResponseEntity<>(todoService.save(todo,user), HttpStatus.CREATED);
        //return ResponseEntity.status(HttpStatus.CREATED).body(todoService.save(todo));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Todo> updateTodo(@PathVariable Long id, @RequestBody Todo todoNew, @AuthenticationPrincipal User user){
        //return todoService.update(id,todoNew).map(ResponseEntity::ok).orElseThrow(() -> new EntityNotFoundException("Todo non trovato con id: " +id));
        return todoService.update(id,todoNew, user)
                .map(ResponseEntity::ok)// Se l'Optional restituito dal service è pieno, crea una risposta 200 OK
                .orElseGet(() -> ResponseEntity.notFound().build());// Altrimenti, crea una risposta 404
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id, @AuthenticationPrincipal User user) {
        if (todoService.deleteById(id, user)) {
        // Usa 204 No Content, best practice per delete riuscite che non restituiscono un corpo
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}