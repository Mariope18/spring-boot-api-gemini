package com.esempio.primoprogetto.todo;

import com.esempio.primoprogetto.user.User;

import java.util.List;
import java.util.Optional;

public interface TodoService {
    List<Todo> findAllByUser(User user);
    Optional<Todo> findByIdAndUser(Long id, User user);
    Todo save(Todo todo, User user);
    boolean deleteById(Long id, User user);
    Optional<Todo> update(Long id, Todo todoDetails, User user);
}