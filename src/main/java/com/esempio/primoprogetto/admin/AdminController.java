package com.esempio.primoprogetto.admin;

import com.esempio.primoprogetto.todo.Todo;
import com.esempio.primoprogetto.todo.TodoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    @Autowired
    private TodoServiceImpl todoService; // Inietta il service

    @GetMapping("/hello")
    public ResponseEntity<String> sayHelloToAdmin() {
        return ResponseEntity.ok("Ciao, Admin! Questa è un'area protetta.");
    }

    @GetMapping("/todos")
    public ResponseEntity<List<Todo>> getAllTodosAsAdmin(){
        return ResponseEntity.ok(todoService.findAllAsAdmin());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Todo> updateAnyTodo(@PathVariable Long id, @RequestBody Todo todoDetails){
        return todoService.updateTodoAsAdmin(id,todoDetails)
                .map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnyTodo(@PathVariable Long id) {
        if(todoService.deleteTodoAsAdmin(id)){
            return ResponseEntity.noContent().build();
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/todos")
    public ResponseEntity<Todo> createTodoForUser(@RequestBody AdminTodoCreateRequest request) {
        Todo todoCreato = todoService.createTodoAsAdmin(request);
        return new ResponseEntity<>(todoCreato, HttpStatus.CREATED);
    }
}